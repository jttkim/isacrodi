package org.isacrodi.cmdtool;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
// FIXME: seems we don't need this after all...?
// import jsc.distributions.*;

import org.isacrodi.util.SampleableList;

import org.isacrodi.ejb.session.Access;

import org.isacrodi.ejb.io.MemoryDB;

import org.isacrodi.util.io.*;

import org.isacrodi.ejb.entity.*;


abstract class RangedDescriptor
{
  protected String descriptorTypeName;
  protected DescriptorType descriptorType;


  public RangedDescriptor(String descriptorTypeName)
  {
    this.descriptorTypeName = descriptorTypeName;
    this.descriptorType = null;
  }


  public String getDescriptorTypeName()
  {
    return (this.descriptorTypeName);
  }


  public void setDescriptorTypeName(String descriptorTypeName)
  {
    this.descriptorTypeName = descriptorTypeName;
  }


  public void resolve(Access access)
  {
    List<DescriptorType> descriptorTypeList = access.findDescriptorTypeList();
    // FIXME: linear search
    for (DescriptorType descriptorType : descriptorTypeList)
    {
      if (this.descriptorTypeName.equals(descriptorType.getTypeName()))
      {
	this.descriptorType = descriptorType;
	return;
      }
    }
    throw new RuntimeException(String.format("failed to resolve descriptor type \"%s\"", this.descriptorTypeName));
  }


  // FIXME: is distType really a parameter here?
  public abstract Descriptor randomDescriptor(Random rng, String distType);
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


  private double makeRandomValue(Random rng, String distType)
  {
    if (distType.equals("uniform"))
    {
      return (this.minValue + rng.nextDouble() * (this.maxValue - this.minValue));
    }
    else
    {
      //return Math.tan(Math.PI * (uniform() - 0.5));
      return (this.minValue + (Math.tan(Math.PI * (rng.nextDouble() - 0.5))) * (this.maxValue - this.minValue));
    }
    // FIXME: what if distType is neither "uniform" nor "cauchy"? Use an enum...
  }


  public NumericDescriptor randomDescriptor(Random rng, String disType)
  {
    if (this.descriptorType == null)
    {
      throw new RuntimeException(String.format("cannot generate random descriptor: no descriptor type for \"%s\"", this.descriptorTypeName));
    }
    return (new NumericDescriptor((NumericType) this.descriptorType, this.makeRandomValue(rng, disType)));
  }
}


class RangedCategoricalDescriptor extends RangedDescriptor
{
  private SampleableList<String> valueRange;


  public RangedCategoricalDescriptor(String descriptorTypeName)
  {
    super(descriptorTypeName);
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


  public void resolve(Access access)
  {
    super.resolve(access);
    CategoricalType categoricalType = (CategoricalType) this.descriptorType;
    for (String value : this.valueRange)
    {
      if (categoricalType.findCategoricalTypeValue(value) == null)
      {
	throw new RuntimeException(String.format("categorical type \"%s\" has no value \"%s\"", this.descriptorTypeName, value));
      }
    }
  }


  private String[] makeRandomValue(Random rng, String distType)
  {
    ArrayList<String> valueList = new ArrayList<String>();
    CategoricalType categoricalType = (CategoricalType) this.descriptorType;
    boolean multivalue = false;
    if (categoricalType != null)
    {
      multivalue = categoricalType.getMultivalue();
    }
    if (multivalue)
    {
      for (String value : this.valueRange)
      {
	if (rng.nextDouble() < 0.5)
	{
	  valueList.add(value);
	}
      }
    }
    if (valueList.size() == 0)
    {
      valueList.add(this.valueRange.randomSample(rng));
    }
    return (valueList.toArray(new String[0]));
  }


  public CategoricalDescriptor randomDescriptor(Random rng, String distType)
  {
    if (this.descriptorType == null)
    {
      throw new RuntimeException(String.format("cannot generate random descriptor: no descriptor type for \"%s\"", this.descriptorTypeName));
    }
    String[] valueList = this.makeRandomValue(rng, distType);
    CategoricalType categoricalType = (CategoricalType) this.descriptorType;
    HashSet<CategoricalTypeValue> categoricalTypeValueSet = new HashSet<CategoricalTypeValue>();
    for (String value : valueList)
    {
      categoricalTypeValueSet.add(categoricalType.findCategoricalTypeValue(value));
    }
    return (new CategoricalDescriptor(categoricalType, categoricalTypeValueSet));
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
  public ImageDescriptor randomDescriptor(Random rng, String distType)
  {
    if (this.descriptorType == null)
    {
      throw new RuntimeException(String.format("cannot generate random descriptor: no descriptor type for \"%s\"", this.descriptorTypeName));
    }
    ImageDescriptor imageDescriptor = new ImageDescriptor();
    imageDescriptor.setDescriptorType(this.descriptorType);
    imageDescriptor.setMimeType(this.mimeType);
    imageDescriptor.setImageFileName(this.imageFileName);
    return (imageDescriptor);
  }
}


/**
 * Template for generating crop disorder records based on ranges for
 * descriptor values.
 *
 * <p>Limitations:
 * <ul>
 * <li>Multiple values for categorical descriptors are generated
 *     only if the ranged CDR was resolved againsta categorical types.
 *     This is because otherwise the ranged CDR has no way of telling
 *     whether or not to generate multiple values.</li>
 * <li>When multiple values are generated, they are drawn equiprobably from
 *     power set of the range.</li>
 * <li>Generated CDRs always have all descriptors specified in the ranged CDR,
 *     in other words, no missing data is generated.</li>
 * <li>Numeric values are drawn from a uniform distribution over the range,
 *     so if ranges do not overlap, perfect class separation is possible.</li>
 * </ul>
 * </p>
 * <p><strong>Note:</strong> When the resolve facility is used, the
 * database populated with generated CDRs must be the one that was
 * used for resolving as well.</p>
 */
public class RangedCropDisorderRecord
{
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
      rangedDescriptor.resolve(access);
    }
  }


