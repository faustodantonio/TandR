package modules.tandr.model;

import java.util.ArrayList;

import model.MAuthor;
import model.MFeatureVersion;
import model.MReputation;

public abstract class MFIndirectAspect extends MFAspect {
	
	public MFIndirectAspect() {
		this.value = 0.0;
	}
	
	public MFIndirectAspect(Double value) {
		super(value);
	}

	@Override
	public abstract String getEffectName();
	@Override
	public abstract String getAspectName();
	public abstract MFAspect fetchAspectFromReputation(MReputationTandr repo);
	public abstract MFAspect fetchDirectAspectFromReputation(MReputationTandr repo);

	public double calculateTrustworthiness(MReputationTandr authorReputation) {
		
		double t_ind_geom = 0.0;
		
		// get user reputation wrt indirect geometric aspect at date featureVersion.isValidFrom
		// assign to t_ind_geom the user reputation obtained 
		// (this is the first time indirect geometric trustworthiness aspect is calculated.)
//		t_ind_geom = this.fetchAspectFromReputation(authorReputation).getValue();
		
		t_ind_geom = this.fetchDirectAspectFromReputation(authorReputation).getValue();
		
		super.value = t_ind_geom;
		
		return super.value;
	}
	
	public double confirmTrustworthiness(MFeatureVersion featureVersion, ArrayList<MAuthor> confirmers) {
		
		double t_ind_geom = 0.0;		
		double totGeomRepo = 0.0;

		for (MAuthor confirmer : confirmers) {
			MReputation repo = confirmer.getReputation();
//			totGeomRepo += this.fetchAspectFromReputation((MReputationTandr) repo).getValue();
			totGeomRepo += this.fetchDirectAspectFromReputation((MReputationTandr) repo).getValue();
		}
		
		// assign to t_ind_geom the avarage user reputation obtained
		if (confirmers.size() != 0 )
			t_ind_geom = totGeomRepo / confirmers.size();
		else t_ind_geom = 0.0;
		
		super.value = t_ind_geom;
		return super.value;
	}
	
	public String toString()	{
		return this.getClass().getName();
	}

}
