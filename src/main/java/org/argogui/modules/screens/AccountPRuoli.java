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
import org.argogui.om.TurbineUser;
import org.sirio6.utils.CoreRunData;
import org.apache.velocity.context.Context;

/**
 * Editing dei ruoli associati ad un permesso.
 * @author Nicola De Nisco
 */
public class AccountPRuoli extends AccountBaseScreen
{
  @Override
  protected void doBuildTemplateAccount(CoreRunData data, Context context, AccountBean ab)
     throws Exception
  {
    int idPerm = data.getParameters().getInt("permid", -1); // NOI18N
    if(idPerm != -1)
    {
      // singolo utente
      ab.pidSelected = new int[]
      {
        idPerm
      };
    }
    else
    {
      int[] pSel = data.getParameters().getInts("perm_selected"); // NOI18N
      if(pSel != null)
        ab.pidSelected = pSel;
    }

    if(ab.pidSelected != null && ab.pidSelected.length == 1)
    {
      TurbineUser us = ab.getUser(ab.pidSelected[0]);
      context.put("user", us);
    }

    String html = ab.getHtmlTedit6SelectedPermissions();
    context.put("html", html);
    context.put("plist", ab.vPerm);
    context.put("ab", ab);
  }
}
