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
import org.sirio6.modules.screens.rigel.ListaEditBaseMaint;

/**
 * Editing liste-edit XML integrato nella maschera principale (non popup).
 *
 * @author Nicola De Nisco
 */
public class mlistedit extends ListaEditBaseMaint
{
  @Override
  protected String makeSelfUrl(RunData data, String type)
  {
    TemplateURI tl = new TemplateURI(data, "mlistedit.vm"); // NOI18N
    tl.addPathInfo("type", type);
    return tl.getRelativeLink();
  }
}
