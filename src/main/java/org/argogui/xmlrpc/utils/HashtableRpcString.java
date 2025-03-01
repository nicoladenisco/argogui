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
import org.commonlib5.utils.DateTime;
import org.commonlib5.utils.StringOper;

/**
 * Hashtable con controllo sui valori inseriti.
 *
 * @author Nicola De Nisco
 */
public class HashtableRpcString extends Hashtable<String, String>
{
  public HashtableRpcString()
  {
    super(64);
  }

  @Override
  public synchronized String put(String key, String value)
  {
    if((value = StringOper.okStrNull(value)) == null)
      return remove(key);

    return super.put(key, value);
  }

  public synchronized String put(String key, int value)
  {
    return super.put(key, Integer.toString(value));
  }

  public synchronized String put(String key, double value)
  {
    return super.put(key, Double.toString(value));
  }

  public synchronized String put(String key, boolean value)
  {
    return super.put(key, value ? "1" : "0");
  }

  public synchronized String put(String key, Date value)
     throws Exception
  {
    return super.put(key, DateTime.formatIsoFull(value));
  }
}
