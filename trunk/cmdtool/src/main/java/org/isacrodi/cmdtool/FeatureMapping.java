package org.isacrodi.cmdtool;

import java.util.List;

import javax.naming.NamingException;
import javax.naming.InitialContext;

import libsvm.*;

// import org.isacrodi.ejb.session.CropDisorderRecordManager;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.diagnosis.*;



public class FeatureMapping
{
  private static void showMapping(SvmNodeFeatureVectorMapper sfvm, FeatureVector fv)
  {
    System.out.printf("feature vector: %s\n", fv.toString());
    System.out.printf("feature vector mapper: %s\n", sfvm.toString());
    svm_node[] node = sfvm.map(fv);
    System.out.printf("svm_node vector has %d components\n", node.length);
    for (svm_node n : node)
    {
      if (n == null)
      {
	System.out.println("null node");
      }
      else
      {
	System.out.println(String.format("%d: %f", n.index, n.value));
      }
    }
  }


  public static void check() throws Exception
  {
    SvmNodeFeatureVectorMapper sfvm = new SvmNodeFeatureVectorMapper();
    sfvm.addComponentMapper(new NumericSvmNodeComponentMapper("temperature"));
    sfvm.addComponentMapper(new NumericSvmNodeComponentMapper("altitude"));
    CategoricalSvmNodeComponentMapper cm = new CategoricalSvmNodeComponentMapper("leafcondition");
    cm.addState("normal");
    cm.addState("crinkled");
    cm.addState("rotten");
    cm.addState("yellowish");
    sfvm.addComponentMapper(cm);
    sfvm.designateIndexes();
    FeatureVector fv = new FeatureVector();
    showMapping(sfvm, fv);
    fv.putFeature(new NumericFeature("temperature", 47.11));
    showMapping(sfvm, fv);
    fv.putFeature(new CategoricalFeature("leafcondition", "yellowish"));
    showMapping(sfvm, fv);
    fv.putFeature(new CategoricalFeature("leafcondition", "rotten"));
    showMapping(sfvm, fv);
    fv.putFeature(new NumericFeature("nonsense", -1111.11));
    showMapping(sfvm, fv);
    fv.putFeature(new NumericFeature("altitude", 123.45));
    showMapping(sfvm, fv);
  }
}
