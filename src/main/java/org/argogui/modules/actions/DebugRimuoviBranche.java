/*
 *  DebugRimuoviBranche.java
 *  Creato il Mar 16, 2017, 7:57:59 PM
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

import org.argogui.om.StpBranche;
import org.argogui.om.StpBranchePeer;
import org.sirio6.utils.CoreRunData;
import java.sql.Connection;
import java.util.List;
import org.apache.torque.criteria.SqlEnum;
import org.apache.velocity.context.Context;
import org.rigel5.db.DbUtils;
import org.rigel5.db.torque.CriteriaRigel;
import org.rigel5.db.torque.PeerTransactAgent;

/**
 * Cancella fisicamente tutte le branche cancellate logicamente.
 * La cancellazione fisica si ripercuote su tutti i dati collegati.
 *
 * @author Nicola De Nisco
 */
public class DebugRimuoviBranche extends ArgoBaseAction
{
  @Override
  public void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    PeerTransactAgent.execute((dbCon) -> cancellaBranche(dbCon));
    data.setMessagei18n("Le branche cancellate logicamente sono state rimosse fisicamente dal db.");
  }

  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return super.isAuthorizedAll(data, "DebugRimuoviBranche"); // NOI18N
  }

  private boolean cancellaBranche(Connection dbCon)
     throws Exception
  {
    CriteriaRigel c = new CriteriaRigel();
    c.add(StpBranchePeer.STATO_REC, 10, SqlEnum.GREATER_EQUAL);
    List<StpBranche> lsBranche = StpBranchePeer.doSelect(c, dbCon);

    for(StpBranche b : lsBranche)
      DbUtils.deleteCascade(dbCon, b.getTableMap(), b.getPrimaryKey());

    return true;
  }
}
