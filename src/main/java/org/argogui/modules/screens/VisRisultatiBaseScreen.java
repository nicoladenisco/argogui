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

import org.argogui.beans.VisRisultatiBean;
import org.sirio6.utils.CoreRunData;
import java.util.Map;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.sirio6.beans.BeanFactory;

/**
 * Screen di base per tutti i visualizzatori di risultati.
 *
 * @author Nicola De Nisco
 */
public class VisRisultatiBaseScreen extends ArgoBaseScreen
{
  @Override
  protected void doBuildTemplate2(CoreRunData data, Context context)
     throws Exception
  {
    super.doBuildTemplate2(data, context);

    VisRisultatiBean bean = BeanFactory.getFromSession(data, VisRisultatiBean.class);
    context.put("bean", bean);
    context.put("ana", bean.getAna());
    context.put("acc", bean.getAcc());
    context.put("rp", bean.getResPronti());

    String idVis = bean.getCurrVis();
    Map<String, VisRisultatiBean.ResBlock> resMap = bean.getRisultatiPronti();
    VisRisultatiBean.ResBlock block = resMap.get(idVis);
    context.put("res", block.items);
    context.put("idVis", idVis);

    doWork(data, context, bean, block);
  }

  /**
   * Segnaposto per classi derivate.
   * Viene chiamata dopo la preparazione dei dati generali di visualizzazione.
   * @param data
   * @param context
   * @param bean
   */
  protected void doWork(RunData data, Context context, VisRisultatiBean bean, VisRisultatiBean.ResBlock block)
  {
  }
}
