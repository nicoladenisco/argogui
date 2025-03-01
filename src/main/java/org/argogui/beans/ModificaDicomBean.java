/*
 *  ModificaDicomBean.java
 *  Creato il Apr 5, 2017, 4:25:07 PM
 *
 *  Copyright (C) 2017 RAD-IMAGE s.r.l.
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
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import org.argogui.services.SERVICE;
import org.argogui.services.dcmsrv.DicomServer;
import org.argogui.services.dcmsrv.StudyResultBean;
import org.argogui.utils.DT;
import org.argogui.utils.SU;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.fulcrum.parser.ParameterParser;
import org.sirio6.beans.BeanFactory;
import org.sirio6.utils.CoreRunData;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * Modifica database e files DICOM.
 * Bean di supporto.
 *
 * @author Nicola De Nisco
 */
public class ModificaDicomBean extends ArgoBaseBean
{
  private String studyUID, aetitle, mergedStudyUID, mergedStudyArea;
  private StudyResultBean resBean;
  private DicomServer dsrv;
  private List<StudyResultBean> clipboard;
  // costanti
  public static final String BEAN_KEY = "ModificaDicomBean:BEAN_KEY";
  public static final String CLIPBOARD_KEY = "ModificaDicomBean:CLIPBOARD_KEY";

  public static class TagModifyInfo
  {
    public Attribute attr;
    public boolean editabile, modificato;
    public String label;
    public int order;

    public String getTag()
    {
      return attr.getTag().toString();
    }

    public String getLabel()
    {
      return label;
    }

    public String getValue()
    {
      String vr = attr.getVRAsString();
      String value = attr.getSingleStringValueOrDefault("");
      Date d;

      switch(vr)
      {
        case "DT":
        case "DA":
          d = DT.parseDateDicom(value, null);
          return (d == null) ? "" : DT.formatData(d);

        case "TM":
          d = DT.parseTimeDicom(value, null);
          return (d == null) ? "" : DT.formatTime(d);

        default:
          return value;
      }
    }

    public void setValue(String value)
       throws DicomException
    {
      String vr = attr.getVRAsString();
      String oval = attr.getSingleStringValueOrDefault("");
      Date d;

      if((value = SU.okStrNull(value)) == null && !oval.isEmpty())
      {
        attr.removeValues();
        modificato = true;
        return;
      }

      switch(vr)
      {
        case "DT":
        case "DA":
          if((d = DT.parseData(value)) != null)
          {
            String tmp = DT.formatDateDicom(d, null);
            if(!SU.isEqu(tmp, oval))
            {
              attr.setValue(tmp);
              modificato = true;
            }
          }
          break;

        case "TM":
          if((d = DT.parseTime(value)) != null)
          {
            String tmp = DT.formatTimeDicom(d, null);
            if(!SU.isEqu(tmp, oval))
            {
              attr.setValue(tmp);
              modificato = true;
            }
          }
          break;

        default:
          if(!SU.isEqu(value, oval))
          {
            attr.setValue(value);
            modificato = true;
          }
          break;
      }
    }

    public boolean isEditabile()
    {
      return editabile;
    }
  }

  private ArrayList<TagModifyInfo> arTagsmodPatient = new ArrayList<>();
  private ArrayList<TagModifyInfo> arTagsmodStudy = new ArrayList<>();

// <editor-fold defaultstate="collapsed" desc="Getter/Setter">
  public List<TagModifyInfo> getTagsmodPatient()
  {
    return arTagsmodPatient;
  }

  public List<TagModifyInfo> getTagsmodStudy()
  {
    return arTagsmodStudy;
  }

  public List<StudyResultBean> getClipboard()
  {
    return clipboard;
  }

  public String getMergedStudyUID()
  {
    return mergedStudyUID;
  }

  public String getMergedStudyArea()
  {
    return mergedStudyArea;
  }

  public boolean isFusione()
  {
    return SU.isOkStrAll(mergedStudyUID, mergedStudyArea);
  }

  public boolean isNofusione()
  {
    return SU.isOkStr(mergedStudyUID) && !SU.isOkStr(mergedStudyArea);
  }
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="Metodi di servizio per il bean">

