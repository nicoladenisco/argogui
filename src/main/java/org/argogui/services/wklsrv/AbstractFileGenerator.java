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

import org.argogui.om.RisWorklist;
import java.io.File;
import org.apache.commons.configuration2.Configuration;

/**
 * Definizione astratta di un generatore di worklist.
 *
 * @author Alberto Troiano
 */
public interface AbstractFileGenerator
{
  void init(Configuration cfg);

  void shutdown()
     throws Exception;

  public void write(RisWorklist w, File toWrite, String modalita, String aetitle)
     throws Exception;
}
