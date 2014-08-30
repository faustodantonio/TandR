package modules.tandr.model;

import java.util.Map;

import foundation.FFoundationFacade;
import model.MFeatureVersion;
import model.MTrustworthiness;
import modules.tandr.foundation.FTrustworthinessTandr;

public class MTrustworthinessTandr extends MTrustworthiness{

	private FFoundationFacade ffacade;
	
	private MFDirectEffect direct;
	private MFIndirectEffect indirect;
	private MFTemporalEffect temporal;
	
	public MTrustworthinessTandr() {
		ffacade = new FFoundationFacade();
		direct = new MFDirectEffect();
		indirect = new MFIndirectEffect();
		temporal = new MFTemporalEffect();
	}
	
	public MTrustworthinessTandr(MTrustworthiness trust) {
		FTrustworthinessTandr ftrustworthiness = new FTrustworthinessTandr();
		Map<String, MFEffect> effects = ftrustworthiness.retrieveTrustworthinessEffectList(trust);
		MFeatureVersion featureVersion;

		// manage if feature version has not yet been loaded 
		if (trust.getFeatureVersion() == null)
			featureVersion = (MFeatureVersion) ffacade.retrieveByUri(trust.getFeatureVersionUri(), 1, MFeatureVersion.class);
		else featureVersion = trust.getFeatureVersion();
		
		super.setUri(trust.getUri());
		super.setComputedAt(trust.getComputedAt());
		super.setFeatureVersionUri(trust.getFeatureVersionUri());
		super.setFeatureVersion(featureVersion);
		super.setValue(trust.getValue());
		
		if (effects.get("direct").equals("") || effects.get("direct") == null)
				direct = (MFDirectEffect) effects.get("direct");
		else direct = new MFDirectEffect();
		
		if (effects.get("indirect").equals("") || effects.get("indirect") == null)
			indirect = (MFIndirectEffect) effects.get("indirect");
		else indirect = new MFIndirectEffect();
		
		if (effects.get("temporal").equals("") || effects.get("temporal") == null)
			temporal = (MFTemporalEffect) effects.get("temporal");
		else temporal = new MFTemporalEffect();	
	}
	
	public MTrustworthinessTandr(MTrustworthiness trust,String effectGraphUri) {
		FTrustworthinessTandr ftrustworthiness = new FTrustworthinessTandr();
		Map<String, MFEffect> effects = ftrustworthiness.retrieveTrustworthinessEffectList(trust,effectGraphUri);
		MFeatureVersion featureVersion;

		// manage if feature version has not yet been loaded 
		if (trust.getFeatureVersion() == null)
			featureVersion = (MFeatureVersion) ffacade.retrieveByUri(trust.getFeatureVersionUri(), 1, MFeatureVersion.class);
		else featureVersion = trust.getFeatureVersion();
			
		super.setUri(trust.getUri());
		super.setComputedAt(trust.getComputedAt());
		super.setFeatureVersionUri(trust.getFeatureVersionUri());
		super.setFeatureVersion(featureVersion);
		super.setValue(trust.getValue());
		
		if (effects.get("direct").equals("") || effects.get("direct") == null)
				direct = (MFDirectEffect) effects.get("direct");
		else direct = new MFDirectEffect();
		
		if (effects.get("indirect").equals("") || effects.get("indirect") == null)
			indirect = (MFIndirectEffect) effects.get("indirect");
		else indirect = new MFIndirectEffect();
		
		if (effects.get("temporal").equals("") || effects.get("temporal") == null)
			temporal = (MFTemporalEffect) effects.get("temporal");
		else temporal = new MFTemporalEffect();		
	}
	
	public MFDirectEffect getDirectEffect() {
		return direct;
	}
	public void setDirectEffect(MFDirectEffect direct) {
		this.direct = direct;
	}
	public MFIndirectEffect getIndirectEffect() {
		return indirect;
	}
	public void setIndirectEffect(MFIndirectEffect indirect) {
		this.indirect = indirect;
	}
	public MFTemporalEffect getTemporalEffect() {
		return temporal;
	}
	public void setTemporalEffect(MFTemporalEffect temporal) {
		this.temporal = temporal;
	}
	
}
