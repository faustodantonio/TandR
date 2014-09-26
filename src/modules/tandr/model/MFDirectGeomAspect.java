package modules.tandr.model;

import java.util.ArrayList;

import utility.UConfig;
import model.MFeatureVersion;
import model.MTrustworthiness;

public class MFDirectGeomAspect extends MFDirectAspect {
	
	double weightDArea = 0.33;
	double weightDPerimeter = 0.33;
	double weightDNoVertices = 0.34;
	
	double avgArea       = 0.0;
	double avgPerimeter  = 0.0;
	double avgNoVertices = 0.0;
		
	public MFDirectGeomAspect() {}
	
	public MFDirectGeomAspect(Double value){
		super(value);
	}

	@Override
	public double calculateTrustworthiness(ArrayList<MFeatureVersion> featureVersions, MFeatureVersion featureVersion) {
		double t_dir_geom = 0.0;
		
		// TODO : add the maximum distance 
		
		double dArea       = 0.0;
		double dPerimeter  = 0.0;
		double dNoVertices = 0.0;
		
		String geometryType = featureVersion.getGeometry().getGeometryType();
		
		if ( ! featureVersions.isEmpty() ) {
			this.calculateAvgs(featureVersions);
			
			if (geometryType.equals("Point") || geometryType.equals("LineString")) dArea = 1.0;
			else dArea       = Math.abs(featureVersion.getGeometry().getArea()      - avgArea      );
			
			if (geometryType.equals("Point")) dPerimeter = 1.0;
			else dPerimeter  = Math.abs(featureVersion.getGeometry().getLength()    - avgPerimeter );
			
			dNoVertices = 1 - (Math.abs(featureVersion.getGeometry().getNumPoints() - avgNoVertices));
			
			t_dir_geom = this.weightDArea*dArea + this.weightDPerimeter*dPerimeter + this.weightDNoVertices*dNoVertices;
			
			for ( MFeatureVersion fv : featureVersions) {
				MTrustworthiness trust;
				
				if ( fv.getTrustworthiness() == null && fv.getTrustworthinessUri() == null ) {
					trust = new MTrustworthinessTandr( featureVersion );
				}else {
					trust = fv.getTrustworthiness();
				}
				
				((MTrustworthinessTandr)trust).getDirectEffect().getGeometricAspect().updateDirGeomAspect(fv);
			}
		}
		
		super.value = t_dir_geom;
		return super.value;
	}
	
	public double updateDirGeomAspect(MFeatureVersion featureVersion) {
		
		double t_dir_geom = 0.0;
		
		double dArea       = 0.0;
		double dPerimeter  = 0.0;
		double dNoVertices = 0.0;
		
		dArea       = Math.abs(featureVersion.getGeometry().getArea()      - avgArea      );
		dPerimeter  = Math.abs(featureVersion.getGeometry().getLength()    - avgPerimeter );
		dNoVertices = Math.abs(featureVersion.getGeometry().getNumPoints() - avgNoVertices);
		
		dArea       = Math.abs(featureVersion.getGeometry().getArea()      - avgArea      );
		dPerimeter  = Math.abs(featureVersion.getGeometry().getLength()    - avgPerimeter );
		dNoVertices = Math.abs(featureVersion.getGeometry().getNumPoints() - avgNoVertices);
		
		t_dir_geom = this.weightDArea*dArea + this.weightDPerimeter*dPerimeter + this.weightDNoVertices*dNoVertices;
		
		super.value = t_dir_geom;
		return super.value;
	}

	private void calculateAvgs(ArrayList<MFeatureVersion> featureVersions) {
		
		double totArea       = 0.0;
		double totPerimeter  = 0.0;
		double totNoVertices = 0.0;
		
		for ( MFeatureVersion fv : featureVersions) {
			totArea       += fv.getGeometry().getArea();
			totPerimeter  += fv.getGeometry().getLength();
			totNoVertices += fv.getGeometry().getNumPoints();
		}
		
		if (featureVersions.size() != 0) {
			this.avgArea       = totArea       / featureVersions.size();
			this.avgPerimeter  = totPerimeter  / featureVersions.size();
			this.avgNoVertices = totNoVertices / featureVersions.size();
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

}