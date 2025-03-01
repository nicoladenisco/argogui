/*
 *  EditStudy.java
 *  Creato il Apr 5, 2017, 4:08:50 PM
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
package org.argogui.modules.screens;

import org.argogui.beans.ModificaDicomBean;
import org.argogui.services.dcmsrv.StudyResultBean;
import org.sirio6.utils.CoreRunData;
import java.util.List;
import org.apache.velocity.context.Context;
import org.sirio6.beans.BeanFactory;
import org.sirio6.beans.NavigationStackBean;

/**
 * Controllore per EditStudy.vm.
 *
 * @author Nicola De Nisco
 */
public class EditStudy extends ArgoBaseScreen
{
  @Override
  protected void doBuildTemplate2(CoreRunData data, Context context)
     throws Exception
  {
    super.doBuildTemplate2(data, context);
    NavigationStackBean nsb = BeanFactory.getFromSession(data, NavigationStackBean.class);
    nsb.pushUri(data);

    ModificaDicomBean bean = BeanFactory.getFromSession(data, ModificaDicomBean.class);
    context.put("bean", bean);

    List<StudyResultBean> clipboard = bean.getClipboard();
    if(clipboard != null && !clipboard.isEmpty())
      context.put("clipboard", clipboard);
  }

  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return isAuthorizedAll(data, "modifica_dicom");
  }
}
