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
package org.argogui.xmlrpc.utils;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import org.commonlib5.utils.DateTime;
import org.commonlib5.utils.StringOper;

/**
 * Tabella di hashing con funzioni di parser.
 *
 * @author Nicola De Nisco
 */
public class HashtableParser extends Hashtable
{
  public HashtableParser(Map t)
  {
    super(t);
  }

  public String getAsString(String key)
  {
    return StringOper.okStr(get(key));
  }

  public String getAsStringNull(String key)
  {
    return StringOper.okStrNull(get(key));
  }

  public int getAsInt(String key)
  {
    return StringOper.parse(get(key), 0);
  }

  public double getAsDouble(String key)
  {
    return StringOper.parse(get(key), 0.0);
  }

  public boolean getAsBoolean(String key)
  {
    return StringOper.checkTrueFalse(get(key), false);
  }

  public Date getAsDate(String key)
  {
    return DateTime.parseIsoFull(getAsString(key), null);
  }

  public String getAsString(String key, String defVal)
  {
    return StringOper.okStr(get(key), defVal);
}

  public int getAsInt(String key, int defVal)
  {
    return StringOper.parse(get(key), defVal);
  }

  public double getAsDouble(String key, double defVal)
  {
    return StringOper.parse(get(key), defVal);
  }

  public boolean getAsBoolean(String key, boolean defVal)
  {
    return StringOper.checkTrueFalse(get(key), defVal);
  }

  public Date getAsDate(String key, Date defVal)
  {
    return DateTime.parseIsoFull(getAsString(key), defVal);
  }
}
