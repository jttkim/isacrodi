package org.isacrodi.struts;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.isacrodi.ejb.entity.*;

import org.isacrodi.ejb.session.CropDisorderRecordManager;

import org.javamisc.Util;
import static org.javamisc.Util.genericTypecast;


public class CdrFormAction extends CropDisorderRecordActionSupport implements ModelDriven<CropDisorderRecord>, Preparable
{
  private String cropScientificName;
  private Set<Descriptor> descriptorSet;
  private String expertDiagnosisName;
  private Double altitude;
  private Double monthlyaveragetemperature;
  private Double monthlyaveragehumidity;
  private Double monthlyprecipitation;
  private Double cultivatedarea;
  private Double cropage;
  private Double relativeaffectedarea;
  private Double irrigationamount;
  private Double irrigationfrequency;
  private Double pH;
  private Double pestdensity;
  private String irrigationsystem;
  private String irrigationorigin;
  private String soil;
  private String symptom;
  private String firstsymptomcropstage;
  private String affectedpart;
  private String cropstage;
  private String pesttype;
  private String diseasefielddistribution;
  private String seedlingorigin;
  private NumericType altitudeType;
  private NumericType monthlyaveragetemperatureType;
  private NumericType monthlyaveragehumidityType;
  private NumericType monthlyprecipitationType;
  private NumericType cultivatedareaType;
  private NumericType cropageType;
  private NumericType relativeaffectedareaType;
  private NumericType irrigationamountType;
  private NumericType irrigationfrequencyType;
  private NumericType pHType;
  private NumericType pestdensityType;

  public CdrFormAction() throws NamingException
  {
    super();
    this.descriptorSet = new HashSet<Descriptor>();
    this.altitude = null;
    this.monthlyaveragetemperature = null;
    this.monthlyaveragehumidity = null;
    this.monthlyprecipitation = null;
    this.cultivatedarea = null;
    this.cropage = null;
    this.relativeaffectedarea = null;
    this.irrigationamount = null;
    this.irrigationfrequency = null;
    this.pH = null;
    this.pestdensity = null;

    this.altitudeType = this.access.findNumericType("altitude");
    this.monthlyaveragetemperatureType = this.access.findNumericType("monthlyaveragetemperature");
    this.monthlyaveragehumidityType = this.access.findNumericType("monthlyaveragehumidity");
    this.monthlyprecipitationType = this.access.findNumericType("monthlyprecipitation");
    this.cultivatedareaType = this.access.findNumericType("cultivatedarea");
    this.cropageType = this.access.findNumericType("cropage");
    this.relativeaffectedareaType = this.access.findNumericType("relativeaffectedarea");
    this.irrigationamountType = this.access.findNumericType("irrigationamount");
    this.irrigationfrequencyType = this.access.findNumericType("irrigationfrequency");
    this.pHType = this.access.findNumericType("pH");
    this.pestdensityType = this.access.findNumericType("pestdensity");
  }


  public void prepare()
  {
    super.prepare();
    if (this.cropDisorderRecord != null)
    {
      Set<NumericDescriptor> numericDescriptorSet = this.cropDisorderRecord.findNumericDescriptorSet();
      for (NumericDescriptor numericDescriptor : numericDescriptorSet)
      {
	NumericType numericType = (NumericType) numericDescriptor.getDescriptorType();
	if ("altitude".equals(numericType.getTypeName()))
	{
	  this.altitude = new Double(numericDescriptor.getNumericValue());
	}
      }
    }
  }


  public NumericType getAltitudeType()
  {
    return (this.altitudeType);
  }

  public NumericType getMonthlyaveragetemperatureType()
  {
    return (this.monthlyaveragetemperatureType);
  }

  public NumericType getMonthlyaveragehumidityType()
  {
    return (this.monthlyaveragehumidityType);
  }

  public NumericType getMonthlyprecipitationType()
  {
    return (this.monthlyprecipitationType);
  }

  public NumericType getCultivatedareaType()
  {
    return (this.cultivatedareaType);
  }

