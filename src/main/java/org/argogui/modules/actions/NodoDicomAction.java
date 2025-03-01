/*
 *  NodoDicomAction.java
 *  Creato il Jan 13, 2017, 10:25:18 AM
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

import org.argogui.om.StpNodiDicom;
import org.argogui.om.StpNodiDicomPeer;
import org.argogui.services.dcmsrv.DicomServer;
import org.sirio6.utils.CoreRunData;
import org.argogui.utils.SU;
import java.util.Map;
import org.apache.velocity.context.Context;

/**
 * Action per la manipolazione di nodi dicom.
 *
 * @author Nicola De Nisco
 */
public class NodoDicomAction extends ArgoBaseAction
{
  @Override
  public void doPerform2(CoreRunData data, Context context)
     throws Exception
  {
    String command = SU.okStrNull(data.getParameters().getString("command"));

    if(command != null)
    {
      Map params = SU.getParMap(data);
      doCommand(command, data, params, context);
    }
  }

  public void doCmd_ping(CoreRunData data, Map params, Object... args)
     throws Exception
  {
    DicomServer srv = (DicomServer) getService(DicomServer.SERVICE_NAME);
    int idTarget = SU.parseInt(params.get("id"));
    if(idTarget == 0)
      return;

    StpNodiDicom nodo = null;
    switch(srv.pingTarget(idTarget))
    {
      case DicomServer.TEST_TARGET_INVALID_ADDRESS:
        nodo = StpNodiDicomPeer.retrieveByPK(idTarget);
        data.setMessagei18n("Indirizzo %s per %s non valido.", nodo.getIndirizzo(), nodo.getAetitle());
        break;
      case DicomServer.TEST_TARGET_TCP_UNREACHABLE:
        nodo = StpNodiDicomPeer.retrieveByPK(idTarget);
        data.setMessagei18n("Indirizzo %s per %s non raggiungibile.", nodo.getIndirizzo(), nodo.getAetitle());
        break;
      case DicomServer.TEST_TARGET_TCP_OK:
        data.setMessagei18n("OK: collegamento riuscito.");
        break;
      default:
        data.setMessagei18n("Risultato inaspettato!");
        break;
    }
  }

  public void doCmd_echoscu(CoreRunData data, Map params, Object... args)
     throws Exception
  {
    DicomServer srv = (DicomServer) getService(DicomServer.SERVICE_NAME);
    int idTarget = SU.parseInt(params.get("id"));
    if(idTarget == 0)
      return;

    StpNodiDicom nodo = null;
    StringBuilder sb = new StringBuilder();
    switch(srv.echoscuTarget(idTarget, sb))
    {
      case DicomServer.TEST_TARGET_INVALID_ADDRESS:
        nodo = StpNodiDicomPeer.retrieveByPK(idTarget);
        data.setMessagei18n("Indirizzo %s per %s non valido.", nodo.getIndirizzo(), nodo.getAetitle());
        break;
      case DicomServer.TEST_TARGET_TCP_UNREACHABLE:
        nodo = StpNodiDicomPeer.retrieveByPK(idTarget);
        data.setMessagei18n("Indirizzo %s per %s non raggiungibile.", nodo.getIndirizzo(), nodo.getAetitle());
        break;
      case DicomServer.TEST_TARGET_TCP_OK:
        data.setMessagei18n("OK: collegamento riuscito.");
        break;
      case DicomServer.TEST_TARGET_ECHOSCU_FAILURE:
        data.setMessagei18n("ECHO-SCU non eseguito: controllare errori.");
        data.addMessage("<br><pre><code>" + sb.toString() + "</pre></code>");
        break;
      case DicomServer.TEST_TARGET_ECHOSCU_OK:
        data.setMessagei18n("ECHO-SCU alcuni AE-TITLE non sono correttamente configurati: controllare errori.");
        data.addMessage("<br><pre><code>" + sb.toString() + "</pre></code>");
        break;
      case DicomServer.TEST_TARGET_ECHOSCU_ALL_OK:
        data.setMessagei18n("ECHO-SCU eseguito con successo.");
        data.addMessage("<br><pre><code>" + sb.toString() + "</pre></code>");
        break;
      default:
        data.setMessagei18n("Risultato inaspettato!");
        break;
    }
  }
}
