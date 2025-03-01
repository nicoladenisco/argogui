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
package org.argogui.services.localization;

import org.argogui.utils.I;
import org.sirio6.services.localization.CoreLocalizationService;

/**
 * Servizio di localizzazione specializzato.
 * Estende le funzioni del serivzio localizzazione di Turbine,
 * aggiungendo anche la gestione dei files translation.xml.
 *
 * Le stringhe non presenti in translation.xml sono inviate alla log
 * con 'Unknow key [stringa non presente]'. Questo consente di
 * recuperarle velocemente con un codice tipo:
 *
 * <code>
 * cat services.log | perl -n -e'/Unknow key \[(.+)\]/ && print $1 . "\n"' | sort | uniq
 * </code>
 *
 * FILENOI18N
 * @author Nicola De Nisco
 */
public class ArgoLocalizationService extends CoreLocalizationService
{
  @Override
  public void initialize()
     throws Exception
  {
    super.initialize(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody

    // imposta localizzatore nella I di om
    I.setInterface((msg) -> getString(null, null, msg));
  }
}
