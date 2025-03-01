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
package org.argogui.services.dcmsrv;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import org.argogui.om.StpStorage;
import org.argogui.services.ArgoBaseService;
import org.argogui.services.cache.ServerConfiguration;
import java.io.File;
import java.util.List;
import java.util.Set;
import org.commonlib5.utils.LongOperListener;

/**
 * Servizio nullo di generazione
 * @author Nicola De Nisco
 */
public class NullDicomServer extends ArgoBaseService
   implements DicomServer
{
  @Override
  public boolean isRunning()
  {
    return false;
  }

  @Override
  public void argoInit()
     throws Exception
  {
  }

  @Override
  public void ricaricaConfigurazione()
     throws Exception
  {
  }

  @Override
  public String getCanonicalServerAETitle()
  {
    return "";
  }

  @Override
  public void rejectDicomFile(File toReject)
  {
  }

  @Override
  public void deleteDicomFiles(String accno, String aetitle)
     throws Exception
  {
  }

  @Override
  public void deleteDicomFiles(List<File> lsFiles, String aetitle)
     throws Exception
  {
  }

  @Override
  public void cancellaStorage()
     throws Exception
  {
  }

  @Override
  public List<File> getDicomFiles(String accno, int tipo)
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<String> getDicomRelativePaths(String accno, int tipo)
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<StudyResultBean> queryStudyLocalNode(Set<Attribute> queryParams)
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<AttributeTag> getDefaultTags()
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public ServerConfiguration getServerConfiguration()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public StudyResultBean populateStudy(String studyUID, String aetitle, StudyResultBean bean)
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<StpStorage> getAllStorages()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<File> getDicomFiles(String studyUID, String serieUID, int tipo)
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int pingTarget(int idTarget)
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int echoscuTarget(int idTarget, StringBuilder handshake)
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int descriviConnessioni(StringBuilder sb)
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void modificaFilesDatabase(String studyUID, String aetitle, AttributeList lsModify)
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void storescu(int idTarget, String callingAetitle, List<File> arFiles, LongOperListener lol)
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
