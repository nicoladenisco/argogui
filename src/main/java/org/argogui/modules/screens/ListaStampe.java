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
package org.argogui.modules.screens;

import org.argogui.om.*;
import org.sirio6.utils.CoreRunData;
import java.util.List;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.rigel5.db.torque.CriteriaRigel;

/**
 * Controllore della lista statistiche.
 *
 * @author Mario Mazzetti
 * @author Nicola De Nisco
 */
public class ListaStampe extends ArgoBaseScreen
{
  @Override
  protected void doBuildTemplate2(CoreRunData data, Context context)
     throws Exception
  {
    BuildFields(data, context);
  }

  protected void BuildFields(RunData data, Context context)
     throws Exception
  {
    CriteriaRigel cStatistiche = new CriteriaRigel(GenStampePeer.TABLE_NAME);
    cStatistiche.addAscendingOrderByColumn(GenStampePeer.STAMPE_ID);
    List lStampe = GenStampePeer.doSelect(cStatistiche);
    context.put("lstampe", lStampe);
  }

  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return super.isAuthorizedAll(data, "lista_stampe");
  }
}
