package modules.tandr.model;

import model.MFeatureVersion;

public abstract class MFAspect extends MFFactor {
	
	public MFAspect() {
		this.value = 0.0;
	}
	
	public MFAspect(Double value) {
		super(value);
	}

	public abstract double calculate(MFeatureVersion featureVersion);
	
	public String toString()	{
		return this.getClass().getName();
	}
	
}
