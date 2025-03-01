/*
 *  Copyright (C) 2016 RAD-IMAGE s.r.l.
 *
 *  Questo software è proprietà di RAD-IMAGE s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  RAD-IMAGE s.r.l.
 *  Via San Giovanni, 1 - Contrada Belvedere
 *  San Nicola Manfredi (BN)
 *
 *  Creato il 10 Febbraio 2016, 19:06:00
 */
package org.argogui.modules.actions;

import org.argogui.om.GenAllarmi;
import org.argogui.om.GenAllarmiPeer;
import org.sirio6.utils.CoreRunData;
import org.argogui.utils.SU;
import java.util.Date;
import java.util.Map;
import org.apache.velocity.context.Context;
import org.rigel5.db.DbUtils;
import org.sirio6.services.allarmi.ServAllarmi;
import org.sirio6.services.security.SEC;
import org.sirio6.utils.DT;

/**
 * Action per modificare lo stato degli allarmi.
 *
 * @author Nicola De Nisco
 */
public class Editallarmi extends ArgoBaseAction
{
  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return isAuthorizedAll(data, "disinnesco_allarmi"); // NOI18N
  }

  @Override
  public void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    int id = data.getParameters().getInt("id", -1); // NOI18N
    int idUser = SEC.getUserID(data);
    String note = data.getParameters().getString("descnote", ""); // NOI18N
    String command = data.getParameters().getString("command"); // NOI18N

    if(command == null)
      throw new Exception(data.i18n("Errore interno: manca il parametro 'command'."));

    if(!SU.isOkStr(note))
      if(idUser == 0)
        note = data.i18n("disattivato da amministratore");
      else
        data.throwMessagei18n("Le note di disinnesco sono obbligatorie.");

    doCommand(command, data, null, id, idUser, note);

    ServAllarmi all = (ServAllarmi) getService(ServAllarmi.SERVICE_NAME);
    all.disattivaAllarmiAttivi(idUser, data.i18n("Disattivato da amministratore."));
  }

  public void doCmd_uno(CoreRunData data, Map params, Object... args)
     throws Exception
  {
    int id = (Integer) args[0];
    int idUser = (Integer) args[1];
    String note = (String) args[2];

    if(id == -1)
      data.throwMessagei18n("Parametri non corretti.");

    GenAllarmi a = GenAllarmiPeer.retrieveByPK(id);
    a.setStato("D");
    a.setNoteDisabilitazione(note);
    a.setIdUser(idUser);
    a.setUltModif(new Date());
    a.save();

    log.info(
       "ALLARME " + a.getServizio() + "/" + a.getComponente() + " \n" // NOI18N
       + note + "\n"
       + "Disabilitato dall'indirizzo " + data.getRemoteAddr() + " -- Utente:" + idUser); // NOI18N
  }

  public void doCmd_tutti(CoreRunData data, Map params, Object... args)
     throws Exception
  {
    int idUser = (Integer) args[1];
    String note = (String) args[2];

    if(!isAuthorizedAll(data, "disinnesco_multiplo_allarmi")) // NOI18N
      return;

    String sSQL = "UPDATE gen.allarmi "
       + " SET stato='D', note_disabilitazione='" + note + "',"
       + " id_user=0, ult_modif='" + DT.formatIso(new Date()) + "'"
       + " WHERE stato='A'"; // NOI18N
    int numClear = DbUtils.executeStatement(sSQL);

    log.info(
       "TUTTI GLI ALLARMI (" + numClear + ")\n" // NOI18N
       + note + "\n"
       + "Disabilitato dall'indirizzo " + data.getRemoteAddr() + " -- Utente:" + idUser); // NOI18N
  }

  public void doCmd_abbandona(CoreRunData data, Map params, Object... args)
     throws Exception
  {
    data.setScreenTemplate("Unisetup.vm"); // NOI18N
    data.getParameters().setString("type", "maint-allarmi"); // NOI18N
  }
}
