package modules.tandr.model;

import model.MFeatureVersion;

public class MFIndirectGeomAspect extends MFIndirectAspect {

	public MFIndirectGeomAspect() {	}
	
	public MFIndirectGeomAspect(Double value){
		super(value);
	}

	@Override
	public String getEffectName() {
		return "Indirect Effect";
	}

	@Override
	public String getAspectName() {
		return "Geometric Indirect Aspect";
	}
	
	@Override
	public MFAspect fetchAspectFromReputation(MReputationTandr repo) {
		return repo.getIndirectEffect().getGeometricAspect();
	}

	public double validaterustworthiness(MFeatureVersion fv1, MFeatureVersion fv2) {		
		super.value = 1;
		return super.value;
	}
	
}
