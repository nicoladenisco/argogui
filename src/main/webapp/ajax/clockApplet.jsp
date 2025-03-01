<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>
<%@page import="java.text.*" %>
<%@page import="it.radimage.argo.utils.*" %>

<%
  Date now = new Date();
  String sData = DT.formatData(now);
  String sOra = DT.formatTime(now);
%>

<div style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 24px; color: red;">
  <%= sOra%>
</div>
<div style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 12px; color: red;">
  <%= sData%>
</div>

