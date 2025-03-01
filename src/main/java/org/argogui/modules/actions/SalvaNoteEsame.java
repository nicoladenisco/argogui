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

import org.argogui.Costanti;
import org.argogui.beans.VisRisultatiBean;
import org.argogui.om.InfInEsami;
import org.sirio6.utils.CoreRunData;
import org.argogui.utils.SU;
import org.apache.velocity.context.Context;
import org.argogui.ArgoBusMessages;
import org.sirio6.beans.BeanFactory;
import org.sirio6.rigel.CachedObjectSaver;
import org.sirio6.services.bus.BUS;
import org.sirio6.services.bus.BusContext;
import org.sirio6.services.security.SEC;

/**
 * Salvataggio note per esame.
 *
 * @author Nicola De Nisco
 */
public class SalvaNoteEsame extends ArgoBaseAction
{
  @Override
  public void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    String idVis = SU.okStrNull(data.getParameters().getString("idvis"));
    boolean confirmed = data.getParameters().getInt("conferma") != 0; // NOI18N

    if(idVis == null)
      throw new Exception(data.i18n("Errore interno: manca il parametro idvis."));

    VisRisultatiBean bean = BeanFactory.getFromSession(data, VisRisultatiBean.class);
    VisRisultatiBean.ResBlock block = bean.getRisultatiPronti().get(idVis);

    for(VisRisultatiBean.ResItem r : block.items)
    {
      InfInEsami esa = r.ie;
      String note = SU.okStrNull(data.getParameters().getString("note"));
      esa.setNote(note);

      if(confirmed)
        esa.setStatoRec(Costanti.STATO_REC_USER_CONFIRMED);

      int idUser = SEC.getUserID(data);
      CachedObjectSaver.save(null, esa, idUser, esa.getStatoRec(), 0);
    }

    if(confirmed)
      BUS.sendMessageAsync(ArgoBusMessages.STUDY_RESULT_CONFIRMED, this, new BusContext("block", block));
  }
}
