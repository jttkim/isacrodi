package org.isacrodi.ejb.entity;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import java.util.Set;
import java.util.HashSet;

import org.javamisc.Util;
import org.javamisc.jee.entitycrud.CrudConfig;

import org.isacrodi.diagnosis.DiagnosisProvider;


@Entity
public class SerializedDiagnosisProvider
{
  private Integer id;
  private String name;
  private int version;
  private byte[] byteSequence;


  public SerializedDiagnosisProvider()
  {
    super();
    this.byteSequence = null;
  }


  public SerializedDiagnosisProvider(String name)
  {
    this();
    this.name = name;
  }


  @Id
  @GeneratedValue
  public Integer getId()
  {
    return this.id;
  }


  public void setId(Integer id)
  {

    this.id = id;
  }


  @Version
  public int getVersion()
  {
    return (this.version);
  }


  public void setVersion(int version)
  {
    this.version = version;
  }


  @Column(unique = true)
  public String getName()
  {
    return (this.name);
  }


  public void setName(String name)
  {
    this.name = name;
  }


  public byte[] getByteSequence()
  {
    return (this.byteSequence);
  }


  public void setByteSequence(byte[] byteSequence)
  {
    this.byteSequence = byteSequence;
  }


  public DiagnosisProvider deserializeDiagnosisProvider() throws IOException, ClassNotFoundException
  {
    if (this.byteSequence == null)
    {
      return (null);
    }
    ByteArrayInputStream bis = new ByteArrayInputStream(this.byteSequence);
    ObjectInputStream diagnosisProviderIn = new ObjectInputStream(bis);
    DiagnosisProvider diagnosisProvider = (DiagnosisProvider) diagnosisProviderIn.readObject();
    diagnosisProviderIn.close();
    return (diagnosisProvider);
  }


  public void serializeDiagnosisProvider(DiagnosisProvider diagnosisProvider) throws IOException
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream diagnosisProviderOut = new ObjectOutputStream(bos);
    diagnosisProviderOut.writeObject(diagnosisProvider);
    diagnosisProviderOut.close();
    this.byteSequence = bos.toByteArray();
  }
}

