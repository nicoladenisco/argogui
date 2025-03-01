/*
 *  RicercaDicomBean.java
 *  Creato il 20-apr-2016, 18.18.03
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
import com.pixelmed.dicom.AttributeFactory;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.TagFromName;
import org.argogui.services.dcmsrv.DicomServer;
import org.argogui.services.dcmsrv.StudyResultBean;
import org.argogui.utils.DT;
import org.argogui.utils.SU;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.sirio6.services.security.SEC;
import org.sirio6.utils.CoreRunData;
import org.sirio6.utils.LI;

public class RicercaDicomBean extends ArgoBaseBean
{
  private String ricval, allmod;
  private Date dstart, dstop;
  private int dttype = 1;
  private DicomServer dsrv;
  private List<StudyResultBean> lsQueryResults;
  private List<AttributeTag> defTags;
  // costanti
  public static final String BEAN_KEY = "RicercaDicomBean:BEAN_KEY";
  public static final String Modalita = "CR CT MG XA RF NM DX ES PT SR SC MR AU OT RF DR XC VL US PX";

// <editor-fold defaultstate="collapsed" desc="Getter/Setter">
  public String getRicval()
  {
    return ricval;
  }

  public void setRicval(String ricval)
  {
    this.ricval = ricval;
  }

  public String getAllmod()
  {
    return allmod;
  }

  public void setAllmod(String allmod)
  {
    this.allmod = allmod;
  }

  public int getDttype()
  {
    return dttype;
  }

  public void setDttype(int dttype)
  {
    this.dttype = dttype;
  }

  public List<String> getModalita()
  {
    return SU.string2List(Modalita, " ");
  }

  public String getDstart()
  {
    return dstart == null ? "" : DT.formatData(dstart);
  }

  public void setDstart(String dstart)
  {
    this.dstart = DT.parseData(dstart);
  }

  public String getDstop()
  {
    return dstop == null ? "" : DT.formatData(dstop);
  }

  public void setDstop(String dstop)
  {
    this.dstop = DT.parseData(dstop);
  }

// </editor-fold>
  @Override
  public void init(CoreRunData data)
     throws Exception
  {
    super.init(data);

    dsrv = (DicomServer) getService(DicomServer.SERVICE_NAME);
    defTags = dsrv.getDefaultTags();
  }

  public String getDttypeField(int value)
  {
    String sel = value == dttype ? " checked=\"checked\"" : "";
    return "<input type=\"radio\" name=\"dttype\" value=\"" + value + "\" " + sel + ">";
  }

  public String getCbmodField(String mod)
  {
    String sel = allmod != null && allmod.contains(mod) ? " checked=\"checked\"" : "";
    return "<input type=\"checkbox\" name=\"mod" + mod + "\" value=\"1\" " + sel + ">";
  }

  public void doCmd_ricerca(CoreRunData data, Map params, Object... args)
     throws Exception
  {
    // legge i checkbox modalita
    allmod = null;
    StringBuilder sb = new StringBuilder();
    List<String> lsMod = getModalita();
    for(String mod : lsMod)
    {
      if(params.containsKey("mod" + mod))
        sb.append("\\").append(mod);
    }
    if(sb.length() > 0)
      allmod = sb.substring(1);

    // prepara parametri per query
    Set<Attribute> queryParams = new HashSet<>();
    if(SU.isOkStr(allmod))
    {
      Attribute attr = AttributeFactory.newAttribute(TagFromName.ModalitiesInStudy);
      attr.setValue(allmod);
      queryParams.add(attr);
    }

    String dt = null;
    switch(dttype)
    {
      case 1: // oggi
      {
        dt = DT.formatDateDicom(today);
        break;
      }
      case 2: // ieri
      {
        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        dt += DT.formatDateDicom(cal.getTime());
        break;
      }
      case 3: // ultima settimana
      {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        dt = DT.formatDateDicom(cal.getTime()) + "-" + DT.formatDateDicom(today);
        break;
      }
      case 4: // ultimo mese
      {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        dt = DT.formatDateDicom(cal.getTime()) + "-" + DT.formatDateDicom(today);
        break;
      }
      case 5: // intervallo libero
      {
        if(dstart == null || dstop == null)
          data.throwMessagei18n("Intervallo di date non definito.");

        if(dstop.before(dstart))
          data.throwMessagei18n("La data finale non può precedere la data iniziale.");

        dt = DT.formatDateDicom(dstart) + "-" + DT.formatDateDicom(dstop);
        break;
      }
    }

    if(dt != null)
    {
      Attribute attr = AttributeFactory.newAttribute(TagFromName.StudyDate);
      attr.setValue(dt);
      queryParams.add(attr);
    }

    String[] rs = SU.split(ricval, ',');
    if(rs.length == 1)
    {
      // un solo valore = cognome
      Attribute attr = AttributeFactory.newAttribute(TagFromName.PatientName);
      attr.setValue(rs[0] + "*");
      queryParams.add(attr);
    }
    else if(rs.length == 2)
    {
      // due valori = cognome e nome
      Attribute attr = AttributeFactory.newAttribute(TagFromName.PatientName);
      attr.setValue(rs[0] + "^" + rs[1]);
      queryParams.add(attr);
    }
    else if(rs.length >= 3)
    {
      // tre valori = cognome, nome e data di nascita
      Attribute attr1 = AttributeFactory.newAttribute(TagFromName.PatientName);
      attr1.setValue(rs[0] + "^" + rs[1]);
      queryParams.add(attr1);

      Date nascita = DT.parseData(rs[2]);
      if(nascita == null)
        data.throwMessagei18n("Formato della data di nascita non valido");

      Attribute attr2 = AttributeFactory.newAttribute(TagFromName.PatientBirthDate);
      attr2.setValue(DT.formatDateDicom(nascita));
      queryParams.add(attr2);
    }

    lsQueryResults = dsrv.queryStudyLocalNode(queryParams);
  }

  public String getHtmlRicerca(CoreRunData data)
     throws Exception
  {
    if(lsQueryResults == null)
      return null;

    if(lsQueryResults.isEmpty())
      return data.i18n("Nessun risultato trovato.");

    StringBuilder sb = new StringBuilder(4096);
    sb.append("<table id=\"queryres\" class=\"table table-striped table-bordered\" width=\"100%\" cellspacing=\"0\">\n");
    sb.append("<thead>\n");
    sb.append("<tr>");
    sb.append("<th>Storage Area</th>");
    for(AttributeTag tag : defTags)
    {
      sb.append("<th>").append(formatTagName(tag)).append("</th>");
    }
    if(SEC.checkAnyPermission(data, "visualizza_dicom_semplice,visualizza_dicom_avanzato,modifica_dicom"))
    {
      sb.append("<th>Fun.</th>");
    }
    sb.append("</tr>\n");
    sb.append("</thead>\n");
    sb.append("<tbody>\n");

    for(StudyResultBean rb : lsQueryResults)
    {
      sb.append("<tr>");
      sb.append("<td>").append(rb.storageAetitle).append("</td>");
      for(AttributeTag tag : defTags)
      {
        Attribute val = rb.al.get(tag);
        if(val == null)
          sb.append("<td>&nbsp;</td>");
        else
          sb.append("<td>").append(formatTagValue(tag, val)).append("</td>");
      }

      if(SEC.checkAnyPermission(data, "visualizza_dicom_semplice,visualizza_dicom_avanzato,modifica_dicom"))
      {
        sb.append("<td>");
        if(SEC.checkAnyPermission(data, "visualizza_dicom_semplice"))
        {
          sb.append("<a href=\"javascript:apriVis1('").append(rb.StudyInstanceUID).
             append("', '").append(rb.storageAetitle).append("')\">").
             append(LI.getImgGlyphicon("eye-open", data.i18n("Visualizza esame."))).
             append("</a>&nbsp;");
        }
        if(SEC.checkAnyPermission(data, "visualizza_dicom_avanzato"))
        {
          sb.append("<a href=\"javascript:apriVis2('").append(rb.StudyInstanceUID).
             append("', '").append(rb.storageAetitle).append("')\">").
             append(LI.getImgGlyphicon("eye-close", data.i18n("Visualizza esame con visualizzatore avanzato."))).
             append("</a>&nbsp;");
        }
        if(SEC.checkAnyPermission(data, "modifica_dicom"))
        {
          sb.append("<a href=\"javascript:editStudy('").append(rb.StudyInstanceUID).
             append("', '").append(rb.storageAetitle).append("')\">").
             append(LI.getImgGlyphicon("edit", data.i18n("Modifica dati esame/paziente."))).
             append("</a>&nbsp;");
        }
        sb.append("</td>");
      }

      sb.append("</tr>\n");
    }

    sb.append("</tbody>\n");
    sb.append("</table>\n");

    return sb.toString();
  }

  private String formatTagName(AttributeTag tag)
  {
    return AttributeList.getDictionary().getFullNameFromTag(tag);
  }

  private String formatTagValue(AttributeTag tag, Attribute val)
  {
    String rv = val.getSingleStringValueOrEmptyString();
    String vr = val.getVRAsString();

    switch(vr)
    {
      case "DA":
        // campo data: lo formatta in italiano
        Date d = DT.parseDateDicom(rv, null);
        if(d != null)
          return DT.formatData(d);
        break;
      case "TM":
        rv = (rv + "000000").substring(0, 6);
        return rv.substring(0, 2) + ":" + rv.substring(2, 4) + ":" + rv.substring(4, 6);
    }

    return rv;
  }

  /**
   * Cerca un risultato fra quelli acquisiti.
   * @param studyUID uuid dell'esame
   * @return il risultato se presente altrimenti null
   */
  public StudyResultBean findResult(String studyUID)
  {
    for(StudyResultBean b : lsQueryResults)
    {
      if(SU.isEqu(studyUID, b.StudyInstanceUID))
        return b;
    }
    return null;
  }

  public void doCmd_pulisci(CoreRunData data, Map params, Object... args)
     throws Exception
  {
    lsQueryResults = null;
    ricval = allmod = null;
    dstart = dstop = null;
    dttype = 1;
  }
}
