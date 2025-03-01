<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:java="http://xml.apache.org/xslt/java">
	<xsl:template match="PrenotazioniPazienteBean">
            <fo:root>
                     <!-- The full XSL-FO document goes here -->
                     <fo:layout-master-set>
                            <!-- All page templates go here -->
                            <fo:simple-page-master master-name="mainPage"
                                    page-height="29.7cm" page-width="21cm" margin-top="1cm"
                                    margin-bottom="1.5cm" margin-left="1.5cm" margin-right="1.5cm">
                                    <fo:region-body region-name="xsl-region-body" margin-top="3.5cm" margin-left="0.6cm"/>
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
                            <fo:block font-size="12pt" font-family="Helvetica" color="black" line-height="8mm" text-align="center" space-after="7mm">
                                RICEVUTA DI PRENOTAZIONE
                            </fo:block>
                            <fo:block>
                                <!--fo:table width="90mm" padding="30mm" space-after="15mm"-->
                                <fo:table width="21cm" padding="30mm" space-after="15mm">
                                    <!-- NUMERO COLONNE TABELLA -->
                                    <!--fo:table-column column-width="30mm" />
                                    <fo:table-column column-width="20mm" />
                                    <fo:table-column column-width="10mm" />
                                    <fo:table-column column-width="30mm" /-->
                                    
                                    <fo:table-column column-width="7cm" />
                                    <fo:table-column column-width="1cm" />
                                    <fo:table-column column-width="1cm" />
                                    <fo:table-column column-width="7cm" />
                                    <!-- HEADER TABELLA -->
                                    <!-- CORPO TABELLA - INIZIO -->
                                    <fo:table-body space-after="5mm">
                                        <fo:table-row >
                                                <fo:table-cell >
                                                        <fo:block font-size="{$font-size-label}">CODICE PAZIENTE: </fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell >
                                                        <fo:block font-size="{$font-size-text}"> <xsl:value-of select="paziente/codiceAlternativo"/> </fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell >
                                                        <fo:block font-size="{$font-size-label}">SIG.</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell >
                                                        <fo:block font-size="{$font-size-text}"> <xsl:value-of select="paziente/cognome"/> </fo:block>
                                                </fo:table-cell>
                                        </fo:table-row>
                                        </fo:table-body>
                               </fo:table>
                               <fo:table space-before="25mm">
                                    <fo:table-column column-width="20cm" />
                                        <fo:table-body>
                                        <fo:table-row  >
                                                <fo:table-cell >
                                                        <fo:block  font-size="{$font-size-label}" >Le abbiamo riservato i seguenti appuntamenti </fo:block>
                                                </fo:table-cell>                                        
                                        </fo:table-row>
                                        
                                    </fo:table-body>
                                    <!-- CORPO TABELLA - FINE -->
                                </fo:table >
                                
                                <!--fo:block space-before="3mm">
                                Le abbiamo riservato i seguenti appuntamenti
                                </fo:block-->
                            
                            </fo:block>
                        </fo:static-content>
                        
<fo:static-content flow-name="xsl-region-after">
  <fo:block text-align="right">Pagina <fo:page-number/> di
<fo:page-number-citation ref-id="theEnd"/></fo:block>
  
</fo:static-content>
                        <fo:flow flow-name="xsl-region-body">
                            <xsl:apply-templates select="listaPrenotazioni"/>
                        </fo:flow>
                    </fo:page-sequence>

		</fo:root>
	</xsl:template>
        
        <xsl:template match="listaPrenotazioni">
        <xsl:for-each select="AgeDisponibilitaListBean">
                        <fo:block keep-together.within-page="always">
                                <fo:table width="150mm" padding="3mm" space-after="5mm">
                                        <!-- NUMERO COLONNE TABELLA -->
                                        <fo:table-column column-width="10mm" />
                                        <fo:table-column column-width="25mm" />
                                        <fo:table-column column-width="10mm" />
                                        <fo:table-column column-width="10mm" />
                                        <!-- HEADER TABELLA -->
                                        <!--fo:table-header>
                                                <fo:table-cell padding-bottom="3mm">
                                                        <fo:block font-weight="bold">Prenotazioni</fo:block>
                                                </fo:table-cell>
                                        </fo:table-header-->
                                        <!-- CORPO TABELLA - INIZIO -->
                                                <fo:table-body >
                                                        <fo:table-row>
                                                                <fo:table-cell >
                                                                        <fo:block font-size="8pt">Data:</fo:block>
                                                                </fo:table-cell>
                                                                <fo:table-cell>
                                                                        <fo:block font-size="8pt" font-weight="bold"><xsl:value-of select="dateFormatted"/></fo:block>
                                                                </fo:table-cell>
                                                          <!--/fo:table-row>
                                                          <fo:table-row-->
                                                                <fo:table-cell >
                                                                        <fo:block font-size="8pt">Ora:</fo:block>
                                                                </fo:table-cell>
                                                                <fo:table-cell >
                                                                        <fo:block font-size="8pt" font-weight="bold"><xsl:value-of select="orainizio"/></fo:block>
                                                                </fo:table-cell>                                                                                                                
                                                                <!--fo:table-cell number-columns-spanned="2">
                                                                        <fo:table width="20mm" padding="0mm">

                                                                                <fo:table-column column-width="50mm" />
                                                                                <fo:table-column column-width="50mm" />

                                                                                        <fo:table-body>
                                                                                                <fo:table-row>
                                                                                                        <fo:table-cell>
                                                                                                                <fo:block font-size="8pt" font-weight="bold"><xsl:value-of select="dateFormatted"/></fo:block>
                                                                                                        </fo:table-cell>
                                                                                                        <fo:table-cell>
                                                                                                                <fo:block font-size="8pt" font-weight="bold" text-align="right"><xsl:value-of select="orainizio"/></fo:block>
                                                                                                        </fo:table-cell>
                                                                                                </fo:table-row>
                                                                                        </fo:table-body>
                                                                                </fo:table>
                                                                </fo:table-cell-->
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                                <fo:table-cell >
                                                                        <fo:block font-size="8pt">Esame:</fo:block>
                                                                </fo:table-cell>
                                                                <fo:table-cell number-columns-spanned="4">
                                                                        <fo:block font-size="8pt" font-weight="bold"><xsl:value-of select="descPrestazione"/></fo:block>
                                                                </fo:table-cell>
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                                <fo:table-cell >
                                                                        <fo:block font-size="8pt">Sede:</fo:block>
                                                                </fo:table-cell>
                                                                <fo:table-cell >
                                                                        <fo:block font-size="8pt" font-weight="bold"><xsl:value-of select="descSito"/></fo:block>
                                                                </fo:table-cell>
                                                        </fo:table-row>
                                                </fo:table-body>
                                        <!-- CORPO TABELLA - FINE -->
                                </fo:table>
                        </fo:block>
                </xsl:for-each>           
           
            <fo:block id="theEnd"/>
        </xsl:template>
        
</xsl:stylesheet>