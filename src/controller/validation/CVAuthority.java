package controller.validation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

import model.MAuthor;
import model.MFeature;
import model.MFeatureVersion;
import model.MReputation;
import utility.UConfig;
import utility.UDebug;
import foundation.FFoundationFacade;
import foundation.FReputation;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;


public class CVAuthority {

	private FFoundationFacade foundation;
	private FReputation frep;
	
	private Map<MFeature, MFeatureVersion> authorityFeatures;
	private MAuthor authority;

	private CSVParser csv;	
	
	public CVAuthority() {
		this.foundation = new FFoundationFacade();
		this.frep = new FReputation();
		this.authority = this.setAuthoritiesAuthor();
		this.authorityFeatures = new HashMap<MFeature, MFeatureVersion>();
		
		this.initCSV();
		this.initFeatures();
	}

	private void initCSV() {
		this.csv = null;
		CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');
		try {
			this.csv = new CSVParser(new FileReader(UConfig.validationPath), format);
		} catch (FileNotFoundException e) {
			UDebug.error("CSV validation file not found");
			e.printStackTrace();
		} catch (IOException e) {
			UDebug.error("Invalid CSV validation file");
			e.printStackTrace();
		}
	}

	public void authorPopulateGraph(String graph) {		
		foundation.create(this.authority, graph);
		frep.create(this.authority.getReputation(), graph);
	}
	
	private MAuthor setAuthoritiesAuthor() {		
		this.authority = new MAuthor();
		MReputation reputation;
		
		this.authority.setUri("http://semantic.web/data/hvgi/author.rdf#authority");
		this.authority.setAccountName("OGD");
		this.authority.setAccountServerHomepage("https://open.wien.at/site/");
		
		reputation = foundation.getMaximumReputation(UConfig.getMinDateTimeAsString());
		
		this.authority.setReputationUri(reputation.getUri(), 1);
		this.authority.setReputation(reputation);
		reputation.setAuthor(authority);
		reputation.setAuthorUri( "http://semantic.web/data/hvgi/author.rdf#authority" );
		
		UDebug.print( "\n\nAuthor URI : " + authority.getUri() + "\n", 6 );
		UDebug.print( "Reputation URI : " + authority.getReputation().getUri() + "\n\n", 6 );

		UDebug.print( "\n\nAuthor cross URI : " + authority.getReputation().getAuthorUri() + "\n", 6 );
		UDebug.print( "Reputation cross URI : " + authority.getReputationUri() + "\n\n", 6 );
		
		UDebug.print( "\n\nAuthor : " + foundation.convertToRDFXML(authority) + "\n\n", 6 );
		
		return authority;
	}
	
	public void featurePopulateGraph(String graphUri) {
		
		int dbgLevel = 1;
		String fUri;
		String vUri;
		
		UDebug.print( "\n\nGraph " + graphUri + " population", dbgLevel+5);
		UDebug.print( "\n\nFeatures to be inserted in graph: " + graphUri, dbgLevel+5);
		
		for ( Entry<MFeature, MFeatureVersion> fEntry : this.authorityFeatures.entrySet()) {
			
			fUri = fEntry.getKey().getUri();
			vUri = fEntry.getValue().getUri();
			UDebug.print( "\n\tkey (feature uri)  -> " + fUri ,dbgLevel+5);
			UDebug.print( " ; \tvalue (version uri) -> " + vUri ,dbgLevel+5);
			UDebug.print( "\n\tvalue (version uri) -> " + fUri ,dbgLevel+10);
			UDebug.print( "\n" + foundation.convertToRDFTTL( fEntry.getKey() ) ,dbgLevel+10);
//			UDebug.print( "\n\t\t" + fEntry.getValue().toString("\t\t")  ,dbgLevel+1);
			
			foundation.create(fEntry.getKey(), graphUri); // save feature
			foundation.create(fEntry.getValue(), graphUri); // save feature version			
		}
	}
	