  public NumericType getCropageType()
  {
    return (this.cropageType);
  }

  public NumericType getRelativeaffectedareaType()
  {
    return (this.relativeaffectedareaType);
  }

  public NumericType getIrrigationamountType()
  {
    return (this.irrigationamountType);
  }

  public NumericType getIrrigationfrequencyType()
  {
    return (this.irrigationfrequencyType);
  }

  public NumericType getPHType()
  {
    return (this.pHType);
  }

  public NumericType getPestdensityType()
  {
    return (this.pestdensityType);
  }

  public String getCropScientificName()
  {
    if (this.cropScientificName == null)
    {
      if ((this.cropDisorderRecord != null) && (this.cropDisorderRecord.getCrop() != null))
      {
	this.cropScientificName = this.cropDisorderRecord.getCrop().getScientificName();
      }
    }
    return (this.cropScientificName);
  }


  public void setCropScientificName(String cropScientificName)
  {
    this.cropScientificName = cropScientificName;
  }


  public String getExpertDiagnosisName()
  {
    return (this.expertDiagnosisName);
  }


  public void setExpertDiagnosisName(String expertDiagnosisName)
  {
    this.expertDiagnosisName = expertDiagnosisName;
  }


  public String getIrrigationsystem()
  {
    return (this.irrigationsystem);
  }


  public void setIrrigationsystem(String irrigationsystem)
  {
    this.irrigationsystem = irrigationsystem;
    this.LOG.info(String.format("CdrFormAction.setIrrigationsystem: set to %s", irrigationsystem));
  }


  public List<CategoricalTypeValue> getIrrigationsystemValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("irrigationsystem");
    /*
    for (CategoricalTypeValue categoricalTypeValue : categoricalTypeValueList)
    {
      this.LOG.info(String.format("irrigation system value: %s", categoricalTypeValue.getValueType()));
    }
    this.LOG.info(String.format("irrigation system value list size: %d", categoricalTypeValueList.size()));
    this.LOG.info(String.format("returning %s", categoricalTypeValueList.toString()));
    */
    return(categoricalTypeValueList);
  }


  public List<CategoricalTypeValue> getIrrigationoriginValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("irrigationorigin");
    return(categoricalTypeValueList);
  }


  public List<CategoricalTypeValue> getSoilValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("soil");
    return(categoricalTypeValueList);
  }


  public List<CategoricalTypeValue> getSymptomValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("symptom");
    return(categoricalTypeValueList);
  }


  public List<CategoricalTypeValue> getFirstsymptomcropstageValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("firstsymptomcropstage");
    return(categoricalTypeValueList);
  }


  public List<CategoricalTypeValue> getAffectedpartValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("affectedpart");
    return(categoricalTypeValueList);
  }


  public List<CategoricalTypeValue> getCropstageValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("cropstage");
    return(categoricalTypeValueList);
  }


  public List<CategoricalTypeValue> getPesttypeValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("pesttype");
    return(categoricalTypeValueList);
  }


  public List<CategoricalTypeValue> getDiseasefielddistributionValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("diseasefielddistribution");
    return(categoricalTypeValueList);
  }


  public List<CategoricalTypeValue> getSeedlingoriginValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("seedlingorigin");
    return(categoricalTypeValueList);
  }


  // Numerical data

  public Double getAltitude()
  {
    this.LOG.info("getAltitude");
    return (this.altitude);
  }


  public void setAltitude(Double altitude)
  {
    this.LOG.info("setAltitude");
    this.altitude = altitude;
  }


  public Double getMonthlyaveragetemperature()
  {
    return (this.monthlyaveragetemperature);
  }


  public void setMonthlyaveragetemperature(Double monthlyaveragetemperature)
  {
    this.monthlyaveragetemperature = monthlyaveragetemperature;
  }


  public Double getMonthlyaveragehumidity()
  {
    return (this.monthlyaveragehumidity);
  }


  public void setMonthlyaveragehumidity(Double monthlyaveragehumidity)
  {
    this.monthlyaveragehumidity = monthlyaveragehumidity;
  }


  public Double getMonthlyprecipitation()
  {
    return (this.monthlyprecipitation);
  }

  public void setMonthlyprecipitation(Double monthlyprecipitation)
  {
    this.monthlyprecipitation = monthlyprecipitation;
  }


  public Double getCultivatedarea()
  {
    return (this.cultivatedarea);
  }

  public void setCultivatedarea(Double cultivatedarea)
  {
    this.cultivatedarea = cultivatedarea;
  }


  public Double getCropage()
  {
    return (this.cropage);
  }

  public void setCropage(Double cropage)
  {
    this.cropage = cropage;
  }

  public Double getRelativeaffectedarea()
  {
    return (this.relativeaffectedarea);
  }

  public void setRelativeaffectedarea(Double relativeaffectedarea)
  {
    this.relativeaffectedarea = relativeaffectedarea;
  }


  public Double getIrrigationamount()
  {
    return (this.irrigationamount);
  }

  public void setIrrigationamount(Double irrigationamount)
  {
    this.irrigationamount = irrigationamount;
  }

  public Double getIrrigationfrequency()
  {
    return (this.irrigationfrequency);
  }

  public void setIrrigationfrequency(Double irrigationfrequency)
  {
    this.irrigationfrequency = irrigationfrequency;
  }

  public Double getPH()
  {
    return (this.pH);
  }

  public void setPH(Double pH)
  {
    this.pH = pH;
  }


  public Double getPestdensity()
  {
    return (this.pestdensity);
  }


  public void setPestdensity(Double pestdensity)
  {
    this.pestdensity = pestdensity;
  }


