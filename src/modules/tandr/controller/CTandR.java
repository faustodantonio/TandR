package modules.tandr.controller;

import java.util.ArrayList;

import utility.UConfig;
import utility.UDebug;
import model.MFeatureVersion;
import modules.tandr.foundation.FTandrFacade;
import modules.tandr.model.MTrustworthinessTandr;
import controller.CCalculusAbstract;

public class CTandR extends CCalculusAbstract{

	private FTandrFacade ffacade;
		
	private static double dirEffectWeight = 0.33;
	private static double indEffectWeight = 0.33;
	private static double tempEffectWeight = 0.33;
	
	public CTandR() {
		
		ffacade = new FTandrFacade();
		
	}

	@Override
	public boolean computeTW(MFeatureVersion featureVersion) {
		boolean result = true;
		
		String tandrGraphUri = UConfig.getTANDRGraphURI();
		String vgihGraphUri = UConfig.getVGIHGraphURI();
		
		MTrustworthinessTandr trustworthiness = new MTrustworthinessTandr( featureVersion.getTrustworthiness(), tandrGraphUri );
		
		double trustValue = 0.0;
		trustValue = trustValue + (dirEffectWeight  * trustworthiness.getDirectEffect().calculate(featureVersion)) ;
		trustValue = trustValue + (indEffectWeight  * trustworthiness.getIndirectEffect().calculate(featureVersion));
		trustValue = trustValue + (tempEffectWeight * trustworthiness.getTemporalEffect().calculate(featureVersion));
		trustworthiness.setValue(trustValue);
		
		//saveTrustworthiness
		ffacade.create(trustworthiness, tandrGraphUri);
		
		//debug Trustworthiness
		UDebug.print("\n\n" + ffacade.convertToRDFTTL(trustworthiness),4);
		

		//expand confirmation
//		ArrayList<String> neighbours = ffacade.retrieveFVPreviousesNeighbours(featureVersion, featureVersion.getGeometryBuffer());
//		for ( String neighbour : neighbours)
//			this.confirm((MFeatureVersion) ffacade.retrieveByUri(neighbour, 1, MFeatureVersion.class),featureVersion);
		
		//update Reputation
//		this.updateUserReputation(featureVersion);
		
		return result;
	}
	
	public boolean confirm(MFeatureVersion fvToConfirm, MFeatureVersion fvConfirmer) {
		boolean result = true;
		
		//TODO: Implement CONFIRMATION logic
		
		MTrustworthinessTandr trustworthiness = new MTrustworthinessTandr( fvToConfirm.getTrustworthiness() );
		double trustValue = fvToConfirm.getTrustworthiness().getValue();
		
		trustValue = trustValue + (dirEffectWeight  * trustworthiness.getDirectEffect().getValue()) ;
		trustValue = trustValue + (indEffectWeight  * trustworthiness.getIndirectEffect().calculate(fvToConfirm));
		trustValue = trustValue + (tempEffectWeight * trustworthiness.getTemporalEffect().getValue()); // TODO :: update time too
		
		trustworthiness.setValue(trustValue);
		trustworthiness.setComputedAt(fvConfirmer.getIsValidFrom());
		
		ffacade.create(trustworthiness);
		return result;
	}

	public void updateUserReputation(MFeatureVersion featureVersion) {
		
		//TODO: implement user reputation calculation
		
//		MReputation reputation = featureVersion.getAuthor().getReputation();
	}

}
