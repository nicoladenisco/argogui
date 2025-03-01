<%@ page contentType="text/xml; charset=UTF8" pageEncoding="UTF-8"%>
<%@ page errorPage="GenericXmlErrorPage.jsp" %>
<%@ page language="java" import="it.radimage.argo.om.*, java.util.*" %>
<%@ page import="org.apache.turbine.om.security.Permission" %>
<%@ page import="org.apache.turbine.util.security.PermissionSet" %>
<%@ page import="org.apache.turbine.om.security.Role" %>
<%@ page import="org.apache.turbine.util.security.RoleSet" %>
<%@ page import="org.apache.turbine.services.security.TurbineSecurity" %>
<%@ page import="it.radimage.argo.services.security.SEC" %>
<%@ page import="it.radimage.argo.om.utils.I"%>

<%
  if(!SEC.checkAnyPermission(session, "visualizza_account"))
    throw new Exception(I.I("Permessi non sufficienti per accedere a queste informazioni."));

  RoleSet rs = TurbineSecurity.getAllRoles();
  PermissionSet ps = TurbineSecurity.getAllPermissions();
%>

<data>
  <roles>
    <%
      for(Iterator it = rs.iterator(); it.hasNext();)
      {
        Role r = (Role) it.next();
        if(r.getName().equals("0"))
          continue;

        out.println("<role name=\"" + r.getName() + "\"/>");
      }
    %>
  </roles>

  <permissions>
    <%
      for(Iterator it = ps.iterator(); it.hasNext();)
      {
        Permission p = (Permission) it.next();
        if(p.getName().equals("0"))
          continue;

        out.println("<permission name=\"" + p.getName() + "\"/>");
      }
    %>
  </permissions>

  <grants>
    <%
      for(Iterator itr = rs.iterator(); itr.hasNext();)
      {
        Role r = (Role) itr.next();
        if(r.getName().equals("0"))
          continue;

        PermissionSet pr = TurbineSecurity.getPermissions(r);
        for(Iterator itp = pr.iterator(); itp.hasNext();)
        {
          Permission p = (Permission) itp.next();
          if(r.getName().equals("0") || p.getName().equals("0"))
            continue;

          out.println("<grant role=\"" + r.getName() + "\" permission=\"" + p.getName() + "\"/>");
        }
      }
    %>
  </grants>
</data>
