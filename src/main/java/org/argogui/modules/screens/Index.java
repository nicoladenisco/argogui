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

import org.argogui.om.StpStorage;
import org.argogui.services.dcmsrv.DicomServer;
import org.apache.velocity.context.Context;
import org.sirio6.utils.CoreRunData;
import java.util.List;
import java.util.stream.Collectors;
import org.sirio6.beans.BeanFactory;
import org.sirio6.beans.NavigationStackBean;

/**
 * Controllore per Index.vm
 * @author Nicola De Nisco
 */
public class Index extends ArgoBaseScreen
{
  @Override
  protected void doBuildTemplate2(CoreRunData data, Context context)
     throws Exception
  {
    super.doBuildTemplate2(data, context);

    NavigationStackBean nsb = BeanFactory.getFromSession(data, NavigationStackBean.class);
    nsb.clear();
    nsb.pushUri(data);

    DicomServer dcmsrv = (DicomServer) getService(DicomServer.SERVICE_NAME);
    List<StpStorage> lsStg = dcmsrv.getAllStorages().stream().filter((s) -> s.isArea()).collect(Collectors.toList());
    for(StpStorage stg : lsStg)
      stg.loadDiskStatistic();
    context.put("storages", lsStg);

    StringBuilder sb = new StringBuilder();
    sb.append("<pre><code>");
    switch(dcmsrv.descriviConnessioni(sb))
    {
      case 0:
      default:
        // non supportato nella piattaforma
        break;
      case 1:
        context.put("connessioni", data.i18n("<b>Nessuna connessione attiva.</b>"));
        break;
      case 2:
        sb.append("</code></pre>");
        context.put("connessioni", sb.toString());
        break;
    }

    data.setRefresh(10);
  }
}
