package modules.tandr.model;

import java.util.Map;
import java.util.Map.Entry;

import model.MAuthor;
import modules.tandr.foundation.FTandrFacade;

public abstract class MFEffect extends MFFactor{
	
	public MFEffect ()	{
		this.value = 0.0;
	}
	
	public MFEffect (Double value)	{
		super(value);
	}
	
	public abstract String getEffectName(); 
	
//	/*
//	 * (non-Javadoc)
//	 * @see modules.tandr.model.MFEffect#calculateReputation(model.MAuthor)
//	 * get all trustworthiness temporal effects and compute the average
//	 * the moment 
//	 */
//	public double calculateReputation(MAuthor author, String untilDate) {
//		
////		double repEffect = 0.0;
////		
////		FTandrFacade foundation = new FTandrFacade();
////		Map<String, Double> effectList = foundation.getEffectList( this.getEffectName(), author.getUri(), untilDate, true);
////		
////		for (Entry<String, Double> effect : effectList.entrySet())
////			repEffect += effect.getValue();
////		
////		if ( ! effectList.entrySet().isEmpty() )
////			repEffect = repEffect / effectList.entrySet().size();
////		else repEffect = 0.0;
////		
////		this.value = repEffect;
//		
//		return this.value;
//
//	}
	
	public String toString()	{
		return this.getClass().getName();
	}

	
}
