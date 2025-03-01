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

import org.argogui.beans.VisRisultatiBean;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * Controllore per VisRadiologia.vm.
 *
 * @author Nicola De Nisco
 */
public class VisRadiologia extends VisRisultatiBaseScreen
{
  @Override
  protected void doWork(RunData data, Context context, VisRisultatiBean bean, VisRisultatiBean.ResBlock block)
  {
    super.doWork(data, context, bean, block);

    if(!block.items.isEmpty())
    {
      VisRisultatiBean.ResItem item = block.items.get(0);
      context.put("info", item);

      String descrizione = "";
      for(VisRisultatiBean.ResItem ri : block.items)
      {
        descrizione += " / " + ri.ie.getDescrizione();
      }

      context.put("descrizione", descrizione.substring(3)); // NOI18N
    }
  }
}