	private void initFeatures() {
		
		Map<String, MFeature> features = new HashMap<String, MFeature>();
		Map<String,Map<String,String>> intersectedFVs = new HashMap<String,Map<String,String>>();
		
		int dbgLevel = 1;
		
    	UDebug.print("\nInizializing features map", dbgLevel);
		
        for(CSVRecord record : this.csv){
        	/**
        	 * check which is the feature version that intersects mostly the record geometry (if none skip)
        	 * create a feature
        	 * and the first feature version owned by the authority (such that fv has the maximum trustworthiness)
        	 */
        	MFeature bindedFeature;
        	
        	String wktAuthority = record.get("SHAPE");
        	wktAuthority = this.transformGeometry(wktAuthority);
        	intersectedFVs = foundation.getIntersectedFV(wktAuthority,UConfig.getVGIHGraphURI());
        	
        	UDebug.print("\nProcessing object ID:" + record.get("OBJECTID") + ".", dbgLevel + 1);
//        	UDebug.print("\n\tExpected Intersections " + record.get("EXPECTED_INTERSECTIONS"), dbgLevel + 1);
        	UDebug.print("\tObtained Intersections " + intersectedFVs.size() +"\n", dbgLevel + 1);
        	
        	if ( ! intersectedFVs.isEmpty() ) {
        		String bindedFeatureUri = this.getHigherIntersects(wktAuthority, intersectedFVs);
        		UDebug.print("\t\tFeature Selected " + bindedFeatureUri +"\n", dbgLevel + 1);
        		if ( ! bindedFeatureUri.equals("")) {
        			bindedFeature = (MFeature) foundation.retrieveByUri(bindedFeatureUri, UConfig.getVGIHGraphURI(), 0, MFeature.class);
        			features.put(wktAuthority, bindedFeature);
//        			UDebug.print("\t\tFeature Selected " + bindedFeature.getUri() +"\n", dbgLevel + 1);
        		}
        	}
        }
        
        this.generateAuthoritiesFeatures(features);
	}
	
	private void generateAuthoritiesFeatures(Map<String, MFeature> features) {
		
		int dbgLevel = 10;
		String fUri;
		String fGeometry;
		
		UDebug.print( "\n\nFeatures selected: ",dbgLevel+1);
		
		for (Entry<String, MFeature> fEntry : features.entrySet()) {
			fUri = fEntry.getValue().getUri();
			fGeometry = fEntry.getKey();
			
			UDebug.print( "\n\tkey (geom)  -> " + fGeometry ,dbgLevel+1);
			UDebug.print( " ; \tvalue (uri) -> " + fUri ,dbgLevel+1);
			
			MFeatureVersion version = new MFeatureVersion();
			MFeature feature = new MFeature();
			
			feature.setUri(fUri);
			
			version.setVersionNo("official");
			version.setAuthor(authority);
			version.setFeature(feature);
			version.setIsDeleted(false);
			version.setGeometry(fGeometry);
			version.setIsValidFrom(UConfig.getMinDateTime());
			version.setIsValidTo(UConfig.getMinDateTime());
			version.generateUri();
			
			feature.addVersion(version.getUri(),"official", version);
			
			UDebug.print("\nUri Version: " + version.getUri(), dbgLevel+1);
			
			this.authorityFeatures.put(feature, version);
		}
		
	}
	
