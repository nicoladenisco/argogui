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

import java.util.Date;
import org.apache.turbine.modules.screens.VelocityScreen;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.velocity.context.Context;
import org.sirio6.beans.BeanFactory;
import org.sirio6.beans.CalendarioBean;
import org.sirio6.utils.CoreRunData;
import org.sirio6.utils.format.DateOnlyFormat;

/**
 * Controllore per Calendario.vm.
 *
 * @author Nicola De Nisco
 */
public class Calendario extends VelocityScreen
{
  @Override
  protected void doBuildTemplate(PipelineData pipelineData, Context context)
     throws Exception
  {
    super.doBuildTemplate(pipelineData, context);
    CoreRunData data = (CoreRunData) pipelineData.getRunData();

    CalendarioBean bean = BeanFactory.getFromSession(data, CalendarioBean.class);
    data.getParameters().setProperties(bean);
    bean.buildCalendar();

    context.put("bean", bean);
    context.put("giorno", bean.getDay()); // NOI18N
    context.put("mese", bean.getMonth());
    context.put("anno", bean.getYear());

    DateOnlyFormat df = new DateOnlyFormat();
    String today = df.format(new Date());

    context.put("today", today);

    int nextMese = bean.getMonth() + 1;
    int nextAnno = bean.getYear();
    if(nextMese > 11)
    {
      nextMese = 0;
      nextAnno++;
    }
    context.put("nextMese", nextMese); // NOI18N
    context.put("nextAnno", nextAnno); // NOI18N

    int prevMese = bean.getMonth() - 1;
    int prevAnno = bean.getYear();
    if(prevMese < 0)
    {
      prevMese = 0;
      prevAnno--;
    }
    context.put("prevMese", prevMese); // NOI18N
    context.put("prevAnno", prevAnno); // NOI18N
  }
}
