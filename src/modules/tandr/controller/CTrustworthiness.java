package modules.tandr.controller;

import java.util.ArrayList;

import utility.UDebug;
import model.MFeatureVersion;
import model.MTrustworthiness;
import modules.tandr.model.MFDirectEffect;
import modules.tandr.model.MFEffect;
import modules.tandr.model.MFIndirectEffect;
import modules.tandr.model.MFTemporalEffect;
import controller.CMainFactor;
import foundation.FFoundationFacade;

public class CTrustworthiness extends CMainFactor{

	private FFoundationFacade ffacade;
	
	private MFEffect directEff;
	private MFEffect indirectEff;
	private MFEffect temporalEff;
	
	private static double dirEffectWeight = 0.33;
	private static double indEffectWeight = 0.33;
	private static double tempEffectWeight = 0.33;
	
	public CTrustworthiness() {
		
		ffacade = new FFoundationFacade();
		
		this.directEff   = new MFDirectEffect();
		this.indirectEff = new MFIndirectEffect();
		this.temporalEff = new MFTemporalEffect();
		
	}

	@Override
	public boolean computeTW(MFeatureVersion featureVersion) {
		boolean result = true;
		
		MTrustworthiness trustworthiness = featureVersion.getTrustworthiness();
		double trustValue = 0.0;
		
		trustValue = trustValue + (dirEffectWeight  * directEff.calculate(featureVersion)) ;
		trustValue = trustValue + (indEffectWeight  * indirectEff.calculate(featureVersion));
		trustValue = trustValue + (tempEffectWeight * temporalEff.calculate(featureVersion));
		
		trustworthiness.setValue(trustValue);
		trustworthiness.setComputedAt(featureVersion.getIsValidFrom());
		
		//debug Trustworthiness
		UDebug.print("\n\n" + ffacade.convertToRDF(trustworthiness),3);
		
		//saveTrustworthiness
//		ffacade.create(trustworthiness);
//		
//		//expand confirmation
//		ArrayList<String> neighbours = ffacade.retrieveFVPreviousesNeighbours(featureVersion, featureVersion.getGeometryBuffer());
//		for ( String neighbour : neighbours)
//			this.confirm((MFeatureVersion) ffacade.retrieveByUri(neighbour, 1, MFeatureVersion.class),featureVersion);
//		
//		//update Reputation
//		this.updateUserReputation(featureVersion);
		
		return result;
	}
	
	public boolean confirm(MFeatureVersion fvToConfirm, MFeatureVersion fvConfirmer) {
		boolean result = true;
		
			//TODO: Implement CONFIRMATION logic
		
		MTrustworthiness trustworthiness = fvToConfirm.getTrustworthiness();
		double trustValue = 0.0;

		trustValue = trustValue + (dirEffectWeight  * directEff.getValue());
		trustValue = trustValue + (indEffectWeight  * indirectEff.calculate(fvToConfirm));
		trustValue = trustValue + (tempEffectWeight * temporalEff.getValue());
		
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
