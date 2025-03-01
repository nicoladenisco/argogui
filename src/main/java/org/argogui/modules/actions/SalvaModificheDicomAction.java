/*
 *  SalvaModificheDicomAction.java
 *  Creato il Apr 5, 2017, 6:47:18 PM
 *
 *  Copyright (C) 2017 RAD-IMAGE s.r.l.
 *
 *  Questo software è proprietà di RAD-IMAGE s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  RAD-IMAGE s.r.l.
 *  Via San Giovanni 1 - Contrada Belvedere
 *  San Nicola Manfredi (BN)
 */
package org.argogui.modules.actions;

import org.argogui.beans.ModificaDicomBean;
import org.sirio6.utils.CoreRunData;
import org.argogui.utils.SU;
import java.util.Map;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.sirio6.beans.BeanFactory;
import org.sirio6.beans.NavigationStackBean;

/**
 * Action per il salvataggio modifiche ai files DICOM.
 *
 * @author Nicola De Nisco
 */
public class SalvaModificheDicomAction extends ArgoBaseAction
{
  @Override
  public void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    ModificaDicomBean bean = BeanFactory.getFromSession(data, ModificaDicomBean.class);

    String command = SU.okStrNull(data.getParameters().getString("command"));

    if(command != null)
    {
      Map params = SU.getParMap(data);
      bean.doCommand(command, data, params, context);
      doCommand(command, data, params, context);
    }
  }

  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return isAuthorizedAll(data, "modifica_dicom");
  }

  public void doCmd_abbandona(CoreRunData data, Map params, Object... args)
     throws Exception
  {
    // verifica lo stack di navigazione per eventuale indirizzo di ritorno
    if(NavigationStackBean.ret2Session(data))
    {
      BeanFactory.removeFromSession(data, ModificaDicomBean.class);
      return;
    }
  }
}
