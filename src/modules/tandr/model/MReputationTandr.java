package modules.tandr.model;

import java.util.Map;

import foundation.FFoundationFacade;
import model.MAuthor;
import model.MFeatureVersion;
import model.MReputation;
import model.MTrustworthiness;
import modules.tandr.foundation.FTrustworthinessTandr;

public class MReputationTandr extends MReputation {

private FFoundationFacade ffacade;
	
	private MFDirectEffect direct;
	private MFIndirectEffect indirect;
	private MFTemporalEffect temporal;
	
	public MReputationTandr() {
		ffacade = new FFoundationFacade();
		direct = new MFDirectEffect();
		indirect = new MFIndirectEffect();
		temporal = new MFTemporalEffect();
	}
	
	public MReputationTandr(MReputation reputation) {
		FReputationTandr freputation = new FReputationTandr();
		Map<String, MFEffect> effects = freputation.retrieveReputationEffectList(reputation);
		MAuthor author;

		// manage if author has not yet been loaded 
		if (reputation.getAuthor() == null)
			author = (MAuthor) ffacade.retrieveByUri(reputation.getAuthorUri(), 1, MAuthor.class);
		else author = reputation.getAuthor();
		
		super.setUri(reputation.getUri());
		super.setComputedAt( reputation.getComputedAt() );
		super.setAuthorUri(reputation.getAuthorUri());
		super.setAuthor(reputation.getAuthor());
		super.setValue(reputation.getValue());
		
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
	
	public MReputationTandr(MReputation reputation,String effectGraphUri) {
		FReputationTandr freputation = new FReputationTandr();
		Map<String, MFEffect> effects = freputation.retrieveReputationEffectList(reputation);
		MAuthor author;

		// manage if author has not yet been loaded 
		if (reputation.getAuthor() == null)
			author = (MAuthor) ffacade.retrieveByUri(reputation.getAuthorUri(), 1, MAuthor.class);
		else author = reputation.getAuthor();
			
		super.setUri(reputation.getUri());
		super.setComputedAt( reputation.getComputedAt() );
		super.setAuthorUri(reputation.getAuthorUri());
		super.setAuthor(reputation.getAuthor());
		super.setValue(reputation.getValue());
		
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
