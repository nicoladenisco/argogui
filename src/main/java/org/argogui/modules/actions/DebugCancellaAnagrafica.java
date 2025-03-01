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

import org.argogui.om.*;
import org.sirio6.utils.CoreRunData;
import java.sql.Connection;
import java.util.List;
import org.apache.torque.criteria.Criteria;
import org.apache.velocity.context.Context;
import org.rigel5.db.DbUtils;
import org.rigel5.db.torque.PeerTransactAgent;

/**
 * Cancella le anagrafiche e tutti i dati collegati.
 * In pratica cancella tutti i dati di input.
 *
 * @author Nicola De Nisco
 */
public class DebugCancellaAnagrafica extends ArgoBaseAction
{
  private int dc = 0;

  @Override
  public synchronized void doPerform2(final CoreRunData data, Context context)
     throws Exception
  {
    PeerTransactAgent ta = new PeerTransactAgent()
    {
      @Override
      public boolean run(Connection dbCon, boolean transactionSupported)
         throws Exception
      {
        dc = cancellaAnagrafiche(dbCon);

        log.debug(data.i18n("Risultati cancellati."));

        return true;
      }
    };

    ta.runNow();
    data.setMessagei18n("Cancellati %d anagrafiche dal db; cancellati rack e risultati.", dc);
  }

  public int cancellaAnagrafiche(Connection con)
     throws Exception
  {
    List<InfInAnagrafiche> lsAna = InfInAnagrafichePeer.doSelect(new Criteria(), con);

    int count = 0;
    for(InfInAnagrafiche ana : lsAna)
    {
      if(ana.getInAnagraficheId() == 0)
        continue;

      log.debug("Cancello " + ana.getInAnagraficheId() + " " + ana.getCognome()); // NOI18N

      DbUtils.deleteCascade(con, ana.getTableMap(), ana.getPrimaryKey());
      count++;
    }

    return count;
  }

  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return super.isAuthorizedAll(data, "DebugCancellaAnagrafica"); // NOI18N
  }
}
