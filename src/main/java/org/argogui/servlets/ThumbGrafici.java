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
package org.argogui.servlets;

import org.argogui.utils.I;
import org.argogui.services.SERVICE;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.fulcrum.mimetype.MimeTypeService;
import org.apache.turbine.services.TurbineServices;
import org.sirio6.utils.LI;

/**
 * Servlet per info thumbnail dei grafici.
 *
 * @author Nicola De Nisco
 */
public class ThumbGrafici extends HttpServlet
{
  private MimeTypeService mts = null;

  @Override
  public void init(ServletConfig config)
     throws ServletException
  {
    super.init(config);
    mts = (MimeTypeService) TurbineServices.getInstance().getService(MimeTypeService.ROLE);
  }

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException
  {
    try
    {
      if(request.getSession(false) == null)
        throw new Exception(I.I("Sessione non valida."));

      // estrae nome della richiesta
      String sRequest = request.getPathInfo().substring(1);
      File toSend = SERVICE.getWorkGraphFile(sRequest);
      String contentType = mts.getContentType(toSend);

      if(contentType != null && contentType.startsWith("image"))
      {
        // invia il file
        FU.sendFileResponse(request, response, toSend, contentType, toSend.getName(), false);
        return;
      }

      // invia una immagine di servizio (preleva dallo skin)
      String dummyImg = LI.getImageUrl("thumb-generic.png");
      String cpath = request.getContextPath();
      if(dummyImg.startsWith(cpath))
        dummyImg = dummyImg.substring(cpath.length());

      toSend = new File(getServletContext().getRealPath(dummyImg));
      FU.sendFileResponse(request, response, toSend, "image/png", "thumb-generic.png", false);
    }
    catch(Exception ex)
    {
      throw new ServletException(ex);
    }
  }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /**
   * Handles the HTTP <code>GET</code> method.
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException
  {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException
  {
    processRequest(request, response);
  }

  /**
   * Returns a short description of the servlet.
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo()
  {
    return "Short description";
  }// </editor-fold>

}
