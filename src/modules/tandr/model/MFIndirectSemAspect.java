package modules.tandr.model;

import model.MFeatureVersion;

public class MFIndirectSemAspect extends MFAspect{

	public MFIndirectSemAspect() {
		super();
	}

	@Override
	public double calculate(MFeatureVersion featureVersion) {
		double t_ind_sem = 1.0;
		return t_ind_sem;
	}

}
