package modules.tandr.model;

import java.util.ArrayList;
import java.util.Date;

import utility.UConfig;
import utility.UDebug;
import model.MAuthor;
import model.MFeatureVersion;

public class MFDirectEffect extends MFEffect{

	private MFDirectGeomAspect geometricAspect;
	private MFDirectQualAspect qualitativeAspect;
	private MFDirectSemAspect semanticAspect;
	
	private static double dirGeomWeight = 0.3333333;
	private static double dirQualWeight = 0.3333333;
	private static double dirSemWeight  = 0.3333333;
	
	public MFDirectEffect() {
		
		this.geometricAspect   = new MFDirectGeomAspect();
		this.qualitativeAspect = new MFDirectQualAspect();
		this.semanticAspect    = new MFDirectSemAspect();
		
	}
	
	public MFDirectEffect(Double value) {
		super(value);
		
		this.geometricAspect   = new MFDirectGeomAspect();
		this.qualitativeAspect = new MFDirectQualAspect();
		this.semanticAspect    = new MFDirectSemAspect();
		
		this.setComputedAt(UConfig.getMinDateTime());
	}
	
	public double calculateTrustworthiness(ArrayList<MFeatureVersion> versions, MFeatureVersion featureVersion) {
		
		super.value = 0.0;
		
		this.geometricAspect.calculateWeightedAvgs(versions);
		super.value = super.value + (dirGeomWeight * this.geometricAspect.calculateTrustworthiness(featureVersion));		
		super.value = super.value + (dirQualWeight * this.qualitativeAspect.calculateTrustworthiness(versions, featureVersion));
		super.value = super.value + (dirSemWeight  * this.semanticAspect.calculateTrustworthiness(versions, featureVersion));
		
		return super.value;
	}
	
	public double calculateReputation(MAuthor author, String untilDate) {
		
		super.value = 0.0;
		
//		super.value = super.value + (dirGeomWeight * this.geometricAspect.calculateReputation(author,untilDate));
//		super.value = super.value + (dirQualWeight * this.qualitativeAspect.calculateReputation(author,untilDate));
//		super.value = super.value + (dirSemWeight  * this.semanticAspect.calculateReputation(author,untilDate));
		
		return super.value;
	}
	
	public MFDirectGeomAspect getGeometricAspect() {
		return geometricAspect;
	}
	public void setGeometricAspect(MFDirectGeomAspect geometricAspect) {
		this.geometricAspect = geometricAspect;
	}
	public MFDirectQualAspect getQualitativeAspect() {
		return qualitativeAspect;
	}
	public void setQualitativeAspect(MFDirectQualAspect qualitativeAspect) {
		this.qualitativeAspect = qualitativeAspect;
	}
	public MFDirectSemAspect getSemanticAspect() {
		return semanticAspect;
	}
	public void setSemanticAspect(MFDirectSemAspect semanticAspect) {
		this.semanticAspect = semanticAspect;
	}
	
	public double getDirValue() {
		return super.value;
	}
	
	public void setComputedAt(Date isValidFrom) {
		super.setComputedAt(isValidFrom);
		
		this.geometricAspect.setComputedAt(isValidFrom);
		this.qualitativeAspect.setComputedAt(isValidFrom);
		this.semanticAspect.setComputedAt(isValidFrom);
	}
    public void setComputedAt(String isValidFrom) {
    	super.setComputedAt(isValidFrom);
    	
		this.geometricAspect.setComputedAt(isValidFrom);
		this.qualitativeAspect.setComputedAt(isValidFrom);
		this.semanticAspect.setComputedAt(isValidFrom);
    }

    public void debugTDirectInfo(int dbgLevel) {
		
		UDebug.print("Direct Trust : "+ super.getValueString(), dbgLevel);
		UDebug.print("(geom->" + this.geometricAspect  .getValueString() + "; ", dbgLevel+1);
		UDebug.print("qual->"  + this.qualitativeAspect.getValueString() + "; ", dbgLevel+1);
		UDebug.print("sem->"   + this.semanticAspect   .getValueString() + ")", dbgLevel+1);

    }
    
    public void debugRDirectInfo(int dbgLevel) {

		UDebug.print("Direct Rep   : "+ super.getValueString(), dbgLevel);
		UDebug.print("(geom->" + this.geometricAspect  .getValueString() + "; ", dbgLevel+1);
		UDebug.print("qual->"  + this.qualitativeAspect.getValueString() + "; ", dbgLevel+1);
		UDebug.print("sem->"   + this.semanticAspect   .getValueString() + ")", dbgLevel+1);
    }

	@Override
	public String getEffectName() {
		return "Direct Effect";
	}

	public double validateTrustworthiness(MFeatureVersion fv1,	MFeatureVersion fv2) {
		super.value = 0.0;
		
		double dirGeomValWeight = 0.5;
		double dirQualValWeight = 0.5;
		
//		this.geometricAspect.calculateWeightedAvgs(versions);
		super.value = super.value + (dirGeomValWeight * this.geometricAspect.validateTrustworthiness(fv1, fv2));		
		super.value = super.value + (dirQualValWeight * this.qualitativeAspect.validateTrustworthiness(fv1, fv2));
//		super.value = super.value + (dirSemWeight  * this.semanticAspect.validateTrustworthiness(fv1, fv2));
		
		return super.value;
	}
    
}
