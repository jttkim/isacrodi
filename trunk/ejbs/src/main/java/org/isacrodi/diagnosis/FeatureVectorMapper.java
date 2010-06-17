package org.isacrodi.diagnosis;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.Iterator;
import javax.naming.*;


import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;
import org.isacrodi.util.io.*;
import java.util.List;
import java.util.ArrayList;
import libsvm.*;


/**
 *  Feature Vector Mapper.
 */


public class FeatureVectorMapper
{

  private List<AbstractComponentMapper> componentMapperList;


  public FeatureVectorMapper()
  {
    super();
    this.componentMapperList = new ArrayList<AbstractComponentMapper>();
  }

  
  public FeatureVectorMapper(List<AbstractComponentMapper> componentMapperList)
  {
    this();
    this.componentMapperList = componentMapperList;
  }


  public void addComponentMapper(AbstractComponentMapper componentMapper)
  {
    this.componentMapperList.add(componentMapper);
  }


  public void parseNumericFeatureBlock(String featurename, SimpleScanner s) throws IOException
  {
    Token indexToken = s.nextToken(Token.TokenType.NAMEVALUE, "index");
    Token index_presenceToken = s.nextToken(Token.TokenType.NAMEVALUE, "indexpresence");
    Token value_missingToken = s.nextToken(Token.TokenType.NAMEVALUE, "valuemissing");
    NumericComponentMapper numericComponentMapper = new NumericComponentMapper(featurename, Integer.parseInt(indexToken.getValue()), Integer.parseInt(index_presenceToken.getValue()), Double.parseDouble(value_missingToken.getValue()));
    this.componentMapperList.add(numericComponentMapper);
  }


  public void parseCategoricalFeatureBlock(String featurename, SimpleScanner s) throws IOException
  {

    Token endBlockToken = new Token(Token.TokenType.SYMBOL, "}");
    Token index_presenceToken = s.nextToken(Token.TokenType.NAMEVALUE, "indexpresence");
    s.nextToken(Token.TokenType.SYMBOL, "{");
    CategoricalComponentMapper categoricalComponentMapper = new CategoricalComponentMapper(featurename, Integer.parseInt(index_presenceToken.getValue()));
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
    FeatureVectorMapper featureVectorMapper = new FeatureVectorMapper();
    // FIXME: doesn't really parse anything
    return (featureVectorMapper);
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


  public int getMappedVectorDimension()
  {
    // FIXME: check for nonredundant and contiguous indexes is really parseant, this method depends on it.
    int d = 0;
    for (AbstractComponentMapper c : this.componentMapperList)
    {
      int n = c.getMaxIndex();
      if (n > d)
      {
	d = n;
      }
    }
    return (d);
  }


  public svm_node[] map(FeatureVector featureVector)
  {
    svm_node[] node = new svm_node[this.getMappedVectorDimension() + 1];
    for (AbstractComponentMapper c : this.componentMapperList)
    {
      AbstractFeature f = featureVector.get(c.getFeatureName());
      node = c.map(f, node);
    }
    return (node);
  }


  public static void main(String[] args) throws Exception
  {
    FeatureVectorMapper fvm = new FeatureVectorMapper();
    fvm.addComponentMapper(new NumericComponentMapper("temperature", 0, 1, 23.4));
    fvm.addComponentMapper(new NumericComponentMapper("altitude", 2, 3, 471.1));
    CategoricalComponentMapper m = new CategoricalComponentMapper("leafcondition", 5);
    m.addState("normal", 6);
    m.addState("crinkled", 7);
    m.addState("rotten", 8);
    m.addState("yellowish", 9);
    // fvm.parseFile(args[0]);
    svm_node[] node = fvm.map(new FeatureVector());
    for (svm_node n : node)
    {
      System.out.println(String.format("%d: %f", n.index, n.value));
    }
  }
}
