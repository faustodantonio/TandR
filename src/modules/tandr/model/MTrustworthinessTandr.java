package modules.tandr.model;

import java.util.Date;

import utility.UConfig;
import model.MTrustworthiness;

public class MTrustworthinessTandr extends MTrustworthiness{
	
	private MFDirectEffect direct;
	private MFIndirectEffect indirect;
	private MFTemporalEffect temporal;
	
	public MTrustworthinessTandr() {
		super();
		
		this.direct = new MFDirectEffect();
		this.indirect = new MFIndirectEffect();
		this.temporal = new MFTemporalEffect();
		
		this.value = 0.0;
		this.setComputedAt( UConfig.getMinDateTime() );
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
