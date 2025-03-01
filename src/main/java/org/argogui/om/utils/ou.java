package org.argogui.om.utils;

import com.workingdogs.village.DataSetException;
import com.workingdogs.village.Record;
import java.lang.reflect.Method;
import java.util.*;
import org.apache.torque.map.ColumnMap;
import org.apache.torque.map.TableMap;
import org.apache.torque.om.ColumnAccessByName;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.SimpleKey;
import org.commonlib5.utils.DateTime;
import org.rigel5.db.torque.TableMapHelper;
import org.sirio6.CoreConst;
import org.sirio6.services.localization.INT;
import org.sirio6.utils.SU;

public class ou extends SU
{
  /** campi ignororati da copyTorqueObject() */
  public static final String[] ignoreTorqueFields =
  {
    "StatoRec", "IdUser", "IdUcrea", "UltModif", "Creazione"
  };

  public static boolean equEpsi(double a, double b, double epsilon)
  {
    return Math.abs(a - b) < epsilon;
  }

  public static boolean zeroEpsi(double num, double epsilon)
  {
    return Math.abs(num) < epsilon;
  }

  public static boolean equEpsiValuta(double a, double b)
  {
    return Math.abs(a - b) < CoreConst.EPSI_VALUTA;
  }

  public static boolean zeroEpsiValuta(double val)
  {
    return Math.abs(val) < CoreConst.EPSI_VALUTA;
  }

  public static boolean zeroOrLessEpsiValuta(double val)
  {
    if(zeroEpsiValuta(val))
      return true;

    return val < 0.0;
  }

  public static boolean equEpsiQta(double a, double b)
  {
    return Math.abs(a - b) < CoreConst.EPSI_QTA;
  }

  public static boolean zeroEpsiQta(double val)
  {
    return Math.abs(val) < CoreConst.EPSI_QTA;
  }

  public static boolean equEpsiDim(double a, double b)
  {
    return Math.abs(a - b) < CoreConst.EPSI_DIM;
  }

  public static boolean zeroEpsiDim(double val)
  {
    return Math.abs(val) < CoreConst.EPSI_DIM;
  }

  public static boolean equEpsiGeneric(double a, double b)
  {
    return Math.abs(a - b) < CoreConst.EPSI_GENERIC;
  }

  public static boolean zeroEpsiGeneric(double val)
  {
    return Math.abs(val) < CoreConst.EPSI_GENERIC;
  }

  public static boolean string2bool(String val)
  {
    return checkTrue(okStr(val));
  }

  public static String bool2string(boolean val)
  {
    return val ? "1" : "0";
  }

  public static boolean int2bool(int val)
  {
    return val != 0;
  }

  public static int bool2int(boolean val)
  {
    return val ? 1 : 0;
  }

  public static Date getSoloOra(Date d)
  {
    return DateTime.mergeDataOra(0, 0, 0, d);
  }

  public static Date getSoloOra(int ora, int minuto)
  {
    return new Date(70, 0, 1, ora, minuto);
  }

  public static Date getSoloOraUTC(int ora, int minuto)
  {
    long tmsec = ora * CoreConst.MSEC_ORA
       + minuto * CoreConst.MSEC_MINUTO;

    return new Date(tmsec);
  }

  public static Date getSoloData(Date d)
  {
    return DateTime.inizioGiorno(d);
  }

  public static long getTdiff(Date dStart, Date dStop)
  {
    return dStop.getTime() - dStart.getTime();
  }

  /**
   * Ritorna vero se la data ricade nell'intevallo consentito
   */
  public static boolean checkDate(Date inizio, Date fine, Date d)
  {
    if(d.before(inizio))
      return false;

    if(d.after(fine))
      return false;

    return true;
  }

  public static java.sql.Date Cvt(Date d)
  {
    return new java.sql.Date(d.getTime());
  }

  public static Date Cvt(java.sql.Date d)
  {
    return new Date(d.getTime());
  }

  public static java.sql.Timestamp CvtTs(Date d)
  {
    // converte da ora locale a UTC in modo implicito
    return new java.sql.Timestamp(d.getTime());
  }

  public static Date CvtTs(java.sql.Timestamp t)
  {
    return new Date(t.getTime());
    // converte da UTC a ora locale
    // int minOffset = t.getTimezoneOffset();
    // return new Date(t.getTime() - (minOffset * 60000));
  }

