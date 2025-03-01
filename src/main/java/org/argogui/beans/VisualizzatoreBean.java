/*
 *  VisualizzatoreBean.java
 *  Creato il 4-mag-2016, 14.41.15
 *
 *  Copyright (C) 2016 RAD-IMAGE s.r.l.
 *
 *  Questo software è proprietà di RAD-IMAGE s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  RAD-IMAGE s.r.l.
 *  Via San Giovanni 1 - Contrada Belvedere
 *  San Nicola Manfredi (BN)
 */
package org.argogui.beans;

import org.argogui.services.SERVICE;
import org.argogui.services.dcmsrv.DicomServer;
import org.argogui.services.dcmsrv.StudyResultBean;
import org.argogui.utils.SU;
import java.util.HashMap;
import org.apache.fulcrum.parser.ParameterParser;
import org.sirio6.beans.BeanFactory;
import org.sirio6.utils.CoreRunData;

/**
 * Bean per supporto al visualizzatore.
 *
 * @author Nicola De Nisco
 */
public class VisualizzatoreBean extends ArgoBaseBean
{
  // costanti
  public static final String BEAN_KEY = "VisualizzatoreBean:BEAN_KEY";
  //
  private HashMap<String, VisContext> mapParams = new HashMap<>();
  private DicomServer dsrv;

// <editor-fold defaultstate="collapsed" desc="Getter/Setter">
// </editor-fold>
  @Override
  public void init(CoreRunData data)
     throws Exception
  {
    super.init(data);
    dsrv = (DicomServer) getService(DicomServer.SERVICE_NAME);
  }

  /**
   * Recupera o crea context.
   * @param data dati richiesta http
   * @return context con i dati per l'esame richiesto
   * @throws Exception
   */
  public VisContext getOrAddStudy(CoreRunData data)
     throws Exception
  {
    ParameterParser pp = data.getParameters();

    String studyUID;
    if((studyUID = SU.okStrNull(pp.getString("studyUID"))) == null)
      throw new Exception(data.i18n("Parametro 'uuid' non specificato nella richiesta."));

    return getOrAddStudy(studyUID, data);
  }

  public VisContext getOrAddStudy(String studyUID, CoreRunData data)
     throws Exception
  {
    VisContext ctx;
    if((ctx = mapParams.get(studyUID)) != null)
      return ctx;

    StudyResultBean res;
    RicercaDicomBean bean = BeanFactory.getFromSession(data, RicercaDicomBean.class);
    if((res = bean.findResult(studyUID)) == null)
      throw new Exception(data.i18n("Esame non presente nel set dei risulati di ricerca."));

    // crea nuovo context
    ctx = new VisContext();
    ctx.studyUID = studyUID;
    ctx.res = dsrv.populateStudy(studyUID, null, new StudyResultBean());

    // imposta un valore iniziale per la serie corrente
    SERVICE.ASSERT(!ctx.res.arSeries.isEmpty(), "!ctx.res.arSeries.isEmpty()");
    ctx.currSerie = ctx.res.arSeries.get(0);
    SERVICE.ASSERT(!ctx.currSerie.arInstances.isEmpty(), "!ctx.currSerie.arInstances.isEmpty()");
    ctx.currInstance = ctx.currSerie.arInstances.get(0);

    mapParams.put(studyUID, ctx);
    return ctx;
  }
}
