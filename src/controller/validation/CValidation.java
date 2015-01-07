package controller.validation;

import java.util.Set;

import model.MFeature;
import model.MFeatureVersion;
import foundation.FFoundationFacade;
import utility.UConfig;
import utility.UDebug;

public class CValidation {
	
	private int dbgLevel = 1;
	
	private FFoundationFacade foundation;
	private CVAuthority authority;
//	private CVModule module;
	
	private CValidationAbstract validation;
	
//	private String lowestTGraph  = UConfig.getLOWESTTGraphURI();
//	private String averageTGraph = UConfig.getAVERAGETraphURI();
//	private String highestTGraph = UConfig.getHIGHESTTGraphURI();

	public CValidation() {
		this.foundation = new FFoundationFacade();
		
		authority = new CVAuthority();
//		module = new CVModule();
		
		String validationClass = "modules." + UConfig.module_trustworthiness_calculus +"."+ UConfig.validation_trustworthiness;
		try {
			validation = (CValidationAbstract) Class.forName(validationClass).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.out.print("Unable to instantiate the class " + validationClass + "\n"
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			validation = new modules.tandr.controller.CValidationTandR();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.out.print("Unable to access the class " + validationClass + ". "
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			validation = new modules.tandr.controller.CValidationTandR(); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.print("Unable to locate the class " + validationClass + ". "
					+validationClass + " do not exists \n"
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			validation = new modules.tandr.controller.CValidationTandR(); 
		}
	}

	public boolean validate() {
		boolean result = true;
		
		this.checkValidationsGraphs();
		this.prepareAuthorityVersions();
		this.executeValidation();
		
		return result;
	}
	
	/*************************
	 * 
	 * Graph Creation and Population FUNCTIONS
	 *
	 *************************/	

//	private void checkValidationsGraphs() {
//		
//		boolean lowerTExists  = foundation.checkGraphExists(UConfig.lowerTGraph  , UConfig.graphURI);
//		if (lowerTExists) 
//			foundation.deleteGraph(UConfig.lowerTGraph   , "graphs");
//		boolean averageTExists  = foundation.checkGraphExists(UConfig.averageTGraph  , UConfig.graphURI);
//		if (averageTExists) 
//			foundation.deleteGraph(UConfig.averageTGraph   , "graphs");
//		boolean higherExists  = foundation.checkGraphExists(UConfig.higherTGraph  , UConfig.graphURI);
//		if (higherExists) 
//			foundation.deleteGraph(UConfig.higherTGraph   , "graphs");
//
//		foundation.createGraph(UConfig.lowerTGraph  , "graphs");
//		foundation.createGraph(UConfig.averageTGraph  , "graphs");
//		foundation.createGraph(UConfig.higherTGraph  , "graphs");
//	}
	
	private void checkValidationsGraphs() {
		
		boolean lowerTExists  = foundation.checkGraphExists(UConfig.getValidationGraphURI()  , UConfig.graphURI);
		if (lowerTExists) 
			foundation.deleteGraph(UConfig.getValidationGraphURI()   , "graphs");

		foundation.createGraph(UConfig.getValidationGraphURI()  , "graphs");
	}	
	
	/**
	 * Check and create graphs on triplestore
	 * Populate graphs with
	 * 		The authoritie's author with maximum reputation 
	 * 		The authoritie's features (with no reputation)
	 * 		
	 * 		The featureVersions with the desired trustworthiness
	 */
//	private void arrangeVersions() {
//		
//		this.authority.authorPopulateGraph(lowestTGraph);
//		this.authority.authorPopulateGraph(averageTGraph);
//		this.authority.authorPopulateGraph(highestTGraph);
//		
//		this.authority.featurePopulateGraph(lowestTGraph);
//		this.authority.featurePopulateGraph(averageTGraph);
//		this.authority.featurePopulateGraph(highestTGraph);
//		
//		Set<MFeature> authorityFeatures = this.authority.getAuthorityFeatures().keySet();
//		
//		this.module.setFeatures(authorityFeatures);
//		
//		this.module.featurePopulateLowerGraph(lowestTGraph);
//		this.module.featurePopulateAverageGraph(averageTGraph);
//		this.module.featurePopulateHigherGraph(highestTGraph);
//	}

	private void prepareAuthorityVersions() {
		
		this.authority.authorPopulateGraph(UConfig.getValidationGraphURI());
		this.authority.featurePopulateGraph(UConfig.getValidationGraphURI());
		
//		Set<MFeature> authorityFeatures = this.authority.getAuthorityFeatures().keySet();
//		this.module.setFeatures(authorityFeatures);
//		this.module.featurePopulateGraph(UConfig.validationGraph);
	}
	
	/*************************
	 * 
	 * Validation FUNCTIONS
	 *
	 *************************/	
	
	private void executeValidation() {
		
		Set<MFeature> authorityFeatures = this.authority.getAuthorityFeatures().keySet();

		UDebug.print("\n\n*****************************************\n", dbgLevel);
		UDebug.print("***** Starting Validation Execution *****\n", dbgLevel);
		UDebug.print("*****                               *****\n\n", dbgLevel);
		
		for (MFeature feature : authorityFeatures) {
			
//			System.out.print(feature.getUri());
			
			// This feature is just for refresh the information required
			
			UDebug.print("\tProcessing feature: " + feature.getUriID() + "(#FeatureVersions: " + feature.getVersions().values().size() + ")" + "\n", dbgLevel);
			MFeature featureAux = (MFeature) foundation.retrieveByUri(feature.getUri(), UConfig.getVGIHGraphURI(), 1, MFeature.class);
			String fvOfficialUri = this.generateOfficialoVersionUri(feature);
			MFeatureVersion fvOffical = (MFeatureVersion) foundation.retrieveByUri(fvOfficialUri, UConfig.getValidationGraphURI(), 0, MFeatureVersion.class);
			
//			for (String versionUri : featureAux.getVersions().keySet()) {
//				MFeatureVersion version = feature.getFeatureVersionByURI(versionUri, 0);
			for (MFeatureVersion version : featureAux.getVersions().values()) {
				UDebug.print("\t\tProcessing version: " + version.getUriID() +"\n", dbgLevel+1);
				foundation.create(version, UConfig.getValidationGraphURI());
				validation.validateTrustworthiness(fvOffical, version, UConfig.getValidationGraphURI());
			}
			UDebug.print("\n", dbgLevel);
		}
		UDebug.print("\n*****                          *****\n", dbgLevel);
		UDebug.print("*****Validation Execution Ended*****\n", dbgLevel);
		UDebug.print("************************************\n\n", dbgLevel);
	}	
	
	public String generateOfficialoVersionUri(MFeature feature)	{
		String fvUri = "";
//		UDebug.print("\n\n(generating fv uri) Feature Uri: "+feature.getUri()+"\n",1);
		fvUri = "http://semantic.web/data/hvgi/featureVersions.rdf#featureVersion";
		fvUri = fvUri + feature.getUriID() + "_official";
//		UDebug.print("(generating fv uri) Feature Version Uri: "+fvUri+"\n\n",1);
		return fvUri;
	}
	
//	private void executeValidation() {
//		
//		Set<MFeature> authorityFeatures = this.authority.getAuthorityFeatures().keySet();
//		
//		for (MFeature feature : authorityFeatures) {
//			
//			UDebug.print("\t * feature "+ feature.getUriID() +"",dbgLevel+2);
//			
//			String fv1Uri = feature.generateGeneralFeatureVesionUri("1");
//			String fv2Uri = feature.generateGeneralFeatureVesionUri("2");
//			
//			MFeatureVersion fv1 = (MFeatureVersion) foundation.retrieveByUri(fv1Uri, lowestTGraph, 0, MFeatureVersion.class);
//			
//			MFeatureVersion fv2 = (MFeatureVersion) foundation.retrieveByUri(fv2Uri, lowestTGraph, 0, MFeatureVersion.class);
//			validation.validateTrustworthiness(fv1, fv2, lowestTGraph);
//			
//			fv2 = (MFeatureVersion) foundation.retrieveByUri(fv2Uri, averageTGraph, 0, MFeatureVersion.class);
//			validation.validateTrustworthiness(fv1, fv2, averageTGraph);
//			
//			fv2 = (MFeatureVersion) foundation.retrieveByUri(fv2Uri, highestTGraph, 0, MFeatureVersion.class);
//			validation.validateTrustworthiness(fv1, fv2, highestTGraph);
//		}
//	}	
	
}




