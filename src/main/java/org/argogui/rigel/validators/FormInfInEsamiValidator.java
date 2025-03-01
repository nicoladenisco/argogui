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
package org.argogui.rigel.validators;

import org.rigel5.glue.validators.PostParseValidator;
import java.sql.Connection;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.jdom2.Element;
import org.rigel5.table.RigelTableModel;
import org.rigel5.table.html.hEditTable;
import org.argogui.om.InfInEsami;
import org.argogui.om.InfInEsamiPeer;
import org.argogui.om.StpEsami;
import org.argogui.om.StpEsamiPeer;
import org.argogui.utils.SU;
import org.rigel5.RigelI18nInterface;

/**
 * Validatore di salvataggio per form FormInfInEsami.
 * Aggiunge tutti i parametri dell'esame.
 * @author Nicola De Nisco
 */
public class FormInfInEsamiValidator implements PostParseValidator
{
  @Override
  public void init(Element eleXML)
     throws Exception
  {
  }

  @Override
  public boolean validate(Object obj, RigelTableModel tableModel, hEditTable table,
     int row, HttpSession session, Map param, RigelI18nInterface i18n, Connection dbCon,
     Map custom)
     throws Exception
  {
    int idEsa;
    InfInEsami inEsa = (InfInEsami) obj;

    if((idEsa = inEsa.getInEsamiId()) == 0 || inEsa.isNew() || checkCange(inEsa))
    {
      StpEsami stpEsa = null;
      if((stpEsa = StpEsamiPeer.retrieveByAlternateKey1Quiet(inEsa.getCodice(), dbCon)) != null)
      {
        // copia la descrizione se vuota
        if(!SU.isOkStr(inEsa.getDescrizione()) || SU.isEqu("0", inEsa.getDescrizione()))
          inEsa.setDescrizione(stpEsa.getDescrizione());
      }
    }

    return true;
  }

  private boolean checkCange(InfInEsami inEsa)
     throws Exception
  {
    InfInEsami oldEsa = InfInEsamiPeer.retrieveByPK(inEsa.getInEsamiId());
    boolean isChanged = !SU.isEqu(inEsa.getCodice(), oldEsa.getCodice());

    if(isChanged && SU.isEqu(oldEsa.getDescrizione(), inEsa.getDescrizione()))
      inEsa.setDescrizione(null);

    return isChanged;
  }
}
