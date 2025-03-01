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
package org.argogui.services.wklsrv;

import java.io.File;
import org.commonlib5.exec.ProcessHelper;

/**
 * Classe di servizio per mantenere i dati significativi
 * dei server worklist in esecuzione.
 * @author Nicola De Nisco
 */
public class ServerInfo
{
  public int           id = 0;
  public File          spool = null;
  public String        cmd = null;
  public ProcessHelper pro = null;

  public boolean isRunning()
  {
    return pro != null && pro.isRunning();
  }
}
