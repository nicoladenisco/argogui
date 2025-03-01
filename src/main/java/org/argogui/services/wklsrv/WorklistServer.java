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

import org.argogui.om.InfInAccettazioni;
import java.sql.Connection;
import org.apache.turbine.services.Service;

/**
 * Servizio di worklist server per apparecchiature DICOM.
 * @author Nicola De Nisco
 */
public interface WorklistServer extends Service
{
  public static final String SERVICE_NAME = "WorklistServer";
  //
  public static final String DEFAULT_AETITLE = "ARGO";
  public static final String LOCK_FILE = "lockfile";

  public void uptdateWorklist()
     throws Exception;

  public void uptdateWorklist(int oid, Connection con)
     throws Exception;

  public void updateWorklist(InfInAccettazioni acc, Connection con)
     throws Exception;

  public int startWrkServer()
     throws Exception;

  public int stopWrkServer()
     throws Exception;

  public boolean isRunning()
     throws Exception;
}
