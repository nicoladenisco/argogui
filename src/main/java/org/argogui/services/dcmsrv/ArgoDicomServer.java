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

import com.pixelmed.dicom.*;
import com.pixelmed.network.DeleteSOPClassSCU;
import org.argogui.Costanti;
import org.argogui.Semafori;
import org.argogui.om.StpStorage;
import org.argogui.om.StpStoragePeer;
import org.argogui.om.InfInEsami;
import org.argogui.om.InfInEsamiPeer;
import org.argogui.om.StpNodiDicom;
import org.argogui.om.StpNodiDicomPeer;
import org.argogui.utils.I;
import org.argogui.services.ArgoBaseService;
import org.argogui.services.cache.ServerConfiguration;
import org.argogui.utils.SU;
import java.io.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argogui.ArgoBusMessages;
import org.commonlib5.exec.ExecHelper;
import org.commonlib5.exec.ProcessHelper;
import org.commonlib5.exec.ProcessWatchListner;
import org.commonlib5.utils.CommonFileUtils;
import org.commonlib5.utils.CommonNetUtils;
import org.commonlib5.utils.LongOperListener;
import org.commonlib5.utils.OsIdent;
import org.commonlib5.utils.PropertyManager;
import org.commonlib5.utils.StringOper;
import org.rigel5.db.DbUtils;
import org.rigel5.db.torque.CriteriaRigel;
import org.rigel5.db.torque.PeerTransactAgent;
import org.sirio6.services.InfoSetupBlock;
import org.sirio6.services.InfoSetupInterface;
import org.sirio6.services.allarmi.ALLARM;
import org.sirio6.services.bus.BUS;
import org.sirio6.services.bus.BusContext;
import org.sirio6.services.bus.MessageBusListener;

/**
 * Server DICOM in collegamento con ARGO.
 * In effetti questo non è un server DICOM, ma solo un frontend
 * al server ARGO da cui legge le immagini ricevute.
 *
 * @author Nicola De Nisco
 */
