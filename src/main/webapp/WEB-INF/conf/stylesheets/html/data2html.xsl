<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="data">
   <html>
    <head>
     <title>
      Elenco <xsl:value-of select="tablename"/>
     </title>
    </head>
    <body bgcolor="white" alink="red" link="blue" vlink="blue">
     <xsl:apply-templates/>
    </body>
   </html>
  </xsl:template>

  <xsl:template match="content">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="tablename">
   <h2 style="color: navy; text-align: center">
      Elenco <xsl:apply-templates/>
   </h2>
  </xsl:template>

  <xsl:template match="para">
   <p align="left">
    <xsl:apply-templates/>
   </p>
  </xsl:template>

  <xsl:template match="records">
    <CENTER>
    <TABLE BORDER="1" bgcolor="navy" width="100%">
      <xsl:apply-templates/>
    </TABLE>
    </CENTER>
  </xsl:template>

  <xsl:template match="record">
    <TR bgcolor="white">
      <xsl:apply-templates/>
    </TR>
  </xsl:template>

  <xsl:template match="fields">
    <TR bgcolor="yellow">
      <xsl:apply-templates/>
    </TR>
  </xsl:template>

  <xsl:template match="fld">
    <xsl:element name="TD">
      <xsl:attribute name="WIDTH"><xsl:value-of select="@WIDTH"/>%</xsl:attribute>
      <xsl:if test="@align">
	<xsl:attribute name="align"><xsl:value-of select="@align"/></xsl:attribute>
      </xsl:if>	
      <xsl:value-of select="name"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="df">
    <xsl:element name="TD">
      <xsl:if test="@align">
	<xsl:attribute name="align"><xsl:value-of select="@align"/></xsl:attribute>
      </xsl:if>	
      <xsl:value-of select="."/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="user"/>

  <!-- ========================================= -->
  <!-- Per la visualizzazione pagine d'errore    -->
  <!-- ========================================= -->

  <xsl:template match="content">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="ntable">
    <TABLE BORDER="1" bgcolor="navy">
      <xsl:apply-templates/>
    </TABLE>
  </xsl:template>

  <xsl:template match="ntr">
    <TR bgcolor="white">
      <xsl:apply-templates/>
    </TR>
  </xsl:template>

  <xsl:template match="nth">
    <TR bgcolor="yellow">
      <xsl:apply-templates select="*"/>
    </TR>
  </xsl:template>

  <xsl:template match="ntd">
    <TD>
      <xsl:value-of select="."/>
    </TD>
  </xsl:template>

  <xsl:template match="page">
   <html>
    <head>
     <title>
      <xsl:value-of select="title"/>
     </title>
    </head>
    <body bgcolor="white" alink="red" link="blue" vlink="blue">
     <xsl:apply-templates/>
    </body>
   </html>
  </xsl:template>

  <xsl:template match="title">
   <h2 style="color: navy; text-align: center">
      <xsl:apply-templates/>
   </h2>
  </xsl:template>

  <xsl:template match="para">
   <p align="left">
    <xsl:apply-templates/>
   </p>
  </xsl:template>

  <xsl:template match="para-pre">
   <p align="left"><pre>
    <xsl:apply-templates/>
   </pre></p>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-2"><xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy></xsl:template>
  <xsl:template match="text()" priority="-1"><xsl:value-of select="."/></xsl:template>

</xsl:stylesheet>