  public static String purge(String s)
  {
    return s.replace('-', '_').replace('+', '_').replace('*', '_').replace('/', '_').replace(' ', '_');
  }

  public static String bool2string(boolean val, String trueVal, String falseVal)
  {
    return val ? trueVal : falseVal;
  }

  /**
   * Copia i campi di due oggetti torque anche diversi fra loro.
   * @param from origine
   * @param to destinazione
   * @param tmap
   * @return numero di campi copiati
   * @throws Exception
   */
  public static int copyTorqueObject(ColumnAccessByName from, ColumnAccessByName to, TableMap tmap)
     throws Exception
  {
    return copyTorqueObject(from, to, tmap, false, null);
  }

  /**
   * Copia i campi di due oggetti torque anche diversi fra loro.
   * @param from origine
   * @param to destinazione
   * @param tmap
   * @param ignoreNull se vero ignora i valori nulli letti (accumula valori)
   * @param ignoreFields array di campi da ignorare
   * @return numero di campi copiati
   * @throws Exception
   */
  public static int copyTorqueObject(ColumnAccessByName from, ColumnAccessByName to, TableMap tmap,
     boolean ignoreNull, String[] ignoreFields)
     throws Exception
  {
    // estrae lista campi dagli oggetti torque
    int count = 0;
    Method getFieldNames_Mfrom = from.getClass().getMethod("getFieldNames");
    List<String> fldNamesFrom = (List<String>) getFieldNames_Mfrom.invoke(null);
    TableMapHelper tmTo = new TableMapHelper(tmap);

    // legge e scrive i campi possibili
    for(String s : fldNamesFrom)
    {
      if(isEquNocase(s, ignoreTorqueFields))
        continue;

      if(ignoreFields != null && isEquNocase(s, ignoreFields))
        continue;

      ColumnMap cm = tmTo.getCampo(s);
      if(cm != null && cm.isPrimaryKey())
        continue;

      try
      {
        Object value = from.getByName(s);
        if(!(ignoreNull && value == null))
          if(to.setByName(s, value))
            count++;
      }
      catch(Throwable t)
      {
      }
    }

    return count;
  }

  /**
   * Copia i campi di due oggetti torque anche diversi fra loro.Simile a copyTorqueObject2() ma consente di specificare
   * i
   * campi da ignorare come argomenti supplementari, senza la
   * necessità di creare un apposito array.
   * @param from origine
   * @param to destinazione
   * @param tmap
   * @param ignoreNull se vero ignora i valori nulli letti (accumula valori)
   * @param ignoreFields campi da ignorare
   * @return numero di campi copiati
   * @throws Exception
   */
  public static int copyTorqueObject2(ColumnAccessByName from, ColumnAccessByName to, TableMap tmap,
     boolean ignoreNull, String... ignoreFields)
     throws Exception
  {
    return copyTorqueObject(from, to, tmap, ignoreNull, ignoreFields);
  }

  public static String getAge(Date dataNascita)
  {
    long diffTime = System.currentTimeMillis() - dataNascita.getTime();

    if(diffTime > (DateTime.MSEC_ANNO * 2))
      return (diffTime / DateTime.MSEC_ANNO) + " " + INT.I("anni");

    if(diffTime > DateTime.MSEC_MESE)
      return (diffTime / DateTime.MSEC_MESE) + " " + INT.I("mesi");

    return (diffTime / DateTime.MSEC_GIORNO) + " " + INT.I("giorni");
  }

  public static boolean checkTrueFalse(Object oBool)
     throws IllegalArgumentException
  {
    return checkTrueFalse(oBool, false);
  }

  public static List<ObjectKey> getKeysFromRecords(List<Record> lsRecs, int numCampo)
     throws DataSetException
  {
    ArrayList<ObjectKey> rv = new ArrayList<>();
    Set<Long> keys = getSetFromRecords(lsRecs, numCampo);

    for(Long l : keys)
      rv.add(SimpleKey.keyFor(l));

    return rv;
  }

  public static Set<Long> getSetFromRecords(List<Record> lsRecs, int numCampo)
     throws DataSetException
  {
    HashSet<Long> hsKeys = new HashSet<>();
    for(Record r : lsRecs)
      hsKeys.add(r.getValue(numCampo).asLong());
    return hsKeys;
  }
}
