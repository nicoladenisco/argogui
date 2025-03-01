/*
 *  SerieResultBean.java
 *  Creato il 4-mag-2016, 15.39.23
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

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomAttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import org.argogui.utils.SU;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Bean per incapsulamento dati serie.
 *
 * @author Nicola De Nisco
 */
public class SerieResultBean implements Cloneable
{
  public String SerieInstanceUID, modalita;
  public DicomAttributeList al = new DicomAttributeList();
  public ArrayList<File> arFiles = new ArrayList<>();
  public ArrayList<InstanceResultBean> arInstances = new ArrayList<>();
  public int serieNumber = -1;

  public InstanceResultBean findInstance(String instUID)
  {
    for(InstanceResultBean ins : arInstances)
    {
      if(SU.isEqu(instUID, ins.SOPInstanceUID))
        return ins;
    }
    return null;
  }

  public boolean addInstance(InstanceResultBean ib)
     throws Exception
  {
    Attribute tagSerieUID = ib.al.get(TagFromName.SeriesInstanceUID);
    Attribute tagModalita = ib.al.get(TagFromName.Modality);

    if(tagSerieUID == null)
      return false;

    if(arInstances.isEmpty())
      al.putAll(ib.al);

    if(SerieInstanceUID == null)
      SerieInstanceUID = tagSerieUID.getSingleStringValueOrNull();
    if(modalita == null && tagModalita != null)
      modalita = tagModalita.getSingleStringValueOrNull();

    arInstances.add(ib);
    arFiles.add(ib.fileDicom);
    return true;
  }

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

  public String getSerieInstanceUID()
  {
    return SU.okStr(SerieInstanceUID);
  }

  public String getModalita()
  {
    return SU.okStr(modalita);
  }

  public String getDescrizione()
  {
    return getTagAsStringNotNull(TagFromName.SeriesDescription);
  }

  public int getNum()
  {
    if(serieNumber == -1)
      serieNumber = SU.parse(getTagAsStringNotNull(TagFromName.SeriesNumber), -1);

    return serieNumber;
  }

  public int getNumimg()
  {
    return arInstances.size();
  }

  public String getImageUID()
  {
    return arInstances.get(0).getSOPInstanceUID();
  }

  public String getSopClassUID()
  {
    return arInstances.get(0).getSOPClassUID();
  }

  public List<InstanceResultBean> getImages()
  {
    return arInstances;
  }

  public Date getSeriesDate()
  {
    return al.getSeriesDate();
  }

  public String getSeriesDateAsString()
  {
    return getTagAsStringNotNull(TagFromName.SeriesDate);
  }

  public String getSeriesTimeAsString()
  {
    return getTagAsStringNotNull(TagFromName.SeriesTime);
  }

  public int compareTo(SerieResultBean sb)
  {
    Date d1 = getSeriesDate();
    Date d2 = sb.getSeriesDate();

    if(d1 != null && d2 != null)
      return d1.compareTo(d2);

    String s1 = SU.okStrNull(SerieInstanceUID);
    String s2 = SU.okStrNull(sb.SerieInstanceUID);

    if(s1 != null && s2 != null)
      return s1.compareTo(s2);

    return 0;
  }

  public void sort()
  {
    arInstances.sort((i1, i2) -> i1.compareTo(i2));
  }
}
