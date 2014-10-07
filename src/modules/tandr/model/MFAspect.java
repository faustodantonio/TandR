package modules.tandr.model;

import java.util.Map;
import java.util.Map.Entry;

import model.MAuthor;
import modules.tandr.foundation.FTandrFacade;

public abstract class MFAspect extends MFFactor {
	
	public MFAspect() {
		this.value = 0.0;
	}
	
	public MFAspect(Double value) {
		super(value);
	}

	public abstract String getEffectName();
	public abstract String getAspectName();
	
	/**
	 * get author's features versions
	 * for eache fv
	 * 		get the direct effect value
	 * calculate the average and assign it to Trustworthiness 
	 */
	public double calculateReputation(MAuthor author,String untilDate) {

		double repAspect = 0.0;
		
		FTandrFacade foundation = new FTandrFacade();
		Map<String, Double> aspectList = foundation.getAspectList( this.getEffectName(), this.getAspectName(), author.getUri(), untilDate, true);
		
		for (Entry<String, Double> aspect : aspectList.entrySet())
			repAspect += aspect.getValue();
		
		if ( ! aspectList.entrySet().isEmpty() )
			repAspect = repAspect / aspectList.entrySet().size();
		else repAspect = 0.0;
		
		this.value = repAspect;
		
		return this.value;
	}
	
	public String toString()	{
		return this.getClass().getName();
	}

}
