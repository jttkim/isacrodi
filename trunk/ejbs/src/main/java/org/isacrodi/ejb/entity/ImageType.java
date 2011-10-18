package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import java.util.Set;
import java.util.HashSet;

import org.javamisc.Util;
import org.javamisc.jee.entitycrud.CrudConfig;


@Entity
@CrudConfig(propertyOrder = {"id", "typeName", "description", "descriptorSet", "*"})
public class ImageType extends DescriptorType
{
  private static final long serialVersionUID = 1;


  public String toString()
  {
    return (String.format("ImageType(id = %s, typeName = s, description = %d)", Util.safeStr(this.id), Util.safeStr(this.typeName), Util.safeStr(this.description)));
  }

  public ImageType()
  {
    super();
  }


  public ImageType(String typeName)
  {
    super(typeName);
  }


  public void unlink()
  {
    super.unlink();
  }


  public String fileRepresentation()
  {
    String x;
    String s = "imagetype\n{\n";
    s += String.format("  typename: %s\n", this.typeName);
    x = "";
    if (this.description != null)
    {
      x = this.description;
    }
    s += String.format("  description: %s\n", x);
    s += "}\n";
    return (s);
  }
}
