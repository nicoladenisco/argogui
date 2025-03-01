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
package org.argogui.servlets;

import org.argogui.services.ArgoServiceBroker;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.TurbineServices;

/**
 * Servelt Turbine specializzata per Argo.
 *
 * @author Nicola De Nisco
 */
public class TurbineArgoServlet extends Turbine
{
  static
  {
    TurbineServices.setManager(new ArgoServiceBroker());
  }
}
