package org.isacrodi.cmdtool;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import org.isacrodi.util.SampleableList;

import org.isacrodi.ejb.session.Access;

import org.isacrodi.util.io.*;

import org.isacrodi.ejb.entity.*;


abstract class RangedDescriptor
{
  protected String descriptorTypeName;


  public RangedDescriptor(String descriptorTypeName)
  {
    this.descriptorTypeName = descriptorTypeName;
  }


    public String getDescriptorTypeName()
  {
    return (this.descriptorTypeName);
  }


  public void setDescriptorTypeName(String descriptorTypeName)
  {
    this.descriptorTypeName = descriptorTypeName;
  }


  public abstract String randomDescriptorString(Random rng);
}


class RangedNumericDescriptor extends RangedDescriptor
{
  private double minValue;
  private double maxValue;


  public RangedNumericDescriptor(String descriptorTypeName, double minValue, double maxValue)
  {
    super(descriptorTypeName);
    this.minValue = minValue;
    this.maxValue = maxValue;
  }


  public double getMinValue()
  {
    return (this.minValue);
  }


  public void setMinValue(double minValue)
  {
    this.minValue = minValue;
  }


  public double getMaxValue()
  {
    return (this.maxValue);
  }


  public void setMaxValue(double maxValue)
  {
    this.maxValue = maxValue;
  }


  public String randomDescriptorString(Random rng)
  {
    double randomValue = this.minValue + rng.nextDouble() * (this.maxValue - this.minValue);
    return (String.format("%s: %1.18e", this.descriptorTypeName, randomValue));
  }
}


class RangedCategoricalDescriptor extends RangedDescriptor
{
  private CategoricalType categoricalType;
  private SampleableList<String> valueRange;


  public RangedCategoricalDescriptor(String descriptorTypeName)
  {
    super(descriptorTypeName);
    this.categoricalType = null;
    this.valueRange = new SampleableList<String>();
  }


  public List<String> getValueRange()
  {
    return (this.valueRange);
  }


  public void setValueRange(Collection<String> valueRange)
  {
    this.valueRange = new SampleableList<String>(valueRange);
  }


  public void addValue(String value)
  {
    this.valueRange.add(value);
  }


  public CategoricalType getCategoricalType()
  {
    return (this.categoricalType);
  }


  public void setCategoricalType(CategoricalType categoricalType)
  {
    this.categoricalType = categoricalType;
  }


  public void resolve(Access access)
  {
    List<CategoricalType> categoricalTypeList = access.findCategoricalTypeList();
    // FIXME: linear search
    for (CategoricalType categoricalType : categoricalTypeList)
    {
      if (this.descriptorTypeName.equals(categoricalType.getTypeName()))
      {
	for (String value : this.valueRange)
	{
	  if (categoricalType.findCategoricalTypeValue(value) == null)
	  {
	    throw new RuntimeException(String.format("categorical type \"%s\" has no value \"%s\"", this.descriptorTypeName, value));
	  }
	}
	this.categoricalType = categoricalType;
	return;
      }
    }
    throw new RuntimeException(String.format("failed to resolve categorical type \"%s\"", this.descriptorTypeName));
  }


  public String randomDescriptorString(Random rng)
  {
    // FIXME: generates single valued descriptor specs only
    boolean multivalue = false;
    if (this.categoricalType != null)
    {
      // System.err.println(String.format("categorical descriptor \"%s\" has type \"%s\"", this.descriptorTypeName, this.categoricalType.getTypeName()));
      multivalue = categoricalType.getMultivalue();
    }
    // System.err.println(String.format("categorical descriptor \"%s\": multiple = %b", this.descriptorTypeName, multivalue));
    String s = String.format("%s: ", this.descriptorTypeName);
    int numValues = 0;
    if (multivalue)
    {
      String glue = "";
      for (String value : this.valueRange)
      {
	if (rng.nextDouble() < 0.5)
	{
	  s += String.format("%s%s", glue, value);
	  numValues++;
	  glue = ", ";
	}
      }
    }
    if (numValues == 0)
    {
      s += this.valueRange.randomSample(rng);
    }
    return (s);
  }
}