  @Override
  public void init(CoreRunData data)
     throws Exception
  {
    super.init(data);
    dsrv = (DicomServer) getService(DicomServer.SERVICE_NAME);
    ParameterParser pp = data.getParameters();

    if((studyUID = SU.okStrNull(pp.getString("studyUID"))) == null)
      throw new Exception(data.i18n("Parametro 'studyUID' non specificato nella richiesta."));
    if((aetitle = SU.okStrNull(pp.getString("aetitle"))) == null)
      throw new Exception(data.i18n("Parametro 'aetitle' non specificato nella richiesta."));

    StudyResultBean res;
    RicercaDicomBean bean = BeanFactory.getFromSession(data, RicercaDicomBean.class);
    if((res = bean.findResult(studyUID)) == null)
      throw new Exception(data.i18n("Esame non presente nel set dei risulati di ricerca."));

    resBean = dsrv.populateStudy(studyUID, aetitle, new StudyResultBean());
    leggiListaAttributi();

    // recupera o crea la clipboard permanente
    if((clipboard = (List<StudyResultBean>) data.getSession().getAttribute(CLIPBOARD_KEY)) == null)
    {
      clipboard = new ArrayList<>();
      data.getSession().setAttribute(CLIPBOARD_KEY, clipboard);
    }
  }

  /**
   * Verifica se questo bean è ancora valido.
   * @param data dati della richiesta HTML
   * @return vero se il bean è valido
   */
  public boolean isValid(CoreRunData data)
  {
    ParameterParser pp = data.getParameters();

    if(!SU.isEqu(studyUID, pp.getString("studyUID", studyUID)))
      return false;
    if(!SU.isEqu(aetitle, pp.getString("aetitle", aetitle)))
      return false;

    return true;
  }

// </editor-fold>
  public void doCmd_salvamodifiche(CoreRunData data, Map params, Object... args)
     throws Exception
  {
    AttributeList al = new AttributeList();

    salvaBlocco(params, al, arTagsmodPatient);
    salvaBlocco(params, al, arTagsmodStudy);

    if(SU.isOkStr(mergedStudyUID) && SU.checkTrueFalse(params.get("chk_fondiesami")))
    {
      if(!SU.isOkStr(mergedStudyArea))
        data.throwMessagei18n("La fusione di esami fra aree di storage differenti non è possibile.");

      salvaVariazione(al, mergedStudyUID, TagFromName.StudyInstanceUID);
    }

    if(!al.isEmpty())
    {
      dsrv.modificaFilesDatabase(studyUID, aetitle, al);
      data.setMessagei18n("Modifiche effettuate con successo.");
    }
    else
    {
      data.setMessagei18n("Nessuna modifica da apportare.");
    }
  }

  protected void salvaBlocco(Map params, AttributeList al, List<TagModifyInfo> arTagsmod)
     throws DicomException
  {
    for(TagModifyInfo ti : arTagsmod)
    {
      if(!ti.editabile)
        continue;

      String tag = ti.getTag();
      String value = SU.okStrNull(params.get(tag));
      ti.setValue(value);

      if(ti.modificato)
        al.put(ti.attr);
    }
  }

  protected void salvaVariazione(AttributeList al, String valore, AttributeTag tag)
     throws DicomException
  {
    if((valore = SU.okStrNull(valore)) != null)
    {
      Attribute attr1 = AttributeFactory.newAttribute(tag);
      attr1.setValue(valore);
      al.put(attr1);
    }
  }

  public void doCmd_copiaclipboard(CoreRunData data, Map params, Object... args)
     throws Exception
  {
    if(clipboard.contains(resBean))
    {
      data.setMessagei18n("Esame già presente in clipboard.");
      return;
    }

    StudyResultBean cl = (StudyResultBean) resBean.clone();
    clipboard.add(cl);
  }

  public void doCmd_pulisciclipboard(CoreRunData data, Map params, Object... args)
     throws Exception
  {
    clipboard.clear();
  }

