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
package org.argogui.beans;

import com.workingdogs.village.Record;
import org.argogui.om.*;
import org.argogui.services.dcmsrv.DicomServer;
import org.argogui.utils.TR;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.commonlib5.utils.Pair;
import org.rigel5.db.DbUtils;
import org.sirio6.utils.CoreRunData;

/**
 * Bean per la visualizzazione dei risultati.
 *
 * @author Nicola De Nisco
 */
public class VisRisultatiBean extends ArgoBaseBean
{
  private int idAcc;
  private InfInAccettazioni acc;
  private InfInAnagrafiche ana;
  private Map<String, ResBlock> resMap = new TreeMap<>();
  private int resPronti;
  private String currVis;
  private DicomServer srv;

  // costanti
  public static final String BEAN_KEY = "VisRisultatiBean:BEAN_KEY";

  public static class ResBlock
  {
    public int idVis;
    public String titolo, screen;
    public List<ResItem> items;

    public boolean haveStudy(int idEsa)
    {
      if(items != null)
        for(ResItem item : items)
        {
          if(item.ie != null && item.ie.getInEsamiId() == idEsa)
            return true;
        }

      return false;
    }
  }

  public static class ResItem
  {
    public InfInEsami ie;
    public StpEsami se;
    public StpCategorie sc;

    public InfInEsami getIe()
    {
      return ie;
    }

    public void setIe(InfInEsami ie)
    {
      this.ie = ie;
    }

    public StpEsami getSe()
    {
      return se;
    }

    public void setSe(StpEsami se)
    {
      this.se = se;
    }

    public StpCategorie getSc()
    {
      return sc;
    }

    public void setSc(StpCategorie sc)
    {
      this.sc = sc;
    }
  }

// <editor-fold defaultstate="collapsed" desc="Getter/Setter">
  // TODO: inserire qui i getter e setter
  // di eventuali proprietà del bean
  public InfInAnagrafiche getAna()
  {
    return ana;
  }

  public int getIdAcc()
  {
    return idAcc;
  }

  public InfInAccettazioni getAcc()
  {
    return acc;
  }

  public String getCurrVis()
  {
    return currVis;
  }

  public void setCurrVis(String currVis)
  {
    this.currVis = currVis;
  }

  public Map<String, ResBlock> getRisultatiPronti()
  {
    return resMap;
  }

  public int getResPronti()
  {
    return resPronti;
  }

// </editor-fold>
  @Override
  public void init(CoreRunData data)
     throws Exception
  {
    super.init(data);
    srv = (DicomServer) getService(DicomServer.SERVICE_NAME);

    idAcc = data.getParameters().getInt("ID");
    acc = InfInAccettazioniPeer.retrieveByPK(idAcc);
    ana = acc.getInfInAnagrafiche();
  }

  /**
   * Verifica se questo bean è ancora valido.
   * Questa funzione viene chiamata quando
   * il bean viene recuperato dalla sessione.
   * Se nella richiesta vi sono parametri che
   * ne inficiano l'utilizzo questo metodo
   * deve ritornare false e una nuova istanza
   * di questo bean verrà creata e inizializzata.
   * @param data dati della richiesta HTML
   * @return vero se il bean è valido
   */
  @Override
  public boolean isValid(CoreRunData data)
  {
    int testAcc = data.getParameters().getInt("ID", idAcc);

    if(testAcc != idAcc)
      return false;

    return true;
  }

