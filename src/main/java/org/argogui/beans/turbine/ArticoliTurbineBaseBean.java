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
package org.argogui.beans.turbine;

import org.argogui.beans.ArgoBaseBean;
import javax.servlet.http.HttpSession;
import org.sirio6.services.security.SEC;
import org.sirio6.utils.CoreRunData;

/**
 * Bean di base per la gestione articoli; versione specializzata
 * per Turbine.
 *
 * @author Nicola De Nisco
 */
public class ArticoliTurbineBaseBean extends ArgoBaseBean
{
  protected boolean isVisArt = false;
  protected boolean isModArt = false;
  protected boolean isNewArt = false;

  /**
   * Ridefinita: imposta flags dei permessi.
   * @param data
   * @throws Exception
   */
  @Override
  public void init(CoreRunData data)
     throws Exception
  {
    super.init(data);

    HttpSession session = data.getSession();
    isVisArt = SEC.checkPermission(session, "visualizza_anag_articoli"); // NOI18N
    isModArt = SEC.checkPermission(session, "modifica_anag_articoli"); // NOI18N
    isNewArt = SEC.checkPermission(session, "nuovo_anag_articoli"); // NOI18N

    if(isNewArt)
      isModArt = true;

    if(isModArt)
      isVisArt = true;
  }

  public boolean isModArt()
  {
    return isModArt;
  }

  public boolean isVisArt()
  {
    return isVisArt;
  }

  public boolean isNewArt()
  {
    return isVisArt;
  }
}

