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
import java.util.List;
import org.sirio6.services.InfoSetupInterface;

/**
 * Accesso alle funzioni di base dei servizi
 * @author Nicola De Nisco
 */
public class SERVICE
{
  private static ArgoBaseService mb = null;

  public static ArgoBaseService getService()
  {
    return mb;
  }

  protected static void setService(ArgoBaseService srv)
  {
    mb = srv;
  }

  public static void ASSERT(boolean test, String cause) throws Exception
  {
    getService().ASSERT(test, cause);
  }

  public static void ASSERT_FILE(File toTest)
     throws Exception
  {
    getService().ASSERT_FILE(toTest);
  }

  public static void ASSERT_DIR(File toTest)
     throws Exception
  {
    getService().ASSERT_DIR(toTest);
  }

  public static void ASSERT_DIR_WRITE(File toTest)
     throws Exception
  {
    getService().ASSERT_DIR_WRITE(toTest);
  }

  public static void TRACE(String mess)
  {
    getService().TRACE(mess);
  }

  public static void die(String causa) throws Exception
  {
    getService().die(causa);
  }

  public static String getCanonicalServerAddress()
  {
    return getService().getCanonicalServerAddress();
  }

  public static String getCanonicalServerName()
  {
    return getService().getCanonicalServerName();
  }

  public static File getConfCertFile(String subFile)
  {
    return getService().getConfCertFile(subFile);
  }

  public static File getConfMainFile(String subFile)
  {
    return getService().getConfMainFile(subFile);
  }

  public static File getConfReportFile(String subFile)
  {
    return getService().getConfReportFile(subFile);
  }

  public static File getConfSchemasFile(String subFile)
  {
    return getService().getConfSchemasFile(subFile);
  }

  public static File getConfSetupFile(String subFile)
  {
    return getService().getConfSetupFile(subFile);
  }

  public static File getConfXlsFile(String subFile)
  {
    return getService().getConfXlsFile(subFile);
  }

  public static String getPgsqlPgm(String pgm)
  {
    return getService().getPgsqlPgm(pgm);
  }

  public static File getPgsqlPgmFile(String pgm)
  {
    return getService().getPgsqlPgmFile(pgm);
  }

  public static String getRealPath(String path)
  {
    return getService().getRealPath(path);
  }

  public static String getServerUrlGeneric(String url)
  {
    return getService().getServerUrlGeneric(url);
  }

  public static String getServerUrlJSP(String url)
  {
    return getService().getServerUrlJSP(url);
  }

  public static String getTurbineContextPath()
  {
    return getService().getTurbineContextPath();
  }

  public static File getWorkCacheFile(String subFile)
  {
    return getService().getWorkCacheFile(subFile);
  }

  public static File getWorkDocsFile(String subFile)
  {
    return getService().getWorkDocsFile(subFile);
  }

  public static File getLogFile(String subFile)
  {
    return getService().getLogFile(subFile);
  }

  public static File getWorkMainFile(String subFile)
  {
    return getService().getWorkMainFile(subFile);
  }

  public static File getWorkGraphFile(String subFile)
  {
    return getService().getWorkGraphFile(subFile);
  }

  public static File getWorkSpoolFile(String subFile)
  {
    return getService().getWorkSpoolFile(subFile);
  }

  public static File getWorkTmpFile(String subFile)
  {
    return getService().getWorkTmpFile(subFile);
  }

  /**
   * Ritorna un file temporaneo per usi generici.
   * Il file viene creato nella directory /var/argo/tmp
   * ed è assicurato univoco.
   * Si autocancella alla chiusura dell'app server.
   * @return il file temoraneo
   * @throws Exception
   */
  public static File getTmpGeneric()
     throws Exception
  {
    File dirTmp = getWorkTmpFile(null);
    File rv = File.createTempFile("gen", ".tmp", dirTmp);
    rv.deleteOnExit();
    return rv;
  }

  public static List<InfoSetupInterface> getInfoSetup()
  {
    return getService().getInfoSetup();
  }

  public static int getAziendaID()
  {
    return getService().getAziendaId();
  }

  public static String getAziendaNome()
  {
    return getService().getAziendaNome();
  }

  public static File getConfWithAlternative(String nomeFile)
  {
    File rv = getWorkMainFile(nomeFile);
    if(rv.exists())
      return rv;

    return getConfMainFile(nomeFile);
  }
}