class RangedImageDescriptor extends RangedDescriptor
{
  private String imageFileName;
  private String mimeType;


  public RangedImageDescriptor(String descriptorTypeName)
  {
    super(descriptorTypeName);
  }


  public RangedImageDescriptor(String descriptorTypeName, String mimeType, String imageFileName)
  {
    this(descriptorTypeName);
    this.mimeType = mimeType;
    this.imageFileName = imageFileName;
  }


  // FIXME: returns itself with no randomisation, as image descriptors cannot really be ranged.
  // idea: could return images with some probability only -- but where to get the probability from?
  public String randomDescriptorString(Random rng)
  {
    String s = String.format("%s\n", this.descriptorTypeName);
    s += "    {\n";
    s += String.format("      mimeType: %s\n", this.mimeType);
    s += String.format("      file: %s\n", this.imageFileName);
    s += "    }";
    return (s);
  }
}


public class RangedCropDisorderRecord
{
  private String name;
  private String description;
  private List<RangedDescriptor> rangedDescriptorList;
  private String isacrodiUserName;
  private String cropScientificName;
  private String expertDiagnosedCropDisorderName;


  public RangedCropDisorderRecord()
  {
    super();
    this.rangedDescriptorList = new ArrayList<RangedDescriptor>();
  }


  public String getName()
  {
    return (this.name);
  }


  public void setName(String name)
  {
    this.name = name;
  }


  public String getDescription()
  {
    return (this.description);
  }


  public void setDescription(String description)
  {
    this.description = description;
  }


  public String getIsacrodiUserName()
  {
    return (this.isacrodiUserName);
  }


  public void setIsacrodiUserName(String isacrodiUserName)
  {
    this.isacrodiUserName = isacrodiUserName;
  }


  public String getCropScientificName()
  {
    return (this.cropScientificName);
  }


  public void setCropScientificName(String cropScientificName)
  {
    this.cropScientificName = cropScientificName;
  }


  public String getExpertDiagnosedCropDisorderName()
  {
    return (this.expertDiagnosedCropDisorderName);
  }


  public void setExpertDiagnosedCropDisorderName(String expertDiagnosedCropDisorderName)
  {
    this.expertDiagnosedCropDisorderName = expertDiagnosedCropDisorderName;
  }


  public List<RangedDescriptor> getRangedDescriptorList()
  {
    return (this.rangedDescriptorList);
  }


  public void setRangedDescriptorList(List<RangedDescriptor> rangedDescriptorList)
  {
    this.rangedDescriptorList = rangedDescriptorList;
  }


  public void addRangedDescriptor(RangedDescriptor rangedDescriptor)
  {
    this.rangedDescriptorList.add(rangedDescriptor);
  }


  public void resolve(Access access)
  {
    for (RangedDescriptor rangedDescriptor : this.rangedDescriptorList)
    {
      if (rangedDescriptor instanceof RangedCategoricalDescriptor)
      {
	RangedCategoricalDescriptor rangedCategoricalDescriptor = (RangedCategoricalDescriptor) rangedDescriptor;
	rangedCategoricalDescriptor.resolve(access);
      }
    }
  }


  public String randomCropDisorderRecordString(Random rng)
  {
    String s = "cdr\n";
    s += "{\n";
    s += String.format("  user: %s\n", this.isacrodiUserName);
    s += String.format("  crop: %s\n", this.cropScientificName);
    s += "  numericDescriptors\n";
    s += "  {\n";
    for (RangedDescriptor rangedDescriptor : this.rangedDescriptorList)
    {
      if (rangedDescriptor instanceof RangedNumericDescriptor)
      {
	s += String.format("    %s\n", rangedDescriptor.randomDescriptorString(rng));
      }
    }
    s += "  }\n";
    s += "  categoricalDescriptors\n";
    s += "  {\n";
    for (RangedDescriptor rangedDescriptor : this.rangedDescriptorList)
    {
      if (rangedDescriptor instanceof RangedCategoricalDescriptor)
      {
	s += String.format("    %s\n", rangedDescriptor.randomDescriptorString(rng));
      }
    }
    s += "  }\n";
    s += "  imageDescriptors\n";
    s += "  {\n";
    for (RangedDescriptor rangedDescriptor : this.rangedDescriptorList)
    {
      if (rangedDescriptor instanceof RangedImageDescriptor)
      {
	s += String.format("    %s\n", rangedDescriptor.randomDescriptorString(rng));
      }
    }
    s += "  }\n";
    s += "  expertDiagnosis:";
    if (this.expertDiagnosedCropDisorderName != null)
    {
      s += this.expertDiagnosedCropDisorderName;
    }
    s += "\n";
    s += "}\n";
    return (s);
  }


