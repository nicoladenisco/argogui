/*
 *  ArgoReportParametersInfo.java
 *  Creato il Jun 4, 2017, 6:23:50 PM
 *
 *  Copyright (C) 2017 RAD-IMAGE s.r.l.
 *
 *  Questo software è proprietà di RAD-IMAGE s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  RAD-IMAGE s.r.l.
 *  Via San Giovanni 1 - Contrada Belvedere
 *  San Nicola Manfredi (BN)
 */
package org.argogui.services.print;

import com.workingdogs.village.Record;
import org.argogui.om.GenParstampe;
import org.argogui.om.GenParstampePeer;
import org.argogui.om.GenStampe;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.rigel5.db.torque.CriteriaRigel;
import org.sirio6.services.print.AbstractReportParametersInfo;
import org.sirio6.services.print.ParameterInfo;
import org.sirio6.utils.SU;

/**
 * Descrittore parametri di stampa per Argo.
 *
 * @author Nicola De Nisco
 */
public class ArgoReportParametersInfo extends AbstractReportParametersInfo
{
  private GenStampe gs;

  public ArgoReportParametersInfo(GenStampe gs)
  {
    this.gs = gs;
  }

  /**
   * Costruisce i dati di un parametro della stampa.
   * Viene interrogata la tabella sys_parametristatistiche
   * per determinare come dovrà essere utilizzato questo
   * parametro. Se è un combo vengono caricati tutti
   * i valori possibili dalla relativa tabella oppure
   * per i self-combo dal campo valore di sys_par...
   * @param nomeParametro
   * @param tipoParametro
   * @return
   * @throws java.lang.Exception
   */
  @Override
  public ParameterInfo buildParametro(String nomeParametro, Class tipoParametro)
     throws Exception
  {
    ParameterInfo fs = new ParameterInfo();

    CriteriaRigel cParametri = new CriteriaRigel();
    cParametri.add(GenParstampePeer.CODICE, nomeParametro);
    List lParametri = GenParstampePeer.doSelect(cParametri);

    if(!lParametri.isEmpty())
    {
      GenParstampe parametro = (GenParstampe) lParametri.get(0);
      String tabellaValori = parametro.getTabellaValori();
      String valori = parametro.getValori();

      // valori da tabella
      String campoNome = parametro.getCodice();
      String campoDescrizione = parametro.getDescrizione();

      if(SU.isOkStr(tabellaValori))
      {
        String selectValPar = parametro.getCampoChiave() + " , " + parametro.getCampoValore();
        String fromValPar = tabellaValori;

        List recordsValPar = populateComboData(selectValPar, fromValPar, parametro.getCampoValore());

        Iterator itrValParRec = recordsValPar.iterator();
        while(itrValParRec.hasNext())
        {
          Record valParRec = (Record) (itrValParRec).next();
          String valCampoChiave = valParRec.getValue(1).asString();
          String valCampoDescrizione = valParRec.getValue(2).asString();
          fs.addListaValori(valCampoChiave, valCampoDescrizione);
        }
      }

      if(SU.isOkStr(valori))
      {
        if(valori.indexOf(';') == -1)
        {
          // valore di default del parametro
          fs.setValoreDefault(valori);
        }
        else
        {
          // valori statici
          StringTokenizer stValori = new StringTokenizer(valori, ";");
          while(stValori.hasMoreElements())
          {
            String valore = (String) stValori.nextElement();
            // test per valore di default (incomincia con *)
            if(valore.charAt(0) == '*')
            {
              valore = valore.substring(1);
              fs.setValoreDefault(valore);
            }
            fs.addListaValori(valore, valore);
          }
        }
      }

      fs.setDescrizione(campoDescrizione);
      fs.setNome(campoNome);
    }
    else
    {
      // non presente in tabella parametri
      fs.setDescrizione(nomeParametro);
      fs.setNome(nomeParametro);
    }

    fs.setTipo(tipoParametro);

    return fs;
  }

  @Override
  public String getPlugin()
  {
    return SU.okStrNull(gs.getPlugin());
  }

  @Override
  public String getNome()
  {
    return SU.okStrNull(gs.getNome());
  }

  @Override
  public String getInfo()
  {
    return SU.okStrNull(gs.getInfo());
  }

  public GenStampe getGs()
  {
    return gs;
  }

  @Override
  public String getDataMaker()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
