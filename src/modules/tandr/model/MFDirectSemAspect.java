package modules.tandr.model;

import model.MFeatureVersion;

public class MFDirectSemAspect extends MFAspect {

	public MFDirectSemAspect() { }
	
	public MFDirectSemAspect(Double value){
		super(value);
	}

	@Override
	public double calculate(MFeatureVersion featureVersion) {
		double t_dir_sem = 1.0;
		return t_dir_sem;
	}

}
