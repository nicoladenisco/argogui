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
package org.argogui.modules.tools;

import org.argogui.om.TurbineUser;
import java.util.List;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.pull.ApplicationTool;
import org.argogui.services.cache.UsersDataCache;

/**
 * Tool per l'accesso ai dati degli utenti.
 *
 * FILENOI18N
 * @author Nicola De Nisco
 */
public class UsersDataTool implements ApplicationTool
{
  /**
   * Inizializza il tool.
   * Nessuna implementazione.
   * @param data
   */
  @Override
  public void init(Object data)
  {
  }

  /**
   * Aggiornamento del tool.
   * Viene chiamata ad ogni inserimento in un contex di pagina.
   * Nessuna implementazione.
   */
  @Override
  public void refresh()
  {
  }

  /**
   * Ritorna l'utente associato all'ID specificato.
   * Viene ritornato l'oggetto om di Torque relativo
   * alla tabella di database TurbineUser.
   * @param idUser id dell'utente
   * @return oggetto TurbineUser relativo
   * @throws Exception
   */
  public TurbineUser getUser(int idUser) throws Exception
  {
    return UsersDataCache.getInstance().getUser(idUser);
  }

  /**
   * Ritorna l'utente associato all'ID specificato.
   * Viene ritornato un oggetto che implementa l'interfaccia User
   * di Turbine.
   * @param idUser id dell'utente
   * @return oggetto Turbine User relativo
   * @throws Exception
   */
  public User getTurbineUser(int idUser) throws Exception
  {
    return UsersDataCache.getInstance().getTurbineUser(idUser);
  }

  /**
   * Ritorna le 'option' per un combo che contenga un
   * elenco completo degli utenti
   * @param defUser utente di default (default del combo)
   * @return corpo del combo (solo tag option)
   * @throws Exception
   */
  public String getComboUtenti(int defUser) throws Exception
  {
    String rv = "";
    List<TurbineUser> vUsers = UsersDataCache.getInstance().getVUsers();

    for(TurbineUser u : vUsers)
    {
      if(u.getUserId() == 0)
        continue;

      if(u.getUserId() == defUser)
        rv += "<option value=\"" + u.getUserId() + "\" selected>";
      else
        rv += "<option value=\"" + u.getUserId() + "\">";

      rv += u.getFirstName() + " " + u.getLastName() + "</option>\r\n";
    }

    return rv;
  }
}
