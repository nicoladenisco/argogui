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

import java.sql.Connection;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.jdom2.Element;
import org.rigel5.glue.validators.PostParseValidator;
import org.rigel5.table.RigelTableModel;
import org.rigel5.table.html.hEditTable;
import org.argogui.om.TurbineUser;
import org.rigel5.RigelI18nInterface;

/**
 * Validatore del salvataggio utenti.
 *
 * @author Nicola De Nisco
 */
public class SaveUserValidator implements PostParseValidator
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
    Date now = new Date();
    TurbineUser tu = (TurbineUser) obj;
    tu.setModifiedDate(now);
    if(tu.getCreateDate() == null)
      tu.setCreateDate(now);
    return true;
  }
}