// Categorical

  public String getIrrigationorigin()
  {
    return (this.irrigationorigin);
  }


  public void setIrrigationorigin(String irrigationorigin)
  {
    this.irrigationorigin = irrigationorigin;
  }


  public String getSoil()
  {
    return (this.soil);
  }


  public void setSoil(String soil)
  {
    this.soil = soil;
  }


  public String getSymptom()
  {
    return (this.symptom);
  }


  public void setSymptom(String symptom)
  {
    this.symptom = symptom;
  }


  public String getFirstsymptomcropstage()
  {
    return (this.firstsymptomcropstage);
  }


  public void setFirstsymptomcropstage(String fisrtsymptomcropstage)
  {
    this.firstsymptomcropstage = firstsymptomcropstage;
  }


  public String getAffectedpart()
  {
    return (this.affectedpart);
  }


  public void setAffectedpart(String affectedpart)
  {
    this.affectedpart = affectedpart;
  }


  public String getCropstage()
  {
    return (this.cropstage);
  }


  public void setCropstage(String cropstage)
  {
    this.cropstage = cropstage;
  }


  public String getPesttype()
  {
    return (this.pesttype);
  }


  public void setPesttype(String pesttype)
  {
    this.pesttype = pesttype;
  }


  public String getDiseasefielddistribution()
  {
    return (this.diseasefielddistribution);
  }


  public void setDiseasefielddistribution(String diseasefielddistribution)
  {
    this.diseasefielddistribution = diseasefielddistribution;
  }


  public String getSeedlingorigin()
  {
    return (this.seedlingorigin);
  }


  public void setSeedlingorigin(String seedlingorigin)
  {
    this.seedlingorigin = seedlingorigin;
  }


  public List<Crop> getCropList()
  {
    List<Crop> cropList = this.access.findCropList();
    return (cropList);
  }


  private void addCategoricalDescriptorToMap(Map<Integer, Set<String>> categoricalDescriptorMap, String descriptorTypeName, String descriptorValueCsv)
  {
    // FIXME: too many weird checks here...
    if (descriptorValueCsv == null)
    {
      return;
    }
    String[] categoricalTypeValueNameList = Util.splitTrim(descriptorValueCsv, ",");
    if (categoricalTypeValueNameList.length == 0)
    {
      return;
    }
    CategoricalType categoricalType = this.access.findCategoricalType(descriptorTypeName);
    if (categoricalType == null)
    {
      throw new RuntimeException(String.format("categorical type %s does not exist", descriptorTypeName));
    }
    if (!categoricalType.getMultivalue() && (categoricalTypeValueNameList.length != 1))
    {
      throw new RuntimeException(String.format("categorical type %s does not allow multiple values", categoricalType.getTypeName()));
    }
    Set<String> valueSet = new HashSet<String>();
    for (String categoricalValueName : categoricalTypeValueNameList)
    {
      valueSet.add(categoricalValueName);
    }
    categoricalDescriptorMap.put(categoricalType.getId(), valueSet);
  }


  public String execute()
  {
    this.LOG.info("show CDR: executing");
    if (this.altitude == null)
    {
      this.LOG.info("altitude = null");
    }
    else
    {
      this.LOG.info(String.format("altitude = %f", this.altitude.doubleValue()));
    }
    if (this.cropDisorderRecord.getId() == null)
    {
      this.cropDisorderRecordId = this.access.insert(this.isacrodiUser.getUsername(), this.cropScientificName, this.descriptorSet, this.expertDiagnosisName);

      HashMap<Integer, Set<String>> categoricalDescriptorMap = new HashMap<Integer, Set<String>>();
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "irrigationsystem", this.irrigationsystem);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "irrigationorigin", this.irrigationorigin);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "soil", this.soil);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "symptom", this.symptom);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "firstsymptomcropstage", this.firstsymptomcropstage);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "affectedpart", this.affectedpart);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "cropstage", this.cropstage);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "pesttype", this.pesttype);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "diseasefielddistribution", this.diseasefielddistribution);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "seedlingorigin", this.seedlingorigin);
      this.cropDisorderRecordManager.updateCategoricalDescriptors(this.cropDisorderRecordId, categoricalDescriptorMap);

      // sample for updateing numerical types
      Map<Integer, Double> numericDescriptorMap = new HashMap<Integer, Double>();
      if (this.altitude != null)
      {
	numericDescriptorMap.put(this.altitudeType.getId(), this.altitude);
      }

      if (this.monthlyaveragetemperature != null)
      {
	numericDescriptorMap.put(this.monthlyaveragetemperatureType.getId(), this.monthlyaveragetemperature);
      }

      if (this.monthlyaveragehumidity != null)
      {
	numericDescriptorMap.put(this.monthlyaveragehumidityType.getId(), this.monthlyaveragehumidity);
      }

      if (this.monthlyprecipitation != null)
      {
	numericDescriptorMap.put(this.monthlyprecipitationType.getId(), this.monthlyprecipitation);
      }

      if (this.cultivatedarea != null)
      {
	numericDescriptorMap.put(this.cultivatedareaType.getId(), this.cultivatedarea);
      }
      if (this.cropage != null)
      {
	numericDescriptorMap.put(this.cropageType.getId(), this.cropage);
      }
      if (this.relativeaffectedarea != null)
      {
	numericDescriptorMap.put(this.relativeaffectedareaType.getId(), this.relativeaffectedarea);
      }
      if (this.irrigationamount != null)
      {
	numericDescriptorMap.put(this.irrigationamountType.getId(), this.irrigationamount);
      }
      if (this.irrigationfrequency != null)
      {
	numericDescriptorMap.put(this.irrigationfrequencyType.getId(), this.irrigationfrequency);
      }
      if (this.pH != null)
      {
	numericDescriptorMap.put(this.pHType.getId(), this.pH);
      }
      if (this.pestdensity != null)
      {
	numericDescriptorMap.put(this.pestdensityType.getId(), this.pestdensity);
      }

      this.cropDisorderRecordManager.updateNumericDescriptors(this.cropDisorderRecordId, numericDescriptorMap);


    }
    else
    {
      // FIXME: cannot update descriptors...
      this.cropDisorderRecordManager.update(this.cropDisorderRecord, this.cropScientificName, null);
    }
    return (SUCCESS);
  }


  public void validate()
  {
  }
}
