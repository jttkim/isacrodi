package org.isacrodi.diagnosis;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.Iterator;
import javax.naming.*;


import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;
import org.isacrodi.util.io.*;
import java.util.Collection;
import java.util.ArrayList;
import libsvm.*;


/**
*  Feature Vector Mapper
**/


public class FeatureVectorMapper
{

  private Collection<FeatureVectorMapperComponent> fvmc = new ArrayList<FeatureVectorMapperComponent>();


  public FeatureVectorMapper()
  {
    super();
  }

  
  public FeatureVectorMapper(Collection <FeatureVectorMapperComponent> fvmc)
  {
    this();
    this.fvmc = fvmc;
  }


  public void importNumericFeatureBlock(String featurename, SimpleScanner s) throws IOException
  {
    Token indexToken = s.nextToken(Token.TokenType.NAMEVALUE, "index");
    Token index_presenceToken = s.nextToken(Token.TokenType.NAMEVALUE, "indexpresence");
    Token value_missingToken = s.nextToken(Token.TokenType.NAMEVALUE, "valuemissing");
    NumericFeatureVectorMapperComponent nfvmc = new NumericFeatureVectorMapperComponent(featurename, Integer.parseInt(indexToken.getValue()), Integer.parseInt(index_presenceToken.getValue()), Double.parseDouble(value_missingToken.getValue()));
    this.fvmc.add(nfvmc);
  }


  public void importCategoricalFeatureBlock(String featurename, SimpleScanner s) throws IOException
  {

    Token endBlockToken = new Token(Token.TokenType.SYMBOL, "}");
    s.nextToken(Token.TokenType.SYMBOL, "{");
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
      CategoricalFeatureVectorMapperComponent cfvmc = new CategoricalFeatureVectorMapperComponent(featurename, t.getName(), Integer.parseInt(t.getValue()));
      this.fvmc.add(cfvmc);
    }
  }


  public void importFeatureBlock(String featurename, SimpleScanner s) throws IOException
  {
    Token endBlockToken = new Token(Token.TokenType.SYMBOL, "}");
    s.nextToken(Token.TokenType.SYMBOL, "{");
    Token typeToken = s.nextToken(Token.TokenType.NAMEVALUE, "type");
    String type = typeToken.getValue();
    if (type.equals("numeric"))
      importNumericFeatureBlock(featurename, s);
    else if (type.equals("categorical")) 
      importCategoricalFeatureBlock(featurename, s);
    else 
      throw new IllegalStateException("unexpected token type: ");

    s.nextToken(Token.TokenType.SYMBOL, "}");
  }


  public void importFeatureMapperFile(BufferedReader in) throws IOException
  {
    new ArrayList<Object>();

    SimpleScanner s = new SimpleScanner(in); 
    for (Token t = s.nextToken(); t != null; t = s.nextToken())
    {
      String featurename = t.getName();
      importFeatureBlock(featurename, s);
    }
  }


  public void importFile(String filename) throws IOException
  {

    BufferedReader in = new BufferedReader(new FileReader(filename));
    String magic = in.readLine();
    if (magic.equals("isacrodi-featuremapper-0.1"))
    {
      importFeatureMapperFile(in);
    }
    else
    {
      throw new IOException(String.format("cannot identify table type of %s: unknown magic \"%s\"", filename, magic));
    }
  }


  public int searchMaximumIndex()
  {
    int index = 0;

    CategoricalFeatureVectorMapperComponent cfvmc = new CategoricalFeatureVectorMapperComponent();
    NumericFeatureVectorMapperComponent nfvmc = new NumericFeatureVectorMapperComponent();

    for(FeatureVectorMapperComponent o : this.fvmc)
    {
        if(o.getClass().isInstance(new CategoricalFeatureVectorMapperComponent()))
	{
	  cfvmc = (CategoricalFeatureVectorMapperComponent)o;
	  if (cfvmc.getIndex() > index)
	    index = cfvmc.getIndex();
	}

        else if(o.getClass().isInstance(new NumericFeatureVectorMapperComponent()))
	{
	  nfvmc = (NumericFeatureVectorMapperComponent)o;
	  if (nfvmc.getIndex() > index)
	    index = cfvmc.getIndex();
	}
	else 
          throw new IllegalStateException("%s unexpected class ");
    }

    return index;
    
  }


  public int searchCategoricalFeature(String key)
  {

    int index = 0;

    CategoricalFeatureVectorMapperComponent cfvmc = new CategoricalFeatureVectorMapperComponent();

    for(FeatureVectorMapperComponent o : this.fvmc)
    {
      if(o.getName().equals(key))
      {
        if(o.getClass().isInstance(new CategoricalFeatureVectorMapperComponent()))
	{
	  cfvmc = (CategoricalFeatureVectorMapperComponent)o;
	  index = cfvmc.getIndex();
	  break;
	}
      }
    }
    return index;
  }


  public int [] searchNumericFeature(String key)
  {

    int [] numericindex = new int[2];

    NumericFeatureVectorMapperComponent nfvmc = new NumericFeatureVectorMapperComponent();

    for(FeatureVectorMapperComponent o : this.fvmc)
    {
      if(o.getName().equals(key))
      {
        if(o.getClass().isInstance(new NumericFeatureVectorMapperComponent()))
	{
	  nfvmc = (NumericFeatureVectorMapperComponent)o;
	  numericindex[0] = nfvmc.getIndex();
	  numericindex[1] = nfvmc.getIndexPresence();
	  break;
	}
      }
    }
    return numericindex;
  }


  public svm_node[] map(FeatureVector featureVector)
  {
    //FIXME: Unfinished method. It doesn't deal with the problem of empty feature mapper files
    
    int index = searchMaximumIndex();
    if (index <= 0)
      index = 8;

    String filename = "/home/bkx08wju/Stuff/isacrodi/trunk/sampledata/isacrodi_feature_mapper.txt";
    svm_node[] fv = new svm_node[index];
    int [] numericindex = null;
    int i = 0;

   
    try
    {
      importFile(filename);
      for (String k : featureVector.keySet())
      {
        if(featureVector.get(k).getClass().isInstance(new CategoricalFeature()))
	{
          fv[i] = new svm_node();
          fv[i].index = searchCategoricalFeature(k);
          fv[i].value = 1.00;
          i = i + 1;
        }
	else if (featureVector.get(k).getClass().isInstance(new NumericFeature())) 
	{
	  NumericFeature nf = new NumericFeature();
          fv[i] = new svm_node();
          numericindex = searchNumericFeature(k);
          fv[i].index = numericindex[0];
	  nf  = (NumericFeature)featureVector.get(k);
          fv[i].value = nf.getValue();
          i = i + 1;
	}
      }
    }
    catch (IOException ex) 
    {
      ex.printStackTrace();
    }
  
  return fv;
  }

}
