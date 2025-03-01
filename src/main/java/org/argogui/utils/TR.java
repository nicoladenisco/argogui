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
package org.argogui.utils;

import java.util.HashMap;
import org.argogui.om.GenSetupOverride;
import org.argogui.om.GenSetupOverridePeer;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.Configuration;

import org.rigel5.db.torque.CriteriaRigel;

/**
 * Accesso alle variabili di setup generali dell'application server.
 *
 * @author Nicola De Nisco
 */
public class TR extends org.sirio6.utils.TR
{
  /**
   * Carica gli override delle properties da database.
   * @param cfg
   * @throws Exception
   */
  public static void loadOverride(Configuration cfg)
     throws Exception
  {
    CriteriaRigel cr = new CriteriaRigel(GenSetupOverridePeer.TABLE_NAME);
    cr.addAscendingOrderByColumn(GenSetupOverridePeer.NOME);
    List<GenSetupOverride> lp = GenSetupOverridePeer.doSelect(cr);
    Map<String, String> override = new HashMap<>(lp.size());
    lp.forEach((gs) -> override.put(gs.getNome(), gs.getValore()));
    org.sirio6.utils.TR.loadOverride(cfg, override);
  }
}
