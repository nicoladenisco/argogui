/*
 *  InstanceResultBean.java
 *  Creato il 4-mag-2016, 15.39.35
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
package org.argogui.services.dcmsrv;

import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomAttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import org.argogui.utils.SU;
import java.io.File;
import java.util.Date;

/**
 * Bean per incapsulamento dati immagini.
 *
 * @author Nicola De Nisco
 */
public class InstanceResultBean implements Cloneable
{
  public String SOPInstanceUID, SOPClassUID, modalita;
  public DicomAttributeList al = new DicomAttributeList();
  public File fileDicom, fileJpeg;
  public int instanceNumber = -1;
  public int serieNumber = -1;

  public String getTagAsString(AttributeTag tag)
  {
    return al.getStringValue(tag, null);
  }

  public String getTagAsStringNotNull(AttributeTag tag)
  {
    return al.getStringValue(tag, "");
  }

  public String getTagAsStringFromName(String tag)
     throws DicomException
  {
    return al.getStringValue(tag, "");
  }

  public int getNum()
  {
    if(instanceNumber == -1)
      instanceNumber = SU.parse(getTagAsStringNotNull(TagFromName.InstanceNumber), -1);

    return instanceNumber;
  }

  public int getSerieNum()
  {
    if(serieNumber == -1)
      serieNumber = SU.parse(getTagAsStringNotNull(TagFromName.SeriesNumber), -1);

    return serieNumber;
  }

  public String getSOPInstanceUID()
  {
    return SU.okStr(SOPInstanceUID);
  }

  public String getSOPClassUID()
  {
    return SU.okStr(SOPClassUID);
  }

  public String getModalita()
  {
    return SU.okStr(modalita);
  }

  public Date getAcquisitionDate()
  {
    return al.getAcquisitionDate();
  }

  public String getAcquisitionDateAsString()
  {
    return getTagAsStringNotNull(TagFromName.AcquisitionDate);
  }

  public String getAcquisitionTimeAsString()
  {
    return getTagAsStringNotNull(TagFromName.AcquisitionTime);
  }

  public int compareTo(InstanceResultBean sb)
  {
    Date d1 = getAcquisitionDate();
    Date d2 = sb.getAcquisitionDate();

    if(d1 != null && d2 != null)
      return d1.compareTo(d2);

    String s1 = SU.okStrNull(SOPInstanceUID);
    String s2 = SU.okStrNull(sb.SOPInstanceUID);

    if(s1 != null && s2 != null)
      return s1.compareTo(s2);

    return 0;
  }

}
