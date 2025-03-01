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
package org.argogui.services.cache;

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.cache.ObjectExpiredException;
import org.apache.fulcrum.cache.Refreshable;
import org.apache.fulcrum.cache.RefreshableCachedObject;
import org.apache.torque.criteria.Criteria;
import org.apache.turbine.om.security.User;
import org.commonlib5.utils.StringOper;
import org.argogui.om.*;
import org.argogui.utils.SU;
import org.rigel5.db.torque.CriteriaRigel;
import org.sirio6.services.cache.CACHE;
import org.sirio6.services.security.SEC;

/**
 * Fornitore di informazioni circa gli utenti del sistema.
 *
 * @author Nicola De Nisco
 * @version 1.0
 */
public class UsersDataCache implements Refreshable
{
  /** Logging */
  private static Log log = LogFactory.getLog(UsersDataCache.class);
  protected List<TurbineUser> vUsers = null;
  protected Hashtable<Integer, TurbineUser> hUsers = null;
  protected TurbineUser admin = null;

  protected UsersDataCache()
  {
    refresh();
  }

  /**
   * Ritorna l'utente amministratore di sistema.
   * @return oggetto TurbineUser
   */
  public synchronized TurbineUser getAdministrator()
  {
    try
    {
      if(admin == null)
        admin = TurbineUserPeer.retrieveByPK(0);
    }
    catch(Exception ex)
    {
    }
    return admin;
  }

  /**
   * Ritorna l'utente con l'ID richiesto.
   * @param idUser id dell'utente
   * @return oggetto TurbineUser oppure null se utente inesistente
   */
  public synchronized TurbineUser getUser(int idUser)
  {
    return idUser == 0 ? getAdministrator() : hUsers.get(idUser);
  }

  /**
   * Ritorna l'utente con l'ID richiesto.
   * @param idUser id dell'utente
   * @return oggetto User oppure null se utente inesistente
   */
  public synchronized User getTurbineUser(int idUser)
     throws Exception
  {
    TurbineUser tu = getUser(idUser);
    return tu == null ? null : SEC.getUser(tu.getName());
  }

  /**
   * Ritorna l'utente con il login name indicato.
   * @param loginName nome di login (per definizione è univoco)
   * @return oggetto TurbineUser oppure null se utente inesistente
   */
  public synchronized TurbineUser retriveByLoginName(String loginName)
  {
    if(SU.isEqu(loginName, "turbine"))
      return getAdministrator();

    for(TurbineUser u : vUsers)
    {
      if(StringOper.isEqu(loginName, u.getName()))
        return u;
    }

    return null;
  }

  /**
   * Iteratore di tutti gli utenti.
   * @return iteratore
   */
  public synchronized Iterator<TurbineUser> userIterator()
  {
    return vUsers == null ? null : vUsers.iterator();
  }

  /**
   * Vettore di tutti gli utenti.
   * @return vettore
   */
  public synchronized Vector<TurbineUser> getVUsers()
  {
    return new Vector(vUsers);
  }

  @Override
  public synchronized void refresh()
  {
    try
    {
      {
        CriteriaRigel cUser = new CriteriaRigel();
        cUser.add(TurbineUserPeer.USER_ID, 0, Criteria.GREATER_THAN);
        cUser.addAscendingOrderByColumn(TurbineUserPeer.USER_ID);
        vUsers = TurbineUserPeer.doSelect(cUser);

        hUsers = new Hashtable();
        for(TurbineUser us : vUsers)
        {
          hUsers.put(us.getEntityId(), us);
        }
      }
    }
    catch(Exception ex)
    {
      log.error("Errore caricando lista utenti:", ex);
    }
  }
  //
  /////////////////////////////////////////////////////////////////////////////////
  //
  public static final String USERS_DATA_KEY = "USERS_DATA_KEY";
  public static final int REFRESH_TIME = 5 * 60 * 1000; // 5 minuti
  private static final Object semaforo = new Object();

  public static UsersDataCache getInstance()
     throws Exception
  {
    synchronized(semaforo)
    {
      try
      {
        return (UsersDataCache) (CACHE.getObject(USERS_DATA_KEY).getContents());
      }
      catch(ObjectExpiredException ex)
      {
      }

      UsersDataCache ud = new UsersDataCache();
      RefreshableCachedObject rco = new RefreshableCachedObject(ud, REFRESH_TIME);
      CACHE.addObject(USERS_DATA_KEY, rco);
      return ud;
    }
  }
}
