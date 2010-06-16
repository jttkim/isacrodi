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
    Token index_presenceToken = s.nextToken(Token.TokenType.NAMEVALUE, "indexpresence");
    s.nextToken(Token.TokenType.SYMBOL, "{");
    CategoricalColFeatureVectorMapperComponent ccfvmc = new CategoricalColFeatureVectorMapperComponent(featurename, Integer.parseInt(index_presenceToken.getValue()));
    ccfvmc.setCategoricalFeatureVectorMapperComponentSet(new java.util.HashSet<CategoricalFeatureVectorMapperComponent>());
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

      //CategoricalFeatureVectorMapperComponent cfvmc = new CategoricalFeatureVectorMapperComponent(featurename,Integer.parseInt(index_presenceToken.getValue()), t.getName(), Integer.parseInt(t.getValue()));
      CategoricalFeatureVectorMapperComponent cfvmc = new CategoricalFeatureVectorMapperComponent(t.getName(), Integer.parseInt(t.getValue()));
      ccfvmc.addCategoryElement(cfvmc);
    }
    this.fvmc.add(ccfvmc);
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
      if (o.getIndexPresence() >= index)
        index = o.getIndexPresence();
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
	  //numericindex[1] = nfvmc.getIndexPresence();
	  break;
	}
      }
    }
    return numericindex;
  }


  public double getValue(FeatureVector featureVector, String key)
  {
    double value = 0.0;

    NumericFeature nf = new NumericFeature();

    for (String k : featureVector.keySet())
    {
      if (featureVector.get(k).getClass().isInstance(new NumericFeature()))
      {
        nf = (NumericFeature)featureVector.get(k);
        value = nf.getValue();
	break;
      }
    }

    return value;
  }


  public String searchFeatureVector(FeatureVector featureVector, String key)
  {
    String value = "absent";

    for (String k : featureVector.keySet())
    {
      if (featureVector.get(k).equals(key))
      {
        value = "present";
	break;
      }
    }

    return value;
  }


  public String searchFeatureVectorElement(FeatureVector featureVector, String key)
  {
    String value = "";
    CategoricalFeature cf = new CategoricalFeature();

    for (String k : featureVector.keySet())
    {
      if (featureVector.get(k).equals(key))
      {
        cf = (CategoricalFeature)featureVector.get(k);
	value = cf.getValue();
	break;
      }
    }
    return value;
  }


  public double calculateAverage()
  {
    double value = 12.4;
    return(value);
  }


  public svm_node[] map(FeatureVector featureVector)
  {
    //FIXME: Unfinished method. It doesn't deal with the problem of empty feature mapper files. It's too long
    
    int index = searchMaximumIndex();
    if(index <= 0)
      index = 8;
    String filename = "/home/bkx08wju/Stuff/isacrodi/trunk/sampledata/isacrodi_feature_mapper.txt";
    svm_node[] fv = new svm_node[index + 1];
    int [] numericindex = null;
    int i = 0;

    NumericFeatureVectorMapperComponent nfvmc = new NumericFeatureVectorMapperComponent();
    CategoricalColFeatureVectorMapperComponent ccfvmc = new CategoricalColFeatureVectorMapperComponent();
      
    try
    {
      importFile(filename);
      for(FeatureVectorMapperComponent o : this.fvmc)
      {
        if(o.getClass().isInstance(new NumericFeatureVectorMapperComponent()))
	{
          fv[i] = new svm_node();
	  nfvmc = (NumericFeatureVectorMapperComponent)o;
          fv[i].index = nfvmc.getIndex();
          fv[i+1] = new svm_node();
          fv[i+1].index = o.getIndexPresence();

	  if (searchFeatureVector(featureVector, o.getName()).equals("present"))
	  {
            fv[i].value = getValue(featureVector, o.getName());
            fv[i+1].value = 1.0;
	  }
          else
	  {
            fv[i].value = calculateAverage();
            fv[i+1].value = 0.0;
	  }
	  i = i+2;
        }
	else
	{
	  ccfvmc = (CategoricalColFeatureVectorMapperComponent)o;
          fv[i + ccfvmc.getArraySize() ] = new svm_node();
	  fv[i + ccfvmc.getArraySize()].index = ccfvmc.getIndexPresence();
	  if (searchFeatureVector(featureVector, o.getName()).equals("present"))
	    fv[i + ccfvmc.getArraySize()].value = 1.0;
	  else
	    fv[i + ccfvmc.getArraySize()].value = 0.0;

          for(CategoricalFeatureVectorMapperComponent c : ccfvmc.getCategoricalFeatureVectorMapperComponentSet())
	  {
            fv[i] = new svm_node();
	    fv[i].index = c.getIndex();
	    if(searchFeatureVectorElement(featureVector, o.getName()).equals(c.getCatName()))
	      fv[i].value = 1.0;
	    else 
	      fv[i].value = 0.0;
	    i++;
          }
	}  // end categorical
      }
    }


    catch (IOException ex) 
    {
      ex.printStackTrace();
    }

  return fv;
  }

}
