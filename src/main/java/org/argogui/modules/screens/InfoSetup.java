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

import org.argogui.om.GenVersion;
import org.argogui.om.GenVersionPeer;
import org.argogui.services.SERVICE;
import org.sirio6.utils.CoreRunData;
import org.argogui.utils.SU;
import org.argogui.utils.TR;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.*;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;
import javax.servlet.ServletContext;
import org.apache.commons.configuration2.Configuration;
import org.apache.torque.Torque;
import org.apache.turbine.Turbine;
import org.apache.velocity.context.Context;
import org.rigel5.db.torque.CriteriaRigel;
import org.sirio6.beans.CoreBaseBean;
import org.sirio6.services.InfoSetupBlock;
import org.sirio6.services.InfoSetupInterface;
import org.sirio6.services.security.SEC;

/**
 * Controllore per InfoSetup.vm.
 *
 * @author Nicola De Nisco
 */
public class InfoSetup extends ArgoBaseScreen
{
  @Override
  protected void doBuildTemplate2(CoreRunData data, Context context, CoreBaseBean bean)
     throws Exception
  {
    super.doBuildTemplate2(data, context, bean);

    boolean isAdmin = SEC.checkAllPermission(data, "vis-admin-setup");
    List<InfoSetupBlock> arInfo = new ArrayList<InfoSetupBlock>();

    // dati generali
    ServletContext sctx = data.getSession().getServletContext();
    InfoSetupBlock ibGen = new InfoSetupBlock(data.i18n("Generale"), "AAA");
    arInfo.add(ibGen);
    ibGen.add(data.i18n("Directory"), SU.okStr(Turbine.getApplicationRoot())).
       add(data.i18n("Context"), SU.okStr(data.getRequest().getContextPath())).
       add(data.i18n("Durata sessione (secondi)"), SU.okStr(data.getSession().getMaxInactiveInterval())).
       add(data.i18n("Server Info"), SU.okStr(sctx.getServerInfo()));

    int count = 0;
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    Set<ObjectName> objs = mbs.queryNames(new ObjectName("*:type=Connector,*"),
       Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
    String hostname = InetAddress.getLocalHost().getHostName();
    InetAddress[] addresses = InetAddress.getAllByName(hostname);
    for(Iterator<ObjectName> i = objs.iterator(); i.hasNext();)
    {
      ObjectName obj = i.next();
      String scheme = mbs.getAttribute(obj, "scheme").toString();
      String port = obj.getKeyProperty("port");

      String epl = scheme + "://127.0.0.1:" + port;
      ibGen.add(data.i18n("Endpoint %d", ++count), epl);

      for(InetAddress addr : addresses)
      {
        String host = addr.getHostAddress();
        String ep = scheme + "://" + host + ":" + port;
        ibGen.add(data.i18n("Endpoint %d", ++count), ep);
      }
    }

//    ibGen.add(data.i18n("UUID istanza applicazione"), SERVICE.getAppUUID());
    // non legati a nessun servizio
    populateInfoSetup(data, arInfo, isAdmin);
    populateInfoComponenti(data, arInfo, isAdmin);

    // servizi che implementano interfaccia InfoSetupInterface
    List<InfoSetupInterface> lsInfos = SERVICE.getInfoSetup();
    for(InfoSetupInterface isi : lsInfos)
      isi.populateInfoSetup(arInfo, isAdmin);

    // proprietà di sistema
    Properties sysProp = System.getProperties();
    InfoSetupBlock isbSysProp = new InfoSetupBlock("Parametri di sistema", "ZZZ");
    for(Map.Entry<Object, Object> esp : sysProp.entrySet())
    {
      String key = SU.okStr(esp.getKey());
      String val = SU.okStr(esp.getValue());
      isbSysProp.add(key, val);
    }
    arInfo.add(isbSysProp);

    Collections.sort(arInfo, (InfoSetupBlock o1, InfoSetupBlock o2) -> o1.getPriority().compareTo(o2.getPriority()));

    context.put("info", arInfo);
  }

  /**
   * Informazioni di setup.
   * Dati generali di setup non legati a
   * servizi specifici di Argo (database SQL, porta Tomcat, ecc.).
   * @param data
   * @param arInfo
   * @param isAdmin
   * @throws Exception
   */
  public void populateInfoSetup(CoreRunData data, List<InfoSetupBlock> arInfo, boolean isAdmin)
     throws Exception
  {
    Configuration config = Torque.getConfiguration();
    Configuration connection = config.subset("dsfactory.argo.connection");
    arInfo.add(new InfoSetupBlock(data.i18n("Database"), "B").
       add(data.i18n("DB url"), connection.getString("url")).
       add(data.i18n("DB user"), connection.getString("user")).
       add(data.i18n("DB password"), isAdmin ? connection.getString("password") : "****"));

    arInfo.add(new InfoSetupBlock(data.i18n("XML-RPC"), "X").
       add(data.i18n("XML-RPC port"), TR.getString("services.XmlRpcService.port", "12400")));
  }

  /**
   * Informazioni sui componenti installati.
   * Dati sulle versioni dei componenti installati.
   * @param data
   * @param arInfo
   * @param isAdmin
   * @throws Exception
   */
  public void populateInfoComponenti(CoreRunData data, List<InfoSetupBlock> arInfo, boolean isAdmin)
     throws Exception
  {
    List<GenVersion> lsVers = GenVersionPeer.doSelect(new CriteriaRigel());
    HashMap<String, GenVersion> mapComponenti = new HashMap<String, GenVersion>();

    for(GenVersion v : lsVers)
    {
      if(v.getStatoRec() >= 10)
        continue;

      GenVersion vf = mapComponenti.get(v.getComponente());
      if(vf == null || v.compareTo(vf) > 0)
        mapComponenti.put(v.getComponente(), v);
    }

    if(mapComponenti.isEmpty())
      return;

    InfoSetupBlock isb = new InfoSetupBlock(data.i18n("Componenti"), "C");
    for(Map.Entry<String, GenVersion> ecmp : mapComponenti.entrySet())
    {
      String nome = ecmp.getKey();
      GenVersion v = ecmp.getValue();

      isb.add(nome, "<code><pre>" + v.format() + "</pre></code>");
    }
    arInfo.add(isb);
  }
}

/*
  DA InfoSetup.vm

  // istruzioni per scaricamento certificato Informatica Medica

          <tr class="fix-rowheader">
            <td valign="middle" align="center" colspan="2">Setup JNLP e Applet</td>
          </tr>
          <tr class="fix-rowmenu">
            <td valign="middle" align="right"><a href="$content.getURI('infomed.cer')"/>
              <img src="$ui.image('wi0063-48.gif')"></a></td>
            <td valign="middle" align="left">
              $I.I("<p>Per utilizzare alcune componenti è necessario installare questo<br>
                certificato nella propria Java Virtual Machine.</p>
              <p>Utilizzare il link dell'immagine a sinistra per salvare <b>in una directory del proprio hard disk</b><br>
                il certificato collegato.<br></p>
              <p>Utilizzare quindi il pannello di controllo di Java per importarlo come indicato<br>
                nell'immagine sottostante.</p>")
              <img src="$ui.image('helpcert.png')">
            </td>
          </tr>


 */
