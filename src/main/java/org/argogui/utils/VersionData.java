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

import org.argogui.om.GenVersion;
import org.argogui.om.GenVersionPeer;
import java.util.*;
import org.rigel5.db.torque.CriteriaRigel;

/**
 * Dati sulle versioni dei componenti installati.
 * I dati ritornati sono letti una unica volta dal db:
 * si presuppone che le versioni non cambino fra un
 * riavvio e l'altro di Tomcat.
 *
 * @author Nicola De Nisco
 */
public class VersionData
{
  private static Vector versionData = null;

  /**
   * Ritorna una lista di oggetti GenVersion uno per ogni
   * componente con l'ultima versione aggiornata di
   * ogni componente.
   * @return
   * @throws Exception
   */
  public static List getVersionData()
     throws Exception
  {
    if(versionData == null)
      versionData = getVersioni();

    return versionData;
  }

  private static Vector getVersioni()
     throws Exception
  {
    Hashtable ht = new Hashtable();
    CriteriaRigel c = new CriteriaRigel();
    List lv = GenVersionPeer.doSelect(c);

    for(Iterator itVer = lv.iterator(); itVer.hasNext();)
    {
      GenVersion version = (GenVersion) itVer.next();

      String comp;
      if((comp = version.getComponente()) == null)
        continue;

      GenVersion prever = (GenVersion) ht.get(comp);

      if(prever == null)
      {
        ht.put(comp, version);
      }
      else
      {
        if(version.getMajor() > prever.getMajor())
        {
          ht.put(comp, version);
          continue;
        }
        if(version.getMajor() == prever.getMajor()
           && version.getMinor() > prever.getMinor())
        {
          ht.put(comp, version);
          continue;
        }
      }
    }

    Vector rv = new Vector();
    for(Enumeration eVer = ht.keys(); eVer.hasMoreElements();)
    {
      rv.add(ht.get(eVer.nextElement()));
    }

    return rv;
  }
}