  /**
   * Verifica per risultati pronti.
   * Popola una mappa tipo visualizzazione/numero risulati pronti.
   * @param resMap mappa da popolare
   * @return 0=nessuno pronto 1=alcuni pronti 2=tutti pronti
   * @throws Exception
   */
  public int elaboraRisultatiPronti()
     throws Exception
  {
    boolean nonpronti = false;

    // necessario per ricaricare una lista esami aggiornata
    acc = InfInAccettazioniPeer.retrieveByPK(idAcc);

    resMap.clear();
    List<InfInEsami> lsEsa = acc.getInfInEsamis();
    for(InfInEsami ie : lsEsa)
    {
      StpEsami se = ie.getStpEsame(null);
      StpCategorie sc = se.getStpCategorie();
      int idVis = sc.getCodVisualizzazione();

      String key, titolo, screen;
      if(sc.isAccorpamentoVisBool())
      {
        key = Integer.toString(idVis);
        titolo = TR.getString("tipovis." + idVis + ".nome"); // NOI18N
        screen = TR.getString("tipovis." + idVis + ".screen", null); // NOI18N
      }
      else
      {
        key = idVis + "_" + ie.getAccessionNumber();
        titolo = sc.getDescrizione();
        screen = TR.getString("tipovis." + idVis + ".screen", null); // NOI18N
      }

      // controlla per esami di radiologia (numero immagini in InfInEsami)
      if(ie.getNumImmagini() > 0)
      {
        catalogaRisultati(idVis, key, titolo, screen, ie, se, sc);
      }
      else
      {
        // controlla per esami di chimica clinica
        String sSQL = "SELECT OP.*"
           + " FROM ((INF.IN_PARAMETRI AS IP"
           + "    INNER JOIN INF.IN_CAMPIONI AS IC ON IP.ID_CAMPIONI=IC.IN_CAMPIONI_ID)" // NOI18N
           + "    INNER JOIN INF.OUT_RISULTATI AS OP ON IP.CODICE=OP.CODICE_ESAME AND IC.SID=OP.SID)"
           + " WHERE IP.ID_ESAMI=" + ie.getInEsamiId();

        List<Record> lsRecs = DbUtils.executeQuery(sSQL);
        if(!lsRecs.isEmpty())
          catalogaRisultati(idVis, key, titolo, screen, ie, se, sc);
        else
          nonpronti = true;
      }
    }

    if(resMap.isEmpty())
      return resPronti = 0;

    return resPronti = (nonpronti ? 1 : 2);
  }

  private void catalogaRisultati(int idVis, String key, String titolo, String screen,
     InfInEsami ie, StpEsami se, StpCategorie sc)
  {
    ResBlock block = resMap.get(key);
    if(block == null)
    {
      block = new ResBlock();
      block.idVis = idVis;
      block.titolo = titolo;
      block.screen = screen;
      block.items = new ArrayList<ResItem>();
      resMap.put(key, block);
    }

    ResItem ri = new ResItem();
    ri.ie = ie;
    ri.sc = sc;
    ri.se = se;
    block.items.add(ri);
  }

  public String getStrumento(int idStrumento)
     throws Exception
  {
//    StpSetupStrumenti sss = COMSRV.getSetupStrumento(idStrumento);
//    return sss.getDescrizione();
    return "INDEFINITO";
  }

  public List<String> getPathImmaginiEsame(String accno)
     throws Exception
  {
    return srv.getDicomRelativePaths(accno, DicomServer.TIPO_FILE_JPEG);
  }

  public List<Pair<String, String>> getInfoMenu()
  {
    ArrayList<Pair<String, String>> rv = new ArrayList<>();
    for(Map.Entry<String, ResBlock> esRS : resMap.entrySet())
    {
      String key = esRS.getKey();
      ResBlock value = esRS.getValue();

      rv.add(new Pair<String, String>(key, value.titolo));
    }
    return rv;
  }

  public String getCurrVisDescrizione()
  {
    return TR.getString("tipovis." + currVis + ".nome"); // NOI18N
  }

  public InfInEsami getEsameFromRisultati(int idEsa)
  {
    for(Map.Entry<String, ResBlock> esRS : resMap.entrySet())
    {
      String key = esRS.getKey();
      ResBlock value = esRS.getValue();

      for(ResItem ri : value.items)
      {
        if(ri.ie.getInEsamiId() == idEsa)
          return ri.ie;
      }
    }
    return null;
  }
}
