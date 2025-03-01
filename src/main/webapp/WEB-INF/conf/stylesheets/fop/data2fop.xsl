<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsp-request="http://apache.org/xsp/request/2.0"
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
>

  <xsl:template match="data">
   <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <fo:layout-master-set>
     <fo:simple-page-master master-name="page-A4"
                  page-height="29.7cm"
                  page-width="21cm"
                  margin-top="1cm"
                  margin-bottom="1cm"
                  margin-left="0.5cm"
                  margin-right="0.5cm">
       <fo:region-body margin-top="20mm" margin-bottom="6mm"/>
       <fo:region-before extent="20mm"/>
       <fo:region-after extent="5mm"/>
     </fo:simple-page-master>

     <fo:page-sequence-master master-name="all">
       <fo:repeatable-page-master-alternatives>
	 <fo:conditional-page-master-reference master-reference="page-A4"/>
       </fo:repeatable-page-master-alternatives>
     </fo:page-sequence-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="all">

      <fo:static-content flow-name="xsl-region-before">
	<fo:block text-align="center"
	          font-size="26pt"
		  font-family="serif"
		  line-height="24pt">
	 <xsl:value-of select="header"/>
	</fo:block>

	<fo:block text-align="center"
	          font-size="14pt"
		  font-family="serif"
		  line-height="14pt">
	 <xsl:value-of select="titolo"/>
	</fo:block>

        <!-- barra superiore con i nomi dei campi -->
        <fo:block font-size="8pt" font-family="sans-serif" border="solid black 1pt">
        <fo:table>
              <xsl:for-each select="/data/content/records/column-sizes/column">
            <xsl:element name="fo:table-column">
              <!-- calcola la dimensione della colonna in millimetri:
                  size e' normalizzato a 1 quindi la formula è
                  size * 200 dove 200 e' la larghezza in mm dell'area
                   utile sul foglio -->
                  <xsl:attribute name="column-width"><xsl:value-of select="number(@size) * 200.0"/>mm</xsl:attribute>
              <xsl:value-of select="name"/>
            </xsl:element>
          </xsl:for-each>
          <fo:table-body>
            <fo:table-row>
              <xsl:for-each select="/data/content/records/fields/fld">
                <fo:table-cell background-color="lightgray">
                  <xsl:element name="fo:block">
                    <xsl:if test="@align">
                          <xsl:attribute name="text-align">
                            <xsl:value-of select="@align"/>
                          </xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="name"/>
                  </xsl:element>
                </fo:table-cell>
              </xsl:for-each>
            </fo:table-row>
          </fo:table-body>
        </fo:table>
        </fo:block>

      </fo:static-content>

      <fo:static-content flow-name="xsl-region-after">
	<fo:block text-align="center"
	          font-size="10pt"
		  font-family="serif"
                    line-height="14pt">Pagina
            <fo:page-number/>
          </fo:block>
      </fo:static-content>

      <fo:flow flow-name="xsl-region-body">
        <xsl:apply-templates/>
      </fo:flow>

    </fo:page-sequence>
   </fo:root>
  </xsl:template>

  <xsl:template match="tablename"/>
  <xsl:template match="user"/>
  <xsl:template match="header"/>
  <xsl:template match="titolo"/>

  <xsl:template match="content">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="group">
    <fo:block font-size="8pt" font-family="sans-serif" border="solid black 4pt">
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="records">
    <fo:block font-size="8pt" font-family="sans-serif" border="solid black 1pt">
    <fo:table>
        <xsl:for-each select="column-sizes/column">
	<xsl:element name="fo:table-column">
          <!-- calcola la dimensione della colonna in millimetri:
               size e' normalizzato a 1 quindi la formula è
               size * 200 dove 200 e' la larghezza in mm dell'area
               utile sul foglio -->
            <xsl:attribute name="column-width"><xsl:value-of select="number(@size) * 200.0"/>mm</xsl:attribute>
	  <xsl:value-of select="name"/>
	</xsl:element>
      </xsl:for-each>
      <fo:table-body>
	<xsl:apply-templates/>
      </fo:table-body>
    </fo:table>
    </fo:block>
  </xsl:template>

  <xsl:template match="b">
    <fo:block font-weight="bold">
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="df">
    <fo:table-cell background-color="white">
      <xsl:element name="fo:block">
        <xsl:if test="@align">
          <xsl:attribute name="text-align">
            <xsl:value-of select="@align"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:apply-templates/>
      </xsl:element>
    </fo:table-cell>
  </xsl:template>

  <xsl:template match="val">
    <xsl:if test="count(b)=1">
      <fo:block font-weight="bold">
        <xsl:value-of select="b"/>
      </fo:block>
    </xsl:if>
    <xsl:if test="count(b)=0">
      <fo:block>
        <xsl:value-of select="."/>
      </fo:block>
    </xsl:if>
  </xsl:template>

  <xsl:template match="record">
    <fo:table-row>
      <xsl:apply-templates/>
    </fo:table-row>
  </xsl:template>

  <xsl:template match="fields"/>

  <xsl:template match="head">
    <fo:block font-size="8pt" font-family="sans-serif" border="solid black 1pt">
    <fo:table>

      <xsl:for-each select="hfld/fld">
	<xsl:element name="fo:table-column">
          <!-- calcola la dimensione della colonna in millimetri:
               with e' normalizzato a 100 quindi la formula sarebbe:
               (width * 200)/100 dove 200 e' la larghezza in mm dell'area
               utile sul foglio -->
            <xsl:attribute name="column-width">
              <xsl:value-of select="number(@WIDTH) * 2.0"/>mm
            </xsl:attribute>
	  <xsl:value-of select="name"/>
	</xsl:element>
      </xsl:for-each>

      <fo:table-body>
        <fo:table-row>
        <xsl:for-each select="hfld/fld">

          <fo:table-cell background-color="lightgray">
          <xsl:element name="fo:block">
            <xsl:if test="@align">
                    <xsl:attribute name="text-align">
                      <xsl:value-of select="@align"/>
                    </xsl:attribute>
            </xsl:if>
            <xsl:value-of select="name"/>
          </xsl:element>
          </fo:table-cell>

        </xsl:for-each>
        </fo:table-row>

        <fo:table-row>
        <xsl:for-each select="hrec/df">

          <fo:table-cell background-color="white">
          <xsl:element name="fo:block">
            <xsl:if test="@align">
                    <xsl:attribute name="text-align">
                      <xsl:value-of select="@align"/>
                    </xsl:attribute>
            </xsl:if>
            <xsl:value-of select="val"/>
          </xsl:element>
          </fo:table-cell>

        </xsl:for-each>
        </fo:table-row>
      </fo:table-body>

    </fo:table>
    </fo:block>
  </xsl:template>

  <xsl:template match="ntd"/>

  <xsl:template match="ul">
    <fo:list-block>
      <xsl:apply-templates/>
    </fo:list-block>
  </xsl:template>

  <xsl:template match="li">
    <fo:list-item>
      <fo:list-item-label>
        <fo:block>*</fo:block>
      </fo:list-item-label>
      <fo:list-item-body>
        <fo:block>
          <xsl:value-of select="."/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>

  <!-- ========================================= -->
  <!-- Per la visualizzazione pagine d'errore    -->
  <!-- ========================================= -->

  <xsl:template match="page">
   <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <fo:layout-master-set>
     <fo:simple-page-master master-name="page"
                  page-height="29.7cm"
                  page-width="21cm"
                  margin-top="1cm"
                  margin-bottom="2cm"
                  margin-left="2.5cm"
                  margin-right="2.5cm">
       <fo:region-before extent="3cm"/>
       <fo:region-body margin-top="3cm" margin-bottom="2cm"/>
       <fo:region-after extent="1.5cm"/>
     </fo:simple-page-master>

     <fo:page-sequence-master master-name="all">
       <fo:repeatable-page-master-alternatives>
	 <fo:conditional-page-master-reference master-reference="page" page-position="first"/>
       </fo:repeatable-page-master-alternatives>
     </fo:page-sequence-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="all">
      <fo:static-content flow-name="xsl-region-after">
	<fo:block text-align="center"
	          font-size="10pt"
		  font-family="serif"
                    line-height="14pt">page
            <fo:page-number/>
          </fo:block>
      </fo:static-content>

      <fo:flow flow-name="xsl-region-body">
        <xsl:apply-templates/>
      </fo:flow>
    </fo:page-sequence>
   </fo:root>
  </xsl:template>

  <xsl:template match="title">
    <fo:block font-size="36pt" space-before.optimum="24pt" text-align="center">
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="para">
    <fo:block font-size="12pt" space-before.optimum="12pt" text-align="center">
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="para-pre">
    <fo:block font-size="12pt" font-family="courier" space-before.optimum="12pt" text-align="center">
	<xsl:apply-templates/>
    </fo:block>
  </xsl:template>

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
      <fo:list-item-label>
        <fo:block>*</fo:block>
      </fo:list-item-label>
      <fo:list-item-body>
        <fo:block>
          <xsl:value-of select="."/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>

</xsl:stylesheet>
