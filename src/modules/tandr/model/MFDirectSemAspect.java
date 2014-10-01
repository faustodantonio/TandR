package modules.tandr.model;

import java.util.ArrayList;

import model.MAuthor;
import model.MFeatureVersion;

public class MFDirectSemAspect extends MFDirectAspect {

	public MFDirectSemAspect() { }
	
	public MFDirectSemAspect(Double value){
		super(value);
	}

	public double calculateTrustworthiness(ArrayList<MFeatureVersion> featureVersions, MFeatureVersion featureVersion) {
		
		double t_dir_sem = 0.0;
		int differences = 0;
		int fvsNum = featureVersions.size();
		
		if (fvsNum > 0)
		{
			for (MFeatureVersion version : featureVersions) 
				if ( ! featureVersion.getTags().equals( version.getTags()) ) 
					differences++ ;
			
			t_dir_sem = (fvsNum - differences) / fvsNum;
		}
		
		super.value = t_dir_sem;
		return t_dir_sem;
	}

	@Override
	public double calculateReputation(MAuthor author, String untilDate) {
		double r_dir_sem = 1.0;
		super.value = r_dir_sem;
		return super.value;
	}

	@Override
	public String getEffectName() {
		return "Direct Effect";
	}

	@Override
	public String getAspectName() {
		return "Semantic Direct Aspect";
	}


}
