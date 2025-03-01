/*
 *  PatientResultBean.java
 *  Creato il Sep 9, 2016, 6:41:27 PM
 *
 *  Copyright (C) 2016 RAD-IMAGE s.r.l.
 *
 *  Questo software è proprietà di RAD-IMAGE s.r.l.
 *  Tutti gli usi non esplicitimante autorizzati sono da
 *  considerarsi tutelati ai sensi di legge.
 *
 *  RAD-IMAGE s.r.l.
 *  Via San Giovanni 1 - Contrada Belvedere
 *  San Nicola Manfredi (BN)
 */
package org.argogui.services.dcmsrv;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean per l'incapsulamento dati pazienti.
 *
 * @author Nicola De Nisco
 */
public class PatientResultBean
{
  public ArrayList<StudyResultBean> arEsami = new ArrayList<>();
  public String id, name, birth;

  public List<StudyResultBean> getStudies()
  {
    return arEsami;
  }

  public String getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public String getBirth()
  {
    return birth;
  }

  public void sort()
  {
    arEsami.sort((e1, e2) -> e1.compareTo(e2));
    arEsami.forEach((e) -> e.sort());
  }
}
