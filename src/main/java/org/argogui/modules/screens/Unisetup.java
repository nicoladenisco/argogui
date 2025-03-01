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

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.uri.TemplateURI;
import org.apache.velocity.context.Context;
import org.sirio6.beans.BeanFactory;
import org.sirio6.beans.NavigationStackBean;
import org.sirio6.modules.screens.rigel.ListaBaseMaint;
import org.sirio6.utils.CoreRunData;

/**
 * Visualizzatore liste XML dedicato al setup.
 *
 * @author Nicola De Nisco
 */
public class Unisetup extends ListaBaseMaint
{
  @Override
  protected void doBuildTemplate2(CoreRunData data, Context context)
     throws Exception
  {
    NavigationStackBean nsb = BeanFactory.getFromSession(data, NavigationStackBean.class);
    nsb.clear();
    nsb.pushUri(data);

    super.doBuildTemplate2(data, context);
  }

  @Override
  protected String makeSelfUrl(RunData data, String type)
  {
    TemplateURI tl = new TemplateURI(data, "Unisetup.vm"); // NOI18N
    tl.addPathInfo("type", type);
    return tl.getRelativeLink();
  }
}
