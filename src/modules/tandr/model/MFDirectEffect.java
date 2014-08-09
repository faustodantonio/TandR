package modules.tandr.model;

import model.MFeatureVersion;

public class MFDirectEffect extends MFEffect{

	private MFAspect geometricAspect;
	private MFAspect qualitativeAspect;
	private MFAspect semanticAspect;
	
	private double dirValue = 0.0;
	
	private static double dirGeomWeight = 0.33;
	private static double dirQualWeight = 0.33;
	private static double dirSemWeight  = 0.33;
	
	public MFDirectEffect() {
		
		this.geometricAspect   = new MFDirectGeomAspect();
		this.qualitativeAspect = new MFDirectQualAspect();
		this.semanticAspect    = new MFDirectSemAspect();
		
	}
	
	@Override
	public double calculate(MFeatureVersion featureVersion) {
		
		this.dirValue = this.dirValue + (dirGeomWeight * this.geometricAspect.calculate(featureVersion));
		this.dirValue = this.dirValue + (dirQualWeight * this.qualitativeAspect.calculate(featureVersion));
		this.dirValue = this.dirValue + (dirSemWeight  * this.semanticAspect.calculate(featureVersion));		
		
		return dirValue;
	}


}
