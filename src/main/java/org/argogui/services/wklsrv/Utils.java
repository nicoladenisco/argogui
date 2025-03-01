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

import org.argogui.Costanti;
import org.argogui.om.RisWorkrule;
import org.argogui.om.RisWorkrulePeer;
import org.argogui.utils.SU;
import java.util.*;
import org.apache.fulcrum.cache.CachedObject;
import org.apache.fulcrum.cache.ObjectExpiredException;
import org.rigel5.db.torque.CriteriaRigel;
import org.sirio6.services.cache.CACHE;

/**
 * Utilita per il server delle worklist.
 *
 * @author Nicola De Nisco
 */
public class Utils
{
  private static final String RULE_KEY = "Utils:RULE_KEY";
  private static final long RULE_KEY_EXPIRE = 5 * Costanti.ONE_MINUTE_MILLIS;

  public static List<RisWorkrule> getAllRules()
     throws Exception
  {
    try
    {
      return (List) CACHE.getObject(RULE_KEY).getContents();
    }
    catch(ObjectExpiredException e)
    {
      CriteriaRigel c = new CriteriaRigel(RisWorkrulePeer.TABLE_NAME);
      c.addAscendingOrderByColumn(RisWorkrulePeer.WORKRULE_ID);
      List<RisWorkrule> rw = RisWorkrulePeer.doSelect(c);
      CachedObject co = new CachedObject(rw, RULE_KEY_EXPIRE);
      CACHE.addObject(RULE_KEY, co);
      return rw;
    }
  }

  public static List<RisWorkrule> getRules(String schedStationName)
     throws Exception
  {
    ArrayList<RisWorkrule> rv = new ArrayList<RisWorkrule>();
    List<RisWorkrule> allRules = getAllRules();

    for(RisWorkrule wr : allRules)
    {
      if(SU.isEquNocase(schedStationName, wr.getSchedStationName())
         || SU.isEquNocase("0", wr.getSchedStationName()))
        rv.add(wr);
    }

    return rv;
  }
}
