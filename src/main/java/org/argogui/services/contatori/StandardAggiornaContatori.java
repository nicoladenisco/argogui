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
package org.argogui.services.contatori;

import java.util.*;
import java.sql.Connection;
import org.apache.commons.logging.*;
import org.argogui.om.GenGiotab;
import org.argogui.om.GenGiotabPeer;
import org.argogui.services.ArgoBaseService;
import org.rigel5.db.torque.CriteriaRigel;
import org.sirio6.services.contatori.AggiornaContatori;

/**
 * <p>
 * Title: Newstar</p>
 * <p>
 * Description: Servizio per la gestione automatica dei contatori.
 * Implementazione standard.</p>
 * <p>
 * Copyright: Copyright (c) 2002</p>
 * <p>
 * Company: Italsystems s.r.l.</p>
 * @author Nicola De Nisco
 * @version 1.0
 */
public class StandardAggiornaContatori extends ArgoBaseService
   implements AggiornaContatori
{
  /** Logging */
  private static Log log = LogFactory.getLog(StandardAggiornaContatori.class);

  @Override
  public void argoInit()
     throws Exception
  {
  }

  /**
   * Ritorna un valore di contatore incrementando il contatore stesso.
   * @param esercizio codice di esercizio richiesto (puo' essere null)
   * @param chiave identificatore del tipo di contatore richiesto
   * @return il valore del contatore
   * @throws Exception
   */
  @Override
  public synchronized int getContaInc(String esercizio, String chiave, int minVal, Connection con)
     throws Exception
  {
    GenGiotab g = getContaGiotab(esercizio, chiave, con);

    int rv = g.getVint1();
    if(rv <= 0)
      rv = 1;
    if(rv < minVal)
      rv = minVal;
    g.setVint1(rv + 1);
    g.save(con);

    return rv;
  }

  /**
   * Ritorna un valore di contatore incrementando il contatore stesso.
   * @param esercizio codice di esercizio richiesto (puo' essere null)
   * @param chiave identificatore del tipo di contatore richiesto
   * @return il valore del contatore
   * @throws Exception
   */
  @Override
  public int getContaInc(String esercizio, String chiave, Connection con)
     throws Exception
  {
    return getContaInc(esercizio, chiave, 0, con);
  }

  /**
   * Ritorna un valore di contatore incrementando il contatore stesso.
   * @param chiave identificatore del tipo di contatore richiesto
   * @return il valore del contatore
   * @throws Exception
   */
  @Override
  public int getContaInc(String chiave, Connection con)
     throws Exception
  {
    return getContaInc(null, chiave, 0, con);
  }

  /**
   * Ritorna un record di GenGiotab in base ai parametri: all'interno
   * del record e' presente il valore del contatore piu' altri parametri.
   * @param esercizio codice di esercizio richiesto (puo' essere null)
   * @param chiave identificatore del tipo di contatore richiesto
   * @return il valore del contatore
   * @throws Exception
   */
  public synchronized GenGiotab getContaGiotab(String esercizio, String chiave, Connection con)
     throws Exception
  {
    CriteriaRigel c = new CriteriaRigel(GenGiotabPeer.TABLE_NAME);
    if(esercizio != null)
      c.add(GenGiotabPeer.ESERCIZIO, esercizio);
    c.add(GenGiotabPeer.CHIAVE, chiave);

    List v = GenGiotabPeer.doSelect(c, con);
    if(v.isEmpty())
      return creaNuovaChiave(esercizio, chiave);

    return (GenGiotab) v.get(0);
  }

  /**
   * Ritorna un record di GenGiotab in base ai parametri: all'interno
   * del record e' presente il valore del contatore piu' altri parametri.
   * @param chiave identificatore del tipo di contatore richiesto
   * @return il valore del contatore
   * @throws Exception
   */
  public GenGiotab getContaGiotab(String chiave, Connection con)
     throws Exception
  {
    return getContaGiotab(null, chiave, con);
  }

  /**
   * Funzione interna per la creazione di un nuovo record di GenGiotab.
   * Viene utilizzata alla prima richiesta di un tipo di contatore
   * per generare il record corrispondente.
   * @param esercizio codice di esercizio richiesto (puo' essere null)
   * @param chiave identificatore del tipo di contatore richiesto
   * @return
   */
  private GenGiotab creaNuovaChiave(String esercizio, String chiave)
     throws Exception
  {
    GenGiotab g = new GenGiotab();
    g.setEsercizio(esercizio);
    g.setChiave(chiave);
    return g;
  }

  @Override
  public int getContaInc(String esercizio, String chiave, int minVal, int maxVal, Connection con)
     throws Exception
  {
    GenGiotab g = getContaGiotab(esercizio, chiave, con);

    int rv = g.getVint1();
    if(rv <= 0)
      rv = 1;
    if(rv < minVal || rv > maxVal)
      rv = minVal;
    g.setVint1(rv + 1);
    g.save(con);

    return rv;
  }
}
