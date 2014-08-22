package modules.tandr.model;

import model.MFeatureVersion;

public class MFIndirectEffect extends MFEffect{
	
	private MFAspect geometricAspect;
	private MFAspect qualitativeAspect;
	private MFAspect semanticAspect;
	
	private static double indGeomWeight = 0.33;
	private static double indQualWeight = 0.33;
	private static double indSemWeight  = 0.33;
	
	public MFIndirectEffect() {
		
		this.geometricAspect   = new MFDirectGeomAspect();
		this.qualitativeAspect = new MFDirectQualAspect();
		this.semanticAspect    = new MFDirectSemAspect();
		
	}
	
	public MFIndirectEffect(Double value) {
		super(value);
		
		this.geometricAspect   = new MFDirectGeomAspect();
		this.qualitativeAspect = new MFDirectQualAspect();
		this.semanticAspect    = new MFDirectSemAspect();
		
	}
	
	@Override
	public double calculate(MFeatureVersion featureVersion) {
		
		super.value = super.value + (indGeomWeight * this.geometricAspect.calculate(featureVersion));
		super.value = super.value + (indQualWeight * this.qualitativeAspect.calculate(featureVersion));
		super.value = super.value + (indSemWeight  * this.semanticAspect.calculate(featureVersion));		
		
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
	
	public double getIndValue() {
		return super.value;
	}	

}
