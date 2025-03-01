<%--
    Document   : msetup1
    Created on : 31 Agosto 2016
    Author     : Nicola De Nisco
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="msb" scope="session" class="it.radimage.argo.beans.MasterSetupBean" />

<%
  boolean isPost = request.getMethod().equalsIgnoreCase("post");

  if(isPost)
    msb.salvaSetup(request);
%>

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Argo CORE master setup</title>
  </head>
  <body>
    <h1>Master CORE setup</h1>
    <h2>Step 2: impostazioni database</h2>

    <form name="fo" method="post" action="msetup2.jsp">
      <fieldset>
        <legend><b>Creazione database</b></legend>
        <b>ATTENZIONE:</b> questa sezione è valida solo per database PostgreSQL.<br>
        <input type="text" name="dbname" value="<%= msb.dbname%>" size="20">&nbsp;Nome del database all'interno del db server.<br>
        <br>
        <input type="checkbox" name="CKD_CUS" value="1" <%= msb.testOdb("CUS")%> />Crea utente nel db server<br>
        <input type="checkbox" name="CKD_CDB" value="1" <%= msb.testOdb("CDB")%> />Crea database nel db server<br>
        <input type="checkbox" name="CKD_STR" value="1" <%= msb.testOdb("STR")%> />Inizializza struttura del db<br>
        <input type="checkbox" name="CKD_IND" value="1" <%= msb.testOdb("IND")%> />Popola database con i dati di base<br>
        <br>
        <input type="checkbox" name="CKD_MSG" value="1" <%= msb.testOdb("MSG")%> />Visualizza tutti i messaggi (verbose)<br>
        <input type="checkbox" name="CKD_UPR" value="1" <%= msb.testOdb("UPR")%> />Converti nomi in maiuscolo (ORACLE)<br>
        <br>
        <input type="text" name="pwdpos" value="<%= msb.pwdpos%>" size="20">&nbsp;Password dell'utente <b>postgres</b> nel db server.
      </fieldset>

      <br>
      <input type="submit" value="Esegui azioni">
    </form>

    <%
      if(isPost)
      {
    %>
    <br>
    <br>
    <hr>
    <h3>Eseguito script ripristina.sh</h3>
    <code>
      <pre>
        STDOUT:
        <%= "\n" + msb.ehStdout%>
        STDERR:
        <%= "\n" + msb.ehStderr%>
      </pre>
    </code>

    <div style="color: yellow; background: red;">
      <h3>Salvataggio eseguito.</h3>
    </div>

    <%
      }
    %>

  </body>
</html>
