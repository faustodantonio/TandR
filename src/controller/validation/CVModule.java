package controller.validation;

import java.util.HashSet;
import java.util.Set;

import utility.UConfig;
import utility.UDebug;
import foundation.FFoundationFacade;
import model.MFeature;
import model.MFeatureVersion;

public class CVModule {
	
	private int dbgLevel = 100;

	private Set<MFeature> features;
	private FFoundationFacade foundation;
	
	public CVModule() {
		super();

		this.foundation = new FFoundationFacade();
		features = new HashSet<MFeature>();
	}

	public void featurePopulateLowerGraph(String graph) {
		for (MFeature feature : features) {
			UDebug.print(foundation.convertToRDFTTL(feature) , dbgLevel);
			MFeatureVersion version = this.foundation.retrieveLowestTrustworthyFV(feature.getUri(), UConfig.getVGIHGraphURI(), UConfig.getTANDRGraphURI());
			
			//** Update feature version information			
			version.setVersionNo("2");
			version.setPrevFVersionUri(null);
			version.setIsValidFrom(UConfig.getMinDateTime());
			version.setIsValidTo(UConfig.getMaxDateTime());
			version.setUri(version.generateUri());
			
			UDebug.print("\n\n" + foundation.convertToRDFTTL(version), dbgLevel);
			
			foundation.create(version, graph);
		}
	}
	
	public void featurePopulateAverageGraph(String graph) {
		for (MFeature feature : features) {
			UDebug.print(foundation.convertToRDFTTL(feature) , dbgLevel);
			MFeatureVersion version = this.foundation.retrieveAverageTrustworthyFV(feature.getUri(), UConfig.getVGIHGraphURI(), UConfig.getTANDRGraphURI());
			
			version.setVersionNo("2");
			version.setPrevFVersionUri(null);
			version.setIsValidFrom(UConfig.getMinDateTime());
			version.setIsValidTo(UConfig.getMaxDateTime());
			version.setUri(version.generateUri());
			
			foundation.create(version, graph);
		}
	}
	
	public void featurePopulateHigherGraph(String graph) {
		for (MFeature feature : features) {
			UDebug.print(foundation.convertToRDFTTL(feature) , dbgLevel);
			MFeatureVersion version = this.foundation.retrieveHighestTrustworthyFV(feature.getUri(), UConfig.getVGIHGraphURI(), UConfig.getTANDRGraphURI());
			
			version.setVersionNo("2");
			version.setPrevFVersionUri(null);
			version.setIsValidFrom(UConfig.getMinDateTime());
			version.setIsValidTo(UConfig.getMaxDateTime());
			version.setUri(version.generateUri());
			
			foundation.create(version, graph);
		}
	}

	public Set<MFeature> getFeatures() {
		return features;
	}

	public void setFeatures(Set<MFeature> authorityFeatures) {
		this.features = authorityFeatures;
	}
}
