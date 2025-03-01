/*
 *  Copyright (C) 2015 Informatica Medica s.r.l.
 *
 *  Questo software è proprietà di Informatica Medica s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  Informatica Medica s.r.l.
 *  Viale dei Tigli, 19
 *  Casalnuovo di Napoli (NA)
 *
 *  Creato il 18-mar-2015, 16.39.31
 */
package org.argogui.utils;

import org.commonlib5.mail.SimpleMailSender;

/**
 * Funzioni di utilità per invio E-mail.
 *
 * @author Nicola De Nisco
 */
public class SMTP
{
  public static final String SMTP_HOST = "mail.radimage.it";
  public static final int SMTP_PORT = 587;
  public static final String SMTP_USER = "procedure@radimage.it";
  public static final String SMTP_PASS = "zst12v4wsq";
  public static final String SMTP_FROM = "argo@radimage.it";

  public static final int SMTP_PROTOCOL_CLEAR = 0;
  public static final int SMTP_PROTOCOL_TLS = 1;
  public static final int SMTP_PROTOCOL_SSL = 2;
  public static final int SMTP_PROTOCOL_STARTTLS = 3;

  public static void sendEmail(String host, int porta, int protocollo, boolean ignoraCertificato,
     String body, String subject, String from, String recipient,
     final String utente, final String password)
     throws Exception
  {
    SimpleMailSender sm = new SimpleMailSender(host, porta, protocollo, ignoraCertificato);
    sm.setAuth(utente, password);
    sm.setDebugMailSession(true);
    sm.addRecipients(recipient);
    sm.addBodyPart(body);
    sm.sendMail(subject, from);
  }

  public static void sendEmailBugReport(String body, String subject, String recipient)
     throws Exception
  {
    SimpleMailSender sm = new SimpleMailSender(SMTP_HOST, SMTP_PORT, SimpleMailSender.SMTP_PROTOCOL_STARTTLS, true);
    sm.setAuth(SMTP_USER, SMTP_PASS);
    sm.setDebugMailSession(true);
    sm.addRecipients(recipient);
    sm.addBodyPart(body);
    sm.sendMail(subject, SMTP_FROM);
  }
}
