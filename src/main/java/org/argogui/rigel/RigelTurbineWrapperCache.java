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
package org.argogui.rigel;

import org.argogui.rigel.table.AlternateColorTableArgo;
import org.rigel5.glue.table.HeditTableApp;
import org.rigel5.glue.table.PeerAppMaintFormTable;
import org.argogui.rigel.table.HeditTableAppArgo;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import org.apache.turbine.util.RunData;
import org.argogui.ArgoBusMessages;
import org.rigel5.glue.table.AlternateColorTableAppBase;
import org.sirio6.rigel.CoreTurbineWrapperCache;
import org.sirio6.services.bus.BUS;
import org.sirio6.services.bus.BusContext;
import org.sirio6.services.bus.MessageBusListener;

/**
 * Cache degli oggetti wrapper creati da Rigel.
 * Questa cache viene conservata in sessione.
 * Deve essere diversa per ogni utente.
 * Questa versione viene utilizzata nelle maschere
 * rigel utilizzate con Turbine.
 *
 * @author Nicola De Nisco
 */
public class RigelTurbineWrapperCache extends CoreTurbineWrapperCache
   implements MessageBusListener, HttpSessionBindingListener
{
  /**
   * Inizializzazione di questa cache oggetti rigel.
   * @param data dati della richiesta
   */
  @Override
  public void init(RunData data)
  {
    super.init(data);

    basePath = new String[]
    {
      "org.sirio6.rigel.table", // NOI18N
      "org.argogui.rigel.table" // NOI18N
    };
  }

  @Override
  public AlternateColorTableAppBase buildDefaultTableList()
  {
    return new AlternateColorTableArgo();
  }

  @Override
  public HeditTableApp buildDefaultTableEdit()
  {
    return new HeditTableAppArgo();
  }

  @Override
  public PeerAppMaintFormTable buildDefaultPeerTableForm()
  {
    return new PeerAppMaintFormTable();
  }

  @Override
  public int message(int msgID, Object originator, BusContext context)
     throws Exception
  {
    switch(msgID)
    {
      // alla pulizia della global cache corrisponde anche uno svuotamento di questa cache
      case ArgoBusMessages.CLEAR_GLOBAL_CACHE:
        clear();
        break;
    }

    return 0;
  }

  @Override
  public void valueBound(HttpSessionBindingEvent hsbe)
  {
    BUS.registerEventListner(this);
  }

  @Override
  public void valueUnbound(HttpSessionBindingEvent hsbe)
  {
    BUS.removeEventListner(this);
  }
}
