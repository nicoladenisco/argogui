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
package org.argogui.modules.screens;

import org.argogui.om.*;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import org.apache.turbine.util.uri.TemplateURI;
import org.argogui.services.cache.UsersDataCache;
import org.sirio6.utils.CoreRunData;

/**
 * Classe di supporto per la gestione web
 * degli allarmi in particolare per la
 * disattivazione di  questi ultimi.
 *
 * @author Nicola De Nisco
 */
public class Editallarmi extends ArgoBaseScreen
{
  @Override
  protected void doBuildTemplate2(CoreRunData data, Context context)
     throws Exception
  {
    // recupero il record dalla tabella allarmi
    int idall = data.getParameters().getInt("Id", -1); // NOI18N
    if(idall == -1)
      throw new IllegalArgumentException("Parametro 'Id' non trovato.");

    GenAllarmi a = GenAllarmiPeer.retrieveByPK(idall);
    context.put("al", a); // NOI18N

    // recupero utente
    TurbineUser us = UsersDataCache.getInstance().getUser(a.getIdUser());
    context.put("utente", us); // NOI18N

    // Url per abbandona
    TemplateURI uri = new TemplateURI(data, "maint.vm"); // NOI18N
    uri.addPathInfo("type", "maint-allarmi"); // NOI18N
    context.put("urlAbbandona", uri.getRelativeLink()); // NOI18N

    // imposta layout a forms
    data.getTemplateInfo().setLayoutTemplate("Form.vm"); // NOI18N
  }

  /**
   * Controlla permessi dell'utente per visulizzare pagina allarmi.
   *
   * @param data Turbine information.
   * @return True if the user is authorized to access the screen.
   * @exception Exception, a generic exception.
   */
  @Override
  protected boolean isAuthorized(CoreRunData data) throws Exception
  {
    return super.isAuthorizedAll(data, "visualizza_server"); // NOI18N
  }
}
