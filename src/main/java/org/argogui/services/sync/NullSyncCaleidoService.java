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
package org.argogui.services.sync;

import org.argogui.services.ArgoBaseService;

/**
 * Servizio nullo di collegamento con Caleido.
 *
 * @author Nicola De Nisco
 */
public class NullSyncCaleidoService extends ArgoBaseService
   implements SyncCaleido
{
  @Override
  public void argoInit()
     throws Exception
  {
  }

  @Override
  public boolean isRunning()
  {
    return false;
  }

  @Override
  public boolean triggerSync()
     throws Exception
  {
    return false;
  }
}
