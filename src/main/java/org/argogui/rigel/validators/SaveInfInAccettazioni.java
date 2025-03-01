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

import org.argogui.om.InfInAccettazioni;
import org.argogui.om.InfInAnagrafiche;
import org.argogui.utils.SU;
import java.sql.Connection;
import java.util.*;
import javax.servlet.http.HttpSession;
import org.jdom2.Element;
import org.rigel5.RigelI18nInterface;
import org.rigel5.glue.validators.PostParseValidator;
import org.rigel5.table.RigelTableModel;
import org.rigel5.table.html.hEditTable;
import org.sirio6.services.contatori.AGC;

/**
 * Validatore per il salvataggio accettazione.
 *
 * @author Nicola De Nisco
 */
public class SaveInfInAccettazioni implements PostParseValidator
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
    InfInAccettazioni acc = (InfInAccettazioni) obj;

    if(acc.isNew() && acc.getIdAnagrafiche() != 0)
    {
      // copia valori dall'anagrafica
      InfInAnagrafiche ana = acc.getInfInAnagrafiche(dbCon);
      if(acc.getPeso() == 0)
        acc.setPeso(ana.getPeso());
      if(acc.getAltezza() == 0)
        acc.setAltezza(ana.getAltezza());
      if(acc.getDiuresi() == 0)
        acc.setDiuresi(ana.getDiuresi());
    }

    // generazione automatica codice accettazione
    if(!SU.isOkStr(acc.getCodice()))
    {
      String codice = AGC.generaCodice("ACC", 4, 6, dbCon);
      acc.setCodice(codice);
      acc.setAccessionNumber(codice);
    }

    // per default accession number dell'accettazione è uguale al codice
    if(!SU.isOkStr(acc.getAccessionNumber()))
      acc.setAccessionNumber(acc.getCodice());

    return true;
  }
}
