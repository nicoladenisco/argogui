<%--
    Document   : mastersetup
    Created on : 31 Agosto 2016
    Author     : Nicola De Nisco
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="msb" scope="session" class="it.radimage.argo.beans.MasterSetupBean" />

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Argo CORE master setup</title>
  </head>
  <body>
    <h1>Master CORE setup</h1>
    <h2>Step 1: parametri generali</h2>

    <%
      ServletContext context = session.getServletContext();
      msb.init(context, request.getContextPath());

      // legge file di configurazione
      if(msb.outProperties.exists())
      {
    %>

    <div style="color: black; background: #99ccff;">
      <h2>ATTENZIONE: esiste già una configurazione in <%= msb.outProperties.getAbsolutePath()%></h2>
      <p>Per ripetere la configurazione cancellare il file indicato.</p>
      <ul>
        <li>profile = <%= msb.profile%></li>
        <li>os = <%= msb.oss%></li>
        <li>prod = <%= msb.prod%></li>
      </ul>
    </div>

    <%
    }
    else
    {
    %>

    <form name="fo" method="post" action="msetup1.jsp">

      <script type="text/javascript">
        function impostaDefaultDB()
        {
          document.fo.jdbc.value = "jdbc:postgresql:argo";
          document.fo.user.value = "argo";
          document.fo.pass.value = "argo1";
        }
      </script>

      <table width="100%">
        <tr>
          <td valign="top" width="50%">
            <fieldset>
              <legend><b>Profili</b></legend>
              <input type="checkbox" name="CK_WORKLIST" value="1" <%= msb.testProfile("WORKLIST") %> />Worklist<br>
              <input type="checkbox" name="CK_SYNCALEIDO" value="1" <%= msb.testProfile("SYNCALEIDO") %> />Sincronizzazione<br>
            </fieldset>
            <fieldset>
              <legend><b>Database</b></legend>
              url jdbc<br>
              <input type="text" name="jdbc" value="<%= msb.jdbcPath%>" size="40"><br>
              utente database<br>
              <input type="text" name="user" value="<%= msb.jdbcUser%>" size="40"><br>
              password database<br>
              <input type="text" name="pass" value="<%= msb.jdbcPass%>" size="40"><br>
              <br>
              <input type="button" value="Imposta defaults" onclick="impostaDefaultDB();">
            </fieldset>
          </td>
          <td valign="top" width="50%">
            <fieldset>
              <legend><b>Sistema operativo</b></legend>
              <input type="radio" name="os" value="UNIX" <%= msb.testOss("UNIX")%> >Unix (generico)<br>
              <input type="radio" name="os" value="WIN" <%= msb.testOss("WIN")%> >Windows (generico)<br>
            </fieldset>

            <fieldset>
              <legend><b>Prodotto installato</b></legend>
              <input type="radio" name="prod" value="ARGO" <%= msb.testProd("ARGO")%> >Argo<br>
            </fieldset>
          </td>
        </tr>
      </table>

      <br>
      <input type="submit" value="Salva master setup">
      <br><br>
      La configurazione verrà salvata in <%= msb.outProperties.getAbsolutePath()%>
      <br><br>
    </form>

    <%
      }
    %>
  </body>
</html>
