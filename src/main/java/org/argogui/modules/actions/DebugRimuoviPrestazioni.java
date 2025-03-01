/*
 *  DebugRimuoviPrestazioni.java
 *  Creato il Mar 16, 2017, 8:25:03 PM
 *
 *  Copyright (C) 2017 Informatica Medica s.r.l.
 *
 *  Questo software è proprietà di Informatica Medica s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  Informatica Medica s.r.l.
 *  Viale dei Tigli, 19
 *  Casalnuovo di Napoli (NA)
 */
package org.argogui.modules.actions;

import org.argogui.om.StpEsami;
import org.argogui.om.StpEsamiPeer;
import org.sirio6.utils.CoreRunData;
import java.sql.Connection;
import java.util.List;
import org.apache.torque.criteria.SqlEnum;
import org.apache.velocity.context.Context;
import org.rigel5.db.DbUtils;
import org.rigel5.db.torque.CriteriaRigel;
import org.rigel5.db.torque.PeerTransactAgent;

/**
 * Cancella fisicamente tutte le prestazioni cancellate logicamente.
 * La cancellazione fisica si ripercuote su tutti i dati collegati.
 *
 * @author Nicola De Nisco
 */
public class DebugRimuoviPrestazioni extends ArgoBaseAction
{
  @Override
  public void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    PeerTransactAgent.execute((dbCon) -> cancellaPrestazioni(dbCon));
    data.setMessagei18n("Le prestazioni cancellate logicamente sono state rimosse fisicamente dal db.");
  }

  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return super.isAuthorizedAll(data, "DebugRimuoviPrestazioni"); // NOI18N
  }

  private boolean cancellaPrestazioni(Connection dbCon)
     throws Exception
  {
    CriteriaRigel c = new CriteriaRigel();
    c.add(StpEsamiPeer.STATO_REC, 10, SqlEnum.GREATER_EQUAL);
    List<StpEsami> lsEsami = StpEsamiPeer.doSelect(c, dbCon);

    for(StpEsami b : lsEsami)
      if(b.getEsamiId() != 0)
        DbUtils.deleteCascade(dbCon, b.getTableMap(), b.getPrimaryKey());

    return true;
  }
}
