package modules.tandr.controller;

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
		
		trustValue = trustValue + directEff.calculate(featureVersion);
		trustValue = trustValue + indirectEff.calculate(featureVersion);
		trustValue = trustValue + temporalEff.calculate(featureVersion);
		
		trustworthiness.setValue(trustValue);
		
		//saveTrustworthiness
		ffacade.create(trustworthiness);
		
		//expand confirmation 
		
		//update Reputation
		this.updateUserReputation(featureVersion);
		
		return result;
	}
	
	private boolean confirm(MFeatureVersion featureVersion) {
		boolean result = true;
		
			//TODO: Implement CONFIRMATION logic
		
		return result;
	}

	private void updateUserReputation(MFeatureVersion featureVersion) {
		
		//TODO: implement user reputation calculation
		
//		MReputation reputation = featureVersion.getAuthor().getReputation();
	}

}
