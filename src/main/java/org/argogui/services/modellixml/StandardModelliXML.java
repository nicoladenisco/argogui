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
package org.argogui.services.modellixml;

import org.argogui.rigel.RigelTurbineWrapperCache;
import org.apache.turbine.util.RunData;
import org.rigel5.glue.WrapperCacheBase;
import org.rigel5.glue.validators.ValidatorsFactory;
import org.rigel5.glue.custom.CustomButtonFactory;
import org.rigel5.glue.custom.CustomEditFactory;
import org.sirio6.services.modellixml.CoreModelliXML;

/**
 * Implementazione standard del gestore modelli XML.
 *
 * @author Nicola De Nisco
 */
public class StandardModelliXML extends CoreModelliXML
{
  @Override
  public void coreInit()
     throws Exception
  {
    super.coreInit();

    ValidatorsFactory.getInstance().setClassRadixs(new String[]
    {
      "org.sirio6.rigel.validators",
      "org.argogui.rigel.validators"
    });

    CustomEditFactory.getInstance().setClassRadixs(new String[]
    {
      "org.sirio6.rigel.customedit",
      "org.argogui.rigel.customedit"
    });

    CustomButtonFactory.getInstance().setClassRadixs(new String[]
    {
      "org.sirio6.rigel.custombuttons",
      "org.argogui.rigel.custombuttons"
    });
  }

  @Override
  public WrapperCacheBase getWrapperCache(RunData data)
  {
    RigelTurbineWrapperCache rv = (RigelTurbineWrapperCache) data.getSession().getAttribute(WrapperCacheBaseKey);

    if(rv == null)
    {
      rv = new RigelTurbineWrapperCache();
      rv.init(data);
      data.getSession().setAttribute(WrapperCacheBaseKey, rv);
    }

    return rv;
  }
}
