package org.isacrodi.diagnosis;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;

import java.util.Iterator;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;
import org.isacrodi.util.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

// FIXME
import libsvm.*;


/**
 *  Presence indicating feature vector mapper.
 *
 * <p>This class duplicates some code from {@link
 * SvmNodeFeatureVectorMapper} and therefore needs refactoring if it
 * is to be kept and used. At this time, this class is a left-over
 * after moving the basic feature vector mapper ignore non-available
 * features instead of constructing a missing value representation for
 * them.</p>
 */
public class PresenceIndicatingSvmNodeFeatureVectorMapper implements FeatureVectorMapper<svm_node[]>, Serializable
{
  private List<PresenceIndicatingSvmNodeComponentMapper> componentMapperList;

  private static final long serialVersionUID = 1;


  protected static PresenceIndicatingSvmNodeComponentMapper makeComponentMapper(AbstractFeature feature)
  {
    PresenceIndicatingSvmNodeComponentMapper componentMapper = null;
    if (feature instanceof NumericFeature)
    {
      componentMapper = new PresenceIndicatingNumericSvmNodeComponentMapper(feature.getName());
    }
    else if (feature instanceof CategoricalFeature)
    {
      componentMapper = new PresenceIndicatingCategoricalSvmNodeComponentMapper(feature.getName());
    }
    else
    {
      throw new IllegalArgumentException("unsupported feature type: " + feature.getClass().getCanonicalName());
    }
    return (componentMapper);
  }


  protected static void updateComponentMapper(AbstractFeature feature, PresenceIndicatingSvmNodeComponentMapper componentMapper)
  {
    if ((feature instanceof NumericFeature) && (componentMapper instanceof PresenceIndicatingNumericSvmNodeComponentMapper))
    {
      NumericFeature numericFeature = (NumericFeature) feature;
      PresenceIndicatingNumericSvmNodeComponentMapper numericSvmNodeComponentMapper = (PresenceIndicatingNumericSvmNodeComponentMapper) componentMapper;
      // FIXME: nothing to do really if we're going to map to sparse vectors -- could collect set of values to obtain statistics for scaling etc., though.
    }
    else if ((feature instanceof CategoricalFeature) && (componentMapper instanceof PresenceIndicatingCategoricalSvmNodeComponentMapper))
    {
      CategoricalFeature categoricalFeature = (CategoricalFeature) feature;
      PresenceIndicatingCategoricalSvmNodeComponentMapper categoricalSvmNodeComponentMapper = (PresenceIndicatingCategoricalSvmNodeComponentMapper) componentMapper;
      if (!categoricalSvmNodeComponentMapper.hasState(categoricalFeature.getState()))
      {
	// FIXME: using index -1 to try and trigger exceptions if index designation is forgotten or fails -- should really set a proper NA value
	categoricalSvmNodeComponentMapper.addState(categoricalFeature.getState(), -1);
      }
    }
    else
    {
      throw new IllegalArgumentException(String.format("feature / component mapper mismatch or unsupported feature or mapper: feature type %s, mapper type %s", feature.getClass().getSimpleName(), componentMapper.getClass().getSimpleName()));
    }
  }


  public PresenceIndicatingSvmNodeFeatureVectorMapper()
  {
    super();
    this.componentMapperList = new ArrayList<PresenceIndicatingSvmNodeComponentMapper>();
  }


  public PresenceIndicatingSvmNodeFeatureVectorMapper(List<PresenceIndicatingSvmNodeComponentMapper> componentMapperList)
  {
    this();
    this.componentMapperList = componentMapperList;
  }


  /**
   * Construct a mapper based on a collection of feature vectors.
   *
   * Each feature contained in a feature vector of the collection, and
   * each state of a categorical feature occurring in the collection,
   * is guaranteed to be mappable by the mapper constructed.
   *
   * @param featureVectorCollection the feature vector collection
   */
  public PresenceIndicatingSvmNodeFeatureVectorMapper(Collection<FeatureVector> featureVectorCollection)
  {
    this();
    Map<String, PresenceIndicatingSvmNodeComponentMapper> componentMapperMap = new HashMap<String, PresenceIndicatingSvmNodeComponentMapper>();
    for (FeatureVector featureVector : featureVectorCollection)
    {
      for (String featureName : featureVector.keySet())
      {
	AbstractFeature feature = featureVector.get(featureName);
	PresenceIndicatingSvmNodeComponentMapper componentMapper = componentMapperMap.get(featureName);
	if (componentMapper == null)
	{
	  componentMapper = makeComponentMapper(feature);
	  componentMapperMap.put(featureName, componentMapper);
	}
	updateComponentMapper(feature, componentMapper);
      }
    }
    this.componentMapperList = new ArrayList<PresenceIndicatingSvmNodeComponentMapper>(componentMapperMap.values());
    this.designateIndexes();
  }


  public void addComponentMapper(PresenceIndicatingSvmNodeComponentMapper componentMapper)
  {
    this.componentMapperList.add(componentMapper);
  }


