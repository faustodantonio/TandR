package modules.tandr.model;

public abstract class MFDirectAspect extends MFAspect {
	
	public MFDirectAspect() {
		this.value = 0.0;
	}
	
	public MFDirectAspect(Double value) {
		super(value);
	}

	public abstract String getEffectName();
	public abstract String getAspectName();
	
	@Deprecated

//	public double calculateTrustworthiness(MFeatureVersion featureVersion) {
//		// get all feature versions
//		MFeature feature = featureVersion.getFeature();
//		ArrayList<MFeatureVersion> versions = feature.getPreviousVersions(featureVersion.getVersionNo(), 0);
//		
//		return this.calculateTrustworthiness(versions, featureVersion);
//	}
	
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
//		Map<String, Double> aspectList = foundation.getAspectList( this.getEffectName(), this.getAspectName(), author.getUri(), untilDate,true);
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
