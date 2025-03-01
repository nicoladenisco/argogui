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
 *  Creato il 4 Maggio 2016
 */
package org.argogui.servlets;

import org.argogui.services.cache.ImgCache;
import org.argogui.services.cache.ServerConfiguration;
import org.argogui.services.dcmsrv.DicomServer;
import org.argogui.services.dcmsrv.InstanceResultBean;
import org.argogui.services.dcmsrv.StudyResultBean;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.fulcrum.cache.CachedObject;
import org.apache.fulcrum.cache.ObjectExpiredException;
import org.apache.fulcrum.mimetype.MimeTypeService;

import org.apache.log4j.Logger;
import org.apache.turbine.services.TurbineServices;
import org.argogui.utils.I;
import org.commonlib5.utils.CommonFileUtils;
import org.commonlib5.utils.StringOper;
import org.sirio6.services.cache.CACHE;

/**
 * Servlet per fornire immagini Jpeg.
 * Questa servlet è utilizzata dal visualizzatore javascript
 * per ottenere copia jpeg delle immagini DICOM.
 *
 * @author Nicola De Nisco
 */
public class DcmImage extends HttpServlet
{
  private static Logger log = Logger.getLogger(DcmImage.class);
  public static final String WADO_SERVER_CONFIG = "serverConfig";
  public static final String CACHE_STUDY_DATA = "CACHE_STUDY_DATA";

  private MimeTypeService mts = null;
  private DicomServer dcmsrv = null;

  @Override
  public void init(ServletConfig config)
     throws ServletException
  {
    super.init(config);
    mts = (MimeTypeService) TurbineServices.getInstance().getService(MimeTypeService.ROLE);
    dcmsrv = (DicomServer) TurbineServices.getInstance().getService(DicomServer.SERVICE_NAME);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException
  {
    ServerConfiguration sc = (ServerConfiguration) (getServletContext().getAttribute(WADO_SERVER_CONFIG));

    if(sc == null)
    {
      sc = dcmsrv.getServerConfiguration();
      sc.setHostName(request.getServerName());
      getServletContext().setAttribute(WADO_SERVER_CONFIG, sc);
    }

    // Reads the parameters form the request object which is sent by user.
    String study = StringOper.okStrNull(request.getParameter("study"));
    String series = StringOper.okStrNull(request.getParameter("series"));
    String object = StringOper.okStrNull(request.getParameter("object"));
    String frameNo = StringOper.okStrNull(request.getParameter("frameNumber"));
    String contentType = StringOper.okStrNull(request.getParameter("contentType"));
    String rows = StringOper.okStrNull(request.getParameter("rows"));
    String windowCenter = StringOper.okStrNull(request.getParameter("windowCenter"));
    String windowWidth = StringOper.okStrNull(request.getParameter("windowWidth"));

    if(study == null || series == null || object == null)
      return;

    if(contentType != null)
      response.setContentType(contentType);

    File jpegFile = null;
    InputStream resultInStream = null;
    OutputStream resultOutStream = null;

    try
    {
      // verifica per accesso diretto a jpeg via server apache su argo
      if(contentType == null && sc.isArgo() && testSimple(frameNo, contentType, rows, windowCenter, windowWidth))
      {
        StudyResultBean bean = getStudyBean(study);
        if(bean == null)
          return;

        InstanceResultBean ib = bean.findInstance(series, object);
        if(ib == null)
          return;

        jpegFile = ib.fileJpeg;
      }
      else
      {
        jpegFile = ImgCache.getWadoJpegFile(sc, study, series, object,
           frameNo, contentType, rows, windowCenter, windowWidth);
      }

      resultInStream = new FileInputStream(jpegFile);
      resultOutStream = response.getOutputStream();
      CommonFileUtils.copyStream(resultInStream, resultOutStream);

      resultOutStream.flush();
    }
    catch(Exception e)
    {
      String msg = I.I("Comunicazione WADO non riuscita con http://%s:%s", sc.getHostName(), sc.getWadoPort());
      log.error(msg, e);
      throw new ServletException(msg, e);
    }
    finally
    {
      CommonFileUtils.safeClose(resultInStream);
      CommonFileUtils.safeClose(resultOutStream);
    }
  }

  protected boolean testSimple(String frameNo, String contentType, String rows, String windowCenter, String windowWidth)
  {
    if(frameNo != null)
      return false;
    if(contentType != null)
      return false;
    if(rows != null)
      return false;
    if(windowCenter != null)
      return false;
    if(windowWidth != null)
      return false;

    return true;
  }

  private StudyResultBean getStudyBean(String study)
     throws Exception
  {
    try
    {
      return (StudyResultBean) CACHE.getObject(CACHE_STUDY_DATA, study).getContents();
    }
    catch(ObjectExpiredException ignore)
    {
      StudyResultBean bean = dcmsrv.populateStudy(study, null, new StudyResultBean());
      CACHE.addObject(CACHE_STUDY_DATA, study, new CachedObject(bean));
      return bean;
    }
  }
}
