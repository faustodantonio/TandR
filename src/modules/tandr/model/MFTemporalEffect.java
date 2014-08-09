package modules.tandr.model;

import model.MFeatureVersion;

public class MFTemporalEffect extends MFEffect{
	
	private double tempValue = 0.0;
	
	public MFTemporalEffect() { }
	
	@Override
	public double calculate(MFeatureVersion featureVersion) {
		
		
		
		return this.tempValue;
	}

}
