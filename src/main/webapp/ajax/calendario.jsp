<%--
    Document   : calendario.jsp
    Created on : 8-apr-2015, 17.34.32
    Author     : Nicola De Nisco
--%>

<%@page import="org.sirio.beans.BeanFactory"%>
<%@page import="it.radimage.argo.utils.ArgoRunData"%>
<%@page import="org.sirio.beans.CalendarioBean"%>
<%@page import="org.apache.turbine.services.rundata.TurbineRunDataFacade"%>
<%@page import="java.util.*"%>
<%@page import="org.sirio.utils.format.DateOnlyFormat"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
  // costruisce oggetto RunData estraendolo dal pool
  ArgoRunData data = (ArgoRunData) TurbineRunDataFacade.getRunData(request, response, getServletConfig());

  try
  {
    CalendarioBean bean = BeanFactory.getFromSession(data, CalendarioBean.class);
    data.getParameters().setProperties(bean);
    bean.buildCalendar();

    int giorno = bean.getDay();
    int mese = bean.getMonth();
    int anno = bean.getYear();

    DateOnlyFormat df = new DateOnlyFormat();
    String today = df.format(new Date());

    int nextMese = bean.getMonth() + 1;
    int nextAnno = bean.getYear();
    if(nextMese > 11)
    {
      nextMese = 0;
      nextAnno++;
    }

    int prevMese = bean.getMonth() - 1;
    int prevAnno = bean.getYear();
    if(prevMese < 0)
    {
      prevMese = 11;
      prevAnno--;
    }
%>

<DIV id="calbody">
  <FORM name="calendario">
    <TABLE BORDER=0 width=100%>
      <TR>
        <TD>

          <TABLE width="100%" BORDER="0">
            <TR>
              <TD ALIGN="LEFT" VALIGN="MIDDLE" WIDTH="10" class="manina"
                  onclick="goDay(<%= prevAnno%>,<%= prevMese%>, <%= giorno%>)">
                <a href="#” onclick="goDay(<%= prevAnno%>,<%= prevMese%>, <%= giorno%>)">
                   <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                </a>
              </TD>
              <TD ALIGN=LEFT WIDTH=40% style="font-size: 18px;">
                &nbsp; <%= bean.getMonthName() + " " + anno%>
              </TD>
              <TD WIDTH=40% ALIGN=RIGHT>
                <SELECT NAME="LM" onChange="changeMonth();">
                  <%= bean.getHtmlOpMesi()%>
                </SELECT>

                <SELECT NAME="LY" onChange="changeYear();">
                  <%= bean.getHtmlOpAnni()%>
                </SELECT>
              </TD>
              <TD ALIGN="RIGHT" VALIGN="MIDDLE" WIDTH="10" class="manina"
                  onclick="goDay(<%= nextAnno%>,<%= nextMese%>, <%= giorno%>)">
                <a href="#" onclick="goDay(<%= nextAnno%>,<%= nextMese%>, <%= giorno%>)">
                  <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
                </a>
              </TD>
            </TR>
          </TABLE>

        </TD>
      </TR>
      <TR>
        <TD>

          <TABLE width=100% CELLSPACING=1 CELLPADDING=0 BORDER=0>
            <%= bean.getHtml2() %>
          </TABLE>

        </TD>
      </TR>
      <TR>
        <TD ALIGN="CENTER" VALIGN="MIDDLE">
          <br>

          <button type="button" class="btn btn-sm btn-primary" onClick="changeDay('<%= bean.getFunc()%>', '<%= today%>');
              chiudiCalendario();"><%= data.i18n("Oggi")%> <%= today%></button>
          <button type="button" class="btn btn-sm btn-default"
                  onClick="goDay(<%= bean.getOggiy()%>,<%= bean.getOggim()%>, <%= bean.getOggig()%>)"><%= data.i18n("Vai a oggi")%></button>
          <button type="button" class="btn btn-sm btn-default" onClick="chiudiCalendario()"><%= data.i18n("Chiudi")%></button>

          <% if(bean.isInterval())
            {%>

          <div class="dropdown" style="float: right;">
            <button class="btn btn-info btn-sm dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-expanded="true">
              <%= data.i18n("Intervallo") %>
              <span class="caret"></span>
            </button>
            <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">
              <%= bean.computeIntervals(data) %>
            </ul>
            &nbsp;
          </div>

          <div class="dropdown" style="float: right;">
            <button class="btn btn-info btn-sm dropdown-toggle" type="button" id="dropdownMenu2" data-toggle="dropdown" aria-expanded="true">
              <%= data.i18n("Eta") %>
              <span class="caret"></span>
            </button>
            <ul class="dropdown-menu btn-sm" role="menu" aria-labelledby="dropdownMenu2">
              <%= bean.computeYears(data) %>
            </ul>
            &nbsp;
          </div>

          <%}%>

        </TD>
      </TR>
    </TABLE>

    <input type="hidden" name="day" value="<%= giorno%>">
    <input type="hidden" name="month" value="<%= mese%>">
    <input type="hidden" name="year" value="<%= anno%>">
  </FORM>
</DIV>

<%
  }
  finally
  {
    // restituisce RunData al pool
    TurbineRunDataFacade.putRunData(data);
  }
%>