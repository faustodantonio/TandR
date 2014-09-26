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

	@Override
	public double calculateTrustworthiness(ArrayList<MFeatureVersion> featureVersions, MFeatureVersion featureVersion) {
		double t_dir_qual = 0.0;
		
		Map<String,IntersectionMatrix> fvRelates = new HashMap<String, IntersectionMatrix> ();
		Map<String,IntersectionMatrix> fvPrevRelates = new HashMap<String, IntersectionMatrix> ();
		
		Map<String,Boolean> matrixDifferences = new HashMap<String, Boolean> ();
		
		int differences = 0;
		
		if ( ! featureVersion.isFirst() ) {
		
			ArrayList<String> neighborsUris = foundation.retrieveFVPreviousesNeighbours(featureVersion, UConfig.getVGIHGraphURI(), featureVersion.getGeometryBuffer(10.0));	
			for (String uri : neighborsUris)  {
				MFeatureVersion neighbor = (MFeatureVersion) foundation.retrieveByUri(uri, UConfig.getVGIHGraphURI(), 0, MFeatureVersion.class);
				fvRelates.put(uri, featureVersion.getGeometry().relate(neighbor.getGeometry()));
				fvPrevRelates.put(uri, featureVersion.getPrevFVersion().getGeometry().relate(neighbor.getGeometry()));
				matrixDifferences.put(uri, fvRelates.get(uri).equals( fvPrevRelates.get(uri) ));
				if ( fvRelates.get(uri).equals( fvPrevRelates.get(uri) ) ) differences++;
			}
			
			if (neighborsUris.size() == 0)
				t_dir_qual = 0.0;
			else {
				if (differences == 0)
					t_dir_qual = 1.0;
				else
					t_dir_qual = 1 - (differences / neighborsUris.size());
			}
		
		} else { 
			t_dir_qual = 0.0;
		}
		
		super.value = t_dir_qual;
		return super.value;
	}

	@Override
	public String getEffectName() {
		return "Direct Effect";
	}
	@Override
	public String getAspectName() {
		return "Qualitative Direct Aspect";
	}
	
