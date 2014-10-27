package controller.validation;

import java.util.Set;

import controller.CCalculusAbstract;
import model.MFeature;
import model.MFeatureVersion;
import modules.tandr.controller.CValidationTandR;
import modules.tandr.model.MTrustworthinessTandr;
import foundation.FFoundationFacade;
import utility.UConfig;

public class CValidation {
	
	private FFoundationFacade foundation;
	private CVAuthority authority;
	private CVModule module;
	
	private CValidationAbstract validation;
	
	private String lowestTGraph  = UConfig.getLOWESTTGraphURI();
	private String averageTGraph = UConfig.getAVERAGETraphURI();
	private String highestTGraph = UConfig.getHIGHESTTGraphURI();

	public CValidation() {
		this.foundation = new FFoundationFacade();
		
		authority = new CVAuthority();
		module = new CVModule();
		
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
		this.arrangeVersions();
		this.executeValidation();
		
		return result;
	}
	
	/*************************
	 * 
	 * Graph Creation and Population FUNCTIONS
	 *
	 *************************/	

	private void checkValidationsGraphs() {
		
		boolean lowerTExists  = foundation.checkGraphExists(UConfig.lowerTGraph  , UConfig.graphURI);
		if (lowerTExists) 
			foundation.deleteGraph(UConfig.lowerTGraph   , "graphs");
		boolean averageTExists  = foundation.checkGraphExists(UConfig.averageTGraph  , UConfig.graphURI);
		if (averageTExists) 
			foundation.deleteGraph(UConfig.averageTGraph   , "graphs");
		boolean higherExists  = foundation.checkGraphExists(UConfig.higherTGraph  , UConfig.graphURI);
		if (higherExists) 
			foundation.deleteGraph(UConfig.higherTGraph   , "graphs");

		foundation.createGraph(UConfig.lowerTGraph  , "graphs");
		foundation.createGraph(UConfig.averageTGraph  , "graphs");
		foundation.createGraph(UConfig.higherTGraph  , "graphs");
	}
	
	/**
	 * Check and create graphs on triplestore
	 * Populate graphs with
	 * 		The authoritie's author with maximum reputation 
	 * 		The authoritie's features (with no reputation)
	 * 		
	 * 		The featureVersions with the desired trustworthiness
	 */
	private void arrangeVersions() {
		
		this.authority.authorPopulateGraph(lowestTGraph);
		this.authority.authorPopulateGraph(averageTGraph);
		this.authority.authorPopulateGraph(highestTGraph);
		
		this.authority.featurePopulateGraph(lowestTGraph);
		this.authority.featurePopulateGraph(averageTGraph);
		this.authority.featurePopulateGraph(highestTGraph);
		
		Set<MFeature> authorityFeatures = this.authority.getAuthorityFeatures().keySet();
		
		this.module.setFeatures(authorityFeatures);
		
		this.module.featurePopulateLowerGraph(lowestTGraph);
		this.module.featurePopulateAverageGraph(averageTGraph);
		this.module.featurePopulateHigherGraph(highestTGraph);
	}

	/*************************
	 * 
	 * Validation FUNCTIONS
	 *
	 *************************/	
	
	private void executeValidation() {
		
		Set<MFeature> authorityFeatures = this.authority.getAuthorityFeatures().keySet();
		
		for (MFeature feature : authorityFeatures) {
			
			MFeatureVersion fv1 = (MFeatureVersion) foundation.retrieveByUri(feature.generateFeatureVesionUri("1"), lowestTGraph, 0, MFeatureVersion.class);
			
			MFeatureVersion fv2 = (MFeatureVersion) foundation.retrieveByUri(feature.generateFeatureVesionUri("2"), lowestTGraph, 0, MFeatureVersion.class);
			validation.validateTrustworthiness(fv1, fv2, lowestTGraph);
			
			fv2 = (MFeatureVersion) foundation.retrieveByUri(feature.generateFeatureVesionUri("2"), averageTGraph, 0, MFeatureVersion.class);
			validation.validateTrustworthiness(fv1, fv2, averageTGraph);
			
			fv2 = (MFeatureVersion) foundation.retrieveByUri(feature.generateFeatureVesionUri("2"), highestTGraph, 0, MFeatureVersion.class);
			validation.validateTrustworthiness(fv1, fv2, highestTGraph);
		}
	}	
	
}