  private static RangedCropDisorderRecord parseRangedCropDisorderRecord(SimpleScanner scanner) throws IOException
  {
    RangedCropDisorderRecord rangedCropDisorderRecord = new RangedCropDisorderRecord();
    Token endBlockToken = new Token(Token.TokenType.SYMBOL, "}");
    scanner.nextToken(Token.TokenType.SYMBOL, "{");
    Token userToken = scanner.nextToken(Token.TokenType.NAMEVALUE, "user");
    rangedCropDisorderRecord.setIsacrodiUserName(userToken.getValue().trim());
    Token cropToken = scanner.nextToken(Token.TokenType.NAMEVALUE, "crop");
    rangedCropDisorderRecord.setCropScientificName(cropToken.getValue().trim());
    scanner.nextToken(Token.TokenType.BLOCKIDENTIFIER, "numericDescriptors");
    scanner.nextToken(Token.TokenType.SYMBOL, "{");
    Set<RangedDescriptor> rangedDescriptorSet = new HashSet<RangedDescriptor>();
    for (Token t = scanner.nextToken(); !endBlockToken.equals(t); t = scanner.nextToken())
    {
      if (t == null)
      {
	throw new IllegalStateException("unexpected EOF");
      }
      if (t.getTokenType() != Token.TokenType.NAMEVALUE)
      {
	throw new IllegalStateException("unexpected token type: " + t);
      }
      String[] minMax = t.getValue().split("--");
      if (minMax.length != 2)
      {
	throw new IllegalStateException("illegal numeric range: " + t);
      }
      double minValue = Double.parseDouble(minMax[0]);
      double maxValue = Double.parseDouble(minMax[1]);
      rangedCropDisorderRecord.addRangedDescriptor(new RangedNumericDescriptor(t.getName(), minValue, maxValue));
    }
    scanner.nextToken(Token.TokenType.BLOCKIDENTIFIER, "categoricalDescriptors");
    scanner.nextToken(Token.TokenType.SYMBOL, "{");
    for (Token t = scanner.nextToken(); !endBlockToken.equals(t); t = scanner.nextToken())
    {
      if (t == null)
      {
	throw new IllegalStateException("unexpected EOF");
      }
      if (t.getTokenType() != Token.TokenType.NAMEVALUE)
      {
	throw new IllegalStateException("unexpected token type: " + t);
      }
      RangedCategoricalDescriptor d = new RangedCategoricalDescriptor(t.getName());
      String[] valueRange = t.getValue().split(",");
      for (String value : valueRange)
      {
	d.addValue(value.trim());
      }
      rangedCropDisorderRecord.addRangedDescriptor(d);
    }
    // s.nextToken(Token.TokenType.SYMBOL, "}");
    scanner.nextToken(Token.TokenType.BLOCKIDENTIFIER, "imageDescriptors");
    scanner.nextToken(Token.TokenType.SYMBOL, "{");
    for (Token t = scanner.nextToken(); !endBlockToken.equals(t); t = scanner.nextToken())
    {
      if (t == null)
      {
	throw new IllegalStateException("unexpected EOF");
      }
      if (t.getTokenType() != Token.TokenType.BLOCKIDENTIFIER)
      {
	throw new IllegalStateException("unknown token type: " + t);
      }
      scanner.nextToken(Token.TokenType.SYMBOL, "{");
      Token mimeToken = scanner.nextToken(Token.TokenType.NAMEVALUE, "mimeType");
      Token fileToken = scanner.nextToken(Token.TokenType.NAMEVALUE, "file");
      scanner.nextToken(Token.TokenType.SYMBOL, "}");
      rangedCropDisorderRecord.addRangedDescriptor(new RangedImageDescriptor(t.getName(), mimeToken.getValue().trim(), fileToken.getValue().trim()));
    }
    Token expertDiagnosisToken = scanner.nextToken(Token.TokenType.NAMEVALUE, "expertDiagnosis");
    String expertDiagnosis = expertDiagnosisToken.getValue().trim();
    if (expertDiagnosis.length() == 0)
    {
      expertDiagnosis = null;
    }
    rangedCropDisorderRecord.setExpertDiagnosedCropDisorderName(expertDiagnosis);
    scanner.nextToken(Token.TokenType.SYMBOL, "}");
    return (rangedCropDisorderRecord);
  }


