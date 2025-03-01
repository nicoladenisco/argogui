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
package org.argogui.services.print;

import org.argogui.om.GenStampe;
import org.argogui.om.GenStampePeer;
import org.argogui.utils.I;
import org.apache.commons.logging.*;
import org.sirio6.services.CoreServiceException;
import org.sirio6.services.print.AbstractAsyncPdfPrint;
import org.sirio6.services.print.AbstractReportParametersInfo;

/**
 * Servizio di stampa via PDF.
 * Il servizio sovrintende alla generazione al
 * volo di PDF da utilizzare come strumento di stampa.
 * Questa versione del servizio elabora ogni PDF all'interno
 * di un thread dedicato, consentendo di tornare se l'elaborazione
 * diventa troppo lunga.
 * Quando in JobInfo il campo filePdf è a null vuol dire
 * che l'elaborazione è in corso. La servlet che usa il servizio
 * può notificare l'utente e invitarlo a riprovare la richiesta.
 *
 * @author Nicola De Nisco
 */
public class AsyncPdfPrint extends AbstractAsyncPdfPrint
{
  /** Logging */
  private static Log log = LogFactory.getLog(AsyncPdfPrint.class);

  @Override
  protected AbstractReportParametersInfo createReportInfo(String codiceStampa)
     throws Exception
  {
    GenStampe gs = GenStampePeer.retrieveByCodice(codiceStampa);
    if(gs == null)
      throw new CoreServiceException(I.I("Stampa %s non trovata a setup.", codiceStampa));

    return new ArgoReportParametersInfo(gs);
  }
}
