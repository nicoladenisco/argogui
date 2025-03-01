<%--
    Document   : msetup2
    Created on : 31 Agosto 2016
    Author     : Nicola De Nisco
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page errorPage="error.jsp"%>
<jsp:useBean id="msb" scope="session" class="it.radimage.argo.beans.MasterSetupBean" />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Argo CORE master setup</title>
  </head>
  <body>
    <h1>Master CORE setup</h1>

    <%
      boolean isPost = request.getMethod().equalsIgnoreCase("post");

      if(isPost)
      {
    %>
    <code>
      <pre>
        Inizio preparazione base dati ...<br>
        <%
          out.flush();
          msb.runPopolaDB(request, response, out);
        %>
      </pre>
    </code>
    <%
      }
    %>

    <div style="color: yellow; background: red;">
      <h1>GENERAZIONE DATABASE COMPLETATA!!</h1>
      <h3>Master setup completato.</h3>
      <p>Riavviare Tomcat per rendere effettive le modifiche.</p>
    </div>

  </body>
</html>
