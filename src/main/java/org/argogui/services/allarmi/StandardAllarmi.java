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
package org.argogui.services.allarmi;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.workingdogs.village.Record;
import org.argogui.ArgoBusMessages;
import org.argogui.Costanti;
import org.argogui.om.*;
import org.rigel5.db.DbUtils;
import org.rigel5.db.torque.CriteriaRigel;
import org.sirio6.services.allarmi.AbstractCoreAllarmiService;
import org.sirio6.services.bus.BUS;

/**
 * Implementazione standard del servizio di gestion degli allarmi.
 * @author Nicola De Nisco
 */
public class StandardAllarmi extends AbstractCoreAllarmiService
{
  /** Logging */
  private static Log log = LogFactory.getLog(StandardAllarmi.class);

  // scrive l 'allarme nella tabella
  @Override
  public void allarmLog(String sev, String serv, String comp, String msg, int vis)
  {
    try
    {
      Date now = new Date();

      GenAllarmi all = new GenAllarmi();
      all.setCreazione(now);
      all.setSeverity(sev);
      all.setServizio(serv);
      all.setComponente(comp);
      all.setMessaggio(msg);
      all.setVisibilita(vis);
      all.setStato("A");
      all.setStatoRec(0);
      all.setUltModif(now);
      all.save();

      BUS.sendMessageAsync(ArgoBusMessages.ALLARM_ADDED, this);
    }
    catch(Exception ex)
    {
      log.error("Errore durante l'avvio del servizio GenAllarmi.", ex);
    }
  }

  @Override
  public List checkAllarmi(String serv)
  {
    try
    {
      CriteriaRigel c = new CriteriaRigel();
      c.add(GenAllarmiPeer.STATO, Costanti.ALLARME_ATTIVO);
      c.add(GenAllarmiPeer.SERVIZIO, serv);
      CriteriaRigel.removeDeleted(c, GenAllarmiPeer.TABLE_NAME);
      c.addAscendingOrderByColumn(GenAllarmiPeer.ALLARMI_ID);
      return GenAllarmiPeer.doSelect(c);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      return new Vector();
    }
  }

  @Override
  public List checkAllarmi(String serv, String comp)
  {
    try
    {
      CriteriaRigel c = new CriteriaRigel();
      c.add(GenAllarmiPeer.STATO, Costanti.ALLARME_ATTIVO);
      c.add(GenAllarmiPeer.SERVIZIO, serv);
      c.add(GenAllarmiPeer.COMPONENTE, comp);
      CriteriaRigel.removeDeleted(c, GenAllarmiPeer.TABLE_NAME);
      c.addAscendingOrderByColumn(GenAllarmiPeer.ALLARMI_ID);
      return GenAllarmiPeer.doSelect(c);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      return new Vector();
    }
  }

  @Override
  public int getActiveAllarms()
     throws Exception
  {
    if((System.currentTimeMillis() - lastUpdNum) > DELAY_UPD_NUM)
    {
      String sSQL = "SELECT count(ALLARMI_ID) AS num"
         + " FROM GEN.ALLARMI"
         + " WHERE (STATO_REC IS NULL OR STATO_REC < 10)"
         + " AND STATO='" + Costanti.ALLARME_ATTIVO + "'";

      List lRecs = DbUtils.executeQuery(sSQL);
      if(lRecs.isEmpty())
        return 0;

      Record r = (Record) (lRecs.get(0));
      numActive = r.getValue(1).asInt();

      lastUpdNum = System.currentTimeMillis();
    }

    return numActive;
  }

  public void disattivaAllarmiAttivi(int idUser)
     throws Exception
  {
//    CriteriaRigel crSel = new CriteriaRigel(GenAllarmiPeer.TABLE_NAME);
//    crSel.add(GenAllarmiPeer.STATO, Costanti.ALLARME_ATTIVO);
//
//    CriteriaRigel crUpd = new CriteriaRigel();
//    crUpd.add(GenAllarmiPeer.STATO, Costanti.ALLARME_DISATTIVATO);
//    crUpd.add(GenAllarmiPeer.ID_USER, idUser);
//    crUpd.add(GenAllarmiPeer.ULT_MODIF, new Date());
//
//    GenAllarmiPeer.doUpdate(crSel, crUpd);
    BUS.sendMessageAsync(ArgoBusMessages.ALLARM_SIGNED, this);
  }

  @Override
  protected void deleteAllarmi()
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  protected void deleteCommlog()
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  protected int contaAllarmi()
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public void disattivaAllarme(int idAllarme, int idUser, String messaggio)
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public void disattivaAllarmiAttivi(int idUser, String messaggio)
     throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }
}
