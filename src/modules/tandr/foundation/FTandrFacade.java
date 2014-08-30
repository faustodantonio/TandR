package modules.tandr.foundation;

import foundation.FFoundationFacade;

public class FTandrFacade extends FFoundationFacade {

	public FTandrFacade() {
		super();
		super.setFfactory(new FTandrFactory());
	}
	
}
