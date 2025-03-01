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

import org.argogui.beans.turbine.AccountBean;
import org.sirio6.utils.CoreRunData;
import org.apache.velocity.context.Context;

/**
 * Gestione associazione ruoli agli utenti.
 * @author Nicola De Nisco
 */
public class AccountUtentiRuoli extends AccountBaseScreen
{
  @Override
  protected void doBuildTemplateAccount(CoreRunData data, Context context, AccountBean ab)
     throws Exception
  {
    String html = ab.getHtmlTedit5();
    context.put("html", html);
  }
}
