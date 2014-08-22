package modules.tandr.model;

import model.MFeatureVersion;

public class MFDirectQualAspect extends MFAspect {

	public MFDirectQualAspect() { }
	
	public MFDirectQualAspect(Double value){
		super(value);
	}

	@Override
	public double calculate(MFeatureVersion featureVersion) {
		double t_dir_qual = 1.0;
		return t_dir_qual;
	}

}
