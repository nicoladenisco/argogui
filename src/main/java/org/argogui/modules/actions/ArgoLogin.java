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
package org.argogui.modules.actions;

import org.argogui.Costanti;
import org.argogui.beans.turbine.LoginjourBean;
import org.argogui.om.GenSmartcard;
import org.argogui.om.GenSmartcardPeer;
import org.argogui.om.TurbineUser;
import org.argogui.om.TurbineUserPeer;
import org.sirio6.utils.CoreRunData;
import org.argogui.utils.SU;
import java.awt.Dimension;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.util.FulcrumSecurityException;
import org.apache.turbine.modules.actions.LoginUser;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.argogui.ArgoBusMessages;
import org.commonlib5.crypto.KeyCalculator;
import org.commonlib5.utils.StringOper;
import org.rigel5.db.torque.CriteriaRigel;
import org.sirio6.services.bus.BUS;
import org.sirio6.services.bus.BusContext;
import org.sirio6.services.security.CoreSecurity;
import org.sirio6.services.security.SEC;
import org.sirio6.utils.DT;

/**
 * Estensione della login.
 * Salva la risoluzione del client connesso.
 * Salva nel giornale di login l'evento di logon dell'utente.
 *
 * Effettua l'autologon con il meccanismo della chiave
 * inserita direttamente nell'URL (vedi @KeyCalculator).
 *
 * Per eseguire l'autologon occorre che sia stata richiesta una uri:
 * http://server/perso/pgm/action/ArgoLogin?user_id=utente&time=1234&key=045d&tipDoc=1234
 *
 * Il parametro key che sostituisce la password viene calcolato
 * ogni volta a seconda dell'ora corrente (passata attraverso time=)
 * del tipo di richiesta e della password dell'utente.
 *
 * Per una implementazione lato client in C++ e VisualBasic
 * vedi il file secalc.zip
 *
 * @author Nicola De Nisco
 */
public class ArgoLogin extends LoginUser
{
  /** Logging */
  private static Log log = LogFactory.getLog(ArgoLogin.class);
  private static final String SSO_PASSWORD = "ripamer12qw45!";
  public static KeyCalculator kCalc = new KeyCalculator();

  @Override
  public void doPerform(PipelineData pipelineData)
     throws FulcrumSecurityException
  {
    CoreRunData data = (CoreRunData) pipelineData.getRunData();

    int mode = Costanti.LOGON_UNDEFINED;

    if(ssoLogon(data))
    {
      mode = Costanti.LOGON_SSO;
    }
    if(autoLogon(data))
    {
      mode = Costanti.LOGON_AUTO;
    }
    else if(clientCertificateLogon(data))
    {
      mode = Costanti.LOGON_CERTIFICATE;
    }
    else if(clientCertificateLogonTurbine(data))
    {
      mode = Costanti.LOGON_CERTIFICATE_ROOT;
    }
    else
    {
      mode = Costanti.LOGON_NORMAL;
      try
      {
        String username = data.getParameters().getString(CGI_USERNAME, "");
        String password = data.getParameters().getString(CGI_PASSWORD, "");

        if(StringOper.isOkStr(username))
        {
          User tmp = SEC.getUser(username);
          Boolean b = (Boolean) tmp.getPerm(Costanti.ENABLED_PASSWORD_LOGON, true);

          if(b)
          {
            MutableInt mmode = new MutableInt(Costanti.LOGON_UNDEFINED);
            User u = SEC.loginUser(data.getSession(), username, password, mmode);

            if(u != null)
            {
              logonUser(data, username);
              mode = mmode.intValue();
            }
          }
          else
          {
            // utente non abilitato all'uso delle password
            data.setMessagei18n("L'utente ha disabilitato l'uso delle password (eventualmente rivolgersi all'amministratore di sistema).");
            return;
          }
        }
      }
      catch(Exception ex)
      {
        log.error(data.i18n("Logon normale con password fallito:"), ex);
      }
    }

    User user = null;
    if(data.userExists())
      user = data.getUser();

    if(user == null || !user.hasLoggedIn())
    {
      // logon o autologon non riuscito
      data.setMessagei18n("Utente inesistente: nome utente e password sono corretti?");
      return;
    }

    // memorizza nella sessione la modalità di login
    data.getSession().setAttribute(Costanti.LOGIN_MODE, mode);

    int resx = data.getParameters().getInt("resx");
    int resy = data.getParameters().getInt("resy");

    if(resx != 0 && resy != 0)
    {
      data.getSession().setAttribute(Costanti.USER_DIMENSION,
         new Dimension(resx, resy));
    }

    try
    {
      Date now = new Date();

      // registra nel giornale di logon l'avvenuta autenticazione
      log.info("Logging user:" + user.getFirstName() + " " + user.getLastName()
         + " (" + user.getName() + ") " + user.getLastAccessDate()); // NOI18N

      LoginjourBean lj = new LoginjourBean();
      lj.setUserId((Integer) user.getId());
      lj.setSessionid(data.getSession().getId());
      lj.setLoginName(user.getName());
      lj.setMode(mode);
      lj.setFirstName(user.getFirstName());
      lj.setLastName(user.getLastName());
      lj.setTlogin(now);
      lj.setStationName(data.getRemoteHost());
      lj.save();

      data.getSession().setAttribute(Costanti.LOGIN_JOURNAL, lj);

      // notifica ascoltatori del logon utente
      BUS.sendMessageAsync(ArgoBusMessages.USER_LOGON, this, new BusContext(
         "mode", mode,
         "bean", lj
      ));
    }
    catch(Exception ex)
    {
      log.error(data.i18n("Login journal failed:"), ex);
    }
  }

