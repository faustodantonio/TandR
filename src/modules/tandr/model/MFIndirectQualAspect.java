package modules.tandr.model;

import model.MFeatureVersion;

public class MFIndirectQualAspect extends MFIndirectAspect {

	public MFIndirectQualAspect() {	}
	
	public MFIndirectQualAspect(Double value){
		super(value);
	}

	@Override
	public String getEffectName() {
		return "Indirect Effect";
	}

	@Override
	public String getAspectName() {
		return "Qualitative Indirect Aspect";
	}
	
	@Override
	public MFAspect fetchAspectFromReputation(MReputationTandr repo) {
		return repo.getIndirectEffect().getQualitativeAspect();
	}

	public double validaterustworthiness(MFeatureVersion fv1, MFeatureVersion fv2) {
		super.value = 1;
		return super.value;
	}
	
}
