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
package org.argogui.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.parser.ParameterParser;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.argogui.utils.SU;
import org.commonlib5.utils.SimpleTimer;
import org.commonlib5.utils.StringOper;
import org.rigel5.SetupHolder;
import org.rigel5.glue.table.AlternateColorTableAppBase;
import org.rigel5.table.RigelTableModel;
import org.rigel5.table.html.AbstractHtmlTablePagerFilter;
import org.rigel5.table.html.hEditTable;
import org.rigel5.table.html.wrapper.HtmlWrapperBase;
import org.rigel5.table.peer.html.PeerWrapperEditHtml;
import org.sirio6.modules.screens.rigel.FormBase;
import org.sirio6.modules.screens.rigel.ListaBase5;
import org.sirio6.modules.screens.rigel.ListaInfo;
import org.sirio6.rigel.CoreCustomUrlBuilder;
import org.sirio6.rigel.DialogCustomUrlBuilder;
import org.sirio6.rigel.DialogRigelUIManager;
import org.sirio6.rigel.RigelHtmlI18n;
import org.sirio6.utils.CoreRunData;

/**
 * Servlet per i form rigel.
 *
 * @author Nicola De Nisco
 */
public class RigelDirectHtml extends HttpServlet
{
  /** Logging */
  private static final Log log = LogFactory.getLog(RigelDirectHtml.class);

  /** A reference to the RunData Service */
  private RunDataService rundataService = null;

  private VelocityService velocityService = null;

  private final DirectFormRender fbRender = new DirectFormRender();
  private final DirectListaRender lsRender = new DirectListaRender();
  private final DialogRigelUIManager uim = new DialogRigelUIManager();
  private final DialogCustomUrlBuilder urb = new DialogCustomUrlBuilder();
  private String titolo, header;
  //
  public static final String MODELLO_LIST = "setup/RigelLista.vm";
  public static final String MODELLO_FORM = "setup/RigelForm.vm";

  public class DirectListaRender extends ListaBase5
  {
    @Override
    public boolean isPopup()
    {
      return false;
    }

    @Override
    public boolean isEditPopup()
    {
      return true;
    }

    @Override
    protected String makeSelfUrl(RunData data, String type)
    {
      return data.getContextPath() + "/rigel/listaPager?type=" + type;
    }

    @Override
    protected void makeContextHtml(HtmlWrapperBase lso, ListaInfo li, CoreRunData data, Context context, String baseUri)
       throws Exception
    {
      titolo = lso.getTitolo();
      header = lso.getHeader();

      AlternateColorTableAppBase act = (AlternateColorTableAppBase) (lso.getTbl());
      act.setAuthDelete(isAuthorizedDelete(data));
      act.setPopup(true);
      act.setEditPopup(true);
      act.setAuthSel(true);
      //act.setPopupEditFunction("apriFinestraEdit_" + li.type);
      act.setUrlBuilder(urb);
      urb.setFunc(li.func);

      AbstractHtmlTablePagerFilter flt = (AbstractHtmlTablePagerFilter) lso.getPager();
      flt.setFormName("fo" + li.type);
      flt.setUim(uim);
      flt.setI18n(new RigelHtmlI18n(data));

      makeContextHtmlSimplified(lso, li, data, context, baseUri);
    }

    public void buildCtx(RunData data, Context ctx)
       throws Exception
    {
      doBuildTemplate2((CoreRunData) data, ctx);
    }
  }

  public class DirectFormRender extends FormBase
  {
    @Override
    public boolean isPopup()
    {
      return true;
    }

    @Override
    public boolean isEditPopup()
    {
      return true;
    }

    @Override
    protected HtmlWrapperBase getForm(CoreRunData data, String type)
       throws Exception
    {
      HtmlWrapperBase pwl = super.getForm(data, type);

      // imposta il nome form nel table model
      RigelTableModel rtm = pwl.getPtm();
      rtm.setFormName("fo" + type);

      return pwl;
    }

    @Override
    protected PeerWrapperEditHtml getLista(CoreRunData data, String type)
       throws Exception
    {
      PeerWrapperEditHtml pwl = (PeerWrapperEditHtml) super.getLista(data, type);

      // imposta il nome form nel table model
      RigelTableModel rtm = pwl.getPtm();
      rtm.setFormName("fo" + type);

      if(pwl.getTbl() instanceof hEditTable)
      {
        hEditTable tbl = (hEditTable) pwl.getTbl();
        tbl.setPopupListaFunction("apriFinestraLista_" + type);
        tbl.setPopupFormFunction("apriFinestraForm_" + type);
      }

      return pwl;
    }

    @Override
    protected String makeSelfUrl(RunData data, String type)
    {
      return data.getRequest().getContextPath() + "/rigel/mform.vm?type=" + type;
    }

    @Override
    protected void makeContextHtml(boolean forceNew, boolean duplica, boolean nuovoDetail,
       Map params, CoreRunData data, Context context, HtmlWrapperBase pwl, String type, String baseUri)
       throws Exception
    {
      titolo = pwl.getTitolo();
      header = pwl.getHeader();

      super.makeContextHtml(forceNew, duplica, nuovoDetail, params, data, context, pwl, type, baseUri);
    }

    public void buildCtx(RunData data, Context ctx)
       throws Exception
    {
      doBuildTemplate2((CoreRunData) data, ctx);
    }
  }

