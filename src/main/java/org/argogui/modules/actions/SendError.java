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

import org.argogui.om.GenVersion;
import org.argogui.services.SERVICE;
import org.sirio6.utils.CoreRunData;
import org.argogui.utils.SMTP;
import org.argogui.utils.VersionData;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import org.apache.commons.configuration2.Configuration;
import org.apache.turbine.Turbine;
import org.apache.turbine.om.security.User;
import org.apache.turbine.util.*;
import org.apache.velocity.context.*;

/**
 * Invio delle segnalazioni di errore.
 *
 * @author Nicola De Nisco
 */
public class SendError extends ArgoBaseAction
{
  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return true;
  }

  @Override
  public void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    String ewhy = data.getParameters().getString("ewhy");
    String estk = data.getParameters().getString("estk");
    String descrizione = data.getParameters().getString("descr");

    Configuration cfg = Turbine.getConfiguration();
    String to = cfg.getString("mail.smtp.to", "dev@infomedica.it");
    String azid = cfg.getString("azienda.id", "ID_SCONOSCIUTO");
    String azna = cfg.getString("azienda.nome", "AZIENDA_SCONOSCIUTA");

    User u = data.getUser();
    List components = VersionData.getVersionData();

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    pw.println("azienda.id: " + azid);
    pw.println("azienda.nome: " + azna);
    pw.println("utente: " + u.getFirstName() + " " + u.getLastName());
    pw.println("server name: " + SERVICE.getCanonicalServerName());
    pw.println("server address: " + SERVICE.getCanonicalServerAddress());
    pw.println("\n");

    pw.println("Componenti:");
    Iterator itrComp = components.iterator();
    while(itrComp.hasNext())
    {
      GenVersion v = (GenVersion) itrComp.next();
      pw.println(v.toString());
    }
    pw.println("\n");

    System.getProperties().list(pw);
    pw.println("\n");

    pw.println("Descrizione utente:");
    pw.println(descrizione);
    pw.println("\n");

    pw.println(ewhy);
    pw.println("\n");
    pw.println(estk);
    pw.println("\n");

    pw.flush();
    String message = sw.toString();

    try
    {
      SMTP.sendEmailBugReport(message, "Error report da ARGO", to);
      data.setMessage("La segnalazione d'errore e' stata inviata. Grazie.");
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      data.setMessage("Invio segnalazione non riuscito: " + ex.getMessage());
    }
  }
}
