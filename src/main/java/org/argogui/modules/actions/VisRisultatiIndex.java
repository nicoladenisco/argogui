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

import org.argogui.beans.VisRisultatiBean;
import org.sirio6.utils.CoreRunData;
import java.util.Map;
import org.apache.velocity.context.Context;
import org.sirio6.beans.BeanFactory;

/**
 * Action per attivare la visualizzazione dei risultati.
 * Viene utilizzata dalla lista ordini.
 * Se ci sono risultati validi per l'accettazione indicata
 * passa alla visualizzazione dei risultati.
 *
 * @author Nicola De Nisco
 */
public class VisRisultatiIndex extends ArgoBaseAction
{
  @Override
  public void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    // il parametro ID (id acc) è obbligatorio, IDESA è opzionale
    int idAcc = data.getParameters().getInt("ID");
    int idEsa = data.getParameters().getInt("IDESA");

    if(idAcc == 0)
      data.throwMessagei18n("Parametro ID non specificato nella richiesta.");

    VisRisultatiBean bean = BeanFactory.getFromSession(data, VisRisultatiBean.class);
    int rp = bean.elaboraRisultatiPronti();

    if(rp == 0)
    {
      // nessun risultato: torniamo a Index.vm
      data.setMessagei18n("Nessun risultato pronto.");
      return;
    }

    // estrae mappa risultati
    Map<String, VisRisultatiBean.ResBlock> resMap = bean.getRisultatiPronti();

    // se c'è una richiesta esplicita switch a nuova vista (sempre che contenga risultati)
    VisRisultatiBean.ResBlock block;
    String tipoVis = data.getParameters().getString("vis", bean.getCurrVis());
    if(tipoVis != null && !tipoVis.equals(bean.getCurrVis()) && (block = resMap.get(tipoVis)) != null)
    {
      if(block.screen != null)
      {
        // salta a schermo indicato
        bean.setCurrVis(tipoVis);
        data.setScreenTemplate(block.screen);
        return;
      }
    }

    if(idEsa != 0)
    {
      // se è stato specificato un esame esplicito cerca di aprire la visualizzazione con quell'esame
      for(Map.Entry<String, VisRisultatiBean.ResBlock> rsRes : resMap.entrySet())
      {
        String key = rsRes.getKey();
        VisRisultatiBean.ResBlock value = rsRes.getValue();

        if(value.screen != null && value.haveStudy(idEsa))
        {
          // salta a schermo indicato
          bean.setCurrVis(key);
          data.setScreenTemplate(value.screen);
          return;
        }
      }
    }

    // passa a prima vista valida
    for(Map.Entry<String, VisRisultatiBean.ResBlock> rsRes : resMap.entrySet())
    {
      String key = rsRes.getKey();
      VisRisultatiBean.ResBlock value = rsRes.getValue();

      if(value.screen != null)
      {
        // salta a schermo indicato
        bean.setCurrVis(key);
        data.setScreenTemplate(value.screen);
        return;
      }
    }

    throw new Exception(data.i18n("Errore interno. Visualizzazione non possibile: rivedere flusso."));
  }
}
