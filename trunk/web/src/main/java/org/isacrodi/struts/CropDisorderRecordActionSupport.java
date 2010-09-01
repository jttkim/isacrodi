package org.isacrodi.struts;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.isacrodi.ejb.entity.*;

import org.isacrodi.ejb.session.CropDisorderRecordManager;

import static org.javamisc.Util.genericTypecast;


public abstract class CropDisorderRecordActionSupport extends IsacrodiActionSupport implements ModelDriven<CropDisorderRecord>, Preparable
{
  protected Integer cropDisorderRecordId;
  protected CropDisorderRecord cropDisorderRecord;
  protected CropDisorderRecordManager cropDisorderRecordManager;


  public CropDisorderRecordActionSupport() throws NamingException
  {
    super();
    InitialContext context = new InitialContext();
    this.cropDisorderRecordManager = (CropDisorderRecordManager) context.lookup("isacrodi/CropDisorderRecordManagerBean/remote");
  }


  public Integer getCropDisorderRecordId()
  {
    return (this.cropDisorderRecordId);
  }


  public void setCropDisorderRecordId(Integer cropDisorderRecordId)
  {
    this.cropDisorderRecordId = cropDisorderRecordId;
    this.LOG.info(String.format("CDR action support: cdr Id set to %d", this.cropDisorderRecordId));
  }


  public List<NumericDescriptor> getNumericDescriptorList()
  {
    // this.LOG.info("CropDisorderRecordActionSupport.getNumericDescriptorList: start");
    if (this.cropDisorderRecord == null)
    {
      return (null);
    }
    else
    {
      ArrayList<NumericDescriptor> numericDescriptorList = new ArrayList<NumericDescriptor>(this.cropDisorderRecord.findNumericDescriptorSet());
      Comparator<NumericDescriptor> comparator = new Comparator<NumericDescriptor>()
      {
	public int compare(NumericDescriptor n1, NumericDescriptor n2)
	{
	  String t1Name = n1.getDescriptorType().getTypeName();
	  String t2Name = n2.getDescriptorType().getTypeName();
	  return (t1Name.compareTo(t2Name));
	}
      };
      Collections.sort(numericDescriptorList, comparator);
      return (numericDescriptorList);
    }
  }


  public List<CategoricalDescriptor> getCategoricalDescriptorList()
  {
    if (this.cropDisorderRecord == null)
    {
      return (null);
    }
    else
    {
      ArrayList<CategoricalDescriptor> categoricalDescriptorList = new ArrayList<CategoricalDescriptor>(this.cropDisorderRecord.findCategoricalDescriptorSet());
      Comparator<CategoricalDescriptor> comparator = new Comparator<CategoricalDescriptor>()
      {
	public int compare(CategoricalDescriptor c1, CategoricalDescriptor c2)
	{
	  String t1Name = c1.getDescriptorType().getTypeName();
	  String t2Name = c2.getDescriptorType().getTypeName();
	  return (t1Name.compareTo(t2Name));
	}
      };
      Collections.sort(categoricalDescriptorList, comparator);
      return (categoricalDescriptorList);
    }
  }


  public List<ImageDescriptor> getImageDescriptorList()
  {
    if (this.cropDisorderRecord == null)
    {
      return (null);
    }
    else
    {
      ArrayList<ImageDescriptor> imageDescriptorList = new ArrayList<ImageDescriptor>(this.cropDisorderRecord.findImageDescriptorSet());
      Comparator<ImageDescriptor> comparator = new Comparator<ImageDescriptor>()
      {
	public int compare(ImageDescriptor i1, ImageDescriptor i2)
	{
	  String t1Name = i1.getDescriptorType().getTypeName();
	  String t2Name = i2.getDescriptorType().getTypeName();
	  return (t1Name.compareTo(t2Name));
	}
      };
      Collections.sort(imageDescriptorList, comparator);
      return (imageDescriptorList);
    }
  }


  public List<ProcedureScore> getProcedureScoreList()
  {
    if (this.cropDisorderRecord == null)
    {
      return (null);
    }
    Recommendation recommendation = this.cropDisorderRecord.getRecommendation();
    if (recommendation == null)
    {
      return (null);
    }
    List<ProcedureScore> procedureScoreList = new ArrayList<ProcedureScore>(recommendation.getProcedureScoreSet());
    Comparator<ProcedureScore> comparator = new Comparator<ProcedureScore>()
    {
      public int compare(ProcedureScore ps1, ProcedureScore ps2)
      {
	if (ps1.getScore() > ps2.getScore())
	{
	  return (-1);
	}
	else if (ps1.getScore() < ps2.getScore())
	{
	  return (1);
	}
	else
	{
	  return (0);
	}
      }
    };
    Collections.sort(procedureScoreList, comparator);
    return (procedureScoreList);
  }


  public List<DisorderScore> getDisorderScoreList()
  {
    this.LOG.info("CropDisorderRecordActionSupport.getDisorderScoreList: start");
    if (this.cropDisorderRecord == null)
    {
      this.LOG.info("CropDisorderRecordActionSupport.getDisorderScoreList: no cdr");
      return (null);
    }
    Diagnosis diagnosis = this.cropDisorderRecord.getDiagnosis();
    if (diagnosis == null)
    {
      this.LOG.info("CropDisorderRecordActionSupport.getDisorderScoreList: no diagnosis");
      return (null);
    }
    List<DisorderScore> disorderScoreList = new ArrayList<DisorderScore>(diagnosis.getDisorderScoreSet());
    Comparator<DisorderScore> comparator = new Comparator<DisorderScore>()
    {
      public int compare(DisorderScore ds1, DisorderScore ds2)
      {
	if (ds1.getScore() > ds2.getScore())
	{
	  return (-1);
	}
	else if (ds1.getScore() < ds2.getScore())
	{
	  return (1);
	}
	else
	{
	  return (0);
	}
      }
    };
    Collections.sort(disorderScoreList, comparator);
    this.LOG.info(String.format("CropDisorderRecordActionSupport.getDisorderScoreList: returning sorted list of %d scores", disorderScoreList.size()));
    return (disorderScoreList);
  }

  public void prepare()
  {
    if (this.cropDisorderRecordId == null)
    {
      this.LOG.info("no cdr id set");
      this.cropDisorderRecord = new CropDisorderRecord();
    }
    else
    {
      this.cropDisorderRecord = this.cropDisorderRecordManager.findCropDisorderRecord(this.cropDisorderRecordId);
      this.LOG.info(String.format("got cdr %d", this.cropDisorderRecord.getId().intValue()));
    }
  }


  public CropDisorderRecord getModel()
  {
    return (this.cropDisorderRecord);
  }


  public abstract String execute();


  public void validate()
  {
    if (this.cropDisorderRecord.getId() == null)
    {
      this.addActionError("crop disorder record ID missing");
    }
    else
    {
      // FIXME: should validate that CDR to be updated exists etc.
    }
  }
}
