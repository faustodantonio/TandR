package modules.tandr.model;

import java.util.Date;
import java.util.Map;

import utility.UConfig;
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
		
		this.setUri(this.generateReputationUri(author));
		author.setReputation(this);
		this.setAuthor(author);
		
		FReputationTandr freputation = new FReputationTandr();
		Map<String, MFEffect> effects = freputation.retrieveReputationEffectList(this, UConfig.getTANDRGraphURI());
		
		if (effects.get("direct").equals("") || effects.get("direct") == null)
		direct = new MFDirectEffect();
		else direct = (MFDirectEffect) effects.get("direct");
		
		if (effects.get("indirect").equals("") || effects.get("indirect") == null)
		indirect = new MFIndirectEffect();
		else indirect = (MFIndirectEffect) effects.get("indirect");
		
		if (effects.get("temporal").equals("") || effects.get("temporal") == null)
		temporal = new MFTemporalEffect();
		else	temporal = (MFTemporalEffect) effects.get("temporal");
		
		// Retrieving reputation attributes
		MReputation rep = freputation.retrieveByURI(this.getUri(), UConfig.getTANDRGraphURI(), 1); 
		if (rep != null && rep.getUri() != null) {
			this.value = rep.getValue();
			this.setComputedAt( rep.getComputedAt() );
		}
		else {
			this.value = 0.0;
			this.setComputedAt( UConfig.getMinDateTime() );
		}
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