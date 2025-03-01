/*
 * AllignDatabase.java
 *
 * Created on 18 marzo 2008, 13.09
 */
package org.argogui.om.utils;

import gnu.getopt.*;
import java.io.*;
import java.sql.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonlib5.utils.LongOptExt;
import org.rigel5.db.AbstractAlignDatabase;
import org.rigel5.db.DbUtils;

/**
 * Allinea il database leggendo il file updpilot.xml.
 *
 * linea di comanto per il test
 * -v -s torque/src/sql/update-data -H $HOST -D $DATABASE -P caleido torque/src/sql/updpilot.xml
 * @author Nicola De Nisco
 */
public class AlignDatabase extends AbstractAlignDatabase
{
  private static final Log log = LogFactory.getLog(AlignDatabase.class);

  public String dbDriver = "org.postgresql.Driver";
  public String dbUrl = null;
  public String dbHost = null, dbName = null;
  public String dbUser = "argo", dbPasswd = "1234";
  public String product = "argo";

  /** Creates a new instance of AllignDatabase */
  public AlignDatabase()
  {
  }

  public void openConnection()
     throws Exception
  {
    Class.forName(dbDriver);
    con = DriverManager.getConnection(dbUrl, dbUser, dbPasswd);
  }

  public void closeConnection()
     throws Exception
  {
    con.close();
  }

  public void readUpgradeStep()
     throws Exception
  {
    String sSQL = "SELECT major, minor"
       + " FROM version"
       + " WHERE stato_oper=?"
       + " AND componente=?"
       + " ORDER BY version_id DESC";

    try (PreparedStatement ps = con.prepareStatement(sSQL))
    {
      ps.setString(1, "0");
      ps.setString(2, product);

      try (ResultSet rs = ps.executeQuery())
      {
        if(rs.next())
        {
          anno = rs.getInt(1);
          settimana = rs.getInt(2);

          if(verbose)
            log.debug("Sistema con livello di aggiornamento " + anno + "/" + settimana + ".");
        }
      }
    }
  }

  public void writeUpgradeStep(int stato, String error)
     throws Exception
  {
    String sSQL
       = "INSERT INTO version("
       + "     major, minor, stato_oper, error, note, id_user, creazione, componente, VERSION_ID)"
       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    long maxID = DbUtils.getMaxPrimaryKey("VERSION", con);

    try (PreparedStatement ps = con.prepareStatement(sSQL))
    {
      ps.setInt(1, updAnno);
      ps.setInt(2, updSettimana);
      ps.setString(3, error == null ? "0" : "F");
      ps.setString(4, error);
      ps.setString(5, null);
      ps.setInt(6, 0 /* id_user */);
      ps.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis()));
      ps.setString(8, product);

      ps.setLong(9, maxID + 1);

      ps.executeUpdate();
    }
  }

  public static void help(LongOptExt[] longopts)
  {
    log.debug("AlignDatabase [options] updpilot.xml\n");
    for(LongOptExt l : longopts)
    {
      log.debug(l.getHelpMsg());
    }

    System.exit(0);
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args)
  {
    try
    {
      AlignDatabase ad = new AlignDatabase();

      int c;
      LongOptExt[] longopts =
      {
        new LongOptExt("help", LongOptExt.NO_ARGUMENT, null, 'h', "visualizza questo messaggio"),
        new LongOptExt("verbose", LongOptExt.NO_ARGUMENT, null, 'v', "verbose mode"),
        new LongOptExt("very-verbose", LongOptExt.NO_ARGUMENT, null, 'V', "verbose mode con SQL"),
        new LongOptExt("driver", LongOptExt.REQUIRED_ARGUMENT, null, 'd', "imposta driver del database (default PostgreSQL)"),
        new LongOptExt("urldb", LongOptExt.REQUIRED_ARGUMENT, null, 'U', "url del db (default jdbc:postgres://host/nomedb)"),
        new LongOptExt("user", LongOptExt.REQUIRED_ARGUMENT, null, 'u', "utente db (default flower)"),
        new LongOptExt("password", LongOptExt.REQUIRED_ARGUMENT, null, 'p', "password db (default infomemd)"),
        new LongOptExt("host", LongOptExt.REQUIRED_ARGUMENT, null, 'H', "host database (default localhost)"),
        new LongOptExt("database", LongOptExt.REQUIRED_ARGUMENT, null, 'D', "nome database (default flower)"),
        new LongOptExt("scripts", LongOptExt.REQUIRED_ARGUMENT, null, 's', "directory con gli script da eseguire"),
        new LongOptExt("product", LongOptExt.REQUIRED_ARGUMENT, null, 'P', "nome del prodotto (default flower)"),
        new LongOptExt("force-last", LongOptExt.NO_ARGUMENT, null, 'L', "forza la riapplicazione dell'ultimo aggiornamento"),
        new LongOptExt("force-from", LongOptExt.REQUIRED_ARGUMENT, null, 'F', "forza la riapplicazione dallo step indicato (yyyy/ss)")
      };

      if(args.length == 0)
        help(longopts);

      Getopt g = new Getopt("AlignDatabase", args, LongOptExt.getOptstring(longopts), longopts);
      g.setOpterr(false); // We'll do our own error handling

      while((c = g.getopt()) != -1)
      {
        switch(c)
        {
          case 'h':
            help(longopts);
            break;
          case 'd':
            ad.dbDriver = g.getOptarg();
            break;
          case 'U':
            ad.dbUrl = g.getOptarg();
            break;
          case 'u':
            ad.dbUser = g.getOptarg();
            break;
          case 'p':
            ad.dbPasswd = g.getOptarg();
            break;
          case 'P':
            ad.product = g.getOptarg();
            break;
          case 'H':
            ad.dbHost = g.getOptarg();
            break;
          case 'D':
            ad.dbName = g.getOptarg();
            break;
          case 's':
            ad.dirScripts = new File(g.getOptarg());
            break;
          case 'v':
            ad.verbose = true;
            break;
          case 'V':
            ad.veryverbose = true;
            break;
          case 'L':
            ad.forceLast = true;
            break;
          case 'F':
            ad.forceFrom = true;
            ad.parseForceFrom(g.getOptarg());
            break;
        }
      }

      if(ad.dbUrl == null)
      {
        if(ad.dbHost == null)
          ad.dbHost = "localhost";
        if(ad.dbName == null)
          ad.dbName = "flower";

        ad.dbUrl = "jdbc:postgresql://" + ad.dbHost + "/" + ad.dbName;
      }

      ad.openConnection();
      ad.readUpgradeStep();

      if(!ad.forceFrom && (ad.anno == 0 || ad.settimana == 0))
      {
        log.debug("Nessun valore precedente nella tabella vesion.");
        log.debug("Usa --force-from per forzare comunque l'aggiornamento dallo step indicato.");
        return;
      }

      for(int i = g.getOptind(); i < args.length; i++)
      {
        ad.parsingSqlAggiornamento(ad.anno, ad.settimana, args[i]);
      }

      if(ad.updated)
        ad.writeUpgradeStep(0, null);

      ad.closeConnection();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
