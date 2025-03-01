/*
 *  Visualizzatore.java
 *  Creato il 1-mag-2016, 11.28.24
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
package org.argogui.modules.screens;

import com.pixelmed.dicom.TagFromName;
import org.argogui.beans.VisContext;
import org.argogui.beans.VisualizzatoreBean;
import org.sirio6.utils.CoreRunData;
import org.argogui.utils.SU;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.sirio6.beans.BeanFactory;

/**
 * Controllore per Visualizzatore.vm.
 *
 * @author Nicola De Nisco
 */
public class Visualizzatore extends ArgoBaseScreen
{
  @Override
  protected void doBuildTemplate2(CoreRunData data, Context context)
     throws Exception
  {
    super.doBuildTemplate2(data, context);
    data.getTemplateInfo().setLayoutTemplate("Vislayout.vm");

    VisualizzatoreBean bean = BeanFactory.getFromSession(data, VisualizzatoreBean.class);
    VisContext ctx = bean.getOrAddStudy(data);
    context.put("ctx", ctx);

    String serieUID = SU.okStrNull(data.getParameters().getString("serieUID"));
    if(serieUID != null)
      ctx.setCurrSerie(serieUID);

    context.put("images", ctx.currSerie.arInstances);
    context.put("series", ctx.res.arSeries);

    context.put("patientID", ctx.getTagAsString(TagFromName.PatientID));
    context.put("studyUID", ctx.res.StudyInstanceUID);
    context.put("seriesUID", ctx.currSerie.SerieInstanceUID);
    context.put("instanceUID", ctx.currInstance.SOPInstanceUID);
    context.put("sopclassUID", ctx.currInstance.SOPClassUID);
    context.put("modality", ctx.currSerie.modalita);
    context.put("numberOfImages", ctx.currSerie.arFiles.size());
    context.put("patientNameEscape", ctx.getTagAsString(TagFromName.PatientName));
    context.put("totalNoOfStudies", "1");
    context.put("studyDesc", ctx.getTagAsString(TagFromName.StudyDescription));
    context.put("patientSex", ctx.getTagAsString(TagFromName.PatientSex));
    context.put("physicianNameHtml", ctx.getTagAsString(TagFromName.ReferringPhysicianName));
    context.put("patientBirthDate", ctx.getTagAsString(TagFromName.PatientBirthDate));
    context.put("studyDate", ctx.getTagAsString(TagFromName.StudyDate));
    context.put("modalities", ctx.res.modalita);
  }

  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return isAuthorizedAll(data, "visualizza_dicom_semplice");
  }
}
