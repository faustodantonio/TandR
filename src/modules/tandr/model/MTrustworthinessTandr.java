package modules.tandr.model;

import java.util.Map;

import model.MTrustworthiness;
import modules.tandr.foundation.FEffect;

public class MTrustworthinessTandr extends MTrustworthiness{

//	private ArrayList<MFEffect> effects;
//	
//	public MTrustworthinessTandr() {	}
//
//	public ArrayList<MFEffect> getEffects() {
//		return effects;
//	}
//	public void setEffects(ArrayList<MFEffect> effects) {
//		this.effects = effects;
//	}
//	public void addEffect(MFEffect effect){
//		this.effects.add(effect);
//	}

	private MFDirectEffect direct;
	private MFIndirectEffect indirect;
	private MFTemporalEffect temporal;
	
	public MTrustworthinessTandr() {
		direct = new MFDirectEffect(0.0);
		indirect = new MFIndirectEffect(0.0);
		temporal = new MFTemporalEffect(0.0);
	}
	
	public MTrustworthinessTandr(MTrustworthiness trust) {
		
		FEffect feffect = new FEffect();
		Map<String, MFEffect> effects = feffect.retrieveTrustworthinessEffectList(trust);
		
		super.setUri(trust.getUri());
		super.setComputedAt(trust.getComputedAt());
		super.setFeatureVersionUri(trust.getFeatureVersionUri());
		super.setValue(trust.getValue());
		
		if (effects.get("direct").equals("") || effects.get("direct") == null)
				direct = (MFDirectEffect) effects.get("direct");
		else direct = new MFDirectEffect(0.0);
		
		if (effects.get("indirect").equals("") || effects.get("indirect") == null)
			indirect = (MFIndirectEffect) effects.get("indirect");
		else indirect = new MFIndirectEffect(0.0);
		
		if (effects.get("temporal").equals("") || effects.get("temporal") == null)
			temporal = (MFTemporalEffect) effects.get("temporal");
		else temporal = new MFTemporalEffect(0.0);		
	}
	
	public MFDirectEffect getDirect() {
		return direct;
	}
	public void setDirect(MFDirectEffect direct) {
		this.direct = direct;
	}
	public MFIndirectEffect getIndirect() {
		return indirect;
	}
	public void setIndirect(MFIndirectEffect indirect) {
		this.indirect = indirect;
	}
	public MFTemporalEffect getTemporal() {
		return temporal;
	}
	public void setTemporal(MFTemporalEffect temporal) {
		this.temporal = temporal;
	}
	
}
