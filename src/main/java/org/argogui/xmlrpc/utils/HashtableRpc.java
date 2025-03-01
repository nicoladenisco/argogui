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

import java.util.*;
import org.commonlib5.utils.DateTime;
import org.commonlib5.utils.StringOper;

/**
 * Hashtable con controllo sui valori inseriti.
 * I valori null non vengono inseriti e provocano
 * una rimozione della chiave.
 * Tutti i tipi semplici vengono convertiti in stringa,
 * compresi i valori Date (formattati ISO).
 * Map sono inserite sempre come hashtable.
 * List sono inserite sempre come vector.
 * Set sono inseriti sempre come vector.
 *
 * @author Nicola De Nisco
 */
public class HashtableRpc extends Hashtable<String, Object>
{
  @Override
  public synchronized Object put(String key, Object value)
  {
    if(value == null)
      return remove(key);

    if(value instanceof String)
      value = StringOper.okStrNull(value);

    else if(value instanceof java.sql.Date)
      value = DateTime.formatIso((java.sql.Date) value, "");

    else if(value instanceof java.sql.Timestamp)
      value = DateTime.formatIsoFull((java.sql.Timestamp) value, "");

    else if(value instanceof java.util.Date)
      value = DateTime.formatIsoFull((java.util.Date) value, "");

    if(value == null)
      return remove(key);

    return super.put(key, value);
  }

  public synchronized Object put(String key, int value)
  {
    return super.put(key, Integer.toString(value));
  }

  public synchronized Object put(String key, double value)
  {
    return super.put(key, Double.toString(value));
  }

  public synchronized Object put(String key, boolean value)
  {
    return super.put(key, value ? "1" : "0");
  }

  public synchronized Object put(String key, Map value)
  {
    if(value == null)
      return remove(key);

    if(value instanceof Hashtable)
      return super.put(key, value);

    return super.put(key, new Hashtable(value));
  }

  public synchronized Object put(String key, List value)
  {
    if(value == null)
      return remove(key);

    if(value instanceof Vector)
      return super.put(key, value);

    return super.put(key, new Vector(value));
  }

  public synchronized Object put(String key, Set value)
  {
    if(value == null)
      return remove(key);

    return super.put(key, new Vector(value));
  }
}
