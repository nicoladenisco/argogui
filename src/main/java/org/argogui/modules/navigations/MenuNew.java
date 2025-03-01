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
package org.argogui.modules.navigations;

import org.apache.turbine.pipeline.PipelineData;
import org.argogui.beans.turbine.xmlMenuBean;
import org.apache.velocity.context.Context;
import org.sirio6.beans.BeanFactory;
import org.sirio6.utils.CoreRunData;

/**
 * Crea i menu per la navigazione.
 *
 * @author Nicola De Nisco
 */
public class MenuNew extends ArgoNavigation
{
  @Override
  protected void doBuildTemplate(PipelineData pipelineData, Context context)
     throws Exception
  {
    super.doBuildTemplate(pipelineData, context);
    CoreRunData data = (CoreRunData) pipelineData.getRunData();
    xmlMenuBean mb = BeanFactory.getFromSession(data, xmlMenuBean.class);
    context.put("menu", mb.getHtml(data));
  }
}
