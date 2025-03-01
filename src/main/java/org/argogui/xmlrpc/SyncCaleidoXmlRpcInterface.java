/* 
 *  Copyright (C) 2016 RAD-IMAGE s.r.l.
 * 
 *  Questo software è proprietà di RAD-IMAGE s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da 
 *  considerarsi tutelati ai sensi di legge.
 * 
 *  RAD-IMAGE s.r.l.
 *  Via San Giovanni, 1 - Contrada Belvedere
 *  San Nicola Manfredi (BN)
 * 
 *  Creato il 10 Febbraio 2016, 19:06:00
 */
package org.argogui.xmlrpc;

import java.util.Vector;

/**
 * Definizione metodi per il servizio di sincronizzazione con Caleido.
 *
 * @author Nicola De Nisco
 */
public interface SyncCaleidoXmlRpcInterface
{
  /**
   * Ritorna l'elenco dei materiali biologici.
   * Ogni elemento del vettore ritornato è una tabella di hashing
   * con i valori dei campi di un materiale biologico.
   * <ul>
   * <li>CODICE: il codice del materiale</li>
   * <li>DESCRIZIONE: la descrizione </li>
   * <li>DESINENZA</li>
   * </ul>
   * @return Vettore di risultati
   * @throws Exception
   */
  public Vector getMaterialiBiologici()
     throws Exception;

  /**
   * Ritorna l'elenco delle prestazioni.
   * Ogni elemento del vettore ritornato è una tabella di hashing
   * con i valori dei campi di una prestazione.
   * <ul>
   <li>IDPRESTAZIONI</li>
   <li>CODBRANCA</li>
   <li>DESBRANCA</li>
   <li>CODSETTORE</li>
   <li>DESSETTORE</li>
   <li>CODICE</li>
   <li>CODICEMNEMONICO</li>
   <li>CODICEMINISTERIALE</li>
   <li>CODICEREGIONALE</li>
   <li>CODISSUER</li>
   <li>PROCEDURA</li>
   <li>DESCRIZIONE</li>
   <li>ALIAS</li>
   <li>ALIASFATTURA</li>
   <li>PROTETTO</li>
   <li>ATTIVO</li>
   <li>INFO</li>
   <li>CATEGORIA</li>
   <li>CODICEIVA</li>
   * </ul>
   * @param codBranca eventuale codice branca di filtro (string vuota per TUTTE)
   * @return Vettore di risultati
   * @throws Exception
   */
  public Vector getPrestazioni(String codBranca)
     throws Exception;

  /**
   * Ritorna l'elenco dei parametri.
   * Ogni elemento del vettore ritornato è una tabella di hashing
   * con i valori dei campi di un parametro.
   * <ul>
      <li>IDPARAMETRI</li>
      <li>CODICE</li>
      <li>DESCRIZIONE</li>
      <li>CODICEMINISTERIALE</li>
      <li>CODICEREGIONALE</li>
      <li>CODICEMNEMONICO</li>
      <li>CODICELOINC</li>
      <li>CODICE_PRESTAZIONE</li>
      <li>CODICE_MATBIO</li>
      <li>QTMATERIALEBIOLOGICO</li>
      <li>TIPO_RISULTATO</li>
      <li>FORMULA</li>
      <li>CIFRE_DECIMALI</li>
      <li>ESCLUDI_PIANO</li>
      <li>LEGA_GRAFICO</li>
      <li>CONVERTI_RISULTATO</li>
      <li>LEGA_ANTIBIOGRAMMA</li>
      <li>NON_STAMPARE</li>
      <li>RIS_PREDEFINITO</li>
      <li>GRAFICO_PARAMETRO</li>
      <li>RIFIUTA_RISPOSTE</li>
      <li>UNITA</li>
      <li>MOLTIPLICATORE</li>
      <li>SENSIBILITA</li>
      <li>CATEGORIA</li>
      <li>CODISSUER</li>
      <li>PROCEDURA</li>
      <li>INFO</li>
   * </ul>
   * @param codBranca eventuale codice branca di filtro (string vuota per TUTTE)
   * @return Vettore di risultati
   * @throws Exception
   */
  public Vector getParametri(String codBranca)
     throws Exception;
}
