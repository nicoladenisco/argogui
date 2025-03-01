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
package org.argogui.modules.screens;

import org.argogui.Costanti;
import org.argogui.om.GenSmartcard;
import org.argogui.om.GenSmartcardPeer;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.commonlib5.utils.StringOper;
import org.rigel5.db.torque.CriteriaRigel;
import org.sirio6.utils.CoreRunData;
import org.sirio6.services.security.SEC;

/**
 * Controllore per ProfiloUtente.vm.
 * Dati dell'utente (e cambio password).
 *
 * @author Nicola De Nisco
 */
public class ProfiloUtente extends ArgoBaseScreen
{
  public static class ConCerts
  {
    private X509Certificate cert = null;
    private boolean inUse = false;

    public X509Certificate getCert()
    {
      return cert;
    }

    public void setCert(X509Certificate cert)
    {
      this.cert = cert;
    }

    public boolean isInUse()
    {
      return inUse;
    }

    public void setInUse(boolean inUse)
    {
      this.inUse = inUse;
    }
  }

  @Override
  protected void doBuildTemplate2(CoreRunData data, Context context)
     throws Exception
  {
    super.doBuildTemplate2(data, context);
    context.put("user", data.getUser());

    List<X509Certificate> lCc = getClientCertificates(data);
    List<GenSmartcard> lGs = getStoredCertificates(data);

    if(lCc != null && !lCc.isEmpty())
    {
      ArrayList<ConCerts> arCc = new ArrayList<ConCerts>();
      for(X509Certificate cert : lCc)
      {
        ConCerts cc = new ConCerts();
        cc.setCert(cert);
        cc.setInUse(isPresent(lGs, cert.getSubjectDN().getName()));
        arCc.add(cc);
      }
      context.put("certs", arCc);
    }

    if(lGs != null && !lGs.isEmpty())
    {
      context.put("stored", getStoredCertificates(data));
      if(!SEC.isAdmin(data))
      {
        context.put("chooseLogon", true);
        context.put("enabledNormalLogon",
           data.getUser().getPerm(Costanti.ENABLED_PASSWORD_LOGON, true));
      }
    }
  }

  protected List<X509Certificate> getClientCertificates(RunData data)
  {
    try
    {
      X509Certificate[] certChain = (X509Certificate[]) data.getRequest().
         getAttribute("javax.net.ssl.peer_certificate");

      if(certChain == null || certChain.length == 0)
        certChain = (X509Certificate[]) data.getRequest().
           getAttribute("javax.servlet.request.X509Certificate");

      if(certChain == null || certChain.length == 0)
        return null;

      return Arrays.asList(certChain);
    }
    catch(Exception ex)
    {
      log.error("clientCertificateLogon failure:", ex);
    }

    return null;
  }

  protected List<GenSmartcard> getStoredCertificates(RunData data)
  {
    try
    {
      CriteriaRigel cr = new CriteriaRigel(GenSmartcardPeer.TABLE_NAME);
      cr.add(GenSmartcardPeer.LOGIN_NAME, data.getUser().getName());
      cr.addAscendingOrderByColumn(GenSmartcardPeer.SMARTCARD_ID);
      List<GenSmartcard> rv = GenSmartcardPeer.doSelect(cr);
      return rv.isEmpty() ? null : rv;
    }
    catch(Exception e)
    {
      log.error("Errore leggendo lista certificati da database.", e);
      return null;
    }
  }

  protected boolean isPresent(List<GenSmartcard> lGs, String commonName)
  {
    if(lGs == null)
      return false;

    for(GenSmartcard sm : lGs)
    {
      if(StringOper.isEqu(commonName, sm.getCertChain()))
        return true;
    }
    return false;
  }

  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    if(!super.isAuthorized(data))
      return false;

    return isAuthorizedAll(data, "ProfiloUtente");
  }
}
