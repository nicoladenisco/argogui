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

import com.pixelmed.dicom.UIDGenerator;
import org.argogui.om.RisWorklist;
import org.argogui.om.RisWorklistPeer;
import java.io.*;
import java.util.*;
import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sirio6.utils.DT;

/**
 * Legge un file descrittivo di worklist e
 * salva nel db i relativi record nella tabella
 * PacsWorklist. Attraverso ScriviFile genera anche
 * i files di servizio per il server della worklist.
 *
 * @author
 */
public class AggiornaDB
{
  /** Logging */
  private static Log log = LogFactory.getLog(AggiornaDB.class);
  private UIDGenerator uidGen = new UIDGenerator();

  /**
   * Legge da un file una o piu entry di worklist
   * e ritorna un vettore di oggetti peer della tabella worklist.
   * @param toRead file da leggere
   * @param con connessione con db sql
   * @return vettore di oggetti peer della tabella worklist
   * @throws Exception
   */
  public List<RisWorklist> leggi(File toRead, Connection con)
     throws Exception
  {
    ArrayList<RisWorklist> wrkLists = new ArrayList<>();

    try (FileReader fin = new FileReader(toRead))
    {
      BufferedReader br = new BufferedReader(fin, 4096);
      String sin;
      int numLinea = 0;
      while((sin = br.readLine()) != null)
      {
        numLinea++;
        try
        {
          wrkLists.add(parseLinea(sin, con));
        }
        catch(Exception ex)
        {
          ex.printStackTrace();
          log.error("Errore sulla linea " + numLinea + ": " + ex.getMessage());
        }
      }
    }

    return wrkLists;
  }

  /**
   * Legge e interpreta una line del file CSV per creare il
   * corrispondente record nella tabella worklist.
   * @param s linea da leggere
   * @param con connessione sql
   * @return oggetto worklist (viene già salvato in parseLinea)
   * @throws Exception
   */
  private RisWorklist parseLinea(String s, Connection con)
     throws Exception
  {
    RisWorklist wk = null;

    StringTokenizer stk = new StringTokenizer(s, ";");

    String PatName = stk.nextToken();
    String PatId = stk.nextToken();
    String PatNascita = stk.nextToken();
    String PatSex = stk.nextToken();
    String AccessionNumber = stk.nextToken();
    String data = stk.nextToken();
    String ora = stk.nextToken();
    String medRif = stk.nextToken();
    String descEsa = stk.nextToken();
    String descStep = stk.nextToken();
    String amb = stk.nextToken();

    String idEsa = "";
    if(stk.hasMoreTokens())
    {
      idEsa = stk.nextToken();
    }

    String mod = "";
    if(stk.hasMoreTokens())
    {
      mod = stk.nextToken();
    }

    if((wk = RisWorklistPeer.getByAccno(AccessionNumber, con)) == null)
    {
      wk = new RisWorklist();
      wk.setCreazione(new java.util.Date());
    }

    wk.setPatName(PatName);
    wk.setPatId(PatId);
    wk.setPatNascita(DT.parseDateDicom(PatNascita, null));
    wk.setPatSex(PatSex);
    wk.setAccessionNumber(AccessionNumber);

    wk.setSchedProcStepStartDateTime(DT.parseDateTimeDicom(data, ora));
    wk.setReferencephysician(medRif);
    wk.setSchedProcStepDesc(descEsa);
    wk.setSchedStationName(amb);
    wk.setModality(mod);
    wk.setUltModif(new java.util.Date());
    wk.setStudyInstanceUid(uidGen.getAnotherNewUID());
    wk.save(con);

    return wk;
  }
}
