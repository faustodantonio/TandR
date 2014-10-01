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

	public double calculateTrustworthiness(MFeatureVersion featureVersion) {
		
		double t_ind_geom = 0.0;
		
		// get user reputation wrt indirect geometric aspect at date featureVersion.isValidFrom
		MReputationTandr authorReputation = new MReputationTandr( featureVersion.getAuthor() );
		
		// assign to t_ind_geom the user reputation obtained 
		//(this is the first time indirect geometric trustworthiness aspect is calculated.)
		t_ind_geom = this.fetchAspectFromReputation((MReputationTandr) authorReputation).getValue();
		
		super.value = t_ind_geom;
		
		return super.value;
	}
	
	public double confirmTrustworthiness(MFeatureVersion featureVersion, ArrayList<MAuthor> confirmers) {
		
		double t_ind_geom = 1.0;		
		double totGeomRepo = 0.0;

		for (MAuthor confirmer : confirmers) {
			MReputation repo;
			
			// get user reputation wrt indirect geometric aspect at date featureVersion.isValidFrom
			if (confirmer.getReputation() == null)
				repo = new MReputationTandr(confirmer);
			else repo = confirmer.getReputation();
			
			totGeomRepo += this.fetchAspectFromReputation((MReputationTandr) repo).getValue();
		}
		
		// assign to t_ind_geom the avarage user reputation obtained
		if (confirmers.size() != 0 )
			t_ind_geom = totGeomRepo / confirmers.size();
		else t_ind_geom = 0.0;
		
		super.value = t_ind_geom;
		return super.value;
	}
	
//	/**
//	 * get author's features versions
//	 * for eache fv
//	 * 		get the direct effect value
//	 * calculate the average and assign it to Trustworthiness 
//	 */
//	public double calculateReputation(MAuthor author, String untilDate) {
//
//		double r_dir_geom = 0.0;
//		
//		FTandrFacade foundation = new FTandrFacade();
//		Map<String, Double> aspectList = foundation.getAspectList( this.getEffectName(), this.getAspectName(), author.getUri(), untilDate, true);
//		
//		for (Entry<String, Double> aspect : aspectList.entrySet()) 
//			r_dir_geom += aspect.getValue();
//		
//		r_dir_geom = r_dir_geom / aspectList.entrySet().size();
//		
//		return r_dir_geom;
//	}
	
	public String toString()	{
		return this.getClass().getName();
	}

}
