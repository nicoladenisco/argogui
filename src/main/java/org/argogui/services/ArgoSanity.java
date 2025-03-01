/*
 *  ArgoSanity.java
 *  Creato il Jun 4, 2017, 5:53:41 PM
 *
 *  Copyright (C) 2017 RAD-IMAGE s.r.l.
 *
 *  Questo software è proprietà di RAD-IMAGE s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  RAD-IMAGE s.r.l.
 *  Via San Giovanni 1 - Contrada Belvedere
 *  San Nicola Manfredi (BN)
 */
package org.argogui.services;

import java.io.File;
import java.sql.Connection;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.torque.Torque;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.security.SecurityService;
import org.argogui.om.utils.AlignDatabase;
import org.rigel5.db.SanityTorqueUtils;
import org.rigel5.db.torque.TQUtils;
import org.sirio6.services.AbstractCoreBaseService;
import org.sirio6.services.CoreAppSanity;
import org.sirio6.services.security.CoreSecurity;
import org.sirio6.services.security.SEC;

/**
 * Sanity per Argo.
 *
 * @author Nicola De Nisco
 */
public class ArgoSanity extends CoreAppSanity
{
  private final static Log log = LogFactory.getLog(ArgoSanity.class);
  private final static Object semaforo = new Object();

  private SecurityService securityService;

  /**
   * Effettua degli aggiustaggi e controlli su ID_TABLE
   * per prevenire malfunzionamenti difficili da individuare.
   * @throws org.apache.torque.TorqueException
   */
  public void sanityDatabase()
     throws Exception
  {
    synchronized(semaforo)
    {
      SanityTorqueUtils stq = new SanityTorqueUtils();
      stq.setDisableForeign(false);
      stq.sanityIDtable();
      stq.sanitySequence();
      stq.sanityZero();
      stq.sanityData();

      log.info("sanityDatabase superato");
    }
  }

  /**
   * Verifica e regolarizza il database.
   * @throws Exception
   */
  @Override
  protected void sanityDatabase(AbstractCoreBaseService service)
     throws Exception
  {
    securityService = SEC.getTurbineSecurity();

    AlignDatabase ad = new AlignDatabase();
    // carica table map dagli schema
    ad.loadTablesFromResources("application-schema.xml", "org.argogui.om");
    ad.loadTablesFromResources("id-table-schema.xml", "org.argogui.om");
    ad.loadTablesFromResources("torque-security-schema.xml", "org.argogui.om");

    sanityDatabase();

    // esegue allineamento database automatico
    File dirSql = service.getConfMainFile("sql");
    File pilot = new File(dirSql, "updpilot.xml");
    if(pilot.canRead() && dirSql.isDirectory())
    {
      ad.product = "argo";
      ad.dirScripts = new File(dirSql, "update");
      if(ad.dirScripts.isDirectory())
      {
        Connection con = null;
        try
        {
          con = Torque.getConnection();
          ad.setConnection(con, TQUtils.getAdapterName());
          ad.readUpgradeStep();
          ad.parsingSqlAggiornamento(ad.anno, ad.settimana, pilot);
          if(ad.updated)
            ad.writeUpgradeStep(0, null);
        }
        finally
        {
          if(con != null)
            Torque.closeConnection(con);
        }
      }
    }
  }

  /**
   * Verifica le impostazioni di sicurezza di base.
   * L'utente turbine deve esistere ed avere id=0.
   * Il ruolo turbine_root deve esistere ad avere id=1.
   * @throws Exception
   */
  @Override
  protected void sanitySecurity(AbstractCoreBaseService service)
     throws Exception
  {
    User turbine = null;
    Role turbine_root = null;
    Group global_group = null;

    try
    {
      turbine = securityService.getUser(CoreSecurity.ADMIN_NAME);
    }
    catch(Exception e)
    {
    }

    if(turbine == null || SEC.getUserID(turbine) != 0)
      service.die("Grave errore di configurazione: l'utente " + CoreSecurity.ADMIN_NAME
         + " deve esistere e obbligatoriamente avere l'ID = 0");

    try
    {
      turbine_root = securityService.getRoleByName(CoreSecurity.ADMIN_ROLE);
    }
    catch(Exception ex)
    {
    }

    if(turbine_root == null)
    {
      turbine_root = SEC.createRole(CoreSecurity.ADMIN_ROLE);
      service.ASSERT(turbine_root != null, "turbine_root != null");
    }

    try
    {
      global_group = securityService.getGlobalGroup();
    }
    catch(Exception e)
    {
    }

    if(global_group == null)
    {
      global_group = SEC.createGroup("GLOBAL");
      service.ASSERT(global_group != null, "global_group != null");
    }

    // da tutti i permessi a turbine_root
    PermissionSet allPerm = securityService.getAllPermissions();
    for(Iterator itPerm = allPerm.iterator(); itPerm.hasNext();)
    {
      try
      {
        securityService.grant(turbine_root, (Permission) itPerm.next());
      }
      catch(Exception e)
      {
        // l'eccezione viene ignorata: l'associazione può già esistere
      }
    }

    try
    {
      User sviluppo = securityService.getUser(CoreSecurity.SVILUPPO_NAME);
      securityService.grant(sviluppo, global_group, turbine_root);
    }
    catch(Exception e)
    {
      // l'eccezione viene ignorata: l'associazione può già esistere
    }
  }
}
