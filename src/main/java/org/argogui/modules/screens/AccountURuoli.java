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
 * Editing dei ruoli associati ad un utente.
 * @author Nicola De Nisco
 */
public class AccountURuoli extends AccountBaseScreen
{
  @Override
  protected void doBuildTemplateAccount(CoreRunData data, Context context, AccountBean ab)
     throws Exception
  {
    int idUser = data.getParameters().getInt("userid", -1);
    if(idUser != -1)
    {
      // singolo utente
      ab.uidSelected = new int[]
      {
        idUser
      };
    }
    else
    {
      int[] uSel = data.getParameters().getInts("users_selected");
      if(uSel != null)
        ab.uidSelected = uSel;
    }

    if(ab.uidSelected != null && ab.uidSelected.length == 1)
    {
      TurbineUser us = ab.getUser(ab.uidSelected[0]);
      context.put("user", us);
    }

    String html = ab.getHtmlTedit5SelectedUsers();
    context.put("html", html);
    context.put("ulist", ab.vUser);
    context.put("ab", ab);
  }
}
