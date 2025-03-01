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
package org.argogui.services.sync;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.util.Transaction;
import org.commonlib5.utils.SimpleTimer;
import org.argogui.Costanti;
import org.argogui.om.StpEsami;
import org.argogui.om.StpEsamiPeer;
import org.argogui.utils.I;
import org.argogui.services.ArgoBaseService;
import org.argogui.utils.SU;
import org.argogui.xmlrpc.client.SyncCaleidoXmlRpcClient;

/**
 * Implementazione del servizio di sincronizzazione con Caleido.
 *
 * @author Nicola De Nisco
 */
public class ArgoSyncCaleidoService extends ArgoBaseService
   implements SyncCaleido
{
  /** Logging */
  private static Log log = LogFactory.getLog(ArgoSyncCaleidoService.class);
  //
  protected String caleidoHost;
  protected int caleidoPort;
  protected Thread thRun;
  protected boolean mustExit;
  protected long syncDelay;
  protected SimpleTimer tSync = new SimpleTimer();
  private boolean syncSiti, syncReparti, syncEsami, syncTrigger;
  private String filtroBranca;

  @Override
  public void argoInit()
     throws Exception
  {
    Configuration cfg = getConfiguration();

    caleidoHost = cfg.getString("host", null);
    caleidoPort = cfg.getInt("port", 0);
    syncDelay = cfg.getLong("syncDelayHours", 4) * Costanti.ONE_HOUR_MILLIS;

    syncSiti = cfg.getBoolean("sync.siti", false);
    syncReparti = cfg.getBoolean("sync.reparti", false);
    syncEsami = cfg.getBoolean("sync.esami", false);

    filtroBranca = cfg.getString("sync.filtroBranca", "");

    // se non configurato il servizio non si avvia
    if(caleidoHost == null || caleidoPort == 0)
      return;

    thRun = new Thread()
    {
      @Override
      public void run()
      {
        runThread();
      }
    };

    thRun.setName("CaleidoSyncService");
    thRun.setDaemon(true);
    thRun.start();
  }

  @Override
  public void shutdown()
  {
    mustExit = true;
    super.shutdown();
  }

  @Override
  public boolean isRunning()
  {
    return thRun != null && thRun.isAlive();
  }

  @Override
  public boolean triggerSync()
     throws Exception
  {
    syncTrigger = true;
    return true;
  }

  protected void runThread()
  {
    tSync.reset();
    for(mustExit = false; !mustExit;)
    {
      try
      {
        Thread.sleep(1000);
        if(tSync.isElapsed(syncDelay) || syncTrigger)
          runSync();
      }
      catch(Exception e)
      {
        log.error(I.I("Errore nel thread di sincronizzazione:"), e);
      }
    }
  }

  protected synchronized void runSync()
     throws Exception
  {
    Connection con = null;
    try
    {
      con = Transaction.begin();
      runSync(con);
      Transaction.commit(con);
    }
    catch(Exception ex)
    {
      Transaction.safeRollback(con);
      throw ex;
    }
  }

  protected synchronized void runSync(Connection con)
     throws Exception
  {
    syncTrigger = false;
    tSync.reset();

    if(syncEsami)
      runSyncEsami(con);

    tSync.reset();
  }

  protected void runSyncEsami(Connection con)
     throws Exception
  {
    SyncCaleidoXmlRpcClient client = new SyncCaleidoXmlRpcClient(caleidoHost, caleidoPort);
    Vector values = client.getPrestazioni(filtroBranca);

    for(Iterator it = values.iterator(); it.hasNext();)
    {
      Hashtable htRec = (Hashtable) it.next();

      String codice = SU.okStrNull(htRec.get("CODICE"));
      if(codice == null)
        continue;

      StpEsami e = StpEsamiPeer.retrieveByAlternateKey1Quiet(codice, con);
      if(e == null)
      {
        e = new StpEsami();
        e.setCreazione(new Date());
        e.setCodice(codice);
        e.setStatoRec(1);
      }

      e.setDescrizione(SU.okStr(htRec.get("DESCRIZIONE")));
      if(e.isModified())
        e.save(con);


//      e.setIDPRESTAZIONI(htRec.get("IDPRESTAZIONI"));
//      e.setCODBRANCA(htRec.get("CODBRANCA"));
//      e.setDESBRANCA(htRec.get("DESBRANCA"));
//      e.setCODSETTORE(htRec.get("CODSETTORE"));
//      e.setDESSETTORE(htRec.get("DESSETTORE"));
//      e.setCODICEMNEMONICO(htRec.get("CODICEMNEMONICO"));
//      e.setCODICEMINISTERIALE(htRec.get("CODICEMINISTERIALE"));
//      e.setCODICEREGIONALE(htRec.get("CODICEREGIONALE"));
//      e.setCODISSUER(htRec.get("CODISSUER"));
//      e.setPROCEDURA(htRec.get("PROCEDURA"));
//      e.setALIAS(htRec.get("ALIAS"));
//      e.setALIASFATTURA(htRec.get("ALIASFATTURA"));
//      e.setPROTETTO(htRec.get("PROTETTO"));
//      e.setATTIVO(htRec.get("ATTIVO"));
//      e.setINFO(htRec.get("INFO"));
//      e.setCATEGORIA(htRec.get("CATEGORIA"));
//      e.setCODICEIVA(htRec.get("CODICEIVA"));

    }
  }
}
