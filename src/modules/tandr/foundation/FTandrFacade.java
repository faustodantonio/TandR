package modules.tandr.foundation;

import java.util.Map;

import modules.tandr.model.MReputationTandr;
import foundation.FFoundationFacade;

public class FTandrFacade extends FFoundationFacade {

	public FTandrFacade() {
		super();
		super.setFfactory(new FTandrFactory());
	}	
	
	public MReputationTandr getMaximumReputation(String computedAt) {
		FReputationTandr foundation = new FReputationTandr();
		return foundation.getMaximumReputation(computedAt);
	}
	
	public MReputationTandr getCalculatedReputation(String authorUri, String untilDate, boolean graphUri) {
		FReputationTandr foundation = new FReputationTandr();
		return foundation.computeReputationValues(authorUri, untilDate, graphUri);
	}
	
	
	/*************************
	 * 
	 * effect querying Interface
	 *
	 *************************/	
	
//	public Map<String,Double> getEffectList(String effect, String authorUri, String atDateTime, boolean graphUri) {
//		FTrustworthinessTandr feffect = new FTrustworthinessTandr();
//		return feffect.getEffectList(effect, authorUri, atDateTime, graphUri);
//	};
//	
//	public Map<String,Double> getEffectList(String effect, String authorUri, boolean graphUri) {
//		FTrustworthinessTandr feffect = new FTrustworthinessTandr();
//		return feffect.getEffectList(effect, authorUri, graphUri);
//	};
//	
//	public Map<String,Double> getEffectList(String effect, String authorUri, String atDateTime) {
//		FTrustworthinessTandr feffect = new FTrustworthinessTandr();
//		return feffect.getEffectList(effect, authorUri, atDateTime, false);
//	};
//	
//	public Map<String,Double> getEffectList(String effect, String authorUri) {
//		FTrustworthinessTandr feffect = new FTrustworthinessTandr();
//		return feffect.getEffectList(effect, authorUri, false);
//	};
	
	
	/*************************
	 * 
	 * aspect querying Interface
	 *
	 *************************/	
	
//	public Map<String,Double> getAspectList(String effect, String aspect, String authorUri, String atDateTime, boolean graphUri) {
//		FTrustworthinessTandr feffect = new FTrustworthinessTandr();
//		return feffect.getAspectList(effect, aspect, authorUri, atDateTime, graphUri);
//	};
//	
//	public Map<String,Double> getAspectList(String effect, String aspect, String authorUri, boolean graphUri) {
//		FTrustworthinessTandr feffect = new FTrustworthinessTandr();
//		return feffect.getAspectList(effect, aspect, authorUri, graphUri);
//	};
//	
//	public Map<String,Double> getAspectList(String effect, String aspect, String authorUri, String atDateTime) {
//		FTrustworthinessTandr feffect = new FTrustworthinessTandr();
//		return feffect.getAspectList(effect, aspect, authorUri, atDateTime, false);
//	};
//	
//	public Map<String,Double> getAspectList(String effect, String aspect, String authorUri) {
//		FTrustworthinessTandr feffect = new FTrustworthinessTandr();
//		return feffect.getAspectList(effect, aspect, authorUri, false);
//	};
	
}
