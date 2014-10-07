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
		
		// Retrieving trustworthiness effects
		if (effects.get("direct").equals("") || effects.get("direct") == null)
			direct = new MFDirectEffect();
		else direct = (MFDirectEffect) effects.get("direct");
		
		if (effects.get("indirect").equals("") || effects.get("indirect") == null)
			indirect = new MFIndirectEffect();
		else indirect = (MFIndirectEffect) effects.get("indirect");
		
		if (effects.get("temporal").equals("") || effects.get("temporal") == null)
			temporal = new MFTemporalEffect();
		else temporal = (MFTemporalEffect) effects.get("temporal");
		
		// Retrieving trustworthiness attributes
		MTrustworthiness trust = ftrustworthiness.retrieveByURI(this.getUri(), UConfig.getTANDRGraphURI(), 0); 
		if (trust != null && trust.getUri() != null) {
			this.value = trust.getValue();
			this.setComputedAt( trust.getComputedAt() );
		}
		else {
			this.value = 0.0;
			this.setComputedAt(featureVersion.getIsValidFromString());
		}
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
