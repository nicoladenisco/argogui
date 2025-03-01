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
package org.argogui.services.wklsrv;

import org.apache.commons.configuration2.Configuration;
import org.argogui.utils.I;
import org.argogui.services.ArgoServiceException;
import org.commonlib5.utils.ClassOper;

/**
 * Factory per la costruzione del plugin di generazione file.
 *
 * @author Alberto Troiano
 */
public class FileGeneratorFactory
{
  private String[] vPaths = null;
  private String myPackage = null;
  private Configuration cfg = null;
  private static FileGeneratorFactory instance = new FileGeneratorFactory();

  private FileGeneratorFactory()
  {
  }

  public static FileGeneratorFactory getInstance()
  {
    return instance;
  }

  public void init(Configuration cfg)
  {
    this.cfg = cfg;
    vPaths = cfg.getStringArray("classpath");
    myPackage = ClassOper.getClassPackage(this.getClass());
  }

  public AbstractFileGenerator build(String name)
     throws Exception
  {
    String className = cfg.getString("generator." + name + ".class");
    if(className == null)
      throw new ArgoServiceException(I.I("Tipo di generatore non dichiarato in file di setup."));

    AbstractFileGenerator rv = loadClass(className);
    Configuration lcfg = cfg.subset("generator." + name);
    rv.init(lcfg);
    return rv;
  }

  private AbstractFileGenerator loadClass(String className)
     throws Exception
  {
    Class c = ClassOper.loadClass(className, myPackage, vPaths);

    if(c == null)
      throw new ArgoServiceException(I.I("Classe %s non trovata o non istanziabile.", className));

    return (AbstractFileGenerator) c.newInstance();
  }
}
