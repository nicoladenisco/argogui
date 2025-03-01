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

import org.argogui.services.ArgoService;

/**
 * Servizio di sincronizzazione attiva con server Caleido.
 *
 * @author Nicola De Nisco
 */
public interface SyncCaleido extends ArgoService
{
  public static final String SERVICE_NAME = "syncCaleido";

  /**
   * Ritorna stato di attività del servizio.
   * @return vero se attivo
   */
  public boolean isRunning();

  /**
   * Avvia una sessione di sincronizzazione asincrona.
   * @return vero se la sincronizzazione è stata avviata
   * @throws Exception
   */
  public boolean triggerSync()
     throws Exception;
}