  /**
   * Parse a list of ranged crop disorder records from a file.
   *
   * <p>The syntax of the file to be parsed is (lowercase: nonterminals, allcaps: terminals as defined below):</p>
   * <pre>
   * rangedcdrfile ::= MAGIC rangedcdr*
   * rangedcdr ::= "cdr" "{" user crop numericdescriptors categoricaldescriptors imagedescriptors expertdiagnosis "}"
   * user ::= "user" ":" STRINGTONL
   * crop ::= "crop" ":" STRINGTONL
   * numericdescriptors ::= "numericalDescriptors" "{" numericdescriptor* "}"
   * numericdescriptor ::= IDENTIFIER ":" DOUBLE "--" DOUBLE
   * categoricaldescriptors ::= "categoricalDescriptors" "{" categoricaldescriptor* "}"
   * categoricaldescriptor ::= IDENTIFIER ":" csvlist
   * csvlist ::= IDENTIFIER
   *         | IDENTIFIER "," csvlist
   * imagedescriptors ::= "imageDescriptors" "{" imagedescriptor* "}"
   * imagedescriptor ::= IDENTIFIER "{" "mimeType" ":" STRINGTONL "file" ":" STRINGTONL "}"
   * expertdiagnosis ::= "expertDiagnosis" ":" STRINGTONL
   * </pre>
   * Comments on terminals:
   * <ul>
   * <li><code>MAGIC</code> is the value <code>isacrodi-rangedcdrs-0.1</code> (at the time of writing this)</li>
   * <li><code>IDENTIFIER</code> is a string starting with a letter and consisting of alphanumeric characters and underscores.</li>
   * <li><code>STRINGTONL</code> is a string comprised of all characters to the next newline (not including the newline)</li>
   * <li><code>DOUBLE</code> is a string acceptable to <code>java.lang.Double.parseDouble</code></li>
   * </ul>
   * <p>The specification given here may be imprecise regarding required newlines.</p>
   * @param in the buffered reader to parse from
   * @return a list of ranged crop disorder records
   */
  public static List<RangedCropDisorderRecord> parseRangedCropDisorderRecordList(BufferedReader in) throws IOException
  {
    String magic = in.readLine();
    if (!magic.equals("isacrodi-rangedcdrs-0.1"))
    {
      throw new IOException(String.format("bad rangedcdrs magic: %s", magic));
    }
    List<RangedCropDisorderRecord> rangedCropDisorderRecordList = new ArrayList<RangedCropDisorderRecord>();
    Token cdrToken = new Token(Token.TokenType.BLOCKIDENTIFIER, "cdr");
    SimpleScanner scanner = new SimpleScanner(in);
    // t will be null at EOF, so can't just expect a cdr token
    for (Token t = scanner.nextToken(); t != null; t = scanner.nextToken())
    {
      if (!cdrToken.equals(t))
      {
	throw new IllegalStateException("no cdr token");
      }
      rangedCropDisorderRecordList.add(parseRangedCropDisorderRecord(scanner));
    }
    return (rangedCropDisorderRecordList);
  }
}