  /**
   * Procedura di autologon con chiave.
   * ATTENZIONE: questa procedura funziona solo se le password
   * sono memorizzate in chiaro nel db, in quanto la password fa
   * parte della chiave.
   * @param data
   * @return
   */
  protected boolean autoLogon(CoreRunData data)
  {
    try
    {
      String user_id = data.getParameters().getString("user_id", ""); // NOI18N
      String time = data.getParameters().getString("time", ""); // NOI18N
      String key = data.getParameters().getString("key", ""); // NOI18N

      if(SU.isOkStr(user_id) && SU.isOkStr(time) && SU.isOkStr(key))
      {
        //
        // autologon
        //
        long tClient = Long.parseLong(time);

        log.debug(
           "tClient=" + tClient
           + " tServer=" + System.currentTimeMillis()
           + " toll=" + Costanti.TOLL_TIME_LOGIN); // NOI18N

        if(Math.abs(tClient - System.currentTimeMillis()) > Costanti.TOLL_TIME_LOGIN)
        {
          throw new Exception(
             data.i18n("Il parametro time non e' coerente con l'ora attuale (max +/- 10 minuti): %s",
                DT.formatDataFull(new Date())));
        }

        TurbineUser tu = TurbineUserPeer.retriveByLoginName(user_id);
        if(tu != null)
        {
          String pwd = tu.getPassword();
          long lKey = Long.parseLong(SU.okStr(key), 16);
          long cKey = kCalc.calc(user_id, pwd, tClient);

          log.info("lKey=" + Long.toString(lKey, 16) + " cKey=" + Long.toString(cKey, 16)); // NOI18N

          if(lKey == cKey)
          {
            logonUser(data, user_id);
            log.info("Autologon grant for user " + tu.getFirstName() + " " + tu.getLastName()); // NOI18N
            return true;
          }
        }

        log.info("Autologon failure for user " + user_id); // NOI18N
        return false;
      }
    }
    catch(Exception ex)
    {
      log.error("Auotologon failure:", ex); // NOI18N
    }

    return false;
  }

