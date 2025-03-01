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

import org.argogui.om.StpEsami;
import org.argogui.om.StpEsamiPeer;
import org.sirio6.utils.CoreRunData;
import java.sql.Connection;
import java.util.List;
import org.apache.torque.criteria.Criteria;
import org.apache.velocity.context.Context;
import org.rigel5.db.DbUtils;
import org.rigel5.db.torque.PeerTransactAgent;

/**
 * Cancella tutti gli esami di setup con i relativi dati collegati.
 *
 * @author Nicola De Nisco
 */
public class DebugCancellaSetupEsami extends ArgoBaseAction
{
  private int dc = 0;

  @Override
  public synchronized void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    PeerTransactAgent ta = new PeerTransactAgent()
    {
      @Override
      public boolean run(Connection dbCon, boolean transactionSupported)
         throws Exception
      {
        dc = cancellaEsami(dbCon);
        return true;
      }
    };

    ta.runNow();
    data.setMessagei18n("Cancellati %d esami dal setup.", dc);
  }

  public int cancellaEsami(Connection con)
     throws Exception
  {
    List<StpEsami> lsEsami = StpEsamiPeer.doSelect(new Criteria(), con);

    int count = 0;
    for(StpEsami esa : lsEsami)
    {
      if(esa.getEsamiId() == 0)
        continue;

      DbUtils.deleteCascade(con, esa.getTableMap(), esa.getPrimaryKey());
      count++;
    }

    return count;
  }

  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return isAuthorizedAll(data, "DebugCancellaSetupEsami"); // NOI18N
  }
}
