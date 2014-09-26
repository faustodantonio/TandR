package modules.tandr.model;

import java.util.Date;
import java.util.Map;

import utility.UConfig;
import utility.UDebug;
import foundation.FFoundationFacade;
import model.MAuthor;
import model.MReputation;
import modules.tandr.foundation.FReputationTandr;

public class MReputationTandr extends MReputation {
	
	private MFDirectEffect direct;
	private MFIndirectEffect indirect;
	private MFTemporalEffect temporal;
	
	public MReputationTandr() {
		super();
		
		direct = new MFDirectEffect();
		indirect = new MFIndirectEffect();
		temporal = new MFTemporalEffect();
	}
	
	public MReputationTandr(MAuthor author) {
		this.sdf = UConfig.sdf;
		this.foundation = new FFoundationFacade();
		
		this.setUri(this.generateReputationUri(author));
		author.setReputation(this);
		this.setAuthor(author);
		
		FReputationTandr freputation = new FReputationTandr();
		Map<String, MFEffect> effects = freputation.retrieveReputationEffectList(this, UConfig.getTANDRGraphURI());
		
		// TODO: Retrieving Reputation value: MISS
		
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

//	public MReputationTandr(MReputation reputation) {
//		FReputationTandr freputation = new FReputationTandr();
//		Map<String, MFEffect> effects = freputation.retrieveReputationEffectList(reputation);
//		MAuthor author;
//
//		// manage if author has not yet been loaded 
//		if (reputation.getAuthor() == null)
//			author = (MAuthor) ffacade.retrieveByUri(reputation.getAuthorUri(), 1, MAuthor.class);
//		else author = reputation.getAuthor();
//		
//		super.setUri(reputation.getUri());
//		super.setComputedAt( reputation.getComputedAt() );
//		super.setAuthorUri(reputation.getAuthorUri());
//		super.setAuthor(author);
//		super.setValue(reputation.getValue());
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
//	public MReputationTandr(MReputation reputation,String effectGraphUri) {
//		FReputationTandr freputation = new FReputationTandr();
//		Map<String, MFEffect> effects = freputation.retrieveReputationEffectList(reputation);
//		MAuthor author;
//
//		// manage if author has not yet been loaded 
//		if (reputation.getAuthor() == null)
//			author = (MAuthor) ffacade.retrieveByUri(reputation.getAuthorUri(), 1, MAuthor.class);
//		else author = reputation.getAuthor();
//			
//		super.setUri(reputation.getUri());
//		super.setComputedAt( reputation.getComputedAt() );
//		super.setAuthorUri(reputation.getAuthorUri());
//		super.setAuthor(author);
//		super.setValue(reputation.getValue());
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