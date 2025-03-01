<%--
    Document   : memory
    Created on : 30-mar-2010, 10.08.34
    Author     : Nicola De Nisco
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
  <%@page import="java.lang.management.*"%>
  <%@page import="java.util.*"%>

<html>
  <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JVM Memory Monitor</title>
    <style type="text/css">
      td {
        text-align: right;
      }
    </style>
  </head>
<body>

  <%!

    public static final long MB_SIZE = 1024 * 1024;

    String fmtMemory(long l)
    {
      return Long.toString(l / MB_SIZE) +  " Mb";
    }

  %>

  <%
    MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    List<MemoryPoolMXBean> poolBeans = ManagementFactory.getMemoryPoolMXBeans();
  %>

  <h3>Total Memory</h3>
  <table border="1" width="100%">
    <tr>
      <th>usage</th>
      <th>init</th>
      <th>used</th>
      <th>committed</th>
      <th>max</th>
    </tr>
    <tr>
      <td style="text-align: left">Heap Memory Usage</td>
      <td><%= fmtMemory( memoryBean.getHeapMemoryUsage().getInit() ) %></td>
      <td><%= fmtMemory( memoryBean.getHeapMemoryUsage().getUsed() ) %></td>
      <td><%= fmtMemory( memoryBean.getHeapMemoryUsage().getCommitted() ) %></td>
      <td><%= fmtMemory( memoryBean.getHeapMemoryUsage().getMax() ) %></td>
    </tr>
    <tr>
      <td style="text-align: left">Non-heap Memory Usage</td>
      <td><%= fmtMemory( memoryBean.getNonHeapMemoryUsage().getInit() ) %></td>
      <td><%= fmtMemory( memoryBean.getNonHeapMemoryUsage().getUsed() ) %></td>
      <td><%= fmtMemory( memoryBean.getNonHeapMemoryUsage().getCommitted() ) %></td>
      <td><%= fmtMemory( memoryBean.getNonHeapMemoryUsage().getMax() ) %></td>
    </tr>
  </table>

  <h3>Memory Pools</h3>
  <table border="1" width="100%">
    <tr>
      <th>name</th>
      <th>usage</th>
      <th>init</th>
      <th>used</th>
      <th>committed</th>
      <th>max</th>
    </tr>
    <%
      Iterator<MemoryPoolMXBean> itrBeans = poolBeans.iterator();
      while(itrBeans.hasNext())
      {
        MemoryPoolMXBean bean = itrBeans.next();
    %>
      <tr>
        <td style="text-align: left"><%= bean.getName() %></td>
        <td style="text-align: left">Memory Usage</td>
        <td><%= fmtMemory( bean.getUsage().getInit() ) %></td>
        <td><%= fmtMemory( bean.getUsage().getUsed() ) %></td>
        <td><%= fmtMemory( bean.getUsage().getCommitted() ) %></td>
        <td><%= fmtMemory( bean.getUsage().getMax() ) %></td>
      </tr>
      <tr>
        <td></td>
        <td style="text-align: left">Peak Usage</td>
        <td><%= fmtMemory( bean.getPeakUsage().getInit() ) %></td>
        <td><%= fmtMemory( bean.getPeakUsage().getUsed() ) %></td>
        <td><%= fmtMemory( bean.getPeakUsage().getCommitted() ) %></td>
        <td><%= fmtMemory( bean.getPeakUsage().getMax() ) %></td>
      </tr>
     <%
       }
     %>
  </table>

</body>
</html>
