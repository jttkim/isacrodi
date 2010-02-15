package isacrodi.ejb.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.OneToMany;
import javax.persistence.Column;
import java.util.Set;


@Entity
public class IsacrodiUser implements Serializable
{
  private Integer id;
  private int version;
  private String lastname;
  private String firstname;
  private String username;
  private String passwordHash;
  private String email;
  private Set<CropDisorderRecord> cdr;

  private static final long serialVersionUID = 1;


  public IsacrodiUser()
  {
    super();
  }


  public IsacrodiUser(String lastname, String firstname, String username, String passwordHash, String email)
  {
    this();
    this.lastname = lastname;
    this.firstname = firstname;
    this.username = username;
    this.passwordHash = passwordHash;
    this.email = email;
  }


  @Id @GeneratedValue	
  public Integer getId()
  {
    return id;
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


  @OneToMany(mappedBy="isacrodiuser")
  public Set<CropDisorderRecord> getCropDisorderRecordSet()
  {
    return cdr;
  }

  public void setCropDisorderRecordSet(Set<CropDisorderRecord> cdr)
  {
    this.cdr = cdr;
  }


  @Column(nullable = false)
  public String getLastname()
  {
    return (this.lastname);
  }


  public void setLastname(String lastname)
  {
    this.lastname = lastname;
  }


  public String getFirstname()
  {
    return (this.firstname);
  }


  public void setFirstname(String firstname)
  {
    this.firstname = firstname;
  }


  @Column(unique = true, nullable = false)
  public String getEmail()
  {
    return (this.email);
  }


  public void setEmail(String email)
  {
    this.email = email;
  }


  @Column(unique = true, nullable = false)
  public String getUsername()
  {
    return username;
  }
	

  public void setUsername(String username)
  {
    this.username = username;
  }


  public String getPasswordHash()
  {
    return passwordHash;
  }
	

  public void setPasswordHash(String passwordHash)
  {
    this.passwordHash = passwordHash;
  }


  /**
   * Check whether a given password is valid for this user.
   *
   * @param password the password, in clear text (!!)
   * @return {@code true} if the password is valid, {@code false}
   * otherwise.
   */
  public boolean checkPassword(String password)
  {
    if (password == null)
    {
      return (false);
    }
    String passwordHash = hash(password);
    return (this.passwordHash.equals(passwordHash));
  }


  /**
   * Compute the hash, given a password in clear text.
   *
   * @param password the password
   * @return the hashcode
   */
  public static String hash(String password)
  {
    try
    {
      MessageDigest m = MessageDigest.getInstance("MD5");
      m.update(password.getBytes("US-ASCII"));
      byte[] digest = m.digest();
      String h = "";
      for (byte b : digest)
      {
	int i = (int) b;
	if (i < 0)
	{
	  i = 256 + b;
	}
	h += String.format("%02x", i);
      }
      return (h);
    }
    catch (NoSuchAlgorithmException e)
    {
      throw new RuntimeException("caught impossible " + e.toString());
    }
    catch (UnsupportedEncodingException e)
    {
      throw new RuntimeException("caught impossible " + e.toString());
    }
  }
}