	private String getHigherIntersects(String wktAuthority,	Map<String,Map<String,String>> intersectedFV) {
		
		Geometry geomAuthority = null;
		Geometry geomFV = null;
		Double intersectionArea = 0.0;
		Double higherIntersectionArea = 0.0;
		String bindedFeatureUri = "";
		
		try {
			geomAuthority = new WKTReader().read(wktAuthority);
			
			for (Entry<String, Map<String, String>> featureEntry : intersectedFV.entrySet()) {
			
				String featureUri = featureEntry.getKey();
				for ( Entry<String, String> versionEntry : featureEntry.getValue().entrySet() ) {
					
					try {
						geomFV = new WKTReader().read(versionEntry.getValue());
					} catch (com.vividsolutions.jts.io.ParseException e1) {
						UDebug.error("Feature Version wkt geometry badly formatted \n");
						UDebug.error(e1.getMessage());
					}
					
					if (geomFV != null && geomAuthority != null) {					
						if (geomAuthority.getGeometryType().equals( geomFV.getGeometryType() ))	{						
							intersectionArea = geomFV.intersection(geomAuthority).getArea() ;
							if (intersectionArea >= higherIntersectionArea) {
								higherIntersectionArea = intersectionArea;
								bindedFeatureUri = featureUri;
							}
						} else {
							break;
						}
					}
				}
			}
		} 
		catch (com.vividsolutions.jts.io.ParseException e1) {
			UDebug.error("Authority wkt geometry badly formatted \n");
			UDebug.error(e1.getMessage());
		}
		
		return bindedFeatureUri;
	}

//	private String getHigherIntersects(String wktAuthority,	Map<String,Map<String,String>> intersectedFV) {
//		
//		Geometry geomAuthority = null;
//		Geometry geomFV = null;
//		Double intersectionArea = 0.0;
//		Double higherIntersectionArea = 0.0;
//		String bindedFeatureUri = "";
//		
//		try {
//			geomAuthority = new WKTReader().read(wktAuthority);
//			
//			for ( Entry<String, String> fvEntry : intersectedFV.entrySet()) {
//				
//				String fUri = fvEntry.getKey();
//				String fvGeometry = fvEntry.getValue();
//				
//				try {
//					geomFV = new WKTReader().read(fvGeometry);
//				} catch (com.vividsolutions.jts.io.ParseException e1) {
//					UDebug.error("Feature Version wkt geometry badly formatted \n");
//					UDebug.error(e1.getMessage());
//				}
//				
//				if (geomFV != null && geomAuthority != null) {					
//					if (geomAuthority.getGeometryType().equals( geomFV.getGeometryType() ))	{						
//						intersectionArea = geomFV.intersection(geomAuthority).getArea() ;
//						if (intersectionArea >= higherIntersectionArea) {
//							higherIntersectionArea = intersectionArea;
//							bindedFeatureUri = fUri;
//						}
//					}
//					UDebug.print("\n\t\tOGD Geometry: " + new WKTWriter().write(geomAuthority) + ""
//											+ " original string :" + wktAuthority, 100);
//					UDebug.print("\n\t\tOSH Geometry: " + new WKTWriter().write(geomFV) + ""
//							+ " original string :" + fvGeometry, 100);
//				}
//			}
//		} 
//		catch (com.vividsolutions.jts.io.ParseException e1) {
//			UDebug.error("Authority wkt geometry badly formatted \n");
//			UDebug.error(e1.getMessage());
//		}
//		
//		return bindedFeatureUri;
//	}

	public MAuthor getAuthoritiesAuthor() {
		if (this.authority == null)
			this.setAuthoritiesAuthor();
		return this.authority;
	}

	public Map<MFeature, MFeatureVersion> getAuthorityFeatures() {
		return authorityFeatures;
	}

	public void setAuthorityFeatures(
			Map<MFeature, MFeatureVersion> authorityFeatures) {
		this.authorityFeatures = authorityFeatures;
	}
	
	private String transformGeometry(String ogd_geomWKT) {
		
		CoordinateReferenceSystem crs_ogd, crs_rdf;
		MathTransform transform;
		Geometry ogd_geom;
		
		String ogd_geomWKT_transformed =  ogd_geomWKT;
		
		if (! UConfig.ogd_epsg_crs.equals(UConfig.rdf_epsg_crs)) {
			
			try {
				ogd_geom = new WKTReader().read(ogd_geomWKT);
	
				crs_ogd = CRS.decode("EPSG:" + UConfig.ogd_epsg_crs);
				crs_rdf = CRS.decode("EPSG:" + UConfig.rdf_epsg_crs);
				
				transform = CRS.findMathTransform(crs_ogd, crs_rdf, true);		
				ogd_geom = JTS.transform(ogd_geom, transform);
		
				ogd_geomWKT_transformed = new WKTWriter().write(ogd_geom);
				
			} catch (NoSuchAuthorityCodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FactoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (com.vividsolutions.jts.io.ParseException e1) {
				UDebug.print("\n*** ERROR: wkt geometry bad formatted\nCorrection Attempt #1",1);
				UDebug.print("\n*** ERROR: " + e1.getMessage(),2);
			} catch (MismatchedDimensionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ogd_geomWKT_transformed;

	}
	
}
