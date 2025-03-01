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

import org.argogui.beans.turbine.AccountBean;
import org.sirio6.utils.CoreRunData;
import java.io.InputStream;
import javax.servlet.http.Part;
import org.apache.velocity.context.Context;
import org.sirio6.beans.BeanFactory;

/**
 * Importazione ruoli e permessi da file xml.
 *
 * @author Nicola De Nisco
 */
public class ImportRuoliPermessi extends ArgoBaseAction
{
  @Override
  public void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    AccountBean ab = BeanFactory.getFromSession(data, AccountBean.class);

    Part fi = data.getParameters().getPart("file");
    if(fi == null)
      return;

    boolean ckremove = data.getParameters().getBoolean("ckremove", false); // NOI18N

    try (InputStream is = fi.getInputStream())
    {
      ab.importDaFileXML(fi.getName(), is, ckremove);
      data.setMessagei18n("Import eseguito correttamente: nuovi ruoli e permessi importati.");
    }
  }

  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return super.isAuthorizedAll(data, "admin_users"); // NOI18N
  }
}
