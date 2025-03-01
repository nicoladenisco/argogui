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
import org.argogui.services.ArgoBaseService;
import java.sql.Connection;

/**
 * Implementazione vuota del servizio worklist.
 * Viene utilizzato dalla workstation per disabilitare
 * il servizio.
 *
 * @author Nicola De Nisco
 */
public class NullWorklistServer extends ArgoBaseService
   implements WorklistServer
{
  @Override
  public void argoInit()
  {
    // servizio inizializzato correttamente
    setInit(true);
  }

  @Override
  public void shutdown()
  {
  }

  @Override
  public void uptdateWorklist()
     throws Exception
  {
  }

  @Override
  public boolean isRunning()
     throws Exception
  {
    return false;
  }

  @Override
  public int stopWrkServer()
     throws Exception
  {
    return -1;
  }

  @Override
  public int startWrkServer()
     throws Exception
  {
    return -1;
  }

  @Override
  public void uptdateWorklist(int oid, Connection con)
     throws Exception
  {
  }

  @Override
  public void updateWorklist(InfInAccettazioni acc, Connection con)
     throws Exception
  {

  }
}
