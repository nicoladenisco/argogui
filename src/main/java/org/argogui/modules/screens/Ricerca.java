/*
 *  Ricerca.java
 *  Creato il 15-lug-2016, 19.35.40
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
package org.argogui.modules.screens;

import org.argogui.beans.RicercaDicomBean;
import org.sirio6.utils.CoreRunData;
import org.apache.velocity.context.Context;
import org.sirio6.beans.BeanFactory;
import org.sirio6.beans.NavigationStackBean;

/**
 * Controllore per Ricerca.vm.
 *
 * @author Nicola De Nisco
 */
public class Ricerca extends ArgoBaseScreen
{
  @Override
  protected void doBuildTemplate2(CoreRunData data, Context context)
     throws Exception
  {
    super.doBuildTemplate2(data, context);

    NavigationStackBean nsb = BeanFactory.getFromSession(data, NavigationStackBean.class);
    nsb.clear();
    nsb.pushUri(data);

    RicercaDicomBean bean = BeanFactory.getFromSession(data, RicercaDicomBean.class);
    context.put("bean", bean);
    context.put("htmlRicerca", bean.getHtmlRicerca(data));
  }

  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return super.isAuthorizedAll(data, "ricerca");
  }
}
