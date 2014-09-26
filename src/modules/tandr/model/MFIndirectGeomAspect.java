package modules.tandr.model;

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
	
}