public class ArgoDicomServer extends ArgoBaseService
   implements MessageBusListener, DicomServer, InfoSetupInterface
{
  /** Logging */
  private static Log log = LogFactory.getLog(ArgoDicomServer.class);
  //
  public static final String RXQUEUE = "rxqueue";
  public static final String LOCKFILE = "lockfile";
  //
  protected String pacsAEtitle = null;
  protected File dirServer = null, dirReject = null;
  protected File argoCfg = null, argoDist = null;
  protected ArrayList<String> arActiveConnections = new ArrayList<>();
  protected Thread thScan = null;
  protected int delayQueueMillis = 0, portaDicom = 0, portaWado = 0, maxAssociation;
  protected boolean cygwin = true;
  protected boolean mustExit = false;
  protected File arQuery = null, arDump = null, arSend = null,
     dicomDic = null, dirDcmtk = null, dirArgoBin = null, dirArgoLib = null;
  protected ArrayList<File> arRejectToDelete = new ArrayList<>();
  protected ArrayList<AttributeTag> arBeanAttrs = new ArrayList<>();
  protected ArrayList<StpStorage> arStorages = new ArrayList<>();

  @Override
  public boolean isRunning()
  {
    return true;
  }

  @Override
  public void argoInit()
     throws Exception
  {
    Configuration cfg = getConfiguration();
    pacsAEtitle = cfg.getString("pacsAEtitle", "ARGO");
    portaDicom = cfg.getInt("portaDicom", 12001);
    portaWado = cfg.getInt("portaWado", 9009);
    maxAssociation = cfg.getInt("maxAssociation", 256);

    delayQueueMillis = cfg.getInt("delayQueueMillis", 30000);
    cygwin = cfg.getBoolean("argo.cygwin", true);
    mustExit = false;

    dirServer = getWorkMainFile("dcmdata");
    ASSERT_DIR_WRITE(dirServer);

    dirReject = new File(dirServer, "reject");
    ASSERT_DIR_WRITE(dirReject);

    String pathArgoBin = cfg.getString("dirArgoBin", "/usr/local/argobin");
    String pathArgoLib = cfg.getString("dirArgoLib", pathArgoBin + "/lib");
    String pathArQuery = cfg.getString("arQuery", pathArgoBin + "/arquery");
    String pathArDump = cfg.getString("arDump", pathArgoBin + "/ardump");
    String pathArSend = cfg.getString("arSend", pathArgoBin + "/arsend");
    String pathDirDcmtk = cfg.getString("dirDcmtk", "/usr/local/dicom");
    String pathDicomDic = cfg.getString("dicomDic", pathDirDcmtk + "/lib/dicom.dic");

    // questo lo creiamo noi quindi non esiste al momento
    argoCfg = new File(dirServer, "argo.cfg");
    argoCfg.delete();

    // lista pacs distribuito
    argoDist = new File(dirServer, "nodes.cfg");
    argoDist.delete();

    dirArgoBin = new File(pathArgoBin);
    ASSERT_DIR(dirArgoBin);
    dirArgoLib = new File(pathArgoLib);
    ASSERT_DIR(dirArgoLib);
    arQuery = new File(pathArQuery);
    ASSERT_FILE(arQuery);
    arDump = new File(pathArDump);
    ASSERT_FILE(arDump);
    arSend = new File(pathArSend);
    ASSERT_FILE(arSend);
    dirDcmtk = new File(pathDirDcmtk);
    ASSERT_DIR(dirDcmtk);
    dicomDic = new File(pathDicomDic);
    ASSERT_FILE(dicomDic);

    // imposta default dei tag da visualizzare
    arBeanAttrs.add(TagFromName.PatientName);
    arBeanAttrs.add(TagFromName.PatientBirthDate);
    arBeanAttrs.add(TagFromName.StudyDescription);
    arBeanAttrs.add(TagFromName.StudyDate);
    arBeanAttrs.add(TagFromName.StudyTime);
    arBeanAttrs.add(TagFromName.AccessionNumber);
    arBeanAttrs.add(TagFromName.ModalitiesInStudy);
//    arBeanAttrs.add(TagFromName.ReferringPhysicianName);
//    arBeanAttrs.add(TagFromName.NameOfPhysiciansReadingStudy);
//    arBeanAttrs.add(TagFromName.PatientAge);
//    arBeanAttrs.add(TagFromName.PatientSize);
//    arBeanAttrs.add(TagFromName.PatientWeight);
//    arBeanAttrs.add(TagFromName.StudyID);
    arBeanAttrs.add(TagFromName.NumberOfStudyRelatedSeries);
    arBeanAttrs.add(TagFromName.NumberOfStudyRelatedInstances);

    // avvia i server dicom
    startServers();

    // si registra sul bus per ricevere notifiche
    BUS.registerEventListner(getClass().getName(), this);
  }

  @Override
  public void shutdown()
  {
    // segnala uscita
    mustExit = true;

    // cancella il fileDicom di configurazione di argo: questo provocherà un arresto del demone
    argoCfg.delete();

    // attende la chiusura del thread di servizio
    try
    {
      if(thScan != null && thScan.isAlive())
        thScan.join();
    }
    catch(InterruptedException ex)
    {
    }

    super.shutdown();
  }

  protected void startServers()
     throws Exception
  {
    writeArgoConfig();

    thScan = new Thread()
    {
      @Override
      public void run()
      {
        scanDirectoryDicom();
      }
    };

    thScan.setName("Scan DICOM");
    thScan.setDaemon(true);
    thScan.start();
  }

  protected void writeArgoConfig()
     throws Exception
  {
    PeerTransactAgent.execute((con) -> writeArgoConfig(con));
  }

  protected void writeArgoConfig(Connection con)
     throws Exception
  {
    synchronized(Semafori.argolink)
    {
      CriteriaRigel cr = new CriteriaRigel(StpStoragePeer.TABLE_NAME);
      List<StpStorage> lsStg = StpStoragePeer.doSelect(cr, con);

      // inserisce default se non presente nessuna area
      if(lsStg.isEmpty())
      {
        StpStorage stg = new StpStorage();
        stg.setCodice(pacsAEtitle);
        stg.setDescrizione(I.I("Area storage primaria"));
        stg.setPath(dirServer.getAbsolutePath() + File.separator + "main");
        stg.save();
        lsStg.add(stg);
      }

      arStorages.clear();
      for(StpStorage stg : lsStg)
      {
        if(!checkStorage(stg))
          continue;

        arStorages.add(stg);
      }

      // scrive in un fileDicom temporaneo che verrà rinominato dopo il completamento
      File tmp = new File(argoCfg.getAbsolutePath() + ".tmp");

      try (FileWriter wr = new FileWriter(tmp))
      {
        wr.write(String.format("# Generato da ARGO\n"
           + "# non modificare: viene rigenerato periodicamente\n"
           + "NetworkType     = \"tcp\"\n"
           + "NetworkTCPPort  = %d\n"
           + "MaxPDUSize      = 16384\n"
           + "MaxAssociations = %d\n"
           + "Display         = \"no\"\n",
           portaDicom, maxAssociation));

        wr.write("HostTable BEGIN\n");

        wr.write(String.format("argo = (%s, localhost, %d)\n", getCanonicalServerAETitle(), portaDicom));
        wr.write("locals = argo\n");

        List<StpNodiDicom> lsNodi = StpNodiDicomPeer.getNodi(con);
        if(!lsNodi.isEmpty())
        {
          String names = "";
          for(StpNodiDicom nodo : lsNodi)
          {
            String srvName = nodo.getDescrizione().replace(' ', '_');
            wr.write(String.format(
               "%s = (%s, %s, %d)\n",
               srvName, nodo.getAetitle(), nodo.getIndirizzo(), nodo.getPorta()));
            names += "," + srvName;
          }

          wr.write("nodes = " + names.substring(1) + "\n");
        }

        wr.write("HostTable END\n");

        wr.write("VendorTable BEGIN\n");
        wr.write("\"Local hosts\" = locals\n");
        if(!lsNodi.isEmpty())
          wr.write("\"Remote hosts\" = nodes\n");
        wr.write("VendorTable END\n");

        wr.write("AETable BEGIN\n");

        for(StpStorage stg : arStorages)
        {
          String access = "RW";
          switch(stg.getTipo())
          {
            case StpStoragePeer.TIPO_AREA_RO:
              access = "R";
            case StpStoragePeer.TIPO_AREA:
              // directory di storage
              File mainBuffer = new File(stg.getPath());
              String argoStorage = mainBuffer.getAbsolutePath();

              // se argo è cygwin occorre convertire la path nella forma corretta per cygwin
              if(OsIdent.checkOStype() == OsIdent.OS_WINDOWS && cygwin)
                argoStorage = SU.convertPathToCygwin(argoStorage);

              wr.write(String.format(
                 "\"%s\"       \"%s\"       %s  (200, 1024mb) ANY\n",
                 stg.getCodice(), argoStorage, access));
              break;

            case StpStoragePeer.TIPO_LOCALI:
              wr.write(String.format(
                 "\"%s\"       \"LOCAL\"       LOCAL  (200, 1024mb) ANY\n",
                 stg.getCodice()));
              break;

            case StpStoragePeer.TIPO_DISTRIBUITO:
              // lista nodi distribuiti
              String argoDistlist = argoDist.getAbsolutePath();

              // se argo è cygwin occorre convertire la path nella forma corretta per cygwin
              if(OsIdent.checkOStype() == OsIdent.OS_WINDOWS && cygwin)
                argoDistlist = SU.convertPathToCygwin(argoDistlist);

              wr.write(String.format("\"%s\"       \"%s\"       DIST  (200, 1024mb) ANY\n",
                 stg.getCodice(), argoDistlist));
              break;
          }
        }

        wr.write("AETable END\n");
      }

      CommonFileUtils.moveFile(tmp, argoCfg);
    }
  }

  /**
   * Verifica ara di storage.
   * @param stg area da verificare
   * @return vero se area ok
   */
  protected boolean checkStorage(StpStorage stg)
  {
    if(!stg.isArea())
      return true;

    File fdir = new File(stg.getPath());
    if(!fdir.isDirectory())
    {
      if(!fdir.mkdirs())
      {
        String msg = I.I("La directory '%s' non esiste e non è possibile crearla; viene ignorata.", stg.getPath());
        ALLARM.error(DicomServer.SERVICE_NAME, "FileSystem", msg, 0);
        log.error(msg);
        return false;
      }
    }

    File dirDicom = new File(fdir, AREA_DICOM);
    if(!CommonFileUtils.checkDirectoryWritable(dirDicom))
    {
      String msg = I.I("La directory '%s' non è scrivibile; viene ignorata.", stg.getPath());
      ALLARM.error(DicomServer.SERVICE_NAME, "FileSystem", msg, 0);
      log.error(msg);
      return false;
    }

    File dirThumbs = new File(fdir, AREA_THUMB);
    if(!CommonFileUtils.checkDirectoryWritable(dirThumbs))
    {
      String msg = I.I("La directory '%s' non è scrivibile; viene ignorata.", stg.getPath());
      ALLARM.error(DicomServer.SERVICE_NAME, "FileSystem", msg, 0);
      log.error(msg);
      return false;
    }

    return true;
  }

  @Override
  public int message(int msgID, Object originator, BusContext context)
     throws Exception
  {
    return 0;
  }

  /**
   * Funzione di servizio del thread di scansione.
   */
  protected void scanDirectoryDicom()
  {
    while(!mustExit)
    {
      try
      {
        Thread.sleep(3000);
        scanDirectoryDicomShot();
      }
      catch(Exception e)
      {
        log.error(I.I("Errore nel thread di scansione."), e);
      }
    }
  }

  /**
   * Scansione delle directory DICOM a caccia di job da avviare.
   * @throws Exception
   */
  protected void scanDirectoryDicomShot()
     throws Exception
  {
    // test se inizializzazione completata
    if(arStorages.isEmpty())
      return;

    synchronized(Semafori.argolink)
    {
      ArrayList<String> prevConn = new ArrayList<>(arActiveConnections);

      arActiveConnections.clear();
      for(StpStorage stg : arStorages)
      {
        if(!stg.isArea())
          continue;

        File mainBuffer = new File(stg.getPath());
        scanDirForJob(pacsAEtitle, mainBuffer);

        // alla fine della scansione cancella dal db di Argo i files spostati in reject
        if(!arRejectToDelete.isEmpty())
        {
          if(stg.getTipo() == StpStoragePeer.TIPO_AREA)
            deleteDicomFiles(arRejectToDelete, stg.getCodice());

          arRejectToDelete.clear();
        }
      }

      if(!prevConn.equals(arActiveConnections))
        BUS.sendMessageAsync(ArgoBusMessages.ASSOCIATION_LIST_CHANGED, this, new BusContext("connessioni", arActiveConnections));
    }
  }

  /**
   * Scansione di una directory e salvataggio dati essenziali in db.
   * Cerca i fileDicom della coda di ARGO per interpretare i dati DICOM
   * e riportare nel db interno i dati necessari.
   * @param aetitle AETitle associato alla directory da analizzare
   * @param dirToScan directory da analizzare
   * @throws Exception
   */
  protected void scanDirForJob(String aetitle, File dirToScan)
     throws Exception
  {
    File rxqueue = new File(dirToScan, RXQUEUE);
    File semaforo = new File(rxqueue, LOCKFILE);
    if(!semaforo.exists())
      return;

    File[] arFiles = rxqueue.listFiles();
    for(int i = 0; i < arFiles.length; i++)
    {
      File f = arFiles[i];
      if(f.getName().equals(LOCKFILE))
        continue;

      if(!semaforo.exists())
        return;

      if(f.getName().contains(".txt"))
      {
        try
        {
          // estrae dati dal fileDicom descrittore
          PropertyManager pm = new PropertyManager(f);
          String callingAETitle = pm.getString("callingAETitle");
          String calledAETitle = pm.getString("calledAETitle");
          String pathFileDcm = pm.getString("imageFileName");

          if(OsIdent.checkOStype() == OsIdent.OS_WINDOWS && cygwin)
            pathFileDcm = SU.convertPathFromCygwin(pathFileDcm);

          if(pathFileDcm != null)
          {
            File fileDicom = new File(pathFileDcm);
            if(!fileDicom.canRead())
              processFileDicom(callingAETitle, calledAETitle, fileDicom, pm);
          }
        }
        catch(Exception ex)
        {
          log.error(I.I("Errore durante lo scan della directoy %s.", dirToScan.getAbsolutePath()), ex);
        }

        // cancella il testo dalla coda ricevuti
        f.delete();
      }

      if(f.getName().contains(".con"))
      {
        try
        {
          // estrae dati dal fileDicom descrittore
          PropertyManager pm = new PropertyManager(f);
          String callingAETitle = pm.getString("callingAETitle");
          String calledAETitle = pm.getString("calledAETitle");
          arActiveConnections.add(callingAETitle + "|" + calledAETitle);
        }
        catch(Exception ex)
        {
          log.error(I.I("Errore durante lo scan della directoy %s.", dirToScan.getAbsolutePath()), ex);
        }
      }
    }
  }

  /**
   * Segnala un fileDicom DICOM come rifiutato.
   * Generalmente la causa è dovuta ad una corruzione
   * del fileDicom o alla impossibilità di leggerlo.
   * Il server DICOM lo sposta in una apposita directory.
   * @param toReject
   */
  @Override
  public void rejectDicomFile(File toReject)
  {
    try
    {
      File fout = new File(dirReject, toReject.getName());
      CommonFileUtils.moveFile(toReject, fout);

      // salva il fileDicom in un array per rimuoverlo successivamente dal db di argo
      arRejectToDelete.add(fout);
    }
    catch(Exception ex)
    {
      log.error("Non posso spostare file nell'area reject.", ex);
    }
  }

  @Override
  public void deleteDicomFiles(String accno, String aetitle)
     throws Exception
  {
    // test se inizializzazione completata
    if(arStorages.isEmpty())
      return;

    ArrayList<String> filtro = new ArrayList<>();
    filtro.add("AccessionNumber=" + accno);

    for(StpStorage stg : arStorages)
    {
      if(!stg.isArea())
        continue;

      String storageAetitle = SU.okStrNull(stg.getCodice());
      if(storageAetitle == null)
        continue;

      String rvq = queryArgo(filtro, storageAetitle, 1);
      ArrayList<String> files = new ArrayList<>();
      CommonFileUtils.grep(new BufferedReader(new StringReader(rvq)), Pattern.compile("Dicom file [0-9]+: (.+)"), files);

      if(files.isEmpty())
        continue;

      SetOfDicomFiles sdf = new SetOfDicomFiles();
      for(String sf : files)
      {
        File test = new File(sf);
        if(test.exists())
          sdf.add(sf);
      }

      if(sdf.isEmpty())
        continue;

      if(aetitle == null)
        aetitle = pacsAEtitle;

      DeleteSOPClassSCU dsc = new DeleteSOPClassSCU();
      dsc.delete("localhost", portaDicom, aetitle, pacsAEtitle, sdf, 0);
    }
  }

  @Override
  public void deleteDicomFiles(List<File> lsFiles, String aetitle)
     throws Exception
  {
    SetOfDicomFiles sdf = new SetOfDicomFiles();
    for(File f : lsFiles)
    {
      if(f.exists())
        sdf.add(f);
    }

    if(sdf.isEmpty())
      return;

    if(aetitle == null)
      aetitle = pacsAEtitle;

    DeleteSOPClassSCU dsc = new DeleteSOPClassSCU();
    dsc.delete("localhost", portaDicom, aetitle, pacsAEtitle, sdf, 0);
  }

  @Override
  public void cancellaStorage()
     throws Exception
  {
    synchronized(Semafori.argolink)
    {
      // cancella il fileDicom di configurazione di argo: questo provocherà un arresto del demone
      argoCfg.delete();

      // ritardo necessario per attendere la terminazione
      Thread.sleep(2000);

      // rimuove tutti i files dalla directory di spool
      CommonFileUtils.deleteDir(dirServer, false);

      // ricrea il fileDicom di configurazione argo (riavviando il demone)
      writeArgoConfig();

      // ritardo necessario per attendere la riattivazione
      Thread.sleep(2000);
    }
  }

  @Override
  public String getCanonicalServerAETitle()
  {
    return pacsAEtitle;
  }

  /**
   * Imposta nel db lo stato di ricevuto per le immagini.
   * @param callingAETitle
   * @param calledAETitle
   * @param fileDicom
   * @param pm
   */
  private void processFileDicom(String callingAETitle, String calledAETitle, File fileDicom, PropertyManager pm)
  {
    try
    {
      PeerTransactAgent.execute((con) -> processFileDicom(callingAETitle, calledAETitle, fileDicom, pm, con));
    }
    catch(Exception ex)
    {
      log.error(I.I("Il file %s non è un file DICOM leggibile: viene rigettato.", fileDicom.getName()), ex);
      rejectDicomFile(fileDicom);
    }
  }

  private void processFileDicom(String callingAETitle, String calledAETitle, File fileDicom, PropertyManager pm, Connection con)
     throws Exception
  {
    // legge il fileDicom DICOM in memoria
    AttributeList list = new AttributeList();
    try (DicomInputStream ids = new DicomInputStream(fileDicom))
    {
      list.read(ids, TagFromName.PixelData);
    }

    String accno = SU.okStrNull(Attribute.getSingleStringValueOrNull(list, TagFromName.AccessionNumber));
    if(accno == null)
    {
      log.error(I.I("Il file %s non contiene accension number: non può essere associato a nessun esame.", fileDicom.getName()));
      rejectDicomFile(fileDicom);
      return;
    }

    List<InfInEsami> lsEsa = InfInEsamiPeer.getByAccno(accno, con);
    for(InfInEsami esa : lsEsa)
    {
      esa.setNumImmagini(esa.getNumImmagini() + 1);
      esa.setStatoRec(Costanti.STATO_REC_RESULT_RECEIVED);
      esa.save(con);
    }

    // imposta i record worklist come processati
    String sSQL
       = "UPDATE ris.worklist SET stato_rec=" + Costanti.STATO_REC_PROCESSED
       + " WHERE accession_number='" + accno + "'";
    DbUtils.executeStatement(sSQL, con);
  }

  /**
   * Query al database Argo.
   * Utilizza arquery per interrogare il db di Argo.
   * @param filtri filtri da impiegare (tag=valore)
   * @param type 0=cerca dati dicom 1=cerca files
   * @return l'output prodotto da arquery
   * @throws Exception
   */
  private String queryArgo(List<String> filtri, String storageAetitle, int type)
     throws Exception
  {
    String fileCfg = argoCfg.getCanonicalPath();
    String fileDic = dicomDic.getCanonicalPath();
    String pathLib = dirArgoLib.getCanonicalPath();

    if(OsIdent.checkOStype() == OsIdent.OS_WINDOWS && cygwin)
    {
      // se argo è cygwin occorre convertire la path nella forma corretta per cygwin
      fileCfg = SU.convertPathToCygwin(fileCfg);
      fileDic = SU.convertPathToCygwin(fileDic);
      pathLib = SU.convertPathToCygwin(pathLib);
    }

    // predispone parametri della linea di comando
    ArrayList<String> cmdList = new ArrayList<>();
    cmdList.add(arQuery.getAbsolutePath());
    cmdList.add("-c");
    cmdList.add(fileCfg);
    cmdList.add("-a");
    cmdList.add(storageAetitle);
    if(type == 1)
      cmdList.add("-mv");

    // aggiunge i tag di output
    for(AttributeTag tag : arBeanAttrs)
    {
      String s = String.format("%04X,%04X", tag.getGroup(), tag.getElement());
      cmdList.add(s);
    }

    cmdList.addAll(filtri);

    String[] cmdArray = StringOper.toArray(cmdList);
    String[] env =
    {
      "DCMDICTPATH=" + fileDic,
      "LD_LIBRARY_PATH=" + pathLib
    };

    if(log.isDebugEnabled())
    {
      log.debug("Run arquery: " + StringOper.join(cmdArray, ' ')); // NOI18N
      log.debug("Environment: " + StringOper.join(env, ',')); // NOI18N
    }

    ExecHelper eh = ExecHelper.exec(cmdArray, env);
    log.debug("STDERR:\n" + eh.getError());
    log.debug("STDOUT:\n" + eh.getOutput());

    if(eh.getStatus() != 0)
      throw new IOException(I.I("Errore nell'esecuzione di arquery."));

    return eh.getOutput();
  }

  @Override
  public List<File> getDicomFiles(String accno, int tipo)
     throws Exception
  {
    // test se inizializzazione completata
    if(arStorages.isEmpty())
      return Collections.EMPTY_LIST;

    ArrayList<File> rv = new ArrayList<>();
    ArrayList<String> filtro = new ArrayList<>();
    filtro.add("AccessionNumber=" + accno);

    getDicomFilesInternal(filtro, rv, tipo);
    return rv;
  }

  @Override
  public List<File> getDicomFiles(String studyUID, String serieUID, int tipo)
     throws Exception
  {
    // test se inizializzazione completata
    if(arStorages.isEmpty())
      return Collections.EMPTY_LIST;

    ArrayList<File> rv = new ArrayList<>();
    ArrayList<String> filtro = new ArrayList<>();
    filtro.add("StudyInstanceUID=" + studyUID);

    if(serieUID != null)
      filtro.add("SeriesInstanceUID=" + serieUID);

    getDicomFilesInternal(filtro, rv, tipo);
    return rv;
  }

  private void getDicomFilesInternal(List<String> filtro, List<File> rv, int tipo)
     throws Exception
  {
    for(StpStorage stg : arStorages)
    {
      if(!stg.isArea())
        continue;

      String storageAetitle = SU.okStrNull(stg.getCodice());
      if(storageAetitle == null)
        continue;

      // query al db di argo per immagini associate
      String output = queryArgo(filtro, storageAetitle, 1);

      StringTokenizer st = new StringTokenizer(output, "\n", false);
      while(st.hasMoreTokens())
      {
        // Dicom fileDicom 61: /var/argo/dicom/main/dicom/2015/03/03/MR_1_3_46_670589_11_0_0_11_4_2_0_20093_5_5312_2010051814034993991.dcm
        // Jpeg  fileDicom 61: /var/argo/dicom/main/jpeg/3106/IMG_1_3_46_670589_11_0_0_11_4_2_0_20093_5_5312_2010051814034993991.jpg
        // 0
        String s = st.nextToken();

        if((tipo & 1) != 0 && s.startsWith("Jpeg"))
        {
          int pos = s.indexOf(':');
          if(pos == -1 || (pos + 2) > s.length())
            continue;

          String path = s.substring(pos + 2);
          File f = new File(path);
          if(f.canRead())
            rv.add(f);
        }

        if((tipo & 2) != 0 && s.startsWith("Dicom"))
        {
          int pos = s.indexOf(':');
          if(pos == -1 || (pos + 2) > s.length())
            continue;

          String path = s.substring(pos + 2);
          File f = new File(path);
          if(f.canRead())
            rv.add(f);
        }
      }
    }
  }

  @Override
  public List<String> getDicomRelativePaths(String accno, int tipo)
     throws Exception
  {
    ArrayList<String> rv = new ArrayList<>();
    ArrayList<String> filtro = new ArrayList<>();
    filtro.add("AccessionNumber=" + accno);

    // test se inizializzazione completata
    if(arStorages.isEmpty())
      return rv;

    for(StpStorage stg : arStorages)
    {
      if(!stg.isArea())
        continue;

      String storageAetitle = SU.okStrNull(stg.getCodice());
      if(storageAetitle == null)
        continue;

      // query al db di argo per immagini associate
      String output = queryArgo(filtro, storageAetitle, 1);
      String spath = dirServer.getAbsolutePath();
      if(spath.endsWith("/"))
        spath += "/";
      int len = spath.length();

      StringTokenizer st = new StringTokenizer(output, "\n", false);
      while(st.hasMoreTokens())
      {
        // Dicom fileDicom 61: /var/argo/dicom/main/dicom/2015/03/03/MR_1_3_46_670589_11_0_0_11_4_2_0_20093_5_5312_2010051814034993991.dcm
        // Jpeg  fileDicom 61: /var/argo/dicom/main/jpeg/3106/IMG_1_3_46_670589_11_0_0_11_4_2_0_20093_5_5312_2010051814034993991.jpg
        // 0
        String s = st.nextToken();

        if((tipo & 1) != 0 && s.startsWith("Jpeg"))
        {
          int pos = s.indexOf(':');
          if(pos == -1 || (pos + 2) > s.length())
            continue;

          String path = s.substring(pos + 2 + len);
          rv.add(path);
        }

        if((tipo & 2) != 0 && s.startsWith("Dicom"))
        {
          int pos = s.indexOf(':');
          if(pos == -1 || (pos + 2) > s.length())
            continue;

          String path = s.substring(pos + 2 + len);
          rv.add(path);
        }
      }
    }

    return rv;
  }

  @Override
  public void ricaricaConfigurazione()
     throws Exception
  {
    synchronized(Semafori.argolink)
    {
      // cancella il fileDicom di configurazione di argo: questo provocherà un arresto del demone
      argoCfg.delete();

      // ritardo necessario per attendere la terminazione
      Thread.sleep(2000);

      // ricrea il fileDicom di configurazione argo (riavviando il demone)
      writeArgoConfig();

      // ritardo necessario per attendere la riattivazione
      Thread.sleep(2000);
    }
  }

  @Override
  public void populateInfoSetup(List<InfoSetupBlock> arInfo, boolean isAdmin)
     throws Exception
  {
    arInfo.add(new InfoSetupBlock(I.I("Server DICOM"), "D").
       add(I.I("AETITLE"), pacsAEtitle).
       add(I.I("DICOM Port"), Integer.toString(portaDicom)).
       add(I.I("WADO Port"), Integer.toString(portaWado)).
       add(I.I("Massimo associazioni contemporanee"), Integer.toString(maxAssociation)).
       add(I.I("DICOM TLS Port"), "Non attivo").
       add(I.I("Directory DICOM"), dirServer.getCanonicalPath()).
       add(I.I("Directory Reject"), dirReject.getCanonicalPath()).
       add(I.I("Config Argo"), argoCfg.getCanonicalPath()).
       add(I.I("Argo binary"), dirArgoBin.getCanonicalPath()).
       add(I.I("Argo lib"), dirArgoLib.getCanonicalPath()).
       add(I.I("Arquery"), arQuery.getCanonicalPath()).
       add(I.I("DICOM toolkit"), dirDcmtk.getCanonicalPath()).
       add(I.I("DICOM dictionary"), dicomDic.getCanonicalPath())
    );
  }

  /**
   * Estrae dal fileDicom dicom i tag richiesti per il bean.
   * @param fileDicom fileDicom da leggere
   * @param bean bean da popolare con i tag dicom
   * @return il bean popolato
   * @throws Exception
   */
  private StudyResultBean populateBeanFromFile(File fileDicom, StudyResultBean bean)
     throws Exception
  {
    // legge il fileDicom DICOM in memoria
    AttributeList list = new AttributeList();
    try (DicomInputStream ids = new DicomInputStream(fileDicom))
    {
      list.read(ids, TagFromName.PixelData);
    }

    bean.StudyInstanceUID = SU.okStrNull(Attribute.getSingleStringValueOrNull(list, TagFromName.StudyInstanceUID));
    bean.accno = SU.okStrNull(Attribute.getSingleStringValueOrNull(list, TagFromName.AccessionNumber));

    for(AttributeTag tag : arBeanAttrs)
    {
      Attribute attr = list.get(tag);
      if(attr != null)
        bean.al.put(tag, attr);
    }

    bean.arFiles.add(fileDicom);
    return bean;
  }

  public static final Pattern pres
     = Pattern.compile("\\((.+),(.+)\\) (.+) \\[(.+)\\]\\s*\\# \\s+([0-9])+, ([0-9]+) (.+)");

  private StudyResultBean populateBeanFromQuery(List<String> qres, StudyResultBean bean)
     throws Exception
  {
    // arquery risponde con dei blocchi di risultati di questo tipo:
    //
    //# Dicom-Data-Set
    //# Used TransferSyntax: UnknownTransferSyntax
    //(0008,0020) DA [20090223]                               #   8, 1 StudyDate
    //(0008,0030) TM [190809.000000]                          #  14, 1 StudyTime
    //(0008,0050) SH [161061]                                 #   6, 1 AccessionNumber
    //(0008,0052) CS [STUDY]                                  #   6, 1 QueryRetrieveLevel
    //(0008,0061) CS [MR]                                     #   2, 1 ModalitiesInStudy
    //(0008,0090) PN (no value available)                     #   0, 0 ReferringPhysiciansName
    //(0008,1030) LO [ENCEFALO]                               #   8, 1 StudyDescription
    //(0008,1060) PN (no value available)                     #   0, 0 NameOfPhysiciansReadingStudy
    //(0008,1080) LO (no value available)                     #   0, 0 AdmittingDiagnosesDescription
    //(0010,0010) PN [MAZZA^ANTONIO]                          #  14, 1 PatientsName
    //(0010,1010) AS [020Y]                                   #   4, 1 PatientsAge
    //(0010,1020) DS (no value available)                     #   0, 0 PatientsSize
    //(0010,1030) DS [85.000002]                              #  10, 1 PatientsWeight
    //(0010,2180) SH (no value available)                     #   0, 0 Occupation
    //(0010,21b0) LT (no value available)                     #   0, 0 AdditionalPatientHistory
    //(0020,000d) UI [2.16.840.1.113662.4.2306276707573.1235416148.1225922994853406213] #  64, 1 StudyInstanceUID
    //(0020,0010) SH [30432]                                  #   6, 1 StudyID
    //(0020,1070) IS (no value available)                     #   0, 0 OtherStudyNumbers
    //(0020,1206) IS [22]                                     #   2, 1 NumberOfStudyRelatedSeries
    //(0020,1208) IS [507]                                    #   4, 1 NumberOfStudyRelatedInstances

    for(String qre : qres)
    {
      Matcher m = pres.matcher(qre);
      if(!m.find())
        continue;

      if(m.groupCount() != 7)
        continue;

      try
      {
        String tagG = m.group(1);
        String tagE = m.group(2);
        String tagT = m.group(3);
        String tagV = m.group(4);
        String tag1 = m.group(5);
        String tag2 = m.group(6);
        String tagN = m.group(7);

        AttributeTag tag = new AttributeTag(
           Integer.parseInt(tagG.toUpperCase(), 16), Integer.parseInt(tagE.toUpperCase(), 16));
        Attribute attr = AttributeFactory.newAttribute(tag);
        attr.setValue(tagV);
        bean.al.put(tag, attr);

        if(tag.equals(TagFromName.StudyInstanceUID))
          bean.StudyInstanceUID = tagV;
        else if(tag.equals(TagFromName.AccessionNumber))
          bean.accno = tagV;
        else if(tag.equals(TagFromName.ModalitiesInStudy))
          bean.modalita = tagV;
      }
      catch(NumberFormatException | DicomException ex)
      {
        log.error(I.I("Errore non fatale nel parsing di %s: %s", qre, ex.getMessage()));
      }
    }

    return bean;
  }

  @Override
  public List<StudyResultBean> queryStudyLocalNode(Set<Attribute> queryParams)
     throws Exception
  {
    ArrayList<StudyResultBean> rv = new ArrayList<>();
    ArrayList<String> qparam = new ArrayList<>();

    // test se inizializzazione completata
    if(arStorages.isEmpty())
      return rv;

    for(Attribute attr : queryParams)
    {
      String tagName = String.format("%04x,%04x", attr.getTag().getGroup(), attr.getTag().getElement());
      String tagValue = attr.getSingleStringValueOrEmptyString();
      qparam.add(tagName + "=" + tagValue);
    }

    for(StpStorage stg : arStorages)
    {
      if(!stg.isArea())
        continue;

      String storageAetitle = SU.okStrNull(stg.getCodice());
      if(storageAetitle == null)
        continue;

      ArrayList<String> tmp = new ArrayList<>();
      String rvQuery = queryArgo(qparam, storageAetitle, 0);
      List<String> lsQuery = SU.string2List(rvQuery, "\n");

      for(String sq : lsQuery)
      {
        if(!SU.isOkStr(sq) || sq.contains("Dicom-Data-Set"))
        {
          // salva il contenuto corrente
          if(!tmp.isEmpty())
          {
            StudyResultBean bean = new StudyResultBean();
            bean.storageAetitle = storageAetitle;
            rv.add(populateBeanFromQuery(tmp, bean));
            tmp.clear();
          }
        }
        else
        {
          // accumula per parsing
          if(sq.startsWith("("))
            tmp.add(sq);
        }
      }

      // recupera ultimo elemento
      if(!tmp.isEmpty())
      {
        StudyResultBean bean = new StudyResultBean();
        bean.storageAetitle = storageAetitle;
        rv.add(populateBeanFromQuery(tmp, bean));
      }
    }

    return rv;
  }

  @Override
  public List<AttributeTag> getDefaultTags()
     throws Exception
  {
    return arBeanAttrs;
  }

  @Override
  public StudyResultBean populateStudy(String studyUID, String aetitle, StudyResultBean sb)
     throws Exception
  {
    if(arStorages.isEmpty())
      return sb;

    for(StpStorage stg : arStorages)
    {
      if(!stg.isArea())
        continue;

      if(aetitle == null || SU.isEqu(aetitle, stg.getCodice()))
      {
        File mainBuffer = new File(stg.getPath());
        populateStudy(studyUID, mainBuffer, sb);
        sb.storageAetitle = SU.okStrNull(stg.getCodice());

        // se la ricerca esa su tutti gli aetitle si ferma dopo aver trovato il primo risultato
        if(aetitle == null && !sb.arFiles.isEmpty())
          return sb;
      }
    }

    return sb;
  }

  public StudyResultBean populateStudy(String studyUID, File mainBuffer, StudyResultBean sb)
     throws Exception
  {
    if(studyUID == null && (studyUID = sb.StudyInstanceUID) == null)
      die(I.I("Nessun UID specificato per l'esame."));

    File dirDicom = new File(mainBuffer, AREA_DICOM);
    File dirThumbs = new File(mainBuffer, AREA_THUMB);

    Process p = startDump(studyUID, mainBuffer);
    ArrayList<String> tmp = new ArrayList<>();
    ArrayList<InstanceResultBean> results = new ArrayList<>();

    try (InputStream is = p.getInputStream(); InputStreamReader ir = new InputStreamReader(is); BufferedReader br
       = new BufferedReader(ir))
    {
      String linea;
      while((linea = br.readLine()) != null)
      {
        linea = linea.trim();

        if(linea.isEmpty())
        {
          // fine di un blocco di tag: salva risultato
          if(!tmp.isEmpty())
          {
            InstanceResultBean ib = new InstanceResultBean();
            populateInstanceBean(tmp, ib, dirDicom, dirThumbs);
            tmp.clear();

            results.add(ib);
          }
        }
        else
        {
          if(linea.charAt(0) == '#')
            continue;

          // accumula linea
          tmp.add(linea);
        }
      }
    }

    // ordina le immagini per serienumber ed instancenumber
    Collections.sort(results, new Comparator<InstanceResultBean>()
    {
      @Override
      public int compare(InstanceResultBean o1, InstanceResultBean o2)
      {
        if(o1.getSerieNum() == o2.getSerieNum())
          return o1.getNum() - o2.getNum();

        return o1.getSerieNum() - o2.getSerieNum();
      }
    });

    // aggiunge istanze all'esame; popolerà anche le informazioni della serie
    for(InstanceResultBean ib : results)
    {
      sb.addInstance(ib);
    }

    return sb;
  }

  private Process startDump(String studyUID, File mainBuffer)
     throws Exception
  {
    String fileCfg = argoCfg.getCanonicalPath();
    String fileDic = dicomDic.getCanonicalPath();
    String pathLib = dirArgoLib.getCanonicalPath();

    if(OsIdent.checkOStype() == OsIdent.OS_WINDOWS && cygwin)
    {
      // se argo è cygwin occorre convertire la path nella forma corretta per cygwin
      fileCfg = SU.convertPathToCygwin(fileCfg);
      fileDic = SU.convertPathToCygwin(fileDic);
      pathLib = SU.convertPathToCygwin(pathLib);
    }

    // predispone parametri della linea di comando
    ArrayList<String> cmdList = new ArrayList<>();
    cmdList.add(arDump.getAbsolutePath());
    cmdList.add("-d");
    cmdList.add(mainBuffer.getAbsolutePath());
    cmdList.add("StudyInstanceUID=" + studyUID);

    String[] cmdArray = StringOper.toArray(cmdList);
    String[] env =
    {
      "DCMDICTPATH=" + fileDic,
      "LD_LIBRARY_PATH=" + pathLib
    };

    if(log.isDebugEnabled())
    {
      log.debug("Run ardump: " + StringOper.join(cmdArray, ' ')); // NOI18N
      log.debug("Environment: " + StringOper.join(env, ',')); // NOI18N
    }

    return Runtime.getRuntime().exec(cmdArray, env);
  }

  private Pattern pdump = Pattern.compile("\\((.{4})\\,(.{4})\\) \\[(.+)\\]"); // NOI18N

  private void populateInstanceBean(ArrayList<String> tmp, InstanceResultBean bean, File dirDicom, File dirThumbs)
     throws Exception
  {
    for(String qre : tmp)
    {
      if(qre.startsWith("filename="))
      {
        String path = qre.substring(9);
        bean.fileDicom = new File(dirDicom, path);
      }

      Matcher m = pdump.matcher(qre);
      if(!m.find())
        continue;

      if(m.groupCount() != 3)
        continue;

      try
      {
        String tagG = m.group(1);
        String tagE = m.group(2);
        String tagV = m.group(3);

        AttributeTag tag = new AttributeTag(Integer.parseInt(tagG, 16), Integer.parseInt(tagE, 16));
        Attribute attr = AttributeFactory.newAttribute(tag);
        attr.setValue(tagV);
        bean.al.put(tag, attr);

        if(tag.equals(TagFromName.SOPInstanceUID))
          bean.SOPInstanceUID = tagV;
        if(tag.equals(TagFromName.SOPClassUID))
          bean.SOPClassUID = tagV;
        else if(tag.equals(TagFromName.Modality))
          bean.modalita = tagV;
      }
      catch(NumberFormatException | DicomException ex)
      {
        log.error(I.I("Errore non fatale nel parsing di %s", qre));
      }
    }

    ASSERT(bean.SOPClassUID != null, "bean.SOPClassUID != null");
    ASSERT(bean.SOPInstanceUID != null, "bean.SOPInstanceUID != null");
    bean.fileJpeg = getThumbArgo(bean.SOPInstanceUID, dirThumbs);
  }

  protected File getThumbArgo(String objUID, File dirThumbs)
     throws Exception
  {
    int idir = 0;
    char[] ca = objUID.toCharArray(), cb = new char[ca.length];
    for(int i = 0; i < ca.length; i++)
    {
      idir += (int) ca[i];
      cb[i] = ca[i] == '.' ? '_' : ca[i];
    }

    return new File(dirThumbs, idir + "/IMG_" + new String(cb) + ".jpg");
  }

  @Override
  public ServerConfiguration getServerConfiguration()
  {
    ServerConfiguration rv = new ServerConfiguration();
    rv.setAeTitle(pacsAEtitle);
    rv.setArgo(true);
    rv.setDcmProtocol("");
    rv.setHostName("localhost");
    rv.setPort(Integer.toString(portaDicom));
    rv.setWadoPort(Integer.toString(portaWado));
    return rv;
  }

  @Override
  public List<StpStorage> getAllStorages()
  {
    return Collections.unmodifiableList(arStorages);
  }

  @Override
  public int pingTarget(int idTarget)
     throws Exception
  {
    StpNodiDicom nodo = StpNodiDicomPeer.retrieveByPK(idTarget);
    String addr = SU.okStrNull(nodo.getIndirizzo());
    if(addr == null)
      return TEST_TARGET_INVALID_ADDRESS;

    if(CommonNetUtils.ping(addr))
      return TEST_TARGET_TCP_OK;

    return TEST_TARGET_TCP_UNREACHABLE;
  }

  @Override
  public int echoscuTarget(int idTarget, StringBuilder handshake)
     throws Exception
  {
    int rv;
    StpNodiDicom nodo = StpNodiDicomPeer.retrieveByPK(idTarget);

    if((rv = pingTarget(idTarget)) != TEST_TARGET_TCP_OK)
      return rv;

    rv = TEST_TARGET_ECHOSCU_FAILURE;
    File echoscu = new File(dirDcmtk, "/bin/echoscu");

    int countOK = 0;
    for(StpStorage stg : arStorages)
    {
      ArrayList<String> cmd = new ArrayList<>();
      cmd.add(echoscu.getAbsolutePath());
      cmd.add("-v");
      cmd.add("-aec");
      cmd.add(nodo.getAetitle());
      cmd.add("-aet");
      cmd.add(stg.getCodice());
      cmd.add(nodo.getIndirizzo());
      cmd.add(Integer.toString(nodo.getPorta()));
      log.debug(cmd);
      ExecHelper eh = ExecHelper.exec(StringOper.toArray(cmd));
      handshake.append("Test ").append(stg.getCodice()).append("->").append(nodo.getAetitle()).
         append(" address ").append(nodo.getIndirizzo()).append(" port ").append(nodo.getPorta()).append("\n");

      if(SU.isOkStr(eh.getOutput()))
        handshake.append(eh.getOutput()).append("\n");

      if(SU.isOkStr(eh.getError()))
      {
        handshake.append(eh.getError()).append("\n");

        if(!eh.getError().contains("Association Request Failed"))
          countOK++;
      }
    }

    if(countOK == 0)
      return rv;

    return countOK == arStorages.size() ? TEST_TARGET_ECHOSCU_ALL_OK : TEST_TARGET_ECHOSCU_OK;
  }

  //﻿ 5652 ?        R      0:00 argo running rws -> O3-DPACS (C-FIND)
  private static final Pattern pgrepargo = Pattern.compile("^.+argo\\srunning\\s(.+)$");

  @Override
  public int descriviConnessioni(StringBuilder sb)
     throws Exception
  {
    if(OsIdent.checkOStype() != OsIdent.OS_LINUX)
      return 0;

//    String cmd = "/bin/netstat -tpn|/bin/grep '/argo'";
//    ExecHelper eh = ExecHelper.execUsingShell(cmd);
//
//    if(!SU.isOkStr(eh.getOutput()))
//      return 1;
//    sb.append(eh.getOutput());
    String cmd = "/bin/ps ax";
    ExecHelper eh = ExecHelper.execUsingShell(cmd);

    ArrayList<String> arLinee = new ArrayList<>();
    CommonFileUtils.grep(new BufferedReader(new StringReader(eh.getOutput())),
       pgrepargo, arLinee);

    if(arLinee.isEmpty())
      return 1;

    arLinee.forEach((s) -> sb.append(s).append('\n'));

    return 2;
  }

  @Override
  public void modificaFilesDatabase(String studyUID, String aetitle, AttributeList lsModify)
     throws Exception
  {
    String fileCfg = argoCfg.getCanonicalPath();
    String fileDic = dicomDic.getCanonicalPath();
    String pathLib = dirArgoLib.getCanonicalPath();

    if(OsIdent.checkOStype() == OsIdent.OS_WINDOWS && cygwin)
    {
      // se argo è cygwin occorre convertire la path nella forma corretta per cygwin
      fileCfg = SU.convertPathToCygwin(fileCfg);
      fileDic = SU.convertPathToCygwin(fileDic);
      pathLib = SU.convertPathToCygwin(pathLib);
    }

    // predispone parametri della linea di comando
    ArrayList<String> cmdList = new ArrayList<>();
    cmdList.add(arQuery.getAbsolutePath());
    cmdList.add("-c");
    cmdList.add(fileCfg);
    cmdList.add("-a");
    cmdList.add(aetitle);
    cmdList.add("--query-alter");
    cmdList.add(String.format("0020,000D=%s", studyUID));

    for(Iterator itr = lsModify.values().iterator(); itr.hasNext();)
    {
      Attribute a = (Attribute) itr.next();
      String s = String.format("ALT_%04X,%04X=%s", a.getGroup(), a.getElement(),
         a.getSingleStringValueOrEmptyString());
      cmdList.add(s);
    }

    String[] cmdArray = StringOper.toArray(cmdList);
    String[] env =
    {
      "DCMDICTPATH=" + fileDic,
      "LD_LIBRARY_PATH=" + pathLib
    };

    if(log.isDebugEnabled())
    {
      log.debug("Run arquery: " + StringOper.join(cmdArray, ' ')); // NOI18N
      log.debug("Environment: " + StringOper.join(env, ',')); // NOI18N
    }

    ExecHelper eh = ExecHelper.exec(cmdArray, env);
    log.debug(eh.getError());

    if(eh.getStatus() != 0)
      throw new IOException(I.I("Errore nell'esecuzione di arquery."));
  }

  @Override
  public void storescu(int idTarget, String callingAetitle, List<File> arFiles, LongOperListener lol)
     throws Exception
  {
    File fileList = getWorkTmpFile("storescu" + Long.toString(System.currentTimeMillis()) + ".lst");
    fileList.deleteOnExit();

    long count = 0;
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileList)))
    {
      for(File f : arFiles)
      {
        if(f.exists())
        {
          bw.write(f.getCanonicalPath());
          bw.newLine();
          count++;
        }
      }
    }

    storescu(idTarget, callingAetitle, fileList, count, lol);
    fileList.delete();
  }

  public void storescu(int idTarget, String callingAetitle, File fileList, long total, LongOperListener lol)
     throws Exception
  {
    // arsend -vs -aet DCMNAS -aec DATA2 -fl temp-tosend.txt --send-counter casa175 12001
    StpNodiDicom nodo = StpNodiDicomPeer.retrieveByPK(idTarget);

    String fileCfg = argoCfg.getCanonicalPath();
    String fileDic = dicomDic.getCanonicalPath();
    String pathLib = dirArgoLib.getCanonicalPath();
    String fileLis = fileList.getCanonicalPath();

    if(OsIdent.checkOStype() == OsIdent.OS_WINDOWS && cygwin)
    {
      // se argo è cygwin occorre convertire la path nella forma corretta per cygwin
      fileCfg = SU.convertPathToCygwin(fileCfg);
      fileDic = SU.convertPathToCygwin(fileDic);
      pathLib = SU.convertPathToCygwin(pathLib);
      fileLis = SU.convertPathToCygwin(fileLis);
    }

    // predispone parametri della linea di comando
    ArrayList<String> cmdList = new ArrayList<>();
    cmdList.add(arSend.getAbsolutePath());
    cmdList.add("-aet");
    cmdList.add(callingAetitle);
    cmdList.add("-aec");
    cmdList.add(nodo.getAetitle());
    cmdList.add("--file-list");
    cmdList.add(fileLis);
    cmdList.add("--send-counter");
    cmdList.add(nodo.getIndirizzo());
    cmdList.add(Integer.toString(nodo.getPorta()));

    String[] cmdArray = StringOper.toArray(cmdList);
    String[] env =
    {
      "DCMDICTPATH=" + fileDic,
      "LD_LIBRARY_PATH=" + pathLib
    };

    if(log.isDebugEnabled())
    {
      log.debug("Run  arsend: " + StringOper.join(cmdArray, ' ')); // NOI18N
      log.debug("Environment: " + StringOper.join(env, ',')); // NOI18N
    }

    ProgressWatch progressWatch = new ProgressWatch(lol, total);
    ProcessHelper pr = new ProcessHelper(Runtime.getRuntime().exec(cmdArray, env), progressWatch);
    pr.waitFor();

    if(pr.getExitValue() != 0)
      throw new IOException(I.I("Errore nell'esecuzione di arsend."));
  }

  private static class ProgressWatch implements ProcessWatchListner
  {
    private long total;
    private LongOperListener lol;
    private HashMap<Integer, Integer> counters = new HashMap<>();

    public ProgressWatch(LongOperListener lol, long total)
    {
      this.lol = lol;
      this.total = total;
      lol.resetUI();
    }

    @Override
    public void notifyStdout(byte[] output, int offset, int length)
    {
      if(lol != null)
      {
        String s = new String(output, offset, length);

        int pos1, pos2, pid, counter;
        if((pos1 = s.indexOf("pid=")) != -1)
          if((pos2 = s.indexOf("counter=")) != -1)
          {
            pid = SU.parseInt(s.substring(pos1 + 4, pos2 - 1));
            counter = SU.parseInt(s.substring(pos2 + 7));

            if(pid != 0 && counter != 0)
            {
              counters.put(pid, counter);
              notifyCounters();
            }
          }
      }
    }

    @Override
    public void notifyStderr(byte[] output, int offset, int length)
    {
      log.error("arsend: " + new String(output, offset, length));
    }

    private void notifyCounters()
    {
      int part = 0;
      for(Map.Entry<Integer, Integer> entry : counters.entrySet())
        part += entry.getValue();

      lol.updateUI(part, total);
    }
  }
}
