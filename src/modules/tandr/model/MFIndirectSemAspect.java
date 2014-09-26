package modules.tandr.model;

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
}
