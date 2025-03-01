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

import java.io.File;
import org.sirio6.services.CoreServiceExtension;

/**
 * Interfaccia base di tutti i servizi di Newstar.
 * @author Nicola De Nisco
 */
public interface ArgoService extends CoreServiceExtension
{
  /**
   * Ritorna la path completa di un eseguibile
   * del toolkit DICOM utlizzando le path di setup.
   * @param pgm programma da localizzare
   * @return path completa
   */
  public String getDicomPgm(String pgm);

  /**
   * Ritorna la path completa di un eseguibile
   * del toolkit DICOM utlizzando le path di setup.
   * @param pgm programma da localizzare
   * @return oggetto File con path completa
   */
  public File getDicomPgmFile(String pgm);

  /**
   * Ritorna la path completa di un eseguibile
   * di servizio di PostgreSQL.
   * @param pgm programma da localizzare
   * @return path completa
   */
  public String getPgsqlPgm(String pgm);

  /**
   * Ritorna la path completa di un eseguibile
   * di servizio di PostgreSQL.
   * @param pgm programma da localizzare
   * @return oggetto File con path completa
   */
  public File getPgsqlPgmFile(String pgm);

  /**
   * Ritorna un file ubicato nella directory
   * principale di store dei grafici.
   * (/var/argo/grafici per UNIX).
   * @param subFile nome del file SENZA path
   * @return collocazione completa nel file system
   */
  public File getWorkGraphFile(String subFile);
}