  public void parseNumericFeatureBlock(String featurename, SimpleScanner s) throws IOException
  {
    Token indexToken = s.nextToken(Token.TokenType.NAMEVALUE, "index");
    Token index_presenceToken = s.nextToken(Token.TokenType.NAMEVALUE, "indexpresence");
    Token value_missingToken = s.nextToken(Token.TokenType.NAMEVALUE, "valuemissing");
    PresenceIndicatingNumericSvmNodeComponentMapper numericComponentMapper = new PresenceIndicatingNumericSvmNodeComponentMapper(featurename, Integer.parseInt(indexToken.getValue()), Integer.parseInt(index_presenceToken.getValue()), Double.parseDouble(value_missingToken.getValue()));
    this.componentMapperList.add(numericComponentMapper);
  }


  public void parseCategoricalFeatureBlock(String featurename, SimpleScanner s) throws IOException
  {

    Token endBlockToken = new Token(Token.TokenType.SYMBOL, "}");
    Token index_presenceToken = s.nextToken(Token.TokenType.NAMEVALUE, "indexpresence");
    s.nextToken(Token.TokenType.SYMBOL, "{");
    PresenceIndicatingCategoricalSvmNodeComponentMapper categoricalComponentMapper = new PresenceIndicatingCategoricalSvmNodeComponentMapper(featurename, Integer.parseInt(index_presenceToken.getValue()));
    for (Token t = s.nextToken(); !endBlockToken.equals(t); t = s.nextToken())
    {
      if (t == null)
      {
	throw new IllegalStateException("unexpected EOF");
      }
      if (t.getTokenType() != Token.TokenType.NAMEVALUE)
      {
	throw new IllegalStateException("unexpected token type: " + t);
      }
      categoricalComponentMapper.addState(t.getName(), Integer.parseInt(t.getValue()));
    }
    this.componentMapperList.add(categoricalComponentMapper);
  }


  public void parseFeatureBlock(String featurename, SimpleScanner s) throws IOException
  {
    Token endBlockToken = new Token(Token.TokenType.SYMBOL, "}");
    s.nextToken(Token.TokenType.SYMBOL, "{");
    Token typeToken = s.nextToken(Token.TokenType.NAMEVALUE, "type");
    String type = typeToken.getValue();
    if (type.equals("numeric"))
    {
      parseNumericFeatureBlock(featurename, s);
    }
    else if (type.equals("categorical"))
    {
      parseCategoricalFeatureBlock(featurename, s);
    }
    else
    {
      throw new IllegalStateException("unexpected token type: ");
    }
    s.nextToken(Token.TokenType.SYMBOL, "}");
  }


  public void parseFeatureMapperFile(BufferedReader in) throws IOException
  {
    new ArrayList<Object>();

    SimpleScanner s = new SimpleScanner(in);
    for (Token t = s.nextToken(); t != null; t = s.nextToken())
    {
      String featurename = t.getName();
      parseFeatureBlock(featurename, s);
    }
  }


  public static FeatureVectorMapper parse(String filename)
  {
    PresenceIndicatingSvmNodeFeatureVectorMapper presenceIndicatingSvmNodeFeatureVectorMapper = new PresenceIndicatingSvmNodeFeatureVectorMapper();
    // FIXME: doesn't really parse anything
    return (presenceIndicatingSvmNodeFeatureVectorMapper);
  }


  public void parseFile(String filename)
  {
    try
    {
      BufferedReader in = new BufferedReader(new FileReader(filename));
      String magic = in.readLine();
      if (magic.equals("isacrodi-featuremapper-0.1"))
      {
        parseFeatureMapperFile(in);
        // FIXME: should check for completeness (compact index set from 0 to max) after parsing is finished
      }
      else
      {
        throw new IOException(String.format("cannot identify table type of %s: unknown magic \"%s\"", filename, magic));
      }
    }
    catch (IOException e)
    {
      System.err.println(e);
    }
  }


  public int targetSpaceDimension()
  {
    // FIXME: check for nonredundant and contiguous indexes is really parseant, this method depends on it.
    int d = 0;
    for (PresenceIndicatingSvmNodeComponentMapper c : this.componentMapperList)
    {
      int n = c.getMaxIndex();
      if (n > d)
      {
	d = n;
      }
    }
    return (d + 1);
  }


  /**
   * Designate indexes for all component mappers of this feature mapper.
   *
   * <p><strong>Notice:</strong> Existing indexes are wiped out by
   * this method. This method is intended to be used to complete
   * construction of a feature vector mapper based on a training set
   * only.</p>
   */
  public void designateIndexes()
  {
    int startIndex = 0;
    for (PresenceIndicatingSvmNodeComponentMapper componentMapper : this.componentMapperList)
    {
      componentMapper.designateIndexes(startIndex);
      startIndex = componentMapper.getMaxIndex() + 1;
    }
  }


  public svm_node[] map(FeatureVector featureVector)
  {
    svm_node[] node = new svm_node[this.targetSpaceDimension()];
    for (PresenceIndicatingSvmNodeComponentMapper c : this.componentMapperList)
    {
      AbstractFeature f = featureVector.get(c.getFeatureName());
      node = c.map(f, node);
    }
    return (node);
  }
}
