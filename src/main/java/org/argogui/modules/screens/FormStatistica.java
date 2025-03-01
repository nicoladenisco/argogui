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

import org.argogui.om.GenStampe;
import org.argogui.services.print.ArgoReportParametersInfo;
import java.util.*;
import java.sql.Timestamp;

import org.sirio6.utils.CoreRunData;
import org.argogui.utils.SU;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.sirio6.rigel.RigelHtmlI18n;
import org.sirio6.services.formatter.DataFormatter;
import org.sirio6.services.modellixml.modelliXML;
import org.sirio6.services.print.AbstractReportParametersInfo;
import org.sirio6.services.print.ParameterInfo;
import org.sirio6.services.print.PdfPrint;
import org.sirio6.services.print.PrintContext;
import org.sirio6.services.security.SEC;

/**
 * Controller per la maschera FormStatistica.vm.
 *
 * @author Nicola De Nisco
 */
public class FormStatistica extends ArgoBaseScreen
{
  private final Date today = new Date();
  public static final String NOME_FORM = "fo";
  private static final PdfPrint pp = (PdfPrint) (TurbineServices.getInstance().
     getService(PdfPrint.SERVICE_NAME));
  private static final modelliXML mx = (modelliXML) (TurbineServices.getInstance().
     getService(modelliXML.SERVICE_NAME));
  private static final DataFormatter df = (DataFormatter) (TurbineServices.getInstance().
     getService(DataFormatter.SERVICE_NAME));

  @Override
  protected void doBuildTemplate2(CoreRunData data, Context context)
     throws Exception
  {
    String codiceStampa = data.getParameters().getString("codice", null); // NOI18N
    if(codiceStampa == null)
    {
      data.setMessage("Il parametro obbligatorio 'codice' non è presente."); // NOI18N
      return;
    }

    Map params = SU.getParMap(data);
    int idUser = SEC.getUserID(data);
    PrintContext printContext = new PrintContext(params);
    printContext.setI18n(new RigelHtmlI18n(data));
    ArgoReportParametersInfo rstat = (ArgoReportParametersInfo) pp.getParameters(idUser, codiceStampa, printContext);
    GenStampe gs = rstat.getGs();

    if(rstat == null)
    {
      data.setMessage("La statistica richiesta e' inesistente."); // NOI18N
      return;
    }

    context.put("rstat", rstat);
    context.put("plugin", rstat.getPlugin()); // NOI18N
    context.put("codice", gs.getCodice()); // NOI18N
    context.put("descrizione", gs.getDescrizione()); // NOI18N
    context.put("htmlFiltriStatistica", BuildFields(rstat, data, context)); // NOI18N
  }

  protected String BuildFields(AbstractReportParametersInfo rstat, RunData data, Context context)
     throws Exception
  {
    List filtriStatistica = rstat.getFiltriStatistiche();
    Iterator itrFiltriStatistica = filtriStatistica.iterator();

    String htmlFiltriStatistica = "";
    String htmlInizioRiga = "<tr><td align=left valign='top' width='30%' bgcolor=\"#CCCCFF\">";
    String htmlFineRiga = "</td></tr>";

    while(itrFiltriStatistica.hasNext())
    {
      ParameterInfo fs = (ParameterInfo) itrFiltriStatistica.next();

      String htmlRiga = "";

      if(fs.isHidden())
      {
        htmlRiga += "<input type='hidden' name='" + fs.getNome() + "' value='" + fs.getValoreDefault() + "'>";
      }
      else
      {
        htmlRiga += htmlInizioRiga + fs.getDescrizione()
           + "</td><td align=left valign=top bgcolor=\"#CCCCFF\">";

        if(fs.getListaValori().size() > 0)
        {
          String htmlSelectParametro = " <select name=" + fs.getNome() + ">";
          List valori = fs.getListaValori();
          Iterator itrValori = valori.iterator();
          while(itrValori.hasNext())
          {
            Map idDescMap = (Map) itrValori.next();
            Set idDescSet = idDescMap.keySet();
            Object[] idDescArr = idDescSet.toArray();
            String chiaveParametro = (String) idDescArr[0];
            String valoreParametro = (String) idDescMap.get(chiaveParametro);
            Object valoreSelezionato = fs.getValoreDefault();
            String htmlSelected = "";
            if(valoreSelezionato instanceof String && SU.isEqu(chiaveParametro, valoreSelezionato))
              htmlSelected = "selected";
            htmlSelectParametro
               += "<option value='" + chiaveParametro + "' " + htmlSelected + ">" + valoreParametro + "</option>";
          }
          htmlRiga += htmlSelectParametro;
        }
        else
        {
          if(fs.isDate() || fs.isTimeStamp())
          {
            String value = "";
            if(SU.isOkStr(fs.getValoreDefault()))
            {
              long ms = ((Timestamp) fs.getValoreDefault()).getTime();
              value = df.formatData(new Date(ms));
            }
            else
            {
              value = df.formatData(today);
            }

            if(fs.isIntervalloInizio())
            {
              String nomeCampoInizio = fs.getNome();
              String nomeCampoFine = fs.getNomeGemello();
              htmlRiga += mx.getCampoDataIntervalloInizio(nomeCampoInizio, nomeCampoFine, NOME_FORM, value, 15);
            }
            else if(fs.isIntervalloFine())
            {
              String nomeCampoFine = fs.getNome();
              String nomeCampoInizio = fs.getNomeGemello();
              htmlRiga += mx.getCampoDataIntervalloFine(nomeCampoInizio, nomeCampoFine, NOME_FORM, value, 15);
            }
            else
            {
              htmlRiga += mx.getCampoData(fs.getNome(), NOME_FORM, value, 15);
            }
          }
          else
          {
            htmlRiga += "<input type='text' name=" + fs.getNome() + " value='" + fs.getValoreDefault() + "' size='40'>";
          }
        }

        htmlRiga += htmlFineRiga;
      }

      htmlFiltriStatistica += htmlRiga;
    }

    return htmlFiltriStatistica;
  }

}