  public CropDisorderRecord randomCropDisorderRecord(Random rng, String distType, MemoryDB memoryDB, double missingNumericDescriptorProbability, double missingCategoricalDescriptorProbability, double missingImageDescriptorProbability)
  {
    CropDisorderRecord cropDisorderRecord = new CropDisorderRecord();
    IsacrodiUser isacrodiUser = memoryDB.findUser(this.isacrodiUserName);
    if (isacrodiUser == null)
    {
      throw new RuntimeException(String.format("no isacrodi user \"%s\"", this.isacrodiUserName));
    }
    cropDisorderRecord.setIsacrodiUser(isacrodiUser);
    Crop crop = memoryDB.findCrop(this.cropScientificName);
    if (crop == null)
    {
      throw new RuntimeException(String.format("no crop \"%s\"", this.cropScientificName));
    }
    cropDisorderRecord.setCrop(crop);
    cropDisorderRecord.setDescription(this.description);
    if (this.expertDiagnosedCropDisorderName != null)
    {
      CropDisorder expertDiagnosedCropDisorder = memoryDB.findCropDisorder(this.expertDiagnosedCropDisorderName);
      if (expertDiagnosedCropDisorder == null)
      {
	throw new RuntimeException(String.format("no disorder \"%s\" (specified as expert diagnosis)", this.expertDiagnosedCropDisorderName));
      }
      cropDisorderRecord.setExpertDiagnosedCropDisorder(expertDiagnosedCropDisorder);
    }
    for (RangedDescriptor rangedDescriptor : this.rangedDescriptorList)
    {
      // FIXME: descriptor types hard wired to numeric, categorical, image
      Descriptor descriptor = null;
      if (rangedDescriptor instanceof RangedNumericDescriptor)
      {
	if (rng.nextDouble() >= missingNumericDescriptorProbability)
	{
	  descriptor = rangedDescriptor.randomDescriptor(rng, distType);
	}
      }
      else if (rangedDescriptor instanceof RangedCategoricalDescriptor)
      {
	if (rng.nextDouble() >= missingCategoricalDescriptorProbability)
	{
	  descriptor = rangedDescriptor.randomDescriptor(rng, distType);
	}
      }
      else if (rangedDescriptor instanceof RangedImageDescriptor)
      {
	if (rng.nextDouble() >= missingImageDescriptorProbability)
	{
	  descriptor = rangedDescriptor.randomDescriptor(rng, distType);
	}
      }
      else
      {
	throw new RuntimeException("unsupported descriptor type");
      }
      if (descriptor != null)
      {
	cropDisorderRecord.linkDescriptor(descriptor);
      }
    }
    return (cropDisorderRecord);
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
   *
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


  public List<String> findDescriptorTypeNameList()
  {
    List<String> descriptorTypeNameList = new ArrayList<String>();
    for (RangedDescriptor rangedDescriptor : this.rangedDescriptorList)
    {
      descriptorTypeNameList.add(rangedDescriptor.getDescriptorTypeName());
    }
    return (descriptorTypeNameList);
  }


  public static List<String> findDescriptorTypeNameList(List<RangedCropDisorderRecord> rangedCropDisorderRecordList)
  {
    List<String> descriptorTypeNameList = new ArrayList<String>();
    for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      for (String descriptorTypeName : rangedCropDisorderRecord.findDescriptorTypeNameList())
      {
	if (!descriptorTypeNameList.contains(descriptorTypeName))
	{
	  descriptorTypeNameList.add(descriptorTypeName);
	}
      }
    }
    return (descriptorTypeNameList);
  }
}
