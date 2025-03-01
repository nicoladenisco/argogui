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
import org.argogui.services.ArgoService;
import org.argogui.services.cache.ServerConfiguration;
import java.io.File;
import java.util.List;
import java.util.Set;
import org.commonlib5.utils.LongOperListener;

/**
 * Definizione del servizio di ricezione DICOM.
 * Titano attiva due server dicom: il primo produce CD paziente/esame
 * per tutte le associazioni ricevute da remoto.
 * Il secondo produce l'archivio legale per tutte le immagini inviate.
 *
 * @author Nicola De Nisco
 */
public interface DicomServer extends ArgoService
{
  public static final String SERVICE_NAME = "DicomServer";
  //
  public static final String ALLARM_DCMSRV = "DICOM-SERVER";
  //
  public static final String IMG_CD_NAME_COUNTER_KEY = "ICDN";
  //
  // estensione dei files dicom
  public static final String DCMEXT = ".dcm";
  // prefisso delle directory con gli esami
  public static final String DIRPREFIX = "dcd";
  public static final int MAX_PDU = 16384;
  public static final int DEFAULT_SOCKET_BUFFER_SIZE = 0;
  //
  public static final int TIPO_FILE_JPEG = 1;
  public static final int TIPO_FILE_DICOM = 2;
  public static final int TIPO_FILE_ENTRAMBI = 3;
  //
  public static final String AREA_THUMB = "jpeg";
  public static final String AREA_DICOM = "dicom";
  //
  public static final int TEST_TARGET_INVALID_ADDRESS = 1;
  public static final int TEST_TARGET_TCP_UNREACHABLE = 2;
  public static final int TEST_TARGET_TCP_OK = 3;
  public static final int TEST_TARGET_ECHOSCU_FAILURE = 4;
  public static final int TEST_TARGET_ECHOSCU_OK = 5;
  public static final int TEST_TARGET_ECHOSCU_ALL_OK = 6;

  /**
   * Ritorna vero se il server DICOM è stato attivato in configurazione.
   * @return vero se servizio attivo
   */
  public boolean isRunning();

  /**
   * Ricarica la configurazione del dicom server dalle tabelle di setup.
   * @throws Exception
   */
  public void ricaricaConfigurazione()
     throws Exception;

  /**
   * Recupera la configurazione del server DICOM di default.
   * @return descrittore di configurazione
   */
  public ServerConfiguration getServerConfiguration();

  /**
   * Recupera tutti i descrittori di storage attivi.
   * @return lista non modificabile
   */
  public List<StpStorage> getAllStorages();

  /**
   * Ritorna AETITLE del server DICOM.
   * @return aetitle
   */
  public String getCanonicalServerAETitle();

  /**
   * Segnala un file DICOM come rifiutato.
   * Generalmente la causa è dovuta ad una corruzione
   * del file o alla impossibilità di leggerlo.
   * Il server DICOM lo sposta in una apposita directory.
   * @param toReject
   */
  public void rejectDicomFile(File toReject);

  /**
   * Cancella un esame dallo storage.
   * @param accno esame da cancellare
   * @param aetitle aetitle utilizzato come origine comando (può essere null)
   * @throws java.lang.Exception
   */
  public void deleteDicomFiles(String accno, String aetitle)
     throws Exception;

  /**
   * Cancella un blocco di immagini dallo storage.
   * @param lsFiles lista dei files da cancellare
   * @param aetitle aetitle utilizzato come origine comando (può essere null)
   * @throws Exception
   */
  public void deleteDicomFiles(List<File> lsFiles, String aetitle)
     throws Exception;

  /**
   * Elimina il contenuto dello storage e reinizializza il gestore.
   * @throws Exception
   */
  public void cancellaStorage()
     throws Exception;

  /**
   * Ritorna i files associati ad un esame.
   * @param accno accession number dell'esame
   * @param tipo tipo di file (vedi TIPO_...)
   * @return lista di files nel db di argo
   * @throws Exception
   */
  public List<File> getDicomFiles(String accno, int tipo)
     throws Exception;

  /**
   * Ritorna i files associati ad un esame.
   * @param studyUID uid dell'esame
   * @param serieUID uid della serie (opzionale; null=tutte le immagini dell'esame)
   * @param tipo tipo di file (vedi TIPO_...)
   * @return lista di files nel db di argo
   * @throws Exception
   */
  public List<File> getDicomFiles(String studyUID, String serieUID, int tipo)
     throws Exception;

  /**
   * Ritorna i files associati ad un esame.
   * Per ogni files viene ritornata la path realtiva
   * alla directory che contiene il db di argo
   * (solitamente /var/argo/dicom).
   * @param accno accession number dell'esame
   * @param tipo tipo di file (vedi TIPO_...)
   * @return lista di path relative nel db di argo
   * @throws Exception
   */
  public List<String> getDicomRelativePaths(String accno, int tipo)
     throws Exception;

  /**
   * Esegue una query al database locale.
   * @param queryParams parametri della query
   * @return lista di risultati
   * @throws Exception
   */
  public List<StudyResultBean> queryStudyLocalNode(Set<Attribute> queryParams)
     throws Exception;

  /**
   * Tag di default per le ricerche e visualizzazioni.
   * @return elenco di tag possibili in ricerca e da visualizzare
   * @throws Exception
   */
  public List<AttributeTag> getDefaultTags()
     throws Exception;

  /**
   * Popola un esame con tutti i suoi dati.
   * @param studyUID UID dell'esame se null la legge da bean
   * @param aetitle aetitle dell'area di storage (null=default)
   * @param bean bean da popolare
   * @return il bean popolato
   * @throws Exception
   */
  public StudyResultBean populateStudy(String studyUID, String aetitle, StudyResultBean bean)
     throws Exception;

  /**
   * Esegue un ping verso la destinazione indicata.
   * @param idTarget id del nodo dicom da testare
   * @return messaggio con l'esito del ping
   * @throws Exception
   */
  public int pingTarget(int idTarget)
     throws Exception;

  /**
   * Esegue un echoscu verso la destinazione indicata.
   * @param idTarget id del nodo dicom da testare
   * @param handshake restituisce handshake del comando
   * @return messaggio con l'esito dell'echoscu
   * @throws Exception
   */
  public int echoscuTarget(int idTarget, StringBuilder handshake)
     throws Exception;

  /**
   * Descrive le connessioni TCP/IP attive (DICOM).
   * @param sb restituisce una descrizione formattata delle connessioni
   * @return 0=indefinito 1=nessuna risposta 2=ok connessioni
   * @throws Exception
   */
  public int descriviConnessioni(StringBuilder sb)
     throws Exception;

  /**
   * Modifica il database e i files di un esame.
   * @param studyUID UID dell'esame
   * @param aetitle aetitle dell'area di storage (null=default)
   * @param lsModify lista attributi da modificare
   * @throws Exception
   */
  public void modificaFilesDatabase(String studyUID, String aetitle, AttributeList lsModify)
     throws Exception;

  /**
   * Invia un pacchetto di files ad un nodo DICOM.
   * @param idTarget id del nodo dicom destinazione
   * @param callingAetitle l'AETITLE con cui ci presenteremo al nodo DICOM
   * @param arFiles array di files da inviare
   * @param lol per lo stato di avanzamento operazioni
   * @throws Exception
   */
  public void storescu(int idTarget, String callingAetitle, List<File> arFiles, LongOperListener lol)
     throws Exception;
}
