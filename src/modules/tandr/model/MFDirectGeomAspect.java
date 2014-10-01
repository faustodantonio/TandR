package modules.tandr.model;

import java.util.ArrayList;

import model.MFeatureVersion;

public class MFDirectGeomAspect extends MFDirectAspect {
	
	private double weightDArea = 0.33;
	private double weightDPerimeter = 0.33;
	private double weightDNoVertices = 0.34;
	
	private double avgArea       = 0.0;
	private double avgPerimeter  = 0.0;
	private double avgNoVertices = 0.0;
	
	private ArrayList<MFeatureVersion> prevVersions;
	public MFDirectGeomAspect() {}
	
	public MFDirectGeomAspect(Double value){
		super(value);
	}


	public double calculateTrustworthiness(MFeatureVersion featureVersion) {
		double t_dir_geom = 0.0;
		
		// TODO : add the maximum distance 
		
		double dArea       = 0.0;
		double dPerimeter  = 0.0;
		double dNoVertices = 0.0;
		
		if ( ! this.prevVersions.isEmpty() ) {
			dArea       = this.calculateDArea(featureVersion);
			dPerimeter  = this.calculateDPerimeter(featureVersion);
			dNoVertices = this.calculateDNoVertices(featureVersion);
			
			t_dir_geom = this.calculateTDirGeom(dArea, dPerimeter, dNoVertices);
		}
		
		// add the weighting reputation reference (Paolo)
		
		super.value = t_dir_geom;
		return super.value;
	}

	private double calculateTDirGeom(double dArea, double dPerimeter, double dNoVertices)	{
		double t_dir_geom;
		// TODO: modify the formula, it doesn't guarantee T <= 1. 
		t_dir_geom = this.weightDArea*dArea + this.weightDPerimeter*dPerimeter + this.weightDNoVertices*dNoVertices;
		return t_dir_geom;
	}
	
	private double calculateDArea(MFeatureVersion featureVersion){
		double dArea       = 0.0;
		String geometryType = featureVersion.getGeometry().getGeometryType();
		
		if (geometryType.equals("Point") || geometryType.equals("LineString")) 
			dArea = 1.0;
		else 
			dArea = Math.abs(featureVersion.getGeometry().getArea() - avgArea      );
		
		return dArea;
	}
	
	private double calculateDPerimeter(MFeatureVersion featureVersion){
		double dPerimeter  = 0.0;
		String geometryType = featureVersion.getGeometry().getGeometryType();
		
		if (geometryType.equals("Point")) 
			dPerimeter = 1.0;
		else 
			dPerimeter = Math.abs(featureVersion.getGeometry().getLength() - avgPerimeter );
		
		return dPerimeter;
	}
	
	private double calculateDNoVertices(MFeatureVersion featureVersion){
		double dNoVertices       = 0.0;
		
		dNoVertices = 1 - (Math.abs(featureVersion.getGeometry().getNumPoints() - avgNoVertices));
		
		return dNoVertices;
	}
	
	public void calculateAvgs(ArrayList<MFeatureVersion> featureVersions) {
		
		if ( this.prevVersions == null || ! ( this.prevVersions.containsAll(featureVersions) && featureVersions.containsAll(prevVersions)) ) {
			
			this.setPrevVersions(featureVersions);
			this.calculateAvgs();
			
		}
	}
	
	public void calculateAvgs() {
		
		double totArea       = 0.0;
		double totPerimeter  = 0.0;
		double totNoVertices = 0.0;
		
		if (prevVersions.size() != 0) {
			
			for ( MFeatureVersion fv : prevVersions) {
				totArea       += fv.getGeometry().getArea();
				totPerimeter  += fv.getGeometry().getLength();
				totNoVertices += fv.getGeometry().getNumPoints();
			}
			
			this.avgArea       = totArea       / prevVersions.size();
			this.avgPerimeter  = totPerimeter  / prevVersions.size();
			this.avgNoVertices = totNoVertices / prevVersions.size();
		} else {
			this.avgArea       = 0.0;
			this.avgPerimeter  = 0.0;
			this.avgNoVertices = 0.0;
		}
		
	}

	@Override
	public String getEffectName() {
		return "Direct Effect";
	}
	@Override
	public String getAspectName() {
		return "Geometric Direct Aspect";
	}
	
	public ArrayList<MFeatureVersion> getPrevVersions() {
		return prevVersions;
	}
	private void setPrevVersions(ArrayList<MFeatureVersion> prevVersions) {
		this.prevVersions = prevVersions;
	}


}