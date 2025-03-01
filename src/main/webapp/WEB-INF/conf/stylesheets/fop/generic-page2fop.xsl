<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsp-request="http://apache.org/xsp/request/2.0"
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
>

  <xsl:include href="../page/simple-page2fo.xsl"/>

  <xsl:template match="xsp-request:uri">
    <fo:block font-weight="bold"><xsl:value-of select="."/></fo:block>
  </xsl:template>

  <xsl:template match="xsp-request:parameter">
	    <fo:block>

          	<fo:inline font-style="italic"><xsl:value-of select="@name"/></fo:inline>:
          	<fo:inline font-weight="bold"><xsl:value-of select="."/></fo:inline>

	    </fo:block>
  </xsl:template>

  <xsl:template match="xsp-request:parameter-values">
    <fo:block>Parameter Values for "<xsl:value-of select="@name"/>":</fo:block>

    <fo:list-block>
      <xsl:for-each select="xsp-request:value">
        <fo:list-item>

          <fo:list-item-label><fo:block>*</fo:block></fo:list-item-label>
          <fo:list-item-body><fo:block><xsl:value-of select="."/></fo:block></fo:list-item-body>
	  
	</fo:list-item>
      </xsl:for-each>
    </fo:list-block>
  </xsl:template>

  <xsl:template match="xsp-request:parameter-names">
    <fo:block>All Parameter Names:</fo:block>

    <fo:list-block>
      <xsl:for-each select="xsp-request:name">
        <fo:list-item>
          <fo:list-item-label><fo:block>*</fo:block></fo:list-item-label>
          <fo:list-item-body><fo:block><xsl:value-of select="."/></fo:block></fo:list-item-body>
	</fo:list-item>
      </xsl:for-each>
    </fo:list-block>
  </xsl:template>

  <xsl:template match="xsp-request:headers">
    <fo:block>Headers:</fo:block>
    
    <fo:list-block>
      <xsl:for-each select="xsp-request:header">
	<fo:list-item>

          <fo:list-item-label><fo:block>*</fo:block></fo:list-item-label>
          <fo:list-item-body>
	    <fo:block>

          	<fo:inline font-style="italic"><xsl:value-of select="@name"/></fo:inline>:
          	<fo:inline font-weight="bold"><xsl:value-of select="."/></fo:inline>

	    </fo:block>
	  </fo:list-item-body>

	</fo:list-item>
      </xsl:for-each>
    </fo:list-block>
  </xsl:template>

  <xsl:template match="xsp-request:header">
	    <fo:block>

          	<fo:inline font-style="italic"><xsl:value-of select="@name"/></fo:inline>:
          	<fo:inline font-weight="bold"><xsl:value-of select="."/></fo:inline>

	    </fo:block>
  </xsl:template>

  <xsl:template match="xsp-request:header-names">
    <fo:block>All Header names:</fo:block>

    <fo:list-block>
      <xsl:for-each select="xsp-request:name">
        <fo:list-item>
          <fo:list-item-label><fo:block>*</fo:block></fo:list-item-label>
          <fo:list-item-body><fo:block><xsl:value-of select="."/></fo:block></fo:list-item-body>
	</fo:list-item>
      </xsl:for-each>
    </fo:list-block>
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
    <fo:block font-size="10pt" font-family="sans-serif" border="solid black 1pt">
    <fo:table>
      <xsl:for-each select="nth/ntd">
        <fo:table-column column-width="30mm"/>
      </xsl:for-each>
      <fo:table-body>
	<xsl:apply-templates/>
      </fo:table-body>
    </fo:table>
    </fo:block>
  </xsl:template>

  <xsl:template match="ntr">
    <fo:table-row>

      <xsl:for-each select="ntd">

      <fo:table-cell background-color="white">
        <fo:block text-align="center">
	  <xsl:value-of select="."/>
        </fo:block>
      </fo:table-cell>

      </xsl:for-each>

    </fo:table-row>
  </xsl:template>

  <xsl:template match="nth">
    <fo:table-row>

      <xsl:for-each select="ntd">

      <fo:table-cell background-color="lightgray">
        <fo:block text-align="center">
	  <xsl:value-of select="."/>
        </fo:block>
      </fo:table-cell>

      </xsl:for-each>

    </fo:table-row>
  </xsl:template>

  <xsl:template match="ntd"/>

  <xsl:template match="ul">
    <fo:list-block>
      <xsl:apply-templates/>
    </fo:list-block>
  </xsl:template>

  <xsl:template match="li">
    <fo:list-item>
      <fo:list-item-label><fo:block>*</fo:block></fo:list-item-label>
      <fo:list-item-body><fo:block><xsl:value-of select="."/></fo:block></fo:list-item-body>
    </fo:list-item>
  </xsl:template>

</xsl:stylesheet>
