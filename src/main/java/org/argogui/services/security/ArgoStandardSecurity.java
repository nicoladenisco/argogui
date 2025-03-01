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
package org.argogui.services.security;

import org.argogui.services.cache.UsersDataCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.om.security.User;
import org.sirio6.services.security.AbstractCoreSecurity;

/**
 * Servizio avanzato di gestione sicurezza.
 *
 * @author Nicola De Nisco
 */
public class ArgoStandardSecurity extends AbstractCoreSecurity
{
  /** Logging */
  private static Log log = LogFactory.getLog(ArgoStandardSecurity.class);

  @Override
  public User getUser(int idUser)
  {
    try
    {
      return (User) UsersDataCache.getInstance().getTurbineUser(idUser);
    }
    catch(Exception ex)
    {
      log.error("", ex);
      throw new RuntimeException(ex);
    }
  }
}
