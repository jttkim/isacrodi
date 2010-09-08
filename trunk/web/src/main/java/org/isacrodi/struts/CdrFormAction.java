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
  // New batch
  private String overallappearance;
  private String leafdiscoloration;
  private String leafappearance;
  private String leafsymptom;
  private String seedlingsymptom;
  private String rootsymptom;
  private String lesioncolour;
  private String lesionshape;
  private String lesionappearance;
  private String odour;
  private String lesionlocation;
  private String steminternal;
  private String drainage;

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

    // FIXME: consider retrieving these in the execute method -- they are not needed anywhere else
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


  public Double prepareNumericMap(String descriptorName)
  {
    Set<NumericDescriptor> numericDescriptorSet = this.cropDisorderRecord.findNumericDescriptorSet();
    for (NumericDescriptor numericDescriptor : numericDescriptorSet)
    {
      NumericType numericType = (NumericType) numericDescriptor.getDescriptorType();
      if (descriptorName.equals(numericType.getTypeName()))
      {
        return(new Double(numericDescriptor.getNumericValue()));
      }
    }
    return(null);
  }


  public String prepareCategoricalMap(String descriptorName)
  {
    String dv = null;
    Set<CategoricalDescriptor> categoricalDescriptorSet = this.cropDisorderRecord.findCategoricalDescriptorSet();
    for (CategoricalDescriptor categoricalDescriptor : categoricalDescriptorSet)
    {
      CategoricalType categoricalType = (CategoricalType) categoricalDescriptor.getDescriptorType();
      if (descriptorName.equals(categoricalType.getTypeName()))
      {
        if (categoricalType.getMultivalue())
        {

        }
        else
        {
          for(CategoricalTypeValue ctv : categoricalDescriptor.getCategoricalTypeValueSet())
          {
            return(ctv.getValueType());
          }
        }
      }
    }
    return(dv);
  }


  public void prepare()
  {
    super.prepare();
    if (this.cropDisorderRecord != null)
    {
      this.altitude = this.prepareNumericMap("altitude");
      this.monthlyaveragetemperature = this.prepareNumericMap("monthlyaveragetemperature");
      this.monthlyaveragehumidity = this.prepareNumericMap("monthlyaveragehumidity");
      this.monthlyprecipitation = this.prepareNumericMap("monthlyprecipitation");
      this.cultivatedarea = this.prepareNumericMap("cultivatedarea");
      this.cropage = this.prepareNumericMap("cropage");
      this.relativeaffectedarea = this.prepareNumericMap("relativeaffectedarea");
      this.irrigationamount = this.prepareNumericMap("irrigationamount");
      this.irrigationfrequency = this.prepareNumericMap("irrigationfrequency");
      this.pH = this.prepareNumericMap("pH");
      this.pestdensity = this.prepareNumericMap("pestdensity");
      // New batch
      this.prepareCategoricalMap("symptom");
      this.prepareCategoricalMap("overallappearance");
      this.prepareCategoricalMap("leafdiscoloration");
      this.prepareCategoricalMap("leafappearance");
      this.prepareCategoricalMap("leafsymptom");
      this.prepareCategoricalMap("seedlingsymptom");
      this.prepareCategoricalMap("rootsymptom");
      this.prepareCategoricalMap("lesioncolour");
      this.prepareCategoricalMap("lesionshape");
      this.prepareCategoricalMap("lesionappearance");
      this.prepareCategoricalMap("odour");
      this.prepareCategoricalMap("lesionlocation");
      this.prepareCategoricalMap("steminternal");
      this.prepareCategoricalMap("drainage");

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


  // FIXME: type value lists should be supplied by type itself, findCategoricalTypeValueList is not an access method
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

  // New batch

  public List<CategoricalTypeValue> getOverallappearanceValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("overallappearance");
    return(categoricalTypeValueList);
  }

  public List<CategoricalTypeValue> getLeafdiscolorationValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("leafdiscoloration");
    return(categoricalTypeValueList);
  }

  public List<CategoricalTypeValue> getLeafappearanceValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("leafappearance");
    return(categoricalTypeValueList);
  }

  public List<CategoricalTypeValue> getLeafsymptomValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("leafsymptom");
    return(categoricalTypeValueList);
  }
 
  public List<CategoricalTypeValue> getSeedlingsymptomValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("seedlingsymptom");
    return(categoricalTypeValueList);
  }

  public List<CategoricalTypeValue> getRootsymptomValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("rootsymptom");
    return(categoricalTypeValueList);
  }

  public List<CategoricalTypeValue> getLesioncolourValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("lesioncolour");
    return(categoricalTypeValueList);
  }

  public List<CategoricalTypeValue> getLesionshapeValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("lesionshape");
    return(categoricalTypeValueList);
  }

  public List<CategoricalTypeValue> getLesionappearanceValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("lesionappearance");
    return(categoricalTypeValueList);
  }

  public List<CategoricalTypeValue> getOdourValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("odour");
    return(categoricalTypeValueList);
  }

  public List<CategoricalTypeValue> getLesionlocationValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("lesionlocation");
    return(categoricalTypeValueList);
  }

  public List<CategoricalTypeValue> getSteminternalValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("steminternal");
    return(categoricalTypeValueList);
  }

  public List<CategoricalTypeValue> getDrainageValueList()
  {
    List<CategoricalTypeValue> categoricalTypeValueList = this.access.findCategoricalTypeValueList("drainage");
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

  // New batch

  public String getOverallappearance()
  {
    return (this.overallappearance);
  }


  public void setOverallappearance(String overallappearance)
  {
    this.overallappearance = overallappearance;
  }


  public String getLeafdiscoloration()
  {
    return (this.leafdiscoloration);
  }


  public void setLeafdiscoloration(String leafdiscoloration)
  {
    this.leafdiscoloration = leafdiscoloration;
  }

  public String getLeafappearance()
  {
    return (this.leafappearance);
  }


  public void setLeafappearance(String leafappearance)
  {
    this.leafappearance = leafappearance;
  }

 public String getLeafsymptom()
  {
    return (this.leafsymptom);
  }


  public void setLeafsymptom(String leafsymptom)
  {
    this.leafsymptom = leafsymptom;
  }

  public String getSeedlingsymptom()
  {
    return (this.seedlingsymptom);
  }


  public void setSeedlingsymptom(String seedlingsymptom)
  {
    this.seedlingsymptom = seedlingsymptom;
  }


  public String getRootsymptom()
  {
    return (this.rootsymptom);
  }


  public void setRootsymptom(String rootsymptom)
  {
    this.rootsymptom = rootsymptom;
  }

  public String getLesioncolour()
  {
    return (this.lesioncolour);
  }


  public void setLesioncolour(String lesioncolour)
  {
    this.lesioncolour = lesioncolour;
  }

  public String getLesionshape()
  {
    return (this.lesionshape);
  }


  public void setLesionshape(String lesionshape)
  {
    this.lesionshape = lesionshape;
  } 
  
   public String getLesionappearance()
  {
    return (this.lesionappearance);
  }


  public void setLesionappearance(String lesionappearance)
  {
    this.lesionappearance = lesionappearance;
  }


  public String getOdour()
  {
    return (this.odour);
  }


  public void setOdour(String odour)
  {
    this.odour = odour;
  }

  public String getLesionlocation()
  {
    return (this.lesionlocation);
  }


  public void setLesionlocation(String lesionlocation)
  {
    this.lesionlocation = lesionlocation;
  }


  public String getSteminternal()
  {
    return (this.steminternal);
  }


  public void setSteminternal(String steminternal)
  {
    this.steminternal = steminternal;
  } 


  public String getDrainage()
  {
    return (this.drainage);
  }


  public void setDrainage(String drainage)
  {
    this.drainage = drainage;
  }

  //

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


  public void addNumericDescriptorToMap(Map<Integer, Double> numericDescriptorMap, Integer id, Double descriptorValue)
  {
    if (descriptorValue != null)
    {
       numericDescriptorMap.put(id, descriptorValue);
    }
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
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "overallappearance", this.overallappearance);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "leafdiscoloration", this.leafdiscoloration);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "leafappearance", this.leafappearance);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "leafsymptom", this.leafsymptom);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "seedlingsymptom", this.seedlingsymptom);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "rootsymptom", this.rootsymptom);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "lesioncolour", this.lesioncolour);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "lesionshape", this.lesionshape);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "lesionappearance", this.lesionappearance);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "odour", this.odour);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "lesionlocation", this.lesionlocation);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "steminternal", this.steminternal);
      this.addCategoricalDescriptorToMap(categoricalDescriptorMap, "drainage", this.drainage);
      this.cropDisorderRecordManager.updateCategoricalDescriptors(this.cropDisorderRecordId, categoricalDescriptorMap);

      Map<Integer, Double> numericDescriptorMap = new HashMap<Integer, Double>();
      this.addNumericDescriptorToMap(numericDescriptorMap, this.altitudeType.getId(), this.altitude);
      this.addNumericDescriptorToMap(numericDescriptorMap, this.monthlyaveragetemperatureType.getId(), this.monthlyaveragetemperature);
      this.addNumericDescriptorToMap(numericDescriptorMap, this.monthlyaveragehumidityType.getId(), this.monthlyaveragehumidity);
      this.addNumericDescriptorToMap(numericDescriptorMap, this.monthlyprecipitationType.getId(), this.monthlyprecipitation);
      this.addNumericDescriptorToMap(numericDescriptorMap, this.cultivatedareaType.getId(), this.cultivatedarea);
      this.addNumericDescriptorToMap(numericDescriptorMap, this.cropageType.getId(), this.cropage);
      this.addNumericDescriptorToMap(numericDescriptorMap, this.relativeaffectedareaType.getId(), this.relativeaffectedarea);
      this.addNumericDescriptorToMap(numericDescriptorMap, this.irrigationamountType.getId(), this.irrigationamount);
      this.addNumericDescriptorToMap(numericDescriptorMap, this.irrigationfrequencyType.getId(), this.irrigationfrequency);
      this.addNumericDescriptorToMap(numericDescriptorMap, this.pHType.getId(), this.pH);
      this.addNumericDescriptorToMap(numericDescriptorMap, this.pestdensityType.getId(), this.pestdensity);
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
