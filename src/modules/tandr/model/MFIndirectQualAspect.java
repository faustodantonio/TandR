package modules.tandr.model;

import model.MFeatureVersion;

public class MFIndirectQualAspect extends MFAspect {

	public MFIndirectQualAspect() {	}
	
	public MFIndirectQualAspect(Double value){
		super(value);
	}

	@Override
	public double calculate(MFeatureVersion featureVersion) {
		double t_ind_qual = 1.0;
		return t_ind_qual;
	}

}
