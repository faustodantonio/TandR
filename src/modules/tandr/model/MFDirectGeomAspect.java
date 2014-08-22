package modules.tandr.model;

import model.MFeatureVersion;

public class MFDirectGeomAspect extends MFAspect {

	public MFDirectGeomAspect() {}
	
	public MFDirectGeomAspect(Double value){
		super(value);
	}

	@Override
	public double calculate(MFeatureVersion featureVersion) {
		double t_dir_geom = 1.0;
		return t_dir_geom;
	}

}