package modules.tandr.model;

import model.MAuthor;
import model.MFeatureVersion;

public abstract class MFEffect extends MFFactor{
	
	public MFEffect ()	{
		this.value = 0.0;
	}
	
	public MFEffect (Double value)	{
		super(value);
	}

	public abstract double calculateTrustworthiness(MFeatureVersion featureVersion);
	public abstract double calculateReputation(MAuthor Author,String untilDate);
	
	public String toString()	{
		return this.getClass().getName();
	}

	
}
