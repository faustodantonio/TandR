package modules.tandr.model;

import model.MFeatureVersion;

public class MFIndirectGeomAspect extends MFAspect {

	public MFIndirectGeomAspect() {	}
	
	public MFIndirectGeomAspect(Double value){
		super(value);
	}

	@Override
	public double calculate(MFeatureVersion featureVersion) {
		
		double t_ind_geom = 1.0;
		
		return t_ind_geom;
	}

}
