package modules.tandr.model;

import model.MFeatureVersion;

public abstract class MFEffect extends MFFactor{
	
	public MFEffect ()	{
		this.value = 0.0;
	}
	
	public MFEffect (Double value)	{
		super(value);
	}

	public abstract double calculate(MFeatureVersion featureVersion);
	
	public String toString()	{
		return this.getClass().getName();
	}
	
}