  public void doCmd_incollaclipboard(CoreRunData data, Map params, Object... args)
     throws Exception
  {
    String stMergeUID = SU.okStrNull(params.get("cmdparam"));

    if(stMergeUID == null)
      data.throwMessagei18n("Parametro esame non specificato nella richiesta.");

    Optional<StudyResultBean> rbMerge = clipboard.stream()
       .filter((st) -> SU.isEqu(stMergeUID, st.StudyInstanceUID))
       .findFirst();

    if(!rbMerge.isPresent())
      data.throwMessagei18n("Esame %s non trovato nella clipboard.", stMergeUID);

    StudyResultBean sbMerge = rbMerge.get();

    if(SU.isEqu(resBean.StudyInstanceUID, sbMerge.StudyInstanceUID))
      data.throwMessagei18n("Modifica con l'esame corrente non possibile.");

    mergeBlocco(sbMerge, arTagsmodPatient);
    mergeBlocco(sbMerge, arTagsmodStudy);
    mergedStudyUID = sbMerge.StudyInstanceUID;

    mergedStudyArea = null;
    if(SU.isEqu(resBean.storageAetitle, sbMerge.storageAetitle))
      mergedStudyArea = sbMerge.storageAetitle;
  }

  protected void mergeBlocco(StudyResultBean sbMerge, List<TagModifyInfo> arTagsmod)
     throws DicomException
  {
    for(TagModifyInfo tp : arTagsmod)
    {
      String val;
      Attribute pAttr = sbMerge.al.get(tp.attr.getTag());
      if(pAttr != null && (val = pAttr.getSingleStringValueOrNull()) != null)
      {
        if(!SU.isEqu(val, tp.attr.getSingleStringValueOrNull()))
        {
          tp.attr.setValue(val);
          tp.modificato = true;
        }
      }
    }
  }

  protected void leggiListaAttributi()
     throws Exception
  {
    File fxml = SERVICE.getConfWithAlternative("modifica-dicom.xml");
    SAXBuilder builder = new SAXBuilder();
    Document dx = builder.build(fxml);

    Element epPat = dx.getRootElement().getChild("paziente");
    List<Element> lsecPat = epPat.getChildren();
    parseBloccoAttributi(lsecPat, arTagsmodPatient);

    Element epStu = dx.getRootElement().getChild("esame");
    List<Element> lsecStu = epStu.getChildren();
    parseBloccoAttributi(lsecStu, arTagsmodStudy);
  }

  protected void parseBloccoAttributi(List<Element> lsec, List<TagModifyInfo> arTagsmod)
     throws DicomException
  {
    for(Element e : lsec)
    {
      switch(e.getName())
      {
        case "input":
        {
          TagModifyInfo ti = new TagModifyInfo();
          AttributeTag tag = getTag(e.getAttributeValue("tag"));
          if((ti.attr = resBean.al.get(tag)) == null)
            ti.attr = AttributeFactory.newAttribute(tag);
          ti.label = e.getAttributeValue("label");
          ti.order = SU.parseInt(e.getAttributeValue("order"));
          ti.editabile = true;
          arTagsmod.add(ti);
        }
        break;

        case "display":
        {
          TagModifyInfo ti = new TagModifyInfo();
          AttributeTag tag = getTag(e.getAttributeValue("tag"));

          if(tag.equals(TagFromName.NumberOfStudyRelatedInstances))
          {
            ti.attr = AttributeFactory.newAttribute(tag);
            ti.attr.setValue(resBean.arFiles.size());
          }
          else if(tag.equals(TagFromName.NumberOfStudyRelatedSeries))
          {
            ti.attr = AttributeFactory.newAttribute(tag);
            ti.attr.setValue(resBean.arSeries.size());
          }
          else if(tag.equals(TagFromName.RetrieveAETitle))
          {
            ti.attr = AttributeFactory.newAttribute(tag);
            ti.attr.setValue(resBean.storageAetitle);
          }
          else
          {
            if((ti.attr = resBean.al.get(tag)) == null)
              ti.attr = AttributeFactory.newAttribute(tag);
          }

          ti.label = e.getAttributeValue("label");
          ti.order = SU.parseInt(e.getAttributeValue("order"));
          ti.editabile = false;
          arTagsmod.add(ti);
        }
        break;
      }
    }

    arTagsmod.sort((t1, t2) -> Integer.compare(t1.order, t2.order));
  }

  protected AttributeTag getTag(String s)
  {
    try
    {
      return new AttributeTag(s);
    }
    catch(Exception e)
    {
      return null;
    }
  }
}
