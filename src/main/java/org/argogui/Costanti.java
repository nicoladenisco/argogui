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
package org.argogui;

import org.sirio6.CoreConst;

/**
 * <p>
 * Title: Costanti generali.</p>
 * <p>
 * Description: In questa classe sono archiviate le costanti ad uso generale.</p>
 *
 * FILENOI18N
 * @author Nicola De Nisco
 * @version 1.0
 */
public class Costanti extends CoreConst
{
  // valori di STATO_REC per la tabella INF.STP_GRUPPI_STRUMENTI (vedi note-implentazione.txt)
  public static final int STATO_REC_GROUP_ENABLED = 0;
  public static final int STATO_REC_GROUP_DISABLED = 1;

  // modalità dello strumento
  public static final int MODO_BATCH = 0;
  public static final int MODO_ON_LINE = 1;
  public static final int MODO_HOST_QUERY = 2;

  // tipi di risultato
  public static final String TRIS_STRUMENTO = "R";
  public static final String TRIS_CALCOLATO = "C";
  public static final String TRIS_SCELTO = "S";
  public static final String TRIS_MEDIO = "M";
  public static final String TRIS_CORRETTO = "O";
  public static final String TRIS_IMMESSO = "I";

  // tipi priorità accettazione
  public static final String TACC_EMERGENZA = "E";
  public static final String TACC_INTERNO = "I";
  public static final String TACC_ESTERNO = "O";
  public static final String TACC_DAYHOSPITAL = "D";
  public static final String TACC_DAYSERVICE = "S";
  public static final String TACC_ESTERNO_CICLI = "R";
  public static final String TACC_POSTRICOVERO = "C";
  public static final String TACC_DOMICILIARE = "A";
  public static final String TACC_NON_APPLICABILE = "N";
  public static final String TACC_SCONOSCIUTO = "U";

  // tipi urgenza
  public static final String TPRI_ROUTINE = "R";
  public static final String TPRI_URGENZA = "A";
  public static final String TPRI_EMERGENZA = "S";

  //notizie cliniche
  public static final String[] PESO =
  {
    "1", "PESO"
  };
  public static final String[] ALTEZZA =
  {
    "2", "ALTEZZA"
  };
  public static final String[] FUMO =
  {
    "3", "FUMO", "FUMATORE"
  };
  public static final String[] DIURESI =
  {
    "4", "DIURESI"
  };

  //tipi codici anagrafica
  public static final String COD_ALTERNATIVO = "PI";
  public static final String COD_SANITARIO = "SS";
  public static final String COD_FISCALE = "NN";

  // tipi di verifica risultato
  public static final int VAL_UNCHEKED = 0;
  public static final int VAL_ERROR_TOO_LOW = 1;
  public static final int VAL_ERROR_TOO_HIGH = 2;
  public static final int VAL_HPATH_TOO_LOW = 3;
  public static final int VAL_HPATH_TOO_HIGH = 4;
  public static final int VAL_PATH_TOO_LOW = 5;
  public static final int VAL_PATH_TOO_HIGH = 6;
  public static final int VAL_NORMAL = 7;

  // valori di STATO_REC per le tabelle INF.IN_... (vedi note-implentazione.txt)
  public static final int STATO_REC_OK = 0;
  public static final int STATO_REC_RUNNING = 1;
  public static final int STATO_REC_PROCESSED = 2;
  public static final int STATO_REC_ORDER_SENT = 3;
  public static final int STATO_REC_RESULT_RECEIVED = 4;
  public static final int STATO_REC_USER_SUSPEND = 5;
  public static final int STATO_REC_USER_CONFIRMED = 6;
  public static final int STATO_REC_USER_ABORT = 7;
  public static final int STATO_REC_DATA_SENT = 8;
  public static final int STATO_REC_ERROR = 9;
  public static final int STATO_REC_DELETED = 10;
}
