<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsp-request="http://apache.org/xsp/request/2.0"
>

  <xsl:template match="xsp-request:uri">
    <b><xsl:value-of select="."/></b>
  </xsl:template>

  <xsl:template match="xsp-request:parameter">
    <i><xsl:value-of select="@name"/></i>:<b><xsl:value-of select="."/></b>
  </xsl:template>

  <xsl:template match="xsp-request:parameter-values">
    <p>Parameter Values for "<xsl:value-of select="@name"/>":</p>

    <ul>
      <xsl:for-each select="xsp-request:value">
        <li>
	  <xsl:value-of select="."/>
	</li>
      </xsl:for-each>
    </ul>
  </xsl:template>

  <xsl:template match="xsp-request:parameter-names">
    <p>All Parameter Names:</p>

    <ul>
      <xsl:for-each select="xsp-request:name">
        <li>
	  <xsl:value-of select="."/>
	</li>
      </xsl:for-each>
    </ul>
  </xsl:template>

  <xsl:template match="xsp-request:headers">
    <p>Headers:</p>
    
    <ul>
      <xsl:for-each select="xsp-request:header">
	<li>
          <i><xsl:value-of select="@name"/></i>:
          <b><xsl:value-of select="."/></b>
	</li>
      </xsl:for-each>
    </ul>
    <br/>
  </xsl:template>

  <xsl:template match="xsp-request:header">
    <i><xsl:value-of select="@name"/></i>:<b><xsl:value-of select="."/></b>
  </xsl:template>

  <xsl:template match="xsp-request:header-names">
    <p>All Header names:</p>

    <ul>
      <xsl:for-each select="xsp-request:name">
        <li>
	  <xsl:value-of select="."/>
	</li>
      </xsl:for-each>
    </ul>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-1">
   <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
   </xsl:copy>
  </xsl:template>

  <!-- =================== BYNIK =============== -->

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

  <xsl:template match="user"/>

  <xsl:template match="@*|node()" priority="-2"><xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy></xsl:template>
  <xsl:template match="text()" priority="-1"><xsl:value-of select="."/></xsl:template>

</xsl:stylesheet>
