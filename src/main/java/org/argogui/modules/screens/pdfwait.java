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

import org.apache.turbine.services.TurbineServices;
import org.apache.velocity.context.Context;
import org.argogui.services.cache.UsersDataCache;
import org.sirio6.utils.CoreRunData;
import org.sirio6.services.formatter.DataFormatter;
import org.sirio6.services.print.PdfPrint;

/**
 * Controllore per pdfwait.vm.
 *
 * @author Nicola De Nisco
 */
public class pdfwait extends ArgoBaseScreen
{
  private final static PdfPrint pp = (PdfPrint) TurbineServices.getInstance().
     getService(PdfPrint.SERVICE_NAME);
  private final static DataFormatter df = (DataFormatter) TurbineServices.getInstance().
     getService(DataFormatter.SERVICE_NAME);

  @Override
  protected void doBuildTemplate2(CoreRunData data, Context context)
     throws Exception
  {
    super.doBuildTemplate2(data, context);

    String jobCode = data.getParameters().getString("codice", null);
    PdfPrint.JobInfo job = pp.refreshInfo(jobCode);

    if(pp == null)
      data.throwMessagei18n("Job di stampa [%s] non trovato o scaduto.<br>"
         + "Se è passato troppo tempo dalla richiesta il job è stato rimosso.<br>"
         + "Ripetere la stampa per la rigenerazione.", jobCode);

    context.put("codice", jobCode);
    context.put("tstart", df.formatDataFull(job.tStart));
    context.put("job", job);
    context.put("jobs", pp.getJobs());
    context.put("users", UsersDataCache.getInstance());
  }
}
