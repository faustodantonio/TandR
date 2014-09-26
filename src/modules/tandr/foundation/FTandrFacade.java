package modules.tandr.foundation;

import java.util.Map;

import foundation.FFoundationFacade;

public class FTandrFacade extends FFoundationFacade {

	public FTandrFacade() {
		super();
		super.setFfactory(new FTandrFactory());
	}
	
	public Map<String,Double> getAspectList(String effect, String aspect, String authorUri, String atDateTime, boolean graphUri) {
		FTrustworthinessTandr feffect = new FTrustworthinessTandr();
		return feffect.getAspectList(effect, aspect, authorUri, atDateTime, graphUri);
	};
	
	public Map<String,Double> getAspectList(String effect, String aspect, String authorUri, boolean graphUri) {
		FTrustworthinessTandr feffect = new FTrustworthinessTandr();
		return feffect.getAspectList(effect, aspect, authorUri, graphUri);
	};
	
	public Map<String,Double> getAspectList(String effect, String aspect, String authorUri, String atDateTime) {
		FTrustworthinessTandr feffect = new FTrustworthinessTandr();
		return feffect.getAspectList(effect, aspect, authorUri, atDateTime, false);
	};
	
	public Map<String,Double> getAspectList(String effect, String aspect, String authorUri) {
		FTrustworthinessTandr feffect = new FTrustworthinessTandr();
		return feffect.getAspectList(effect, aspect, authorUri, false);
	};
	
}
