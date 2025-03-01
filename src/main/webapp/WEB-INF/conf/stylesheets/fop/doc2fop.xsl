<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsp-request="http://apache.org/xsp/request/2.0"
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
>

  <xsl:template match="/">
   <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <fo:layout-master-set>
     <fo:simple-page-master master-name="page-A4"
                  page-height="29.7cm"
                  page-width="21cm"
                  margin-top="1cm"
                  margin-bottom="1cm"
                  margin-left="0.5cm"
                  margin-right="0.5cm">
       <fo:region-before extent="7cm"/>
       <fo:region-body margin-top="7cm" margin-bottom="2cm"/>
       <fo:region-after extent="2cm"/>
     </fo:simple-page-master>

     <fo:page-sequence-master master-name="all">
       <fo:repeatable-page-master-alternatives>
	 <fo:conditional-page-master-reference master-reference="page-A4" page-position="first"/>
       </fo:repeatable-page-master-alternatives>
     </fo:page-sequence-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="all">

      <!-- ================================================================ -->
      <!-- TESTATA PARTE SUPERIORE                                          -->
      <!-- ================================================================ -->
      <fo:static-content flow-name="xsl-region-before">
	<fo:block text-align="center"
	          font-size="36pt"
		  font-family="serif"
		  line-height="14pt">

	<fo:block font-size="10pt">
	<fo:table>
	  <fo:table-column column-width="80mm"/>
	  <fo:table-column column-width="10mm"/>
	  <fo:table-column column-width="110mm"/>

	  <!-- tabella iniziale con intestazioni -->
	  <fo:table-body>
	    <fo:table-row>
              <fo:table-cell background-color="white">
	        <!-- logo azienda e intestazione -->

		<fo:block>
		<fo:external-graphic content-type="content-type:image/jpg"
			src="file:///usr/local/tdk/webapps/webrap/resources/ui/skins/default/images/logo150.jpg" />
		</fo:block>

		<fo:block space-before="7mm" font-size="24pt">M.I.Medical s.r.l.</fo:block>
		<fo:block space-before="7mm">SS 97 Sannitica, KM 21,200</fo:block>
		<fo:block>S. Marco Evangelista (CE)</fo:block>
		<fo:block>Tel./Fax (+39.0823) 459409 - 421444</fo:block>
		<fo:block>e-mail: info@mimedical.it</fo:block>

	      </fo:table-cell>
              <fo:table-cell background-color="white">
	        <!-- parte centrale: separatore -->
	      </fo:table-cell>
              <fo:table-cell background-color="white">
	        <!-- ragione sociale e dati cliente -->
		<fo:block space-before="35mm" font-size="12pt">
                  <xsl:value-of select="//data/content/header/cliente/DESCRIZIONE/val"/>
                </fo:block>
		<fo:block><xsl:value-of select="//data/content/header/cliente/INDIRIZZO/val"/></fo:block>
		<fo:block>
		<!--
		  <xsl:value-of select="//data/content/header/cliente/CAP/val"/> - <xsl:value-of select="//data/content/header/cliente/CITTA/val"/>
		  (<xsl:value-of select="//data/content/header/cliente/ID_PROVINCIA/val"/>)
		-->
		  <xsl:value-of select="//data/content/header/cliente/CITTA/val"/>

		</fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	  </fo:table-body>
	</fo:table>
	</fo:block>

        <!-- tipo di documento -->
        <fo:block font-size="14pt" space-before="12mm" text-align="left">
          <xsl:value-of select="//data/content/header/doc-des"/>
        </fo:block>

	<!-- tabella con dati TESTATA -->
	<fo:block font-size="9pt" border-style="solid" border-color="black" border-width="1pt" space-before="1mm">
	<fo:table>
	  <fo:table-column column-width="20mm"/>
	  <fo:table-column column-width="20mm"/>
	  <fo:table-column column-width="20mm"/>
	  <fo:table-column column-width="25mm"/>
	  <fo:table-column column-width="20mm"/>
	  <fo:table-column column-width="80mm"/>
	  <fo:table-column column-width="15mm"/>

	  <fo:table-body>
	    <fo:table-row>
              <fo:table-cell background-color="white"
			border-end-style="solid" border-end-color="black" border-end-width="1pt"
			border-bottom-style="solid" border-bottom-color="black" border-bottom-width="1pt">
		<fo:block font-weight="bold">Nr.Doc.</fo:block>
		<fo:block ><xsl:value-of select="//data/content/header/NUM_DOC/val"/></fo:block>
	      </fo:table-cell>
              <fo:table-cell background-color="white"
			border-end-style="solid" border-end-color="black" border-end-width="1pt"
			border-bottom-style="solid" border-bottom-color="black" border-bottom-width="1pt">
		<fo:block font-weight="bold">Data Doc.</fo:block>
		<fo:block ><xsl:value-of select="//data/content/header/DTA_ORD/val"/></fo:block>
	      </fo:table-cell>
              <fo:table-cell background-color="white"
			border-end-style="solid" border-end-color="black" border-end-width="1pt"
			border-bottom-style="solid" border-bottom-color="black" border-bottom-width="1pt">
		<fo:block font-weight="bold">C.Cliente</fo:block>
		<fo:block ><xsl:value-of select="//data/content/header/ID_CLIFOR/id"/></fo:block>
	      </fo:table-cell>
              <fo:table-cell background-color="white"
			border-end-style="solid" border-end-color="black" border-end-width="1pt"
			border-bottom-style="solid" border-bottom-color="black" border-bottom-width="1pt">
		<fo:block font-weight="bold">Partita iva</fo:block>
		<fo:block ><xsl:value-of select="//data/content/header/PARTITA_IVA/val"/></fo:block>
	      </fo:table-cell>
              <fo:table-cell background-color="white"
			border-end-style="solid" border-end-color="black" border-end-width="1pt"
			border-bottom-style="solid" border-bottom-color="black" border-bottom-width="1pt">
		<fo:block font-weight="bold">Porto</fo:block>
		<fo:block ><xsl:value-of select="//data/content/header/ID_PORTO/val"/></fo:block>
	      </fo:table-cell>
              <fo:table-cell background-color="white"
			border-end-style="solid" border-end-color="black" border-end-width="1pt"
			border-bottom-style="solid" border-bottom-color="black" border-bottom-width="1pt">
		<fo:block font-weight="bold">Telefono</fo:block>
		<fo:block ><xsl:value-of select="//data/content/header/ID_VETTORE1/val"/></fo:block>
	      </fo:table-cell>
              <fo:table-cell background-color="white"
			border-bottom-style="solid" border-bottom-color="black" border-bottom-width="1pt">
		<fo:block font-weight="bold">Pagina Nr.</fo:block>
		<fo:block > <fo:page-number/> </fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	  </fo:table-body>
	</fo:table>
	<fo:table>
	  <fo:table-column/>
	  <fo:table-column/>

	  <fo:table-body>
	    <fo:table-row>
              <fo:table-cell background-color="white" border-end-style="solid" border-end-color="black" border-end-width="1pt">
		<fo:block font-weight="bold">Vettore</fo:block>
		<fo:block ><xsl:value-of select="//data/content/header/ID_VETTORE1/val"/></fo:block>
	      </fo:table-cell>
              <fo:table-cell background-color="white">
		<fo:block font-weight="bold">Causale</fo:block>
		<fo:block ><xsl:value-of select="//data/content/header/ID_CAUMAG/val"/></fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	  </fo:table-body>
	</fo:table>
	</fo:block>

	</fo:block>
      </fo:static-content>

      <!-- ================================================================ -->
      <!-- TESTATA PARTE INFERIORE                                          -->
      <!-- ================================================================ -->
      <fo:static-content flow-name="xsl-region-after">
	<fo:block text-align="center"
	          font-size="10pt"
		  font-family="serif"
		  line-height="14pt">

	<!-- riga dei totali -->
	<fo:block font-size="9pt" border-style="solid" border-color="black" border-width="1pt">
	<fo:table>
	  <fo:table-column/>
	  <fo:table-column/>
	  <fo:table-column/>
	  <fo:table-column/>
	  <fo:table-column/>

	  <fo:table-body>
	    <fo:table-row>
              <fo:table-cell background-color="white" border-end-style="solid" border-end-color="black" border-end-width="1pt">
		<fo:block font-weight="bold">Aspetto</fo:block>
		<fo:block ><xsl:value-of select="//data/content/header/ID_ASPETTO/val"/></fo:block>
	      </fo:table-cell>
              <fo:table-cell background-color="white" border-end-style="solid" border-end-color="black" border-end-width="1pt">
		<fo:block font-weight="bold">Colli</fo:block>
		<fo:block ><xsl:value-of select="//data/content/header/COLLI/val"/></fo:block>
	      </fo:table-cell>
              <fo:table-cell background-color="white" border-end-style="solid" border-end-color="black" border-end-width="1pt">
		<fo:block font-weight="bold">Volume</fo:block>
		<fo:block ><xsl:value-of select="//data/content/header/VOLUME/val"/></fo:block>
	      </fo:table-cell>
              <fo:table-cell background-color="white" border-end-style="solid" border-end-color="black" border-end-width="1pt">
		<fo:block font-weight="bold">Peso lordo</fo:block>
		<fo:block ><xsl:value-of select="//data/content/header/PESO_LOR/val"/></fo:block>
	      </fo:table-cell>
              <fo:table-cell background-color="white">
		<fo:block font-weight="bold">Peso netto</fo:block>
		<fo:block ><xsl:value-of select="//data/content/header/PESO_NET/val"/></fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	  </fo:table-body>
	</fo:table>
	</fo:block>

        </fo:block>
      </fo:static-content>

      <!-- ================================================================ -->
      <!-- CORPO DELLA PAGINA (RIGHE DOCUMENTO)                             -->
      <!-- ================================================================ -->
      <fo:flow flow-name="xsl-region-body">
        <xsl:apply-templates/>
      </fo:flow>

    </fo:page-sequence>
   </fo:root>
  </xsl:template>

  <xsl:template match="tablename"/>
  <xsl:template match="user"/>
  <xsl:template match="header"/>

  <xsl:template match="content">
    <xsl:apply-templates select="*[not(self::header)]"/>
  </xsl:template>

  <xsl:template match="detail">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="records">
    <fo:block font-size="8pt" font-family="sans-serif" border="solid black 1pt">
    <fo:table>
      <fo:table-column column-width="40mm"/>
      <fo:table-column column-width="100mm"/>
      <fo:table-column column-width="30mm"/>
      <fo:table-column column-width="30mm"/>
      <fo:table-body>
        <fo:table-row>
	  <fo:table-cell background-color="white" border-end-style="solid" border-end-color="black" border-end-width="1pt"
		border-after-style="solid" border-after-color="black" border-after-width="1pt">
	    <fo:block text-align="center">Codice</fo:block>
	  </fo:table-cell>
	  <fo:table-cell background-color="white" border-end-style="solid" border-end-color="black" border-end-width="1pt"
		border-after-style="solid" border-after-color="black" border-after-width="1pt">
	    <fo:block text-align="center">Descrizione</fo:block>
	  </fo:table-cell>
	  <fo:table-cell background-color="white" border-end-style="solid" border-end-color="black" border-end-width="1pt"
		border-after-style="solid" border-after-color="black" border-after-width="1pt">
	    <fo:block text-align="center">Qta</fo:block>
	  </fo:table-cell>
	  <fo:table-cell background-color="white" border-after-style="solid" border-after-color="black" border-after-width="1pt">
	    <fo:block text-align="center">Unita di misura</fo:block>
	  </fo:table-cell>
        </fo:table-row>

	<xsl:apply-templates/>
      </fo:table-body>
    </fo:table>
    </fo:block>
  </xsl:template>

  <xsl:template match="record">
    <fo:table-row>

	<fo:table-cell background-color="white" border-end-style="solid" border-end-color="black" border-end-width="1pt"
		padding-start="1mm" padding-end="1mm">
	  <fo:block text-align="left"><xsl:value-of select="ID_MAGART/val"/></fo:block>
	</fo:table-cell>

	<fo:table-cell background-color="white" border-end-style="solid" border-end-color="black" border-end-width="1pt"
		padding-start="1mm" padding-end="1mm">
	  <fo:block text-align="left"><xsl:value-of select="DESCRIZIONE/val"/></fo:block>
	</fo:table-cell>

	<fo:table-cell background-color="white" border-end-style="solid" border-end-color="black" border-end-width="1pt"
		padding-start="1mm" padding-end="1mm">
	  <fo:block text-align="right"><xsl:value-of select="QTA/val"/></fo:block>
	</fo:table-cell>

	<fo:table-cell background-color="white" padding-start="1mm" padding-end="1mm">
	  <fo:block text-align="center"><xsl:value-of select="ID_UNMIS/id"/></fo:block>
	</fo:table-cell>

    </fo:table-row>
  </xsl:template>

  <xsl:template match="fields"/>

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
		  line-height="14pt">page <fo:page-number/></fo:block>
      </fo:static-content>

      <fo:flow flow-name="xsl-region-body">
        <xsl:apply-templates/>
      </fo:flow>
    </fo:page-sequence>
   </fo:root>
  </xsl:template>

  <xsl:template match="title">
    <fo:block font-size="36pt" space-before.optimum="24pt" text-align="center"><xsl:apply-templates/></fo:block>
  </xsl:template>

  <xsl:template match="para">
    <fo:block font-size="12pt" space-before.optimum="12pt" text-align="center"><xsl:apply-templates/></fo:block>
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
      <fo:list-item-label><fo:block>*</fo:block></fo:list-item-label>
      <fo:list-item-body><fo:block><xsl:value-of select="."/></fo:block></fo:list-item-body>
    </fo:list-item>
  </xsl:template>

</xsl:stylesheet>
