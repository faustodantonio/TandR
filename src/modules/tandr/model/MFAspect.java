package modules.tandr.model;

import model.MFeatureVersion;

public abstract class MFAspect extends MFFactor {

	protected double value;
	
	public abstract double calculate(MFeatureVersion featureVersion);
	
	public String toString()
	{
		return this.getClass().getName();
	}
	
	public double getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}	
}
