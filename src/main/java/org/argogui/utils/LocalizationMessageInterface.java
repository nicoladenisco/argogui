/*
 *  LocalizationMessageInterface.java
 *  Creato il 24-giu-2015, 19.04.04
 *
 *  Copyright (C) 2015 Informatica Medica s.r.l.
 *
 *  Questo software è proprietà di Informatica Medica s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  Informatica Medica s.r.l.
 *  Viale dei Tigli, 19
 *  Casalnuovo di Napoli (NA)
 */
package org.argogui.utils;

/**
 * Interfaccia di un localizzatore di messaggi.
 *
 * @author Nicola De Nisco
 */
public interface LocalizationMessageInterface
{
  /**
   * Ritorna messaggio localizzato.
   * Usa il messaggio origine come chiave
   * per cercare il messaggio nella traduzione attiva.
   * @param defaultMessage messaggio chiave
   * @return corrispondente localizzato o il messaggio chiave se non trovato
   */
  public String resolve(String defaultMessage);
}
