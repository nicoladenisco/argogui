/*
 *  VisContext.java
 *  Creato il 4-mag-2016, 15.45.14
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

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeTag;
import org.argogui.services.SERVICE;
import org.argogui.services.dcmsrv.InstanceResultBean;
import org.argogui.services.dcmsrv.SerieResultBean;
import org.argogui.services.dcmsrv.StudyResultBean;

/**
 * Bean per contesto di visualizzazione.
 * Vedi VisualizzatoreBean.java.
 *
 * @author Nicola De Nisco
 */
public class VisContext
{
  public String studyUID;
  public StudyResultBean res;
  public SerieResultBean currSerie;
  public InstanceResultBean currInstance;

  public String getTagAsString(AttributeTag tag)
  {
    Attribute att = res.al.get(tag);
    return att == null ? null : att.getSingleStringValueOrNull();
  }

  public String getTagAsStringNotNull(AttributeTag tag)
  {
    Attribute att = res.al.get(tag);
    return att == null ? "" : att.getSingleStringValueOrEmptyString();
  }

  public void setCurrSerie(String serieUID)
     throws Exception
  {
    SerieResultBean ser = res.findSerie(serieUID);
    if(ser != null)
    {
      currSerie = ser;
      SERVICE.ASSERT(!currSerie.arInstances.isEmpty(), "!currSerie.arInstances.isEmpty()");
      currInstance = currSerie.arInstances.get(0);
    }
  }

  public void setCurrInstance(String instanceUID)
     throws Exception
  {
    InstanceResultBean ins = currSerie.findInstance(instanceUID);
    if(ins != null)
      currInstance = ins;
  }

  public void setCurrInstance(String serieUID, String instanceUID)
     throws Exception
  {
    setCurrSerie(serieUID);
    setCurrInstance(instanceUID);
  }
}
