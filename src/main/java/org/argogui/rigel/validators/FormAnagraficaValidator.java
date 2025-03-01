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

import org.argogui.om.InfInAnagrafiche;
import org.argogui.utils.I;
import org.argogui.utils.SU;
import java.sql.Connection;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.jdom2.Element;
import org.rigel5.RigelI18nInterface;
import org.rigel5.glue.validators.PostParseValidator;
import org.rigel5.table.RigelTableModel;
import org.rigel5.table.html.hEditTable;
import org.sirio6.ErrorMessageException;
import org.sirio6.utils.CalcolaCodiceFiscale;

/**
 * Validatore di input anagrafica.
 *
 * @author Nicola De Nisco
 */
public class FormAnagraficaValidator implements PostParseValidator
{
  private boolean autoCalcCF = true;
  private boolean maiuscolo = true;

  @Override
  public void init(Element eleXML)
     throws Exception
  {
    autoCalcCF = SU.checkTrueFalse(eleXML.getAttributeValue("autoCalcCF"), autoCalcCF);
    maiuscolo = SU.checkTrueFalse(eleXML.getAttributeValue("maiuscolo"), maiuscolo);
  }

  @Override
  public boolean validate(Object obj, RigelTableModel tableModel, hEditTable table,
     int row, HttpSession session, Map param, RigelI18nInterface i18n, Connection dbCon,
     Map custom)
     throws Exception
  {
    InfInAnagrafiche ana = (InfInAnagrafiche) obj;

    if(maiuscolo)
    {
      ana.setNome(SU.okStr(ana.getNome()).toUpperCase());
      ana.setCognome(SU.okStr(ana.getCognome()).toUpperCase());
      ana.setCodiceFiscale(SU.okStr(ana.getCodiceFiscale()).toUpperCase());
      ana.setCodiceSanitario(SU.okStr(ana.getCodiceSanitario()).toUpperCase());
    }

    if(ana.getIdComuneNascita() == 0)
      throw new ErrorMessageException(i18n.msg("Inserire il comune di nascita."));

    if(ana.getDataNascita() == null)
      throw new ErrorMessageException(i18n.msg("Inserire la data di nascita."));

    if(ana.getDataNascita().after(new Date()))
      throw new ErrorMessageException(i18n.msg("Inserire correttamente la data di nascita; non può essere nel futuro."));

    String codFis = SU.okStrNull(ana.getCodiceFiscale());
    if(codFis == null && !autoCalcCF)
      throw new ErrorMessageException(i18n.msg("Il campo codice fiscale è obbligatorio."));

    if(codFis == null || codFis.equals("0"))
    {
      try
      {
        codFis = CalcolaCodiceFiscale.calcolaCf(ana.getCognome(), ana.getNome(), ana.getSesso(), ana.getDataNascita(),
           ana.getAnaComuni().getBelfiore());
        ana.setCodiceFiscale(codFis);
      }
      catch(Exception ex)
      {
        throw new ErrorMessageException(i18n.msg("Impossibile calcolare il codice fiscale: %s.", ex.getMessage()));
      }
    }

    if(!SU.isOkStr(ana.getCodiceAlternativo()))
      ana.setCodiceAlternativo(codFis);
    if(!SU.isOkStr(ana.getCodiceSanitario()))
      ana.setCodiceSanitario(codFis);

    return true;
  }
}