//	private void buildRCCDistancesMap() {
//		
//		String DC = "", EC="", PO="", EQ="", 
//			   TPP="", NTPP="", TPP1="", NTPP1="";
//		
//		String[] combo = new String [2];
//		
//		combo [1] = DC; combo[2] = DC	;	this.rccDistances.put( combo , 0);
//		combo [1] = DC; combo[2] = EC	;	this.rccDistances.put( combo , 1);
//		combo [1] = DC; combo[2] = PO	;	this.rccDistances.put( combo , 2);
//		combo [1] = DC; combo[2] = EQ	;	this.rccDistances.put( combo , 3);
//		combo [1] = DC; combo[2] = TPP	;	this.rccDistances.put( combo , 3);
//		combo [1] = DC; combo[2] = NTPP	;	this.rccDistances.put( combo , 4);
//		combo [1] = DC; combo[2] = TPP1	;	this.rccDistances.put( combo , 3);
//		combo [1] = DC; combo[2] = NTPP1;	this.rccDistances.put( combo , 4);
//		
//		combo [1] = EC; combo[2] = DC	;	this.rccDistances.put( combo , 1);
//		combo [1] = EC; combo[2] = EC	;	this.rccDistances.put( combo , 0);
//		combo [1] = EC; combo[2] = PO	;	this.rccDistances.put( combo , 1);
//		combo [1] = EC; combo[2] = EQ	;	this.rccDistances.put( combo , 2);
//		combo [1] = EC; combo[2] = TPP	;	this.rccDistances.put( combo , 2);
//		combo [1] = EC; combo[2] = NTPP	;	this.rccDistances.put( combo , 3);
//		combo [1] = EC; combo[2] = TPP1	;	this.rccDistances.put( combo , 2);
//		combo [1] = EC; combo[2] = NTPP1;	this.rccDistances.put( combo , 3);
//		
//		combo [1] = PO; combo[2] = DC	;	this.rccDistances.put( combo , 2);
//		combo [1] = PO; combo[2] = EC	;	this.rccDistances.put( combo , 1);
//		combo [1] = PO; combo[2] = PO	;	this.rccDistances.put( combo , 0);
//		combo [1] = PO; combo[2] = EQ	;	this.rccDistances.put( combo , 1);
//		combo [1] = PO; combo[2] = TPP	;	this.rccDistances.put( combo , 1);
//		combo [1] = PO; combo[2] = NTPP	;	this.rccDistances.put( combo , 2);
//		combo [1] = PO; combo[2] = TPP1	;	this.rccDistances.put( combo , 1);
//		combo [1] = PO; combo[2] = NTPP1;	this.rccDistances.put( combo , 2);
//		
//		combo [1] = EQ; combo[2] = DC	;	this.rccDistances.put( combo , 3);
//		combo [1] = EQ; combo[2] = EC	;	this.rccDistances.put( combo , 2);
//		combo [1] = EQ; combo[2] = PO	;	this.rccDistances.put( combo , 1);
//		combo [1] = EQ; combo[2] = EQ	;	this.rccDistances.put( combo , 0);
//		combo [1] = EQ; combo[2] = TPP	;	this.rccDistances.put( combo , 1);
//		combo [1] = EQ; combo[2] = NTPP	;	this.rccDistances.put( combo , 1);
//		combo [1] = EQ; combo[2] = TPP1	;	this.rccDistances.put( combo , 1);
//		combo [1] = EQ; combo[2] = NTPP1;	this.rccDistances.put( combo , 1);
//		
//		combo [1] = TPP; combo[2] = DC	 ;	this.rccDistances.put( combo , 3);
//		combo [1] = TPP; combo[2] = EC	 ;	this.rccDistances.put( combo , 2);
//		combo [1] = TPP; combo[2] = PO	 ;	this.rccDistances.put( combo , 1);
//		combo [1] = TPP; combo[2] = EQ	 ;	this.rccDistances.put( combo , 1);
//		combo [1] = TPP; combo[2] = TPP	 ;	this.rccDistances.put( combo , 0);
//		combo [1] = TPP; combo[2] = NTPP ;	this.rccDistances.put( combo , 1);
//		combo [1] = TPP; combo[2] = TPP1 ;	this.rccDistances.put( combo , 2);
//		combo [1] = TPP; combo[2] = NTPP1;	this.rccDistances.put( combo , 2);
//		
//		combo [1] = NTPP; combo[2] = DC	  ;	this.rccDistances.put( combo , 4);
//		combo [1] = NTPP; combo[2] = EC	  ;	this.rccDistances.put( combo , 3);
//		combo [1] = NTPP; combo[2] = PO	  ;	this.rccDistances.put( combo , 2);
//		combo [1] = NTPP; combo[2] = EQ	  ;	this.rccDistances.put( combo , 1);
//		combo [1] = NTPP; combo[2] = TPP  ;	this.rccDistances.put( combo , 1);
//		combo [1] = NTPP; combo[2] = NTPP ;	this.rccDistances.put( combo , 0);
//		combo [1] = NTPP; combo[2] = TPP1 ;	this.rccDistances.put( combo , 2);
//		combo [1] = NTPP; combo[2] = NTPP1;	this.rccDistances.put( combo , 2);
//		
//		combo [1] = TPP1; combo[2] = DC	  ;	this.rccDistances.put( combo , 3);
//		combo [1] = TPP1; combo[2] = EC	  ;	this.rccDistances.put( combo , 2);
//		combo [1] = TPP1; combo[2] = PO	  ;	this.rccDistances.put( combo , 1);
//		combo [1] = TPP1; combo[2] = EQ	  ;	this.rccDistances.put( combo , 1);
//		combo [1] = TPP1; combo[2] = TPP  ;	this.rccDistances.put( combo , 2);
//		combo [1] = TPP1; combo[2] = NTPP ;	this.rccDistances.put( combo , 2);
//		combo [1] = TPP1; combo[2] = TPP1 ;	this.rccDistances.put( combo , 0);
//		combo [1] = TPP1; combo[2] = NTPP1;	this.rccDistances.put( combo , 1);
//		
//		combo [1] = NTPP1; combo[2] = DC	;	this.rccDistances.put( combo , 4);
//		combo [1] = NTPP1; combo[2] = EC	;	this.rccDistances.put( combo , 3);
//		combo [1] = NTPP1; combo[2] = PO	;	this.rccDistances.put( combo , 2);
//		combo [1] = NTPP1; combo[2] = EQ	;	this.rccDistances.put( combo , 1);
//		combo [1] = NTPP1; combo[2] = TPP  	;	this.rccDistances.put( combo , 2);
//		combo [1] = NTPP1; combo[2] = NTPP 	;	this.rccDistances.put( combo , 2);
//		combo [1] = NTPP1; combo[2] = TPP1 	;	this.rccDistances.put( combo , 1);
//		combo [1] = NTPP1; combo[2] = NTPP1	;	this.rccDistances.put( combo , 0);
//	}


}
