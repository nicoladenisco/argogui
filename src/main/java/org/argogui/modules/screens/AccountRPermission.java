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
import org.argogui.om.TurbineRole;
import org.sirio6.utils.CoreRunData;
import org.apache.velocity.context.Context;

/**
 * Editing delle permission associate ad un ruolo.
 * @author Nicola De Nisco
 */
public class AccountRPermission extends AccountBaseScreen
{
  @Override
  protected void doBuildTemplateAccount(CoreRunData data, Context context, AccountBean ab)
     throws Exception
  {
    int idRole = data.getParameters().getInt("roleid", -1); // NOI18N
    if(idRole != -1)
    {
      // singolo ruole
      ab.tidSelected = new int[]
      {
        idRole
      };
    }
    else
    {
      int[] rSel = data.getParameters().getInts("role_selected"); // NOI18N
      if(rSel != null)
        ab.tidSelected = rSel;
    }

    if(ab.tidSelected != null && ab.tidSelected.length == 1)
    {
      TurbineRole tr = ab.getRole(ab.tidSelected[0]);
      context.put("role", tr);
    }

    String html = ab.getHtmlTedit6SelectedRoles();
    context.put("html", html);
    context.put("rlist", ab.vRole);
    context.put("ab", ab);
  }
}
