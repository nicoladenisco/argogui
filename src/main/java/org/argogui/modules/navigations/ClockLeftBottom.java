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

import org.argogui.utils.SU;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.velocity.context.Context;
import org.sirio6.utils.CoreRunData;

/**
 * Controllore per ClockLeftBottom.vm.
 *
 * @author Nicola De Nisco
 */
public class ClockLeftBottom extends ArgoNavigation
{
  public static final String KEY_YEARS = "KEY_YEARS";

  @Override
  protected void doBuildTemplate(PipelineData pipelineData, Context context)
     throws Exception
  {
    super.doBuildTemplate(pipelineData, context);
    CoreRunData data = (CoreRunData) pipelineData.getRunData();

    String sYears = null;
    if((sYears = SU.okStrNull(data.getSession().getAttribute(KEY_YEARS))) == null)
    {
      int firstYear = 2015;
      Date now = new Date();
      Calendar c = new GregorianCalendar();
      c.setTime(now);
      int lastYear = c.get(Calendar.YEAR);

      sYears = "";
      for(int y = firstYear; y <= lastYear; y++)
        sYears += ", " + y;

      sYears = sYears.substring(2);
      data.getSession().setAttribute(KEY_YEARS, sYears);
    }

    context.put("years", sYears);
  }
}
