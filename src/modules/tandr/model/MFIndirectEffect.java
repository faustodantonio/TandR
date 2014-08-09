package modules.tandr.model;

import model.MFeatureVersion;

public class MFIndirectEffect extends MFEffect{
	
	private MFAspect geometricAspect;
	private MFAspect qualitativeAspect;
	private MFAspect semanticAspect;
	
	private double indValue = 0.0;
	
	private static double indGeomWeight = 0.33;
	private static double indQualWeight = 0.33;
	private static double indSemWeight  = 0.33;
	
	public MFIndirectEffect() {
		
		this.geometricAspect   = new MFDirectGeomAspect();
		this.qualitativeAspect = new MFDirectQualAspect();
		this.semanticAspect    = new MFDirectSemAspect();
		
	}
	
	@Override
	public double calculate(MFeatureVersion featureVersion) {
		
		this.indValue = this.indValue + (indGeomWeight * this.geometricAspect.calculate(featureVersion));
		this.indValue = this.indValue + (indQualWeight * this.qualitativeAspect.calculate(featureVersion));
		this.indValue = this.indValue + (indSemWeight  * this.semanticAspect.calculate(featureVersion));		
		
		return indValue;
	}

}
