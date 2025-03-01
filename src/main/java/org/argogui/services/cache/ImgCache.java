/*
 *  ImgCache.java
 *  Creato il Jun 4, 2017, 7:29:48 PM
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
package org.argogui.services.cache;

import com.pixelmed.dicom.TransferSyntax;
import org.argogui.services.SERVICE;
import java.io.File;
import org.apache.fulcrum.cache.ObjectExpiredException;
import org.commonlib5.utils.CommonFileUtils;
import org.sirio6.services.cache.CACHE;
import org.sirio6.services.cache.FileCacheObject;

/**
 *
 * @author Nicola De Nisco
 */
public class ImgCache
{
  public static final String WADO_CACHE_CLASS = "WADO_CACHE_CLASS";

  public static File getWadoDicomFile(ServerConfiguration sc, String studyUID, String seriesUID, String objectUID)
     throws Exception
  {
    String dicomURL = "http://" + sc.getHostName() + ":" + sc.getWadoPort() + "/wado?requestType=WADO&";

    // Generates the URL for the requested DICOM Dataset page.
    dicomURL += "contentType=application/dicom&studyUID=" + studyUID
       + "&seriesUID=" + seriesUID + "&objectUID=" + objectUID
       + "&transferSyntax=" + TransferSyntax.ExplicitVRLittleEndian;
    dicomURL = dicomURL.replace("+", "%2B");

    try
    {
      FileCacheObject fo = (FileCacheObject) CACHE.getObject(WADO_CACHE_CLASS, dicomURL);
      return (File) fo.getContents();
    }
    catch(ObjectExpiredException ex)
    {
      File fcDir = SERVICE.getWorkCacheFile("remote-dicom");
      SERVICE.ASSERT_DIR_WRITE(fcDir);
      File fc = File.createTempFile("tmp", ".dcm", fcDir);
      fc.deleteOnExit();
      CommonFileUtils.readUrlToFile(dicomURL, fcDir);
      CACHE.addObject(WADO_CACHE_CLASS, dicomURL, new FileCacheObject(fc));
      return fc;
    }
  }

  public static File getWadoJpegFile(ServerConfiguration sc, String studyUID, String seriesUID, String objectUID,
     String frameNo, String contentType, String rows, String windowCenter, String windowWidth)
     throws Exception
  {
    String imageURL = "http://" + sc.getHostName() + ":" + sc.getWadoPort()
       + "/wado?requestType=WADO&studyUID=" + studyUID + "&seriesUID="
       + seriesUID + "&objectUID=" + objectUID;

    if(frameNo != null)
    {
      imageURL += "&frameNumber=" + frameNo;
    }
    if(contentType != null)
    {
      imageURL += "&contentType=" + contentType;
    }
    if(rows != null)
    {
      imageURL += "&rows=" + rows;
    }
    if(windowCenter != null)
    {
      imageURL += "&windowCenter=" + windowCenter;
    }
    if(windowWidth != null)
    {
      imageURL += "&windowWidth=" + windowWidth;
    }

    try
    {
      FileCacheObject fo = (FileCacheObject) CACHE.getObject(WADO_CACHE_CLASS, imageURL);
      return (File) fo.getContents();
    }
    catch(ObjectExpiredException ex)
    {
      File fcDir = SERVICE.getWorkCacheFile("remote-jpeg");
      SERVICE.ASSERT_DIR_WRITE(fcDir);
      File fc = File.createTempFile("tmp", ".jpeg", fcDir);
      fc.deleteOnExit();
      CommonFileUtils.readUrlToFile(imageURL, fcDir);
      CACHE.addObject(WADO_CACHE_CLASS, imageURL, new FileCacheObject(fc));
      return fc;
    }
  }
}
