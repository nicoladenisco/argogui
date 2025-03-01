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
package org.argogui.xmlrpc.client;

import java.util.Vector;
import org.argogui.xmlrpc.SyncCaleidoXmlRpcInterface;
import org.sirio6.utils.xmlrpc.BaseXmlRpcClient;

/**
 * Client per l'interrrogazione del servizio SyncCaleidoXmlRpcInterface.
 *
 * @author Nicola De Nisco
 */
public class SyncCaleidoXmlRpcClient extends BaseXmlRpcClient
   implements SyncCaleidoXmlRpcInterface
{
  public SyncCaleidoXmlRpcClient(String server, int port)
     throws Exception
  {
    super("syncCaleido", server, port);
  }

  @Override
  public Vector getMaterialiBiologici()
     throws Exception
  {
    return (Vector) call("getMaterialiBiologici");
  }

  @Override
  public Vector getPrestazioni(String codBranca)
     throws Exception
  {
    return (Vector) call("getPrestazioni", codBranca);
  }

  @Override
  public Vector getParametri(String codBranca)
     throws Exception
  {
    return (Vector) call("getParametri", codBranca);
  }
}
