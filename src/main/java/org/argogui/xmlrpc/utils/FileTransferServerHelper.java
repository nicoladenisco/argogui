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
package org.argogui.xmlrpc.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.argogui.services.SERVICE;
import org.sirio6.services.cache.CACHE;
import org.sirio6.services.cache.CoreCachedObject;

/**
 * Classe di utilità per trasferire file
 * binari attraverso XML-RPC.
 *
 * @author Nicola De Nisco
 */
public class FileTransferServerHelper implements FileTransfer
{
  public static final String REF_TRANSFER_CACHE_KEY = "refTransferCacheKey";

  public static class TransferInfo
  {
    public String id;
    public int numBlock, sizeBlock, currBlock, fileSize;
    public File toTransfer;
    public RandomAccessFile stream = null;
  }

  public static class TransferCachedObject extends CoreCachedObject
  {
    public TransferCachedObject(TransferInfo o)
    {
      super(o);
    }

    @Override
    public synchronized void deletingExpired()
    {
      try
      {
        super.deletingExpired();

        TransferInfo ti = (TransferInfo) getContents();
        synchronized(ti)
        {
          if(ti.stream != null)
            ti.stream.close();
          ti.stream = null;
        }
      }
      catch(IOException ex)
      {
        Logger.getLogger(FileTransferServerHelper.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  public Hashtable preparaDownload(String fileName, File toTransfer, int suggestBlockSize)
     throws Exception
  {
    TransferInfo ti = new TransferInfo();
    ti.id = "dw" + fileName;
    ti.toTransfer = toTransfer;
    ti.fileSize = (int) toTransfer.length();
    ti.sizeBlock = getBlockSize(suggestBlockSize);
    ti.numBlock = (int) (ti.fileSize / ti.sizeBlock);
    if((ti.fileSize % ti.sizeBlock) != 0)
      ti.numBlock++;

    ti.stream = new RandomAccessFile(ti.toTransfer, "r");
    CACHE.addObject(REF_TRANSFER_CACHE_KEY, ti.id,
       new TransferCachedObject(ti));

    return populateTransferInfo(ti, new HashtableRpc());
  }

  protected Hashtable populateTransferInfo(TransferInfo ti, Hashtable rv)
  {
    rv.put(TIPAR_ID, ti.id);
    rv.put(TIPAR_NUM_BLOCK, ti.numBlock);
    rv.put(TIPAR_SIZE_BLOCK, ti.sizeBlock);
    rv.put(TIPAR_CURR_BLOCK, ti.currBlock);
    rv.put(TIPAR_FILE_SIZE, ti.fileSize);
    rv.put(TIPAR_FILE_NAME, ti.toTransfer.getName());
    return rv;
  }

  public byte[] getFileBlock(String idFile, int block)
     throws Exception
  {
    TransferInfo ti = getInfo(idFile);
    if(ti == null)
      throw new IOException("IO non inizializzato o eccessivo timeout.");

    synchronized(ti)
    {
      if(ti.stream == null)
        throw new IOException("Il trasferimento è già stato completato.");

      if(block >= ti.numBlock)
        throw new IOException("Il blocco richiesto è eccessivo per il file indicato.");

      long seek = ti.sizeBlock * block;
      ti.stream.seek(seek);

      long left = ti.fileSize - seek;
      if(left > ti.sizeBlock)
        left = ti.sizeBlock;

      byte[] rv = new byte[(int) left];
      ti.stream.read(rv);

      ti.currBlock = block;
      return rv;
    }
  }

  public Hashtable preparaUpload(String fileName, int fileSize, int suggestBlockSize)
     throws Exception
  {
    TransferInfo ti = new TransferInfo();
    ti.id = "up" + fileName;
    ti.fileSize = fileSize;
    ti.toTransfer = SERVICE.getWorkTmpFile(fileName);
    ti.toTransfer.deleteOnExit();
    ti.sizeBlock = getBlockSize(suggestBlockSize);
    ti.numBlock = (int) (ti.fileSize / ti.sizeBlock);
    if((ti.fileSize % ti.sizeBlock) != 0)
      ti.numBlock++;

    ti.stream = new RandomAccessFile(ti.toTransfer, "rw");
    CACHE.addObject(REF_TRANSFER_CACHE_KEY, ti.id,
       new TransferCachedObject(ti));

    return populateTransferInfo(ti, new HashtableRpc());
  }

  public int putFileBlock(String idFile, int block, byte[] data)
     throws Exception
  {
    TransferInfo ti = getInfo(idFile);
    if(ti == null)
      throw new IOException("IO non inizializzato o eccessivo timeout.");

    synchronized(ti)
    {
      if(ti.stream == null)
        throw new IOException("Il trasferimento è già stato completato.");

      if(block >= ti.numBlock)
        throw new IOException("Il blocco richiesto è eccessivo per il file indicato.");

      long seek = ti.sizeBlock * block;
      ti.stream.seek(seek);
      ti.stream.write(data);
      ti.currBlock = block;
      return 0;
    }
  }

  public int trasferimentoCompletato(String idFile)
     throws Exception
  {
    TransferInfo ti = getInfo(idFile);
    if(ti == null)
      throw new IOException("IO non inizializzato o eccessivo timeout.");

    synchronized(ti)
    {
      ti.stream.close();
      ti.stream = null;
      return 0;
    }
  }

  public TransferInfo getInfo(String idFile)
  {
    return (TransferInfo) CACHE.getContentQuiet(REF_TRANSFER_CACHE_KEY, idFile);
  }

  public int getBlockSize(int suggestBlockSize)
  {
    if(suggestBlockSize == 0)
      return 4096;

    if(suggestBlockSize < 1024)
      return 1024;

    if(suggestBlockSize > 8192)
      return 8192;

    return suggestBlockSize;
  }
}
