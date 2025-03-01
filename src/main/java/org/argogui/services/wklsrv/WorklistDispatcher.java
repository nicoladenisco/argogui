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

import org.argogui.om.RisWorklist;
import org.argogui.om.RisWorkrule;
import org.argogui.utils.SU;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generatore delle worklist.
 *
 * @author Alberto Troiano
 */
public class WorklistDispatcher
{
  protected File dirWrite = null;
  protected AbstractFileGenerator fgen = null;

  public void dispatch(File dirWrite, List<RisWorklist> vWrk, AbstractFileGenerator fgen)
     throws Exception
  {
    // scrive il dato della worklist
    Iterator itList = vWrk.iterator();
    while(itList.hasNext())
    {
      RisWorklist wkl = (RisWorklist) itList.next();
      dispatch(dirWrite, wkl, fgen);
    }
  }

  public synchronized void dispatch(File dirWrite, RisWorklist wklOrigin, AbstractFileGenerator fgen)
     throws Exception
  {
    this.dirWrite = dirWrite;
    this.fgen = fgen;

    RisWorklist wkl = wklOrigin.copy();

    String aetitle = SU.okStr(wkl.getSchedStationAeTitle());
    String modalita = SU.okStr(wkl.getModality());
    String fileToWritePrep = SU.CvtFILEstring(
       SU.okStr(aetitle)
       + (wkl.getAccessionNumber().trim())
       + ((wkl.getReqProcId() != null) ? wkl.getReqProcId().trim() : "")
       + ((wkl.getSchedProcStepId() != null) ? wkl.getSchedProcStepId().trim() : ""));

    // se la modalita o l'aet non sono presenti allora vanno completati con le
    // informazioni presenti nella workrule
    if((aetitle.length() == 0) || (modalita.length() == 0))
    {
      // non c'e' aetitle: cerca nella tabella regole
      List<RisWorkrule> rules = Utils.getRules(wkl.getModality());
      Iterator itRule = rules.iterator();
      while(itRule.hasNext())
      {
        RisWorkrule wr = (RisWorkrule) itRule.next();
        aetitle = SU.okStr(wr.getSchedStationAeTitle());
        modalita = SU.okStr(wr.getModality());

        // verifica per filtro con espressione regolare
        String filter = SU.okStrNull(wr.getFilter());
        if(filter != null)
        {
          String descrizione = SU.okStr(wklOrigin.getReqProcDesc());
          Pattern p = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
          Matcher m = p.matcher(descrizione);
          if(!m.find())
            continue;
        }

        if(aetitle.length() != 0)
        {
          wkl.setSchedStationAeTitle(aetitle);
          wkl.setModality(modalita);

          String fileToWrite = aetitle + fileToWritePrep + ".wl";
          File toWrite = new File(dirWrite, fileToWrite);

          /** se il record  della tabella RisWorklist
           *  viene aggiornato o aggiunto viene creato il file.
           *  Nel caso in cui il record viene cancellato logicamente
           *  allora va eliminato anche il file dallo spool
           */
          if((wkl != null) && (wkl.getStatoRec() < 10))
            scrivi(wkl, toWrite, wr);
          else
            toWrite.delete();
        }
      }
    }
    else
    {
      File toWrite = new File(dirWrite, fileToWritePrep + ".wl");

      if((wkl != null) && (wkl.getStatoRec() < 10))
        scrivi(wkl, toWrite, modalita, aetitle);
      else
        toWrite.delete();
    }
  }

  protected void scrivi(RisWorklist w, File toWrite, RisWorkrule wr)
     throws Exception
  {
    String modalita = SU.isOkStr(w.getModality()) ? w.getModality() : wr.getModality();
    String aetitle = SU.okStr(wr.getSchedStationAeTitle());
    scrivi(w, toWrite, modalita, aetitle);
  }

  /**
   * Crea il file DICOM per la RisWorklist
   *
   * @param w oggetto worklist
   * @param file  indica il file da creare
   *
   */
  protected void scrivi(RisWorklist w, File toWrite, String modalita, String aetitle)
     throws Exception
  {
    fgen.write(w, toWrite, modalita, aetitle);
  }
}
