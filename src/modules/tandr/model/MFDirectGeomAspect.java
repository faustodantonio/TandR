package modules.tandr.model;

import java.util.ArrayList;

import model.MFeatureVersion;

public class MFDirectGeomAspect extends MFDirectAspect {
	
	private double weightDArea       = 0.3333333;
	private double weightDPerimeter  = 0.3333333;
	private double weightDNoVertices = 0.3333334;
	
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
		
		double contribArea       = 0.0;
		double contribPerimeter  = 0.0;
		double contribNoVertices = 0.0;
		
		double dArea       = 0.0;
		double dPerimeter  = 0.0;
		double dNoVertices = 0.0;
		
		if ( ! this.prevVersions.isEmpty() ) {
			dArea       = Math.abs( this.calculateDArea(featureVersion) );
			dPerimeter  = Math.abs( this.calculateDPerimeter(featureVersion) );
			dNoVertices = Math.abs(this.calculateDNoVertices(featureVersion) );
			
			contribArea = 1 - ( dArea / (dArea + 1) );
			contribPerimeter = 1 - ( dPerimeter / (dPerimeter + 1) );
			contribNoVertices = 1 - ( dNoVertices / (dNoVertices + 1) );
			
			t_dir_geom = this.calculateTDirGeom(contribArea, contribPerimeter, contribNoVertices);
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
	
	public void calculateWeightedAvgs(ArrayList<MFeatureVersion> featureVersions) {
		if ( this.prevVersions == null || (! ( this.prevVersions.containsAll(featureVersions) && featureVersions.containsAll(prevVersions)) )) {
			this.setPrevVersions(featureVersions);
			this.calculateWeightedAvgs();			
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
	
	public void calculateWeightedAvgs() {
		
		double totArea       = 0.0;
		double totPerimeter  = 0.0;
		double totNoVertices = 0.0;
		
		double totReputations = 0.0;

		if (prevVersions.size() != 0) {
			
			for ( MFeatureVersion fv : prevVersions) {
				
				MReputationTandr reputation = (MReputationTandr) fv.getAuthor().getReputation();
				
				totArea       += fv.getGeometry().getArea() * reputation.getValue();
				totPerimeter  += fv.getGeometry().getLength() * reputation.getValue();
				totNoVertices += fv.getGeometry().getNumPoints() * reputation.getValue();
				
				totReputations += reputation.getValue();
			}
			
			if (totReputations != 0.0) {
				this.avgArea       = totArea       / totReputations;
				this.avgPerimeter  = totPerimeter  / totReputations;
				this.avgNoVertices = totNoVertices / totReputations;
			} else {
				this.avgArea       = 0.0;
				this.avgPerimeter  = 0.0;
				this.avgNoVertices = 0.0;
			}
			
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