  /**
   * Logon di un utente con smartcard.
   * @param data
   * @return
   */
  protected boolean clientCertificateLogon(RunData data)
  {
    try
    {
      // in questo logon l'utente non è obbligatorio, è utile se lo stesso
      // certificato è stato associato a più utenti
      String username = StringOper.okStrNull(data.getParameters().getString(CGI_USERNAME, null));

      X509Certificate[] certChain = (X509Certificate[]) data.getRequest().
         getAttribute("javax.net.ssl.peer_certificate"); // NOI18N

      if(certChain == null || certChain.length == 0)
        certChain = (X509Certificate[]) data.getRequest().
           getAttribute("javax.servlet.request.X509Certificate"); // NOI18N

      if(certChain == null || certChain.length == 0)
        return false;

      for(int i = 0; i < certChain.length; i++)
      {
        X509Certificate cert = certChain[i];
        String userCertName = cert.getSubjectDN().getName();

        log.info("clientCertificateLogon: verifico per " + userCertName); // NOI18N
        CriteriaRigel cr = new CriteriaRigel(GenSmartcardPeer.TABLE_NAME);
        cr.add(GenSmartcardPeer.CERT_CHAIN, userCertName);
        cr.addAscendingOrderByColumn(GenSmartcardPeer.SMARTCARD_ID);
        List<GenSmartcard> lsSmarts = GenSmartcardPeer.doSelect(cr);

        if(!lsSmarts.isEmpty())
        {
          String userName = null;
          if((userName = testCertificate(username, userCertName, lsSmarts)) != null)
          {
            logonUser(data, userName);
            User tu = data.getUser();
            log.info("clientCertificateLogon grant for user " + tu.getFirstName() + " " + tu.getLastName()); // NOI18N
            return true;
          }
        }
      }

      return false;
    }
    catch(Exception ex)
    {
      log.error("clientCertificateLogon failure:", ex); // NOI18N
    }

    return false;
  }

  /**
   * Logon speciale di un utente amministratore (con smartcard)
   * che impersona un utente diverso.
   * @param data
   * @return
   */
  protected boolean clientCertificateLogonTurbine(RunData data)
  {
    try
    {
      // lo user id è obbligatorio per identificare l'utente desiderato
      String username = StringOper.okStrNull(data.getParameters().getString(CGI_USERNAME, null));
      if(username == null)
        return false;

      X509Certificate[] certChain = (X509Certificate[]) data.getRequest().
         getAttribute("javax.net.ssl.peer_certificate"); // NOI18N

      if(certChain == null || certChain.length == 0)
        certChain = (X509Certificate[]) data.getRequest().
           getAttribute("javax.servlet.request.X509Certificate"); // NOI18N

      if(certChain == null || certChain.length == 0)
        return false;

      // recupera eventuali certificati associati all'utente turbine
      // questi certificati possono essere usati per loggarsi con qualsiasi altro nome utente
      List<GenSmartcard> lsTurbineCerts = GenSmartcardPeer.retrieveByLoginName(CoreSecurity.ADMIN_NAME, null);

      for(int i = 0; i < certChain.length; i++)
      {
        X509Certificate cert = certChain[i];
        String userCertName = cert.getSubjectDN().getName();

        if(!lsTurbineCerts.isEmpty())
        {
          String userName = null;
          if((userName = testCertificate(username, userCertName, lsTurbineCerts)) != null)
          {
            logonUser(data, userName);
            User tu = data.getUser();
            log.info("clientCertificateLogonTurbine grant for user "
               + tu.getFirstName() + " " + tu.getLastName()); // NOI18N
            return true;
          }
        }
      }

      return false;
    }
    catch(Exception ex)
    {
      log.error("clientCertificateLogon failure:", ex); // NOI18N
    }

    return false;
  }

