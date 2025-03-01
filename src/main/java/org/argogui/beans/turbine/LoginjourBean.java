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

import java.util.Date;

import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.commons.logging.*;
import org.argogui.ArgoBusMessages;
import org.argogui.om.GenLoginjour;
import org.argogui.utils.I;
import org.sirio6.services.bus.BUS;
import org.sirio6.services.token.TokenBean;

/**
 * Estende Loginjour per essere notificato alla scadenza della sessione.
 * @author Nicola De Nisco
 */
public class LoginjourBean extends GenLoginjour
   implements TokenBean
{
  /** Logging */
  private static Log log = LogFactory.getLog(LoginjourBean.class);
  private int userID;

  /**
   * Viene chiamata quando l'istanza e' attaccata alla sessione.
   * Non viene effettuata alcuna azione: si presuppone che
   * l'oggetto LoginjourBean sia stato gia' creato e istanziato
   * correttamente.
   * @param event
   */
  @Override
  public void valueBound(HttpSessionBindingEvent event)
  {
  }

  /**
   * Viene chiamata quando la sessione e' scaduta o rimossa.
   * In questo caso viene segnato il momento del logout
   * e salvato su database.
   * @param event
   */
  @Override
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    try
    {
      Date now = new Date();
      setTlogout(now);

      // notifica ascoltatori del logon utente
      BUS.sendMessageAsync(ArgoBusMessages.USER_LOGOUT, this);

      save();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      log.error(I.I("Salvataggio giornale (logout) non possibile."), ex);
    }
  }

  @Override
  public int getUserId()
  {
    return userID;
  }

  @Override
  public void setUserId(int userId)
  {
    this.userID = userId;
  }
}
