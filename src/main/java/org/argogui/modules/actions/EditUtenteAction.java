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
import org.argogui.om.GenSmartcard;
import org.argogui.om.GenSmartcardPeer;
import org.sirio6.utils.CoreRunData;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.PasswordMismatchException;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.turbine.om.security.User;
import org.apache.velocity.context.Context;
import org.commonlib5.utils.StringOper;
import org.sirio6.services.security.SEC;

/**
 * Operazioni di salvataggio del pannello dati utente.
 *
 * @author Nicola De Nisco
 */
public class EditUtenteAction extends ArgoBaseAction
{
  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    if(!super.isAuthorized(data))
      return false;

    return isAuthorizedAll(data, "EditUtenteAction"); // NOI18N
  }

  @Override
  public void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    String command = data.getParameters().getString("command"); // NOI18N
    if(StringOper.isEqu(command, "salva"))
    {
      salvaUtente(data);
    }
    else if(StringOper.isEqu(command, "passwd")) // NOI18N
    {
      cambiaPassword(data);
    }
    else if(StringOper.isEqu(command, "associa")) // NOI18N
    {
      associaCertificato(data);
    }
    else if(StringOper.isEqu(command, "cancella")) // NOI18N
    {
      cancellaCertificato(data);
    }
    else if(StringOper.isEqu(command, "enablepwd")) // NOI18N
    {
      data.getUser().setPerm(Costanti.ENABLED_PASSWORD_LOGON, true);
    }
    else if(StringOper.isEqu(command, "disablepwd")) // NOI18N
    {
      data.getUser().setPerm(Costanti.ENABLED_PASSWORD_LOGON, false);
    }
  }

  protected void salvaUtente(CoreRunData data)
     throws Exception
  {
    User u = data.getUser();
    u.setFirstName(data.getParameters().getString("firstName")); // NOI18N
    u.setLastName(data.getParameters().getString("lastName")); // NOI18N
    u.setEmail(data.getParameters().getString("email")); // NOI18N
    SEC.saveUser(u);
    data.setMessagei18n("Dati utente salvati con successo.");
  }

  protected void cambiaPassword(CoreRunData data)
     throws Exception
  {
    String oldPass = data.getParameters().getString("oldpass"); // NOI18N
    String newPass = data.getParameters().getString("newpass1", ""); // NOI18N
    if(!StringOper.isEqu(newPass, data.getParameters().getString("newpass2"))) // NOI18N
      data.throwMessagei18n("Le due nuove password non coincidono. Rivedere input dati.");

    if(newPass.length() < 8)
      data.throwMessagei18n("La nuova password deve contenere almeno 8 caratteri.");

    int mode = (Integer) data.getSession().getAttribute(Costanti.LOGIN_MODE);
    switch(mode)
    {
      case Costanti.LOGON_SPECIAL:
      case Costanti.LOGON_CERTIFICATE:
      case Costanti.LOGON_CERTIFICATE_ROOT:
      {
        // quando il logon è garantito da un certificato
        // possiamo ignorare il vecchio valore della password
        // e forzare comunque il nuovo valore password ...
        try
        {
          User u = data.getUser();
          SEC.getTurbineSecurity().forcePassword(u, newPass);
          data.setMessagei18n("Cambio password avvenuto con successo.");
        }
        catch(UnknownEntityException ue)
        {
          data.throwMessagei18n("L'utente indicato non è valido.");
        }
        catch(DataBackendException db)
        {
          data.throwMessagei18n("Problemi con il database.");
        }

        return;
      }

      default:
      {
        // ... in tutti gli altri casi la vecchia password
        // deve essere corretta
        try
        {
          User u = data.getUser();
          SEC.getTurbineSecurity().changePassword(u, oldPass, newPass);
          data.setMessagei18n("Cambio password avvenuto con successo.");
        }
        catch(PasswordMismatchException pm)
        {
          data.throwMessagei18n("La vecchia password fornita non corrisponde.");
        }
        catch(UnknownEntityException ue)
        {
          data.throwMessagei18n("L'utente indicato non è valido.");
        }
        catch(DataBackendException db)
        {
          data.throwMessagei18n("Problemi con il database.");
        }

        return;
      }
    }
  }

  protected void associaCertificato(CoreRunData data)
     throws Exception
  {
    User u = data.getUser();
    String serial = data.getParameters().getString("cert"); // NOI18N
    if(serial == null)
      data.throwMessagei18n("Errore interno: certificato non specificato.");

    List<X509Certificate> certs = getClientCertificates(data);
    if(certs == null || certs.isEmpty())
      data.throwMessagei18n("Nessun certificato valido associato alla connessione HTTPS.");

    String commonName = null;
    for(X509Certificate c : certs)
    {
      if(serial.equals(c.getSerialNumber().toString()))
      {
        commonName = c.getSubjectDN().getName();
        break;
      }
    }

    if(commonName == null)
      data.throwMessagei18n("Errore interno: non riesco ad identificare il certificato dal serialnumber.");

    GenSmartcard prev = GenSmartcardPeer.retrieveByAlternateKey1Quiet(u.getName(), commonName);
    if(prev != null)
    {
      // recupero di eventuale record precedente cancellato logicamente
      if(prev.getStatoRec() >= 10)
      {
        prev.setStatoRec(0);
        prev.save();
        data.setMessagei18n("Il certificato è stato associato all'utente %s.", u.getName());
        return;
      }

      data.throwMessagei18n("L'utente %s e il certificato sono già stati associati.", u.getName());
    }

    // salvataggio nuova associazione utente->certificato
    GenSmartcard gs = new GenSmartcard();
    gs.setLoginName(u.getName());
    gs.setCertChain(commonName);
    gs.setCreazione(new Date());
    gs.setStatoRec(0);
    gs.save();

    data.setMessagei18n("Il certificato è stato associato all'utente %s.", u.getName());
  }

  protected void cancellaCertificato(CoreRunData data)
     throws Exception
  {
    User u = data.getUser();
    String serial = data.getParameters().getString("cert");
    if(serial == null)
      data.throwMessagei18n("Errore interno: certificato non specificato.");

    List<X509Certificate> certs = getClientCertificates(data);
    if(certs == null || certs.isEmpty())
      data.throwMessagei18n("Nessun certificato valido associato alla connessione HTTPS.");

    String commonName = null;
    for(X509Certificate c : certs)
    {
      if(serial.equals(c.getSerialNumber().toString()))
      {
        commonName = c.getSubjectDN().getName();
        break;
      }
    }

    if(commonName == null)
      data.throwMessagei18n("Errore interno: non riesco ad identificare il certificato dal serialnumber.");

    GenSmartcard prev = GenSmartcardPeer.retrieveByAlternateKey1Quiet(u.getName(), commonName);
    if(prev == null)
      data.throwMessagei18n("Il certificato indicato non è associato all'utente %s.", u.getName());

    prev.setStatoRec(10);
    prev.save();

    data.setMessagei18n("Il certificato è stato rimosso all'utente %s.", u.getName());
  }

  protected List<X509Certificate> getClientCertificates(CoreRunData data)
  {
    try
    {
      X509Certificate[] certChain = (X509Certificate[]) data.getRequest().
         getAttribute("javax.net.ssl.peer_certificate"); // NOI18N

      if(certChain == null || certChain.length == 0)
        certChain = (X509Certificate[]) data.getRequest().
           getAttribute("javax.servlet.request.X509Certificate"); // NOI18N

      if(certChain == null || certChain.length == 0)
        return null;

      return Arrays.asList(certChain);
    }
    catch(Exception ex)
    {
      log.error("clientCertificateLogon failure:", ex); // NOI18N
    }

    return null;
  }
}
