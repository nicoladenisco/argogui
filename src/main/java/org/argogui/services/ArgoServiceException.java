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
package org.argogui.services;

/**
 * <p>Title: Newstar</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Italsystems s.r.l.</p>
 * @author Nicola De Nisco
 * @version 1.0
 */
public class ArgoServiceException extends Exception
{
  public ArgoServiceException(String msg)
  {
    super(msg);
  }

  public ArgoServiceException(String message, Throwable cause)
  {
    super(message, cause);
  }

  @Override
  public String getMessage()
  {
    return getCause() == null ? super.getMessage()
           : super.getMessage() + " " + getCause().getMessage();
  }
}
