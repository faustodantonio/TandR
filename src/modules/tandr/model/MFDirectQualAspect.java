package modules.tandr.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.IntersectionMatrix;

import utility.UConfig;
import model.MFeatureVersion;

public class MFDirectQualAspect extends MFDirectAspect {
	
//	private Map< String[] ,Integer > rccDistances;

	public MFDirectQualAspect() {
//		rccDistances = new HashMap< String[] , Integer>();
//		this.buildRCCDistancesMap();
	}
	
	public MFDirectQualAspect(Double value){
		super(value);
//		rccDistances = new HashMap<String[], Integer>();
//		this.buildRCCDistancesMap();
	}

	public double calculateTrustworthiness(ArrayList<MFeatureVersion> neighbors, MFeatureVersion featureVersion) {
		double t_dir_qual = 0.0;
		
		Map<String,IntersectionMatrix> fvRelates = new HashMap<String, IntersectionMatrix> ();
		Map<String,IntersectionMatrix> fvPrevRelates = new HashMap<String, IntersectionMatrix> ();
		
//		Map<String,Boolean> matrixDifferences = new HashMap<String, Boolean> ();
		
		int differences = 0;
		
		if ( ! featureVersion.isFirst() && featureVersion.getGeometry() != null) {
			for (MFeatureVersion neighbor : neighbors)  {
				if (neighbor.getGeometry() != null && featureVersion.getPrevFVersion().getGeometry() != null) {
					String uri = neighbor.getUri();
					fvRelates.put(uri, featureVersion.getGeometry().relate(neighbor.getGeometry()));
					fvPrevRelates.put(uri, featureVersion.getPrevFVersion().getGeometry().relate(neighbor.getGeometry()));
//					matrixDifferences.put(uri, fvRelates.get(uri).equals( fvPrevRelates.get(uri) ));
					if ( fvRelates.get(uri).equals( fvPrevRelates.get(uri) ) ) differences++;
				}
			}
			
			if (neighbors.size() == 0)
				t_dir_qual = 0.0;
			else {
				if (differences == 0)
					t_dir_qual = 1.0;
				else
					t_dir_qual = 1 - (differences / neighbors.size());
			}
		
		} else { 
			t_dir_qual = 0.0;
		}
		
		super.value = t_dir_qual;
		return super.value;
	}
	
//	public double calculateTrustworthiness(ArrayList<MFeatureVersion> featureVersions, MFeatureVersion featureVersion) {
//		double t_dir_qual = 0.0;
//		
//		Map<String,IntersectionMatrix> fvRelates = new HashMap<String, IntersectionMatrix> ();
//		Map<String,IntersectionMatrix> fvPrevRelates = new HashMap<String, IntersectionMatrix> ();
//		
//		Map<String,Boolean> matrixDifferences = new HashMap<String, Boolean> ();
//		
//		int differences = 0;
//		
//		if ( ! featureVersion.isFirst() && featureVersion.getGeometry() != null) {
//		
//			ArrayList<String> neighborsUris = foundation.retrieveFVPreviousesNeighbours(featureVersion, UConfig.getVGIHGraphURI(), featureVersion.getGeometryBuffer(UConfig.featureInfluenceRadius));	
//			for (String uri : neighborsUris)  {
//				MFeatureVersion neighbor = (MFeatureVersion) foundation.retrieveByUri(uri, UConfig.getVGIHGraphURI(), 0, MFeatureVersion.class);
//				if (neighbor.getGeometry() != null && featureVersion.getPrevFVersion().getGeometry() != null) {
//					fvRelates.put(uri, featureVersion.getGeometry().relate(neighbor.getGeometry()));
//					fvPrevRelates.put(uri, featureVersion.getPrevFVersion().getGeometry().relate(neighbor.getGeometry()));
//					matrixDifferences.put(uri, fvRelates.get(uri).equals( fvPrevRelates.get(uri) ));
//					if ( fvRelates.get(uri).equals( fvPrevRelates.get(uri) ) ) differences++;
//				}
//			}
//			
//			if (neighborsUris.size() == 0)
//				t_dir_qual = 0.0;
//			else {
//				if (differences == 0)
//					t_dir_qual = 1.0;
//				else
//					t_dir_qual = 1 - (differences / neighborsUris.size());
//			}
//		
//		} else { 
//			t_dir_qual = 0.0;
//		}
//		
//		super.value = t_dir_qual;
//		return super.value;
//	}

	@Override
	public String getEffectName() {
		return "Direct Effect";
	}
	@Override
	public String getAspectName() {
		return "Qualitative Direct Aspect";
	}

	public double validateTrustworthiness(MFeatureVersion fv1, MFeatureVersion fv2) {
		double t_dir_qual = 0.0;
		
		Map<String,IntersectionMatrix> fv2Relates = new HashMap<String, IntersectionMatrix> ();
		Map<String,IntersectionMatrix> fv1Relates = new HashMap<String, IntersectionMatrix> ();
		
		Map<String,Boolean> matrixDifferences = new HashMap<String, Boolean> ();
		
		int differences = 0;
	
		ArrayList<String> neighborsUris = foundation.retrieveFVPreviousesNeighbours(fv1, UConfig.getVGIHGraphURI(), fv1.getGeometryBuffer(10.0));	
		for (String uri : neighborsUris)  {
			MFeatureVersion neighbor = (MFeatureVersion) foundation.retrieveByUri(uri, UConfig.getVGIHGraphURI(), 0, MFeatureVersion.class);
			fv2Relates.put(uri, fv2.getGeometry().relate(neighbor.getGeometry()));
			fv1Relates.put(uri, fv1.getGeometry().relate(neighbor.getGeometry()));
			matrixDifferences.put(uri, fv2Relates.get(uri).equals( fv1Relates.get(uri) ));
			if ( fv2Relates.get(uri).equals( fv1Relates.get(uri) ) ) differences++;
		}
		
		if (neighborsUris.size() == 0)
			t_dir_qual = 0.0;
		else {
			if (differences == 0)
				t_dir_qual = 1.0;
			else
				t_dir_qual = 1 - (differences / neighborsUris.size());
		}
		
		super.value = t_dir_qual;
		return super.value;
	}

}
