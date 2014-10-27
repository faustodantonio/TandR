package modules.tandr.model;

import model.MFeatureVersion;

public class MFIndirectSemAspect extends MFIndirectAspect{

	public MFIndirectSemAspect() {	}
	
	public MFIndirectSemAspect(Double value){
		super(value);
	}

	@Override
	public String getEffectName() {
		return "Indirect Effect";
	}

	@Override
	public String getAspectName() {
		return "Semantic Indirect Aspect";
	}
	
	@Override
	public MFAspect fetchAspectFromReputation(MReputationTandr repo) {
		return repo.getIndirectEffect().getSemanticAspect();
	}

	public double validaterustworthiness(MFeatureVersion fv1, MFeatureVersion fv2) {
		super.value = 1;
		return super.value;
	}
}