  protected boolean ssoLogon(CoreRunData data)
  {
    try
    {
      String user_id = data.getParameters().getString("user_id", ""); // NOI18N
      String time = data.getParameters().getString("time", "");
      String key = data.getParameters().getString("key", "");

      if(SU.isOkStr(user_id) && SU.isOkStr(time) && SU.isOkStr(key))
      {
        //
        // autologon
        //
        long tClient = Long.parseLong(time);

        log.debug(
           "tClient=" + tClient
           + " tServer=" + System.currentTimeMillis()
           + " toll=" + Costanti.TOLL_TIME_LOGIN); // NOI18N

        if(Math.abs(tClient - System.currentTimeMillis()) > Costanti.TOLL_TIME_LOGIN)
        {
          throw new Exception(
             data.i18n("Il parametro time non e' coerente con l'ora attuale (max +/- 10 minuti): %s",
                DT.formatDataFull(new Date())));
        }

        TurbineUser tu = TurbineUserPeer.retriveByLoginName(user_id);
        if(tu != null)
        {
          long lKey = Long.parseLong(SU.okStr(key), 16);
          long cKey = kCalc.calc(user_id, SSO_PASSWORD, tClient, "sso");

          log.info("lKey=" + Long.toString(lKey, 16) + " cKey=" + Long.toString(cKey, 16)); // NOI18N

          if(lKey == cKey)
          {
            logonUser(data, user_id);
            log.info("ssoLogon grant for user " + tu.getFirstName() + " " + tu.getLastName()); // NOI18N
            return true;
          }
        }

        log.info("ssoLogon failure for user " + user_id); // NOI18N
      }

      return false;
    }
    catch(Exception ex)
    {
      log.error("ssoLogon failure:", ex); // NOI18N
    }
    return false;
  }

  /**
   * Verifica le stringhe certificato per cercare di determinare
   * l'utente corrispondente. Esempio:
   * DNQ=2008111290A1201, SURNAME=DE NISCO, GIVENNAME=NICOLA, EMAILADDRESS=denisco@mimedical.it,
   * SERIALNUMBER=DNSNCL66M27G902V, CN="DNSNCL66M27G902V/7420028800054080.fyLq6v3l9ECx/iZxQ9GYGeINUQc=",
   * OU=C.C.I.A.A. DI CASERTA, O=Non Dichiarato, C=IT
   * Lo stesso certificato può essere associato a più utenti, quindi si preferisce
   * nameCandidate fra tutti o diversamente (se = null) l'ultimo valido.
   * @param nameCandidate il nome utente più desiderato (può essere null)
   * @param crtValues stringa certificato X509
   * @param lsSmarts lista di oggetti GenSmartcard
   * @return login name dell'utente o null se non possibile
   * @throws Exception
   */
  protected String testCertificate(String nameCandidate, String crtValues, List<GenSmartcard> lsSmarts)
     throws Exception
  {
    String rv = null;

    for(GenSmartcard sm : lsSmarts)
    {
      if(crtValues.contains(sm.getCertChain()))
      {
        rv = sm.getLoginName();
        if(StringOper.isEqu(rv, nameCandidate))
          return rv;
      }
    }

    return rv;
  }

  public void logoutUser(RunData data)
     throws Exception
  {
    User user = data.getUser();

    if(!SEC.isAnonymousUser(user))
    {
      // Make sure that the user has really logged in...
      if(!user.hasLoggedIn())
      {
        return;
      }

      user.setHasLoggedIn(Boolean.FALSE);
      SEC.saveUser(user);
    }

    // This will cause the acl to be removed from the session in
    // the Turbine servlet code.
    data.setACL(null);

    // Retrieve an anonymous user.
    data.setUser(SEC.getAnonymousUser());
    data.save();

    // pulisce sessione di tutti gli attributi
    HttpSession session = data.getSession();
    Enumeration<String> en = session.getAttributeNames();
    while(en.hasMoreElements())
      session.removeAttribute(en.nextElement());
  }

  /**
   * Fa il login dell'utente specificato.
   * Viene utilizzata quando l'utente è determinato con
   * metodi diversi dalla sua password.
   *
   * @param data
   * @param userName
   * @throws Exception
   */
  protected void logonUser(RunData data, String userName)
     throws Exception
  {
    // Authenticate the user and get the object.
    User user = SEC.getUser(userName);

    // Store the user object.
    data.setUser(user);

    // Mark the user as being logged in.
    user.setHasLoggedIn(Boolean.TRUE);

    // Set the last_login date in the database.
    user.updateLastLogin();

    // This only happens if the user is valid; otherwise, we
    // will get a valueBound in the User object when we don't
    // want to because the username is not set yet.  Save the
    // User object into the session.
    data.save();
  }
}
