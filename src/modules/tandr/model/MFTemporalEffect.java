package modules.tandr.model;

import model.MFeatureVersion;

public class MFTemporalEffect extends MFEffect{

	public MFTemporalEffect() { }
	
	public MFTemporalEffect(Double value) { 
		super(value);
	}
	
	@Override
	public double calculate(MFeatureVersion featureVersion) {
		
		// TODO: implement the logic for temporal effect
		
		return super.value;
	}
	
	public double getTempValue() {
		return super.value;
	}
	
}
