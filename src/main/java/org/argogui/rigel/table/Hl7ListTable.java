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
package org.argogui.rigel.table;

import org.argogui.utils.SU;
import org.rigel5.table.RigelTableModel;

/**
 * Tabella HTML specializzata per messaggi HL7.
 * Limita il contenuto del messaggio al solo segmento MSH.
 *
 * @author Nicola De Nisco
 */
public class Hl7ListTable extends AlternateColorTableMaint
{
  private int idxMessaggio = -1, idxRisultato = -1;

  @Override
  public synchronized void doRows(int rStart, int numRec)
     throws Exception
  {
    RigelTableModel rtm = getTM();
    idxMessaggio = rtm.findColumn("MESSAGGIO"); // NOI18N
    idxRisultato = rtm.findColumn("R."); // NOI18N
    super.doRows(rStart, numRec);
  }

  @Override
  public String doCellHtml(int row, int col, String cellText)
     throws Exception
  {
    if(col == idxMessaggio)
    {
      String val = SU.okStrNull(cellText);
      if(val == null)
        return "&nbsp;"; // NOI18N

      int pos = val.indexOf('\r');
      return pos == -1 ? val : val.substring(0, pos);
    }

    if(col == idxRisultato)
    {
      int ris = SU.parseInt(cellText);
      return ris > 0 ? "KO" : "OK"; // NOI18N
    }

    return super.doCellHtml(row, col, cellText);
  }
}
