package modules.tandr.model;

import model.MFeatureVersion;

public abstract class MFEffect extends MFFactor{

	protected double value;
//	public float weight;
	
	public MFEffect ()	{
		this.value = 0.0;
	}

	//	public abstract double calculate();
	public abstract double calculate(MFeatureVersion featureVersion);
	
	public String toString()	{
		return this.getClass().getName();
	}
	
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
}
