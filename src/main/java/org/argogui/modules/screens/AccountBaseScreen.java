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

import org.apache.velocity.context.Context;
import org.argogui.beans.turbine.AccountBean;
import org.sirio6.beans.CoreBaseBean;
import org.sirio6.utils.CoreRunData;

/**
 * Screen di base per la gestione degli account.
 * @author Nicola De Nisco
 */
public abstract class AccountBaseScreen extends ArgoBaseScreen
{
  public AccountBaseScreen()
  {
    setBeanClass(AccountBean.class);
  }

  @Override
  final protected void doBuildTemplate2(CoreRunData data, Context context)
     throws Exception
  {
    super.doBuildTemplate2(data, context);
  }

  @Override
  final protected void doBuildTemplate2(CoreRunData data, Context context, CoreBaseBean bean)
     throws Exception
  {
    doBuildTemplateAccount(data, context, (AccountBean) bean);
  }

  abstract protected void doBuildTemplateAccount(CoreRunData data, Context context, AccountBean bean)
     throws Exception;

  /**
   * Overide this method to perform the security check needed.
   *
   * @param data Turbine information.
   * @return True if the user is authorized to access the screen.
   * @exception Exception, a generic exception.
   */
  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return isAuthorizedAll(data, "visualizza_account"); // NOI18N
  }
}
