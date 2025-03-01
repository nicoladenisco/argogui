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
package org.argogui.services.cache;

/**
 * Parametri per richieste WADO.
 */
public class ServerConfiguration
{
  private String aeTitle;
  private String hostName;
  private String port;
  private String wadoPort;
  private String dcmProtocol;
  private boolean argo;

  public ServerConfiguration()
  {
    aeTitle = new String();
    hostName = new String();
    port = new String();
    wadoPort = new String();
    dcmProtocol = new String();
  }

  /**
   * @return the aeTitle
   */
  public String getAeTitle()
  {
    return aeTitle;
  }

  /**
   * @param aeTitle the aeTitle to set
   */
  public void setAeTitle(String aeTitle)
  {
    this.aeTitle = aeTitle;
  }

  /**
   * @return the hostName
   */
  public String getHostName()
  {
    return hostName;
  }

  /**
   * @param hostName the hostName to set
   */
  public void setHostName(String hostName)
  {
    this.hostName = hostName;
  }

  /**
   * @return the port
   */
  public String getPort()
  {
    return port;
  }

  /**
   * @param port the port to set
   */
  public void setPort(String port)
  {
    this.port = port;
  }

  /**
   * @return the wadoPort
   */
  public String getWadoPort()
  {
    return wadoPort;
  }

  /**
   * @param wadoPort the wadoPort to set
   */
  public void setWadoPort(String wadoPort)
  {
    this.wadoPort = wadoPort;
  }

  /**
   *
   * @return the dcmProtocol;
   */
  public String getDcmProtocol()
  {
    return this.dcmProtocol;
  }

  /**
   *
   * @param dcmProtocol the dcmProtocol to set
   */
  public void setDcmProtocol(String dcmProtocol)
  {
    this.dcmProtocol = dcmProtocol;
  }

  /**
   * returns the String that can be passed to the DcmURL(String) as argument.
   * @see org.dcm4che.util.DcmURL.
   */
  @Override
  public String toString()
  {
    return dcmProtocol.toLowerCase() + "://" + aeTitle + '@' + hostName + ':' + port;
  }

  public void setArgo(boolean argo)
  {
    this.argo = argo;
  }

  public boolean isArgo()
  {
    return argo;
  }
}
