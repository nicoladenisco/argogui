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
package org.argogui.services.wklsrv;

import com.pixelmed.dicom.DicomAttributeList;
import com.pixelmed.dicom.DicomWorklistBuilder;
import com.pixelmed.dicom.TagFromName;
import org.argogui.om.RisWorklist;
import org.argogui.utils.SU;
import org.argogui.utils.TR;
import java.io.File;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generatore di worklist attraverso le libreire pixelmed.
 *
 * @author Nicola De Nisco
 */
public class PmedScriviFile extends DicomWorklistBuilder
   implements AbstractFileGenerator
{
  /** Logging */
  private static Log log = LogFactory.getLog(PmedScriviFile.class);

  @Override
  public void init(Configuration cfg)
  {
    institutionName = TR.getString("azienda.nome");
  }

  /**
   * Crea il file DICOM per la RisWorklist.
   * @param w oggetto worklist
   * @param toWrite indica il file da creare
   * @param _modalita
   * @param _aetitle
   * @throws Exception
   */
  @Override
  public synchronized void write(RisWorklist w, File toWrite, String _modalita, String _aetitle)
     throws Exception
  {
    try
    {
      if(toWrite.exists())
        toWrite.delete();

      aetitle = _aetitle;
      modality = _modalita;
      accessionNumber = w.getAccessionNumber();

      String refPN = w.getReferPhysician();

      patName = w.getPatName();
      patID = w.getPatId();
      patBirth = w.getPatNascita();
      patSex = w.getPatSex();
      alerts = w.getMedicalalerts();
      allergies = w.getContrastallergies();
      ethnicGroup = w.getEthnicGroup();
      pregnancyStatus = w.getPregnancyStatus();
      studyUID = w.getStudyInstanceUid();
      studyDate = w.getSchedProcStepStartDateTime();

      procedureID = w.getReqProcId();
      procedureDescr = w.getReqProcDesc();

      stepID = w.getSchedProcStepId();
      stepDescr = w.getSchedProcStepDesc();
      stationName = w.getSchedStationName();
      stepLocation = w.getSchedProcStepLocation();
      if(!SU.isOkStr(stepLocation))
        stepLocation = stationName;

      admissionID = w.getAdmissionId();
      issuerAdmissionID = w.getIssuerAdmissionId();
      patLocation = w.getCurrpatloc();
      patState = w.getPatStatus();

      if(!SU.isOkStr(procedureID))
        procedureID = SU.okStrNull(w.getSchedProcStepId());
      if(!SU.isOkStr(procedureDescr))
        procedureDescr = SU.okStrNull(w.getSchedProcStepDesc());

      createDicomWorklistFile(toWrite);
    }
    catch(Exception e)
    {
      log.error("Errore scrivendo file worklist:", e);
    }
  }

  @Override
  public void shutdown()
     throws Exception
  {
  }

  @Override
  public synchronized DicomAttributeList createDicomWorklistEntry()
     throws Exception
  {
    DicomAttributeList dataset = super.createDicomWorklistEntry();

    // tag vuoti inseriti per compatibilità CR Fuji
    dataset.putValue(TagFromName.OtherPatientIDs, ""); //            LO
    dataset.putValue(TagFromName.PatientComments, ""); //            LT
    dataset.putValue(TagFromName.StudyID, ""); //                SH
    dataset.putValue(TagFromName.ReferringPhysicianName, ""); //              PN
    dataset.putValue(TagFromName.RequestingService, ""); //              LO
    dataset.putValue(TagFromName.ExposureDoseSequence, ""); //            SQ
    dataset.putValue(TagFromName.ScheduledPerformingPhysicianName, ""); //    PN
    dataset.putValue(TagFromName.OrderEntererLocation, ""); //          SH
    dataset.putValue(TagFromName.NamesOfIntendedRecipientsOfResults, ""); // PN
    dataset.putValue(TagFromName.ReferringPhysicianName, ""); //        PN
    dataset.putValue(TagFromName.CodingSchemeVersion, ""); //            SH
    dataset.putValue(TagFromName.StudyDescription, procedureDescr); //            LO
    dataset.putValue(TagFromName.ReferencedPatientSequence, ""); //          SQ
    dataset.putValue(TagFromName.ReferencedSOPClassUID, ""); //          UI
    dataset.putValue(TagFromName.ReferencedSOPInstanceUID, ""); //          UI
//dataset.putValue(TagFromName.DistributionCode, ""); //            ST
    dataset.putValue(TagFromName.RadiationMode, ""); //                  CS
    dataset.putValue(TagFromName.KVP, ""); //                    DS
    dataset.putValue(TagFromName.XRayTubeCurrentInuA, ""); //       DS
    dataset.putValue(TagFromName.ExposureTime, ""); //                DS
    dataset.putValue(TagFromName.FilterType, ""); //                LO
    dataset.putValue(TagFromName.FilterMaterial, ""); //               CS
    dataset.putValue(TagFromName.NumberOfFilms, ""); //               ST

    // wcompat
    dataset.putValue(TagFromName.RequestedProcedurePriority, "");
    dataset.putValue(TagFromName.PatientTransportArrangements, "");
    dataset.putValue(TagFromName.ReasonForTheImagingServiceRequest, "");
    dataset.putValue(TagFromName.OrderEnteredBy, "");
    dataset.putValue(TagFromName.OrderCallbackPhoneNumber, "");
    dataset.putValue(TagFromName.PlacerOrderNumberImagingServiceRequest, "");
    dataset.putValue(TagFromName.FillerOrderNumberImagingServiceRequest, "");
    dataset.putValue(TagFromName.ConfidentialityConstraintOnPatientDataDescription, "");

    return dataset;
  }
}
