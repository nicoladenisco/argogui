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
package org.argogui.modules.screens;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.turbine.Turbine;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.commonlib5.utils.StringOper;
import org.argogui.utils.I;
import org.sirio6.utils.CoreRunData;
import org.argogui.utils.SU;
import org.argogui.utils.TR;

/**
 * Controllore per AnalizzaLogs.vm.
 *
 * @author Nicola De Nisco
 */
public class AnalizzaLogs extends ArgoBaseScreen
{
  public static class StringLineNumber
  {
    private int ln;
    private String str;
    private boolean begin;

    public StringLineNumber(int n, String s)
    {
      ln = n;
      str = s;
    }

    public StringLineNumber(int n, String s, boolean b)
    {
      ln = n;
      str = s;
      begin = b;
    }

    public int getLn()
    {
      return ln;
    }

    public String getStr()
    {
      return str;
    }

    public String fmtLineNumber()
    {
      return StringOper.fmtZero(ln, 6);
    }

    public boolean isBegin()
    {
      return begin;
    }
  }

  public static class FileLogData
  {
    private File flog = null;
    private List<StringLineNumber> arLines = null;

    public List<StringLineNumber> getArLines()
    {
      return arLines;
    }

    public File getFlog()
    {
      return flog;
    }
  }

  @Override
  protected void doBuildTemplate2(CoreRunData data, Context context)
     throws Exception
  {
    super.doBuildTemplate2(data, context);

    // carica il file di configurazione di Log4j leggendo
    // le relative impostazioni dal TurbineResources.properties
    String sFile = TR.getString("log4j.file");
    if(!SU.isOkStr(sFile))
      return;

    File fileLog4j = new File(Turbine.getRealPath(sFile));
    if(fileLog4j == null || !fileLog4j.canRead())
      return;

    List<FileLogData> arBeans = loadData(data, fileLog4j);
    if(!arBeans.isEmpty())
      context.put("LOG", arBeans);
  }

  @Override
  protected boolean isAuthorized(CoreRunData data)
     throws Exception
  {
    return super.isAuthorizedAll(data, "AnalizzaLogs");
  }

  /**
   * Legge la configurazione di log4j estraendo i files di log.
   * @param fileLog4j
   * @return
   * @throws Exception
   */
  public List<FileLogData> loadData(CoreRunData data, File fileLog4j)
  {
    try
    {
      String s, ss;
      ArrayList<FileLogData> arFiles = new ArrayList<FileLogData>();
      Pattern p = Pattern.compile("^log4j.appender.(.+).file\\s*=\\s*(.+)$"); // NOI18N
      BufferedReader br = new BufferedReader(new FileReader(fileLog4j));
      while((s = br.readLine()) != null)
      {
        Matcher matcher = p.matcher(s);
        if(matcher.matches() && matcher.groupCount() == 2)
        {
          ss = StringOper.strReplace(matcher.group(2), "${applicationRoot}/", ""); // NOI18N
          File fLog = new File(Turbine.getRealPath(ss));
          if(fLog.exists())
          {
            FileLogData b = new FileLogData();
            b.flog = fLog;
            b.arLines = leggiStringhe(fLog);
            if(!b.arLines.isEmpty())
              arFiles.add(b);
          }
        }
      }
      br.close();
      return arFiles;
    }
    catch(Exception ex)
    {
      log.error(data.i18n("Errore leggendo il file %s:", fileLog4j.getAbsolutePath()), ex);
      return null;
    }
  }
  protected static Pattern pError =
     Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.+ \\[.+\\] ERROR .*$"); // NOI18N
  protected static Pattern pOther =
     Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.+ \\[.+\\] .*$"); // NOI18N

  private List<StringLineNumber> leggiStringhe(File fLog)
     throws Exception
  {
    String s;
    int stato = 0;
    ArrayList<StringLineNumber> arStringhe = new ArrayList<StringLineNumber>();

    try (LineNumberReader br = new LineNumberReader(new FileReader(fLog), 8192))
    {
      while((s = br.readLine()) != null)
      {
        if(stato == 0 && pError.matcher(s).matches())
        {
          arStringhe.add(new StringLineNumber(0, "..."));
          arStringhe.add(new StringLineNumber(br.getLineNumber(), s, true));
          stato = 1;
          continue;
        }

        if(stato == 1 && pError.matcher(s).matches())
        {
          arStringhe.add(new StringLineNumber(br.getLineNumber(), s, true));
          continue;
        }

        if(stato == 1 && pOther.matcher(s).matches())
        {
          arStringhe.add(new StringLineNumber(br.getLineNumber(), s));
          arStringhe.add(new StringLineNumber(0, "..."));
          stato = 0;
          continue;
        }

        if(stato != 0)
        {
          arStringhe.add(new StringLineNumber(br.getLineNumber(), s));
        }
      }
    }

    if(stato != 0)
        arStringhe.add(new StringLineNumber(0, "<EOF>"));

    return arStringhe;
  }
}
