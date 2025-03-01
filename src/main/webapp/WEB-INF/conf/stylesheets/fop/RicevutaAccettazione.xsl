<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:java="http://xml.apache.org/xslt/java">
  <xsl:template match="AccettazionePazienteBean">
    <fo:root>
      <fo:layout-master-set>
        <fo:simple-page-master master-name="mainPage"
                               page-height="29.7cm" page-width="21cm" margin-top="1cm"
                               margin-bottom="2cm" margin-left="1.5cm" margin-right="1.5cm">
          <fo:region-body region-name="xsl-region-body" margin-top="5cm" margin-left="0.6cm"/>
          <fo:region-before extent="2.0cm"/>
          <fo:region-after extent="1cm"/>
          <fo:region-start extent="2.0cm"/>
          <fo:region-end extent="2.0cm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <fo:page-sequence master-reference="mainPage">
        <xsl:variable name="font-size-label">9pt</xsl:variable>
        <xsl:variable name="font-size-text">10pt</xsl:variable>
        <!-- static e' il contenuto che sara' ripetuto in ogni pagina -->
        <fo:static-content flow-name="xsl-region-before">
          <fo:block font-size="12pt" font-family="Helvetica" color="black" line-height="8mm" text-align="center" space-after="7mm" >
            <!--fo:external-graphic margin-left="0cm" height="2pt" width="2pt" src="C:\app/infomedica.gif">

            </fo:external-graphic-->
            RICEVUTA DI ACCETTAZIONE
          </fo:block>
          <fo:block>
            <fo:table width="21cm" padding="3mm" space-after="15mm">
              <fo:table-column column-width="3cm" />
              <fo:table-column column-width="2cm" />
              <fo:table-column column-width="2cm" />
              <fo:table-body space-after="5mm">
                <fo:table-row >
                  <fo:table-cell >
                    <fo:block font-size="{$font-size-label}">Codice accettazione</fo:block>
                  </fo:table-cell>
                  <fo:table-cell >
                    <fo:block font-size="{$font-size-text}"><xsl:value-of select="codiceAccettazione"/></fo:block>
                  </fo:table-cell>
                </fo:table-row >
                <fo:table-row >
                  <fo:table-cell >
                    <fo:block font-size="{$font-size-label}">Data Accettazione</fo:block>
                  </fo:table-cell>
                  <fo:table-cell >
                    <fo:block font-size="{$font-size-text}"><xsl:value-of select="dataAccettazione"/></fo:block>
                  </fo:table-cell>
                </fo:table-row>
                <fo:table-row >
                  <fo:table-cell >
                    <fo:block font-size="{$font-size-label}">Paziente</fo:block>
                  </fo:table-cell>
                  <fo:table-cell >
                    <fo:block font-size="{$font-size-text}"><xsl:value-of select="cognome"/> </fo:block>
                  </fo:table-cell>
                  <!--/fo:table-row>
                  <fo:table-row -->
                  <!--fo:table-cell >
                  <fo:block font-size="{$font-size-label}">Nome</fo:block>
                  </fo:table-cell-->
                  <fo:table-cell >
                    <fo:block font-size="{$font-size-text}"><xsl:value-of select="nome"/></fo:block>
                  </fo:table-cell>
                </fo:table-row>
                <fo:table-row >
                  <fo:table-cell >
                    <fo:block font-size="{$font-size-label}">Codice fiscale</fo:block>
                  </fo:table-cell>
                  <fo:table-cell >
                    <fo:block font-size="{$font-size-text}"><xsl:value-of select="codiceFiscale"/></fo:block>
                  </fo:table-cell>
                </fo:table-row>										
              </fo:table-body>
            </fo:table>
            <fo:table space-before="60mm">
              <fo:table-column column-width="20cm" />
              <fo:table-body>
                <fo:table-row  >
                  <fo:table-cell >
                    <fo:block  font-size="{$font-size-label}" >Prestazioni: </fo:block>
                  </fo:table-cell>                                        
                </fo:table-row>
              </fo:table-body>
            </fo:table >
          </fo:block>
        </fo:static-content>
        <fo:static-content flow-name="xsl-region-after">
          <fo:block text-align="right">Pagina <fo:page-number/> di
          <fo:page-number-citation ref-id="theEnd"/></fo:block>
        </fo:static-content>
        <fo:flow flow-name="xsl-region-body">
          <xsl:apply-templates select="prestazioni"/>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
  
  <xsl:template match="prestazioni">
    <xsl:for-each select="PrestazioneAccettazioneBean">
      <fo:block keep-together.within-page="always">
        <fo:table width="150mm" padding="3mm" space-after="5mm">
          <fo:table-column column-width="30mm" />
          <fo:table-column column-width="40mm" />
          <fo:table-column column-width="10mm" />
          <fo:table-column column-width="10mm" />
          <fo:table-header>
            <fo:table-cell padding-bottom="3mm">
              <fo:block ></fo:block>
            </fo:table-cell>
          </fo:table-header>
          <fo:table-body >
            <fo:table-row>
              <fo:table-cell >
                <fo:block font-size="8pt">codice:</fo:block>
              </fo:table-cell>
              <fo:table-cell>
                <fo:block font-size="8pt" font-weight="bold"><xsl:value-of select="codice"/></fo:block>
              </fo:table-cell>
            </fo:table-row>
            <fo:table-row>
              <fo:table-cell >
                <fo:block font-size="8pt">codice ministeriale</fo:block>
              </fo:table-cell>
              <fo:table-cell>
                <fo:block font-size="8pt" font-weight="bold"><xsl:value-of select="codiceMinisteriale"/></fo:block>
              </fo:table-cell>
            </fo:table-row>                                                                                                        
            <fo:table-row>
              <fo:table-cell >
                <fo:block font-size="8pt">prestazione</fo:block>
              </fo:table-cell>
              <fo:table-cell number-columns-spanned="4">
                <fo:block font-size="8pt" font-weight="bold"><xsl:value-of select="descrizionePrestazione"/></fo:block>
              </fo:table-cell>
            </fo:table-row>                                                                                                        
          </fo:table-body>
        </fo:table>
      </fo:block>
    </xsl:for-each>           
    <fo:block id="theEnd"/>
  </xsl:template>
</xsl:stylesheet>