package modules.tandr.foundation;

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
	
}
