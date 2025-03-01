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

import org.argogui.services.sync.SyncCaleido;
import org.sirio6.utils.CoreRunData;
import org.apache.velocity.context.Context;

/**
 * Attiva un evento di sincronizzazione con un server Caleido.
 *
 * @author Nicola De Nisco
 */
public class AvviaSincronizzazioneAction extends ArgoBaseAction
{
  @Override
  public void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    SyncCaleido sc = (SyncCaleido) getService(SyncCaleido.SERVICE_NAME);
    if(!sc.isRunning())
    {
      data.setMessagei18n("Il servizio di sincronizzazione non è attivo.");
      return;
    }

    sc.triggerSync();
    data.setMessagei18n("Sincronizzazione avviata. Le operazioni procedono in background.");
  }

  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return super.isAuthorizedAll(data, "SyncCaleido"); // NOI18N
  }
}
