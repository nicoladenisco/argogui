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

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.uri.TemplateURI;
import org.sirio6.modules.screens.rigel.ListaBasePopup;

/**
 * Visualizzazione di dati tabellari come da plista.xml.
 *
 * @author Nicola De Nisco
 */
public class plista extends ListaBasePopup
{
  @Override
  protected String makeSelfUrl(RunData data, String type)
  {
    TemplateURI tui = new TemplateURI(data, "plista.vm");
    tui.addPathInfo("type", type);
    return tui.getRelativeLink();
  }

}

