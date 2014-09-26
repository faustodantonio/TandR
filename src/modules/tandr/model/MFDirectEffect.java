package modules.tandr.model;

import java.util.ArrayList;
import java.util.Date;

import utility.UConfig;
import utility.UDebug;
import model.MAuthor;
import model.MFeature;
import model.MFeatureVersion;
import model.MTrustworthiness;

public class MFDirectEffect extends MFEffect{

	private MFDirectGeomAspect geometricAspect;
	private MFDirectQualAspect qualitativeAspect;
	private MFDirectSemAspect semanticAspect;
	
	private static double dirGeomWeight = 0.33;
	private static double dirQualWeight = 0.33;
	private static double dirSemWeight  = 0.33;
	
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
	
	@Override
	public double calculateTrustworthiness(MFeatureVersion featureVersion) {
		
		int dbgLevel = 1;
		super.value = 0.0;
		
		// get all feature versions
		MFeature feature = featureVersion.getFeature();
		ArrayList<MFeatureVersion> versions = feature.getPreviousVersions(featureVersion.getVersionNo(), 0);
		
		super.value = super.value + (dirGeomWeight * this.geometricAspect.calculateTrustworthiness(versions, featureVersion));		
		super.value = super.value + (dirQualWeight * this.qualitativeAspect.calculateTrustworthiness(versions, featureVersion));
		super.value = super.value + (dirSemWeight  * this.semanticAspect.calculateTrustworthiness(versions, featureVersion));
		
		UDebug.print("\t\t Direct Trust : "+ super.value, dbgLevel);
		UDebug.print("(geom->" + this.geometricAspect  .getValue() + "; ", dbgLevel+1);
		UDebug.print("qual->"  + this.qualitativeAspect.getValue() + "; ", dbgLevel+1);
		UDebug.print("sem->"   + this.semanticAspect   .getValue() + ")", dbgLevel+1);
		
		for ( MFeatureVersion fv : versions) {			
			MTrustworthiness trust;
			if ( fv.getTrustworthiness() == null && (fv.getTrustworthinessUri() == null || fv.getTrustworthinessUri().equals("")) ) {
				trust = new MTrustworthinessTandr( fv );
			}else {
				trust = fv.getTrustworthiness();
			}
			
			trust.setComputedAt( featureVersion.getIsValidFromString() );
			foundation.create(trust, UConfig.getTANDRGraphURI());
			UDebug.log("\nCREATE Trustworthiness (upd): " + trust.getUri() ,4);
		}
		
		return super.value;
	}
	
	@Override
	public double calculateReputation(MAuthor author, String untilDate) {
		
		super.value = 0.0;
		
		super.value = super.value + (dirGeomWeight * this.geometricAspect.calculateReputation(author,untilDate));
		super.value = super.value + (dirQualWeight * this.qualitativeAspect.calculateReputation(author,untilDate));
		super.value = super.value + (dirSemWeight  * this.semanticAspect.calculateReputation(author,untilDate));
		
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

}
