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

import org.rigel5.HtmlUtils;
import org.rigel5.glue.table.AlternateColorTableAppBase;
import org.rigel5.table.RigelColumnDescriptor;

/**
 * Tabella rigel a colori alternati.
 * Versione personalizzata per interfaccia argo (bootstrap).
 *
 * @author Nicola De Nisco
 */
public class AlternateColorTableArgo extends AlternateColorTableAppBase
{
  /**
   * La versione argo non introduce classi di riga.
   * @param row
   * @return
   */
  @Override
  public String doRowStatement(int row)
  {
    // ritorna solo TR (senza agginugere altre classi)
    return "TR";
  }

  /**
   * Produce l'header della tabella
   */
  @Override
  public void doHeader()
     throws Exception
  {
    html.append("<THEAD>\r\n<TR>"
       + preHeader());

    for(int i = 0; i < tableModel.getColumnCount(); i++)
    {
      html.append(doCellHeader(i));
    }

    html.append(postHeader()
       + "</TR>\r\n"
       + "</THEAD>");
  }

  @Override
  protected String cellBegin(int row, int col)
     throws Exception
  {
    String align = doAlign(row, col);
    String color = doColor(row, col);
    String style = doStyle(row, col);

    if(row == -1)
    {
      // header della tabella
      if(nosize)
        return "<TH "
           + align + " " + color + " " + style + ">";
      else
        return "<TH WIDTH=\"" + normWidth[col] + "%\""
           + align + " " + color + " " + style + ">";
    }

    // corpo della tabella
    if(nosize)
      return "<" + colStatement + " "
         + align + " " + color + " " + style + ">";
    else
      return "<" + colStatement + " WIDTH=\"" + normWidth[col] + "%\""
         + align + " " + color + " " + style + ">";
  }

  @Override
  protected String cellEnd(int row, int col)
     throws Exception
  {
    if(row == -1)
      return "</TH>\r\n";
    else
      return "</TD>\r\n";
  }

  @Override
  public synchronized void doRows(int rStart, int numRec)
     throws Exception
  {
    html.append("<TBODY>\n");
    super.doRows(rStart, numRec);
    html.append("</TBODY>\n");
  }

  /**
   * Ritorna il testo di una colonna header.
   * A differenza della versione base supporta
   * il link per effettuare l'ordinamento cliccando
   * sul testo del nome colonna.
   * */
  @Override
  public String doFormCellHeader(int row, int col)
     throws Exception
  {
    String colText = getColumnCaption(col);

    RigelColumnDescriptor cd;
    if((cd = getCD(col)) == null)
      return colText;

    if(cd.getFiltroSort() > 0 && cd.getFiltroSort() < 1000)
      colText = "[\u2191]" + colText;
    if(cd.getFiltroSort() >= 1000 && cd.getFiltroSort() < 2000)
      colText = "[\u2193]" + colText;

    return cd.isEscludiRicerca() ? colText
              : HtmlUtils.makeHrefJScript("SimpleSort('" + (col + 1) + "')",
          colText);
  }
}
