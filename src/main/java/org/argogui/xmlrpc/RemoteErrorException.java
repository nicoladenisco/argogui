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
package org.argogui.xmlrpc;

/**
 * Eccezione sollevata quando l'errore avviene nel server
 * e viene segnalato attraverso la restitituzione di un
 * oggetto XmlRpcException al client.
 *
 * @author Nicola De Nisco
 */
public class RemoteErrorException extends Exception
{
  public RemoteErrorException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public RemoteErrorException(String message)
  {
    super(message);
  }
}
