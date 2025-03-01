<%@ page contentType="text/xml; charset=UTF8" %>
<%@ page errorPage="GenericXmlErrorPage.jsp" %>
<%@ page language="java" %>
<%@ page import="org.apache.turbine.om.security.User" %>
<jsp:useBean id="blXML" scope="session" class="org.sirio.beans.xml.jsrefxmlBean" />
<jsp:setProperty name="blXML" property="*" />

<data>

<%
User us = blXML.getUser(request);
%>

<user>
    <name><%= us.getName() %></name>
    <first><%= us.getFirstName() %></first>
    <last><%= us.getLastName() %></last>
</user>

<header><%= blXML.getHeader() %></header>
<titolo><%= blXML.getTitolo() %></titolo>
<tablename><%= blXML.getType() %></tablename>

<content>

<% blXML.getXml(out); %>

</content>
</data>

