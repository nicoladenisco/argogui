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

import org.argogui.beans.turbine.AccountBean;
import org.sirio6.utils.CoreRunData;
import org.apache.velocity.context.Context;
import org.sirio6.beans.BeanFactory;
import org.sirio6.services.security.SEC;

/**
 * Salva il form Utenti/Ruoli sul database.
 *
 * @author Nicola De Nisco
 */
public class SalvaUtentiRuoli extends ArgoBaseAction
{
  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    if(SEC.checkAllPermission(data, "admin_users")) // NOI18N
      return true;

    data.getTemplateInfo().setScreenTemplate("/nopermessi.vm"); // NOI18N
    return false;
  }

  @Override
  public void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    AccountBean ab = BeanFactory.getFromSession(data, AccountBean.class);
    if(ab == null)
      throw new Exception("Manca l'oggetto AccountBean."); // NOI18N

    ab.storeUserGroupRole(data);
    ab.saveUserGroupRole();
  }
}
