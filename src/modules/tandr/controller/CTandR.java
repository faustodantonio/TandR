package modules.tandr.controller;

import java.util.ArrayList;

import utility.UConfig;
import utility.UDebug;
import model.MFeatureVersion;
import modules.tandr.foundation.FTandrFacade;
import modules.tandr.model.MReputationTandr;
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
		
		MTrustworthinessTandr trustworthiness = new MTrustworthinessTandr( featureVersion );
		
		double trustValue = 0.0;
		trustValue = trustValue + (dirEffectWeight  * trustworthiness.getDirectEffect().calculateTrustworthiness(featureVersion)) ;
		trustValue = trustValue + (indEffectWeight  * trustworthiness.getIndirectEffect().calculateTrustworthiness(featureVersion));
		trustValue = trustValue + (tempEffectWeight * trustworthiness.getTemporalEffect().calculateTrustworthiness(featureVersion));
		trustworthiness.setValue(trustValue);
		
		//saveTrustworthiness
		ffacade.create(trustworthiness, tandrGraphUri);
		//debug Trustworthiness
		UDebug.print("\n\n" + ffacade.convertToRDFTTL(trustworthiness),4);
		UDebug.log("\nCREATE Trustworthiness (new): " + trustworthiness.getUri() ,4);
		
		//update Reputation
		this.updateUserReputation(featureVersion);
		
		//expand confirmation
		ArrayList<String> neighbours = ffacade.retrieveFVPreviousesNeighbours(featureVersion, vgihGraphUri, featureVersion.getGeometryBuffer());
		System.out.print(neighbours.size());
		for ( String neighbour : neighbours)
			this.confirm((MFeatureVersion) ffacade.retrieveByUri(neighbour, UConfig.getVGIHGraphURI(), 1, MFeatureVersion.class),featureVersion)
			;
		
		return result;
	}
	
	public boolean confirm(MFeatureVersion fvToConfirm, MFeatureVersion fvConfirmer) {
		boolean result = true;
		
		MTrustworthinessTandr trustworthiness = (MTrustworthinessTandr) fvToConfirm.getTrustworthiness();
		if (trustworthiness == null)
			trustworthiness = new MTrustworthinessTandr(fvToConfirm);
		double trustValue = trustworthiness.getValue();
		
		trustValue = trustValue + (dirEffectWeight  * trustworthiness.getDirectEffect().getValue()) ;
		trustValue = trustValue + (indEffectWeight  * trustworthiness.getIndirectEffect().confirmTrustworthiness(fvToConfirm,fvConfirmer.getIsValidFromString()) );
		trustValue = trustValue + (tempEffectWeight * trustworthiness.getTemporalEffect().confirmTrustworthiness(fvToConfirm)); 
		
		trustworthiness.setValue(trustValue);
		trustworthiness.setComputedAt(fvConfirmer.getIsValidFrom());
		
		ffacade.create(trustworthiness);
		UDebug.log("\nCREATE Trustworthiness (cnf): " + trustworthiness.getUri() ,4);
//		this.updateUserReputation(fvToConfirm);
		
		return result;
	}

	public boolean updateUserReputation(MFeatureVersion featureVersion) {
		boolean result = true;
		
		MReputationTandr reputation = (MReputationTandr) featureVersion.getAuthor().getReputation();
		if (reputation == null)
			reputation = new MReputationTandr(featureVersion.getAuthor());
		
		double repValue = 0.0;
		
		repValue = repValue + (dirEffectWeight  * reputation.getDirectEffect().calculateReputation(featureVersion.getAuthor(),featureVersion.getIsValidFromString())) ;
		repValue = repValue + (indEffectWeight  * reputation.getIndirectEffect().calculateReputation(featureVersion.getAuthor(),featureVersion.getIsValidFromString()));
		repValue = repValue + (tempEffectWeight * reputation.getTemporalEffect().calculateReputation(featureVersion.getAuthor(),featureVersion.getIsValidFromString())); 
		
		reputation.setValue(repValue);
		reputation.setComputedAt(featureVersion.getIsValidFrom());
		
		UDebug.print("\n\n" + ffacade.convertToRDFTTL(reputation),4);
		
		ffacade.create(reputation);
		UDebug.log("\nCREATE Reputation (new): " + reputation.getUri() ,4);
		
		return result;
	}

}
