package org.argogui.om;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Descrittori delle aree di storage
 *
 * The skeleton for this class was autogenerated by Torque on:
 *
 * [Mon Feb 17 23:20:27 CET 2025]
 *
 * You should add additional methods to this class to meet the
 * application requirements. This class will only be generated as
 * long as it does not already exist in the output directory.
 */
public class StpStorage
   extends org.argogui.om.BaseStpStorage
{
  /** Serial version */
  private static final long serialVersionUID = 1739830827265L;

  private String rootFs, allert;
  private long totalSpace, freeSpace, filledSpace;
  private int percFill;

  public long getTotalSpace()
  {
    return totalSpace;
  }

  public void setTotalSpace(long totalSpace)
  {
    this.totalSpace = totalSpace;
  }

  public long getFreeSpace()
  {
    return freeSpace;
  }

  public void setFreeSpace(long freeSpace)
  {
    this.freeSpace = freeSpace;
  }

  public long getFilledSpace()
  {
    return filledSpace;
  }

  public void setFilledSpace(long filledSpace)
  {
    this.filledSpace = filledSpace;
  }

  public int getPercFill()
  {
    return percFill;
  }

  public void setPercFill(int percFill)
  {
    this.percFill = percFill;
  }

  public String getRootFs()
  {
    return rootFs;
  }

  public void setRootFs(String rootFs)
  {
    this.rootFs = rootFs;
  }

  public String getAllert()
  {
    return allert;
  }

  public void setAllert(String allert)
  {
    this.allert = allert;
  }

  public void loadDiskStatistic()
     throws IOException
  {
    Path pDsk = Paths.get(getPath()).toRealPath();
    rootFs = pDsk.getRoot().toString();
    FileStore fileStore = Files.getFileStore(pDsk);
    rootFs = fileStore.toString();
    setFreeSpace(fileStore.getUsableSpace());
    setTotalSpace(fileStore.getTotalSpace());
    setFilledSpace(totalSpace - freeSpace);

    long perc = (filledSpace * 100) / totalSpace;
    setPercFill((int) perc);

    allert = "progress-bar-info";
    if(perc > 75)
      allert = "progress-bar-warning";
    if(perc > 90)
      allert = "progress-bar-danger";
  }

  public boolean isArea()
  {
    return tipo == StpStoragePeer.TIPO_AREA || tipo == StpStoragePeer.TIPO_AREA_RO;
  }

}
