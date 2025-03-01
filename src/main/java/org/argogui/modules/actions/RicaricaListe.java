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
package org.argogui.modules.actions;

import org.argogui.beans.turbine.xmlMenuBean;
import org.sirio6.utils.CoreRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.sirio6.beans.BeanFactory;
import org.sirio6.services.modellixml.MDL;

/**
 * Ricarica le liste xml.
 *
 * @author Nicola De Nisco
 */
public class RicaricaListe extends ArgoBaseAction
{
  @Override
  public void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    ricaricaListe(data);
    ricaricaMenu(data);
    ricaricaOverrideSetup(data);
  }

  public void ricaricaListe(RunData data)
     throws Exception
  {
    MDL.forceReloadXML();
    MDL.removeWrapperCache(data);
  }

  public void ricaricaMenu(RunData data)
     throws Exception
  {
    BeanFactory.removeFromSession(data, xmlMenuBean.class);
  }

  public void ricaricaOverrideSetup(RunData data)
     throws Exception
  {
//    TR.loadOverride();
  }
}
