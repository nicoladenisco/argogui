/*
 *  RicercaAction.java
 *  Creato il 23-apr-2016, 12.41.50
 *
 *  Copyright (C) 2016 RAD-IMAGE s.r.l.
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

import org.argogui.beans.RicercaDicomBean;
import org.sirio6.utils.CoreRunData;
import org.argogui.utils.SU;
import java.util.Map;
import org.apache.velocity.context.Context;
import org.sirio6.beans.BeanFactory;

/**
 * Action per la ricerca nella maschera principale.
 *
 * @author Nicola De Nisco
 */
public class RicercaAction extends ArgoBaseAction
{

  @Override
  public void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    super.doPerform2(data, context);

    RicercaDicomBean bean = BeanFactory.getFromSession(data, RicercaDicomBean.class);
    data.getParameters().setProperties(bean);

    String command = SU.okStrNull(data.getParameters().getString("command"));

    if(command != null)
    {
      Map param = SU.getParMap(data);
      bean.doCommand(command, data, param, context);
      doCommand(command, data, param, context);
    }
  }
}
