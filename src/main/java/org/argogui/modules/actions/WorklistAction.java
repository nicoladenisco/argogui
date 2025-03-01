/*
 *  WorklistAction.java
 *  Creato il May 7, 2017, 8:14:56 PM
 *
 *  Copyright (C) 2017 RAD-IMAGE s.r.l.
 *
 *  Questo software è proprietà di RAD-IMAGE s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  RAD-IMAGE s.r.l.
 *  Via San Giovanni 1 - Contrada Belvedere
 *  San Nicola Manfredi (BN)
 */
package org.argogui.modules.actions;

import org.argogui.services.wklsrv.WorklistServer;
import org.sirio6.utils.CoreRunData;
import org.argogui.utils.SU;
import java.util.Map;
import org.apache.velocity.context.Context;
import org.rigel5.db.DbUtils;

/**
 * Action per mainipolazione worklist.
 *
 * @author Nicola De Nisco
 */
public class WorklistAction extends ArgoBaseAction
{
  @Override
  public void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    String command = SU.okStrNull(data.getParameters().getString("command"));
    if(command != null)
      doCommand(command, data, null, context);
  }

  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return super.isAuthorizedAll(data, "WorklistAction");
  }

  public void doCmd_pulisci(CoreRunData data, Map params, Object... args)
     throws Exception
  {
    String sSQL = "DELETE FROM RIS.WORKLIST WHERE WORKLIST_ID > 0";
    int numDel = DbUtils.executeStatement(sSQL);
    data.setMessagei18n("Cancellati %d records dalle worklist.", numDel);
  }

  public void doCmd_update(CoreRunData data, Map params, Object... args)
     throws Exception
  {
    WorklistServer ws = (WorklistServer) getService(WorklistServer.SERVICE_NAME);
    ws.uptdateWorklist();
    data.setMessagei18n("Aggiornamento delle worklist avviato.");
  }
}
