package modules.tandr.model;

import model.MFeatureVersion;

public abstract class MFAspect extends MFFactor {
	
	public MFAspect() {	}
	
	public MFAspect(Double value) {
		super(value);
	}

	public abstract double calculate(MFeatureVersion featureVersion);
	
	public String toString()
	{
		return this.getClass().getName();
	}
	
}
