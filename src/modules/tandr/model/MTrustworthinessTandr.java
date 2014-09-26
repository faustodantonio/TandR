package modules.tandr.model;

import java.util.Date;
import java.util.Map;

import utility.UConfig;
import model.MFeatureVersion;
import model.MTrustworthiness;
import modules.tandr.foundation.FTrustworthinessTandr;

public class MTrustworthinessTandr extends MTrustworthiness{
	
	private MFDirectEffect direct;
	private MFIndirectEffect indirect;
	private MFTemporalEffect temporal;
	
	public MTrustworthinessTandr() {
		super();
		
		this.direct = new MFDirectEffect();
		this.indirect = new MFIndirectEffect();
		this.temporal = new MFTemporalEffect();
	}
	
	public MTrustworthinessTandr(MFeatureVersion featureVersion) {
		this.sdf = UConfig.sdf;
		
		this.setUri(this.generateTrustworthinessUri(featureVersion));
		featureVersion.setTrustworthiness(this);
		this.setFeatureVersion(featureVersion);
		
		FTrustworthinessTandr ftrustworthiness = new FTrustworthinessTandr();
		Map<String, MFEffect> effects = ftrustworthiness.retrieveTrustworthinessEffectList(this,UConfig.getTANDRGraphURI());
		
		// TODO: Retrieving Trustworthiness value: MISS
		
		if (effects.get("direct").equals("") || effects.get("direct") == null)
			direct = new MFDirectEffect();
		else direct = (MFDirectEffect) effects.get("direct");
		
		if (effects.get("indirect").equals("") || effects.get("indirect") == null)
			indirect = new MFIndirectEffect();
		else indirect = (MFIndirectEffect) effects.get("indirect");
		
		if (effects.get("temporal").equals("") || effects.get("temporal") == null)
			temporal = new MFTemporalEffect();
		else temporal = (MFTemporalEffect) effects.get("temporal");
		
		this.setComputedAt(featureVersion.getIsValidFromString());
	}

	public MFDirectEffect getDirectEffect() {		
		if (this.direct == null) direct = new MFDirectEffect();	
		return direct;
	}
	public void setDirectEffect(MFDirectEffect direct) {
		this.direct = direct;
	}
	public MFIndirectEffect getIndirectEffect() {
		if (this.indirect == null) indirect = new MFIndirectEffect();	
		return indirect;
	}
	public void setIndirectEffect(MFIndirectEffect indirect) {
		this.indirect = indirect;
	}
	public MFTemporalEffect getTemporalEffect() {
		if (this.temporal == null) temporal = new MFTemporalEffect();	
		return temporal;
	}
	public void setTemporalEffect(MFTemporalEffect temporal) {
		this.temporal = temporal;
	}

	public void setComputedAt(Date isValidFrom) {
		super.setComputedAt(isValidFrom);
		
		this.direct.setComputedAt(isValidFrom);
		this.indirect.setComputedAt(isValidFrom);
		this.temporal.setComputedAt(isValidFrom);
	}
    public void setComputedAt(String isValidFrom) {
    	super.setComputedAt(isValidFrom);
    	
		this.direct.setComputedAt(isValidFrom);
		this.indirect.setComputedAt(isValidFrom);
		this.temporal.setComputedAt(isValidFrom);
    }
	
}

//	public MTrustworthinessTandr(MTrustworthiness trust) {
//		FTrustworthinessTandr ftrustworthiness = new FTrustworthinessTandr();
//		Map<String, MFEffect> effects = ftrustworthiness.retrieveTrustworthinessEffectList(trust);
//		
////		MFeatureVersion featureVersion;
////		// manage if feature version has not yet been loaded 
////		if (trust.getFeatureVersion() == null)
////			featureVersion = (MFeatureVersion) ffacade.retrieveByUri(trust.getFeatureVersionUri(), 1, MFeatureVersion.class);
////		else featureVersion = trust.getFeatureVersion();
////		
////		super.setFeatureVersionUri(trust.getFeatureVersionUri());
////		super.setFeatureVersion(featureVersion);
//		
//		super.setUri(trust.getUri());
//		super.setComputedAt(trust.getComputedAt());
//		super.setValue(trust.getValue());
//		
//		if (effects.get("direct").equals("") || effects.get("direct") == null)
//				direct = (MFDirectEffect) effects.get("direct");
//		else direct = new MFDirectEffect();
//		
//		if (effects.get("indirect").equals("") || effects.get("indirect") == null)
//			indirect = (MFIndirectEffect) effects.get("indirect");
//		else indirect = new MFIndirectEffect();
//		
//		if (effects.get("temporal").equals("") || effects.get("temporal") == null)
//			temporal = (MFTemporalEffect) effects.get("temporal");
//		else temporal = new MFTemporalEffect();	
//	}
//	
//	public MTrustworthinessTandr(MTrustworthiness trust,String effectGraphUri) {
//		FTrustworthinessTandr ftrustworthiness = new FTrustworthinessTandr();
//		Map<String, MFEffect> effects = ftrustworthiness.retrieveTrustworthinessEffectList(trust,effectGraphUri);
//
////		Manage feature Version - trustworthiness relation		
////		MFeatureVersion featureVersion;
//
//		// manage if feature version has not yet been loaded 
////		if (trust.getFeatureVersion() == null)
////			featureVersion = (MFeatureVersion) ffacade.retrieveByUri(trust.getFeatureVersionUri(), 1, MFeatureVersion.class);
////		else featureVersion = trust.getFeatureVersion();
////		
////		super.setFeatureVersion(featureVersion);
////		super.setFeatureVersionUri(trust.getFeatureVersionUri());
//		
//		super.setUri(trust.getUri());
//		super.setComputedAt(trust.getComputedAt());
//		super.setValue(trust.getValue());
//		
//		if (effects.get("direct").equals("") || effects.get("direct") == null)
//				direct = (MFDirectEffect) effects.get("direct");
//		else direct = new MFDirectEffect();
//		
//		if (effects.get("indirect").equals("") || effects.get("indirect") == null)
//			indirect = (MFIndirectEffect) effects.get("indirect");
//		else indirect = new MFIndirectEffect();
//		
//		if (effects.get("temporal").equals("") || effects.get("temporal") == null)
//			temporal = (MFTemporalEffect) effects.get("temporal");
//		else temporal = new MFTemporalEffect();		
//	}