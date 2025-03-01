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
package org.argogui;

/**
 * Semafori generali utilizzati dal programma Argo.
 *
 * @author Nicola De Nisco
 */
public class Semafori
{
  /**
   * Semaforo acquisito durante l'elaborazione di una accettazione.
   */
  public static final Object ElaborazioneAccettazione = new Object();

  /**
   * Semaforo acquisito durante manipolazione file conifigurazione argo.
   */
  public static final Object argolink = new Object();
}
