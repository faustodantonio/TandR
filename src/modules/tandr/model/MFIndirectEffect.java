package modules.tandr.model;

import java.util.ArrayList;
import java.util.Date;

import utility.UConfig;
import utility.UDebug;
import model.MAuthor;
import model.MFeatureVersion;

public class MFIndirectEffect extends MFEffect{
	
	private MFIndirectGeomAspect geometricAspect;
	private MFIndirectQualAspect qualitativeAspect;
	private MFIndirectSemAspect semanticAspect;
	
	private static double indGeomWeight = 0.3333333;
	private static double indQualWeight = 0.3333333;
	private static double indSemWeight  = 0.3333333;
	
	public MFIndirectEffect() {
		
		this.geometricAspect   = new MFIndirectGeomAspect();
		this.qualitativeAspect = new MFIndirectQualAspect();
		this.semanticAspect    = new MFIndirectSemAspect();
		
		this.setComputedAt(UConfig.getMinDateTime());
	}
	
	public MFIndirectEffect(Double value) {
		super(value);
		
		this.geometricAspect   = new MFIndirectGeomAspect();
		this.qualitativeAspect = new MFIndirectQualAspect();
		this.semanticAspect    = new MFIndirectSemAspect();
		
		this.setComputedAt(UConfig.getMinDateTime());
	}
	
	public double calculateTrustworthiness(MFeatureVersion featureVersion) {
		return this.calculateTrustworthiness(featureVersion, null);
	}
	
	public double calculateTrustworthiness(MFeatureVersion featureVersion, Date untilDate) {
		
		super.value = 0.0;
		
		MReputationTandr authorReputation = new MReputationTandr( featureVersion.getAuthor() );
		
		// TODO: update temporal aspect
		super.value = super.value + (indGeomWeight * this.geometricAspect.calculateTrustworthiness(authorReputation));
		super.value = super.value + (indQualWeight * this.qualitativeAspect.calculateTrustworthiness(authorReputation));
		super.value = super.value + (indSemWeight  * this.semanticAspect.calculateTrustworthiness(authorReputation));	
		
		return super.value;
	}
	
	public double confirmTrustworthiness(MFeatureVersion featureVersion, String untilDate) {
		
		super.value = 0.0;
		
		ArrayList<MAuthor> confirmers = foundation.retriveAuthorConfirmers(featureVersion, untilDate, UConfig.getVGIHGraphURI(), 0);
		
		super.value = super.value + (indGeomWeight * this.geometricAspect.confirmTrustworthiness(featureVersion,confirmers));
		super.value = super.value + (indQualWeight * this.qualitativeAspect.confirmTrustworthiness(featureVersion,confirmers));
		super.value = super.value + (indSemWeight  * this.semanticAspect.confirmTrustworthiness(featureVersion,confirmers));
		
		return super.value;
	}
	
	public double calculateReputation(MAuthor author, String untilDate) {
		
		super.value = 0.0;
		
		super.value = super.value + (indGeomWeight * this.geometricAspect.calculateReputation(author,untilDate));
		super.value = super.value + (indQualWeight * this.qualitativeAspect.calculateReputation(author,untilDate));
		super.value = super.value + (indSemWeight  * this.semanticAspect.calculateReputation(author,untilDate));		

		return super.value;
	}
	
	public MFAspect getGeometricAspect() {
		return geometricAspect;
	}
	public void setGeometricAspect(MFIndirectGeomAspect geometricAspect) {
		this.geometricAspect = geometricAspect;
	}
	public MFAspect getQualitativeAspect() {
		return qualitativeAspect;
	}
	public void setQualitativeAspect(MFIndirectQualAspect qualitativeAspect) {
		this.qualitativeAspect = qualitativeAspect;
	}
	public MFAspect getSemanticAspect() {
		return semanticAspect;
	}
	public void setSemanticAspect(MFIndirectSemAspect semanticAspect) {
		this.semanticAspect = semanticAspect;
	}
	
	public double getIndValue() {
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

    public void debugTIndirectInfo(int dbgLevel) {
		
		UDebug.print("\t Indirect Trust : " + super.getValueString(), dbgLevel);
		UDebug.print("(geom->" + this.geometricAspect  .getValueString() + "; ", dbgLevel+1);
		UDebug.print("qual->"  + this.qualitativeAspect.getValueString() + "; ", dbgLevel+1);
		UDebug.print("sem->"   + this.semanticAspect   .getValueString() + ")", dbgLevel+1);
		
    }
    
    public void debugRIndirectInfo(int dbgLevel) {
		
		UDebug.print("\t Indirect Rep   : " + super.getValueString(), dbgLevel);
		UDebug.print("(geom->" + this.geometricAspect  .getValueString() + "; ", dbgLevel+1);
		UDebug.print("qual->"  + this.qualitativeAspect.getValueString() + "; ", dbgLevel+1);
		UDebug.print("sem->"   + this.semanticAspect   .getValueString() + ")", dbgLevel+1);
		
    }
    
	@Override
	public String getEffectName() {
		return "Indirect Effect";
	}

	public double validateTrustworthiness(MFeatureVersion fv1,	MFeatureVersion fv2) {
		
		super.value = super.value + (indGeomWeight * this.geometricAspect.validaterustworthiness(fv1, fv2));
		super.value = super.value + (indQualWeight * this.qualitativeAspect.validaterustworthiness(fv1, fv2));
		super.value = super.value + (indSemWeight  * this.semanticAspect.validaterustworthiness(fv1, fv2));
		
		return super.value;
	}
	
}
