package modules.tandr.model;

import model.MFeatureVersion;

public class MFDirectEffect extends MFEffect{

	private MFAspect geometricAspect;
	private MFAspect qualitativeAspect;
	private MFAspect semanticAspect;
	
	private static double dirGeomWeight = 0.33;
	private static double dirQualWeight = 0.33;
	private static double dirSemWeight  = 0.33;
	
	public MFDirectEffect() {
		
		this.geometricAspect   = new MFDirectGeomAspect();
		this.qualitativeAspect = new MFDirectQualAspect();
		this.semanticAspect    = new MFDirectSemAspect();
		
	}
	
	public MFDirectEffect(Double value) {
		super(value);
		
		this.geometricAspect   = new MFDirectGeomAspect();
		this.qualitativeAspect = new MFDirectQualAspect();
		this.semanticAspect    = new MFDirectSemAspect();
	}
	
	@Override
	public double calculate(MFeatureVersion featureVersion) {
		
		super.value = super.value + (dirGeomWeight * this.geometricAspect.calculate(featureVersion));
		super.value = super.value + (dirQualWeight * this.qualitativeAspect.calculate(featureVersion));
		super.value = super.value + (dirSemWeight  * this.semanticAspect.calculate(featureVersion));		
		
		return super.value;
	}

	public MFAspect getGeometricAspect() {
		return geometricAspect;
	}
	public void setGeometricAspect(MFAspect geometricAspect) {
		this.geometricAspect = geometricAspect;
	}
	public MFAspect getQualitativeAspect() {
		return qualitativeAspect;
	}
	public void setQualitativeAspect(MFAspect qualitativeAspect) {
		this.qualitativeAspect = qualitativeAspect;
	}
	public MFAspect getSemanticAspect() {
		return semanticAspect;
	}
	public void setSemanticAspect(MFAspect semanticAspect) {
		this.semanticAspect = semanticAspect;
	}
	
	public double getDirValue() {
		return super.value;
	}

}