  @Override
  public void init()
     throws ServletException
  {
    super.init();

    if((rundataService = (RunDataService) TurbineServices.getInstance()
       .getService(RunDataService.SERVICE_NAME)) == null)
      throw new ServletException("No RunData Service configured!");

    if((velocityService = (VelocityService) TurbineServices.getInstance()
       .getService(VelocityService.SERVICE_NAME)) == null)
      throw new ServletException("No Velocity Service configured!");

    CoreCustomUrlBuilder ub = (CoreCustomUrlBuilder) SetupHolder.getUrlBuilder();
    urb.setBaseMainForm(ub.getBaseMainForm());
    urb.setBaseMainList(ub.getBaseMainList());
    urb.setBasePopupForm(ub.getBasePopupForm());
    urb.setBasePopupList(ub.getBasePopupList());
  }

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException
  {
    // Placeholder for the RunData object.
    CoreRunData data = null;

    response.setContentType("text/html;charset=UTF-8");
    PrintWriter out = response.getWriter();
    SimpleTimer st = new SimpleTimer();
    try
    {
      // estrae nome della richiesta
      String sRequest = request.getPathInfo().substring(1);

      // Get general RunData here...
      // Perform turbine specific initialization below.
      data = (CoreRunData) rundataService.getRunData(request, response, getServletConfig());
      ParameterParser pp = data.getParameterParser();
      pp.setRequest(request);

      // Pull user from session.
      data.populate();

      int pos = sRequest.indexOf('/');
      if(pos != -1)
      {
        String params = sRequest.substring(pos);
        if(!params.isEmpty())
        {
          for(StringTokenizer stk = new StringTokenizer(params, "/"); stk.hasMoreTokens();)
          {
            String key = stk.nextToken();
            if(stk.hasMoreTokens())
            {
              String value = stk.nextToken();
              pp.add(key, value);
            }
          }
        }

        sRequest = sRequest.substring(0, pos);
      }

      switch(SU.okStr(sRequest))
      {
        case "plista.vm":
          runLista(true, data, out);
          break;
        case "maint.vm":
          runLista(false, data, out);
          break;
        case "pform.vm":
          runForm(true, data, out);
          break;
        case "mform.vm":
          runForm(false, data, out);
          break;

        case "listaPager":
          runListaPager(true, data, out);
          break;

        default:
          throw new ServletException(data.i18n("Richiesta non elaborabile: %s", sRequest));
      }

      out.flush();
      st.waitElapsed(300);
    }
    catch(Exception ex)
    {
      log.error("Rigel direct rendering error", ex); // NOI18N
      throw new ServletException(ex);
    }
    finally
    {
      out.close();

      // Return the used RunData to the factory for recycling.
      rundataService.putRunData(data);
    }
  }

  private void runLista(boolean popup, RunData data, PrintWriter out)
     throws Exception
  {
    String html = getListHtml(data, null);
    out.print(html);
  }

  private void runForm(boolean popup, RunData data, PrintWriter out)
     throws Exception
  {
    String html = getFormHtml(data, null);
    out.print(html);
  }

  public synchronized String getListHtml(RunData data, String params)
     throws Exception
  {
    parseParamToRunData(params, data);

    Context ctx = velocityService.getContext(data);
    lsRender.buildCtx(data, ctx);
    String html = velocityService.handleRequest(ctx, MODELLO_LIST);

    ListaInfo li = ListaInfo.getFromSession((CoreRunData) data);
    html = StringOper.strReplace(html, "document.fo" + li.type + ".submit();", "submit_" + li.type + "();");

    return html;
  }

  public synchronized String getFormHtml(RunData data, String params)
     throws Exception
  {
    parseParamToRunData(params, data);

    Context ctx = velocityService.getContext(data);
    fbRender.buildCtx(data, ctx);
    String html = velocityService.handleRequest(ctx, MODELLO_FORM);

    return html;
  }

  private void parseParamToRunData(String params, RunData data)
  {
    // aggiunge i parametri specificati in params
    if(params != null && !params.isEmpty())
    {
      ParameterParser pp = data.getParameters();
      Map<String, String> values = StringOper.string2Map(params, ",", false);
      for(Map.Entry<String, String> es : values.entrySet())
      {
        String key = es.getKey();
        String value = es.getValue();
        pp.setString(key, value.isEmpty() ? "0" : value);
      }
    }
  }

  private void runListaPager(boolean popup, RunData data, PrintWriter out)
     throws Exception
  {
    String html = getListHtmlPager(data, null);
    out.print(html);
  }

  public synchronized String getListHtmlPager(RunData data, String params)
     throws Exception
  {
    parseParamToRunData(params, data);

    Context ctx = velocityService.getContext(data);
    lsRender.buildCtx(data, ctx);
    String type = SU.okStr(ctx.get("type"));
    String phtml = SU.okStr(ctx.get("phtml"));
    String baseURI = lsRender.makeSelfUrl(data, type);

    StringBuilder html = new StringBuilder(1024);
    html.append("<form name=\"fo").append(type).append("\" id=\"fo").append(type)
       .append("\" method=\"post\" action=\"").append(baseURI).append("\">\n")
       .append(phtml).append("\n")
       .append("</form>\n");

    ListaInfo li = ListaInfo.getFromSession((CoreRunData) data);
    return StringOper.strReplace(html.toString(), "document.fo" + li.type + ".submit();", "submit_" + li.type + "();");
  }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /**
   * Handles the HTTP <code>GET</code> method.
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException
  {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException
  {
    processRequest(request, response);
  }

  /**
   * Returns a short description of the servlet.
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo()
  {
    return "Short description";
  }// </editor-fold>
}
