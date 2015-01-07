package modules.tandr.controller;

import utility.UConfig;
import model.MFeatureVersion;
import modules.tandr.foundation.FTandrFacade;
import modules.tandr.model.MTrustworthinessTandr;
import controller.validation.CValidationAbstract;

public class CValidationTandR extends CValidationAbstract {

	private FTandrFacade foundation;
	
	
	
	public CValidationTandR() {
		this.foundation = new FTandrFacade();
	}

	@Override
	public boolean validateTrustworthiness(MFeatureVersion fv_official,	MFeatureVersion fv2, String graph) {
//		MTrustworthinessTandr trustworthiness = new MTrustworthinessTandr( fv2 );
//		MTrustworthinessTandr trustworthiness = (MTrustworthinessTandr) foundation.retrieveByUri(fv2.generateTrustworthinessUri(), UConfig.getVGIHGraphURI(), 0, MTrustworthinessTandr.class);
		MTrustworthinessTandr trustworthiness = (MTrustworthinessTandr) fv2.getTrustworthiness();
		
		double dirEffectWeight  = 1;
		double indEffectWeight  = 0;
		double tempEffectWeight = 0;
		
//		double dirEffectWeight  = 0.3333333;
//		double indEffectWeight  = 0.3333333;
//		double tempEffectWeight = 0.3333333;
		double trustValue = 0.0;
			
		trustValue = trustValue + (dirEffectWeight  * trustworthiness.getDirectEffect().validateTrustworthiness(fv_official, fv2)) ;
//		trustValue = trustValue + (indEffectWeight  * trustworthiness.getIndirectEffect().validateTrustworthiness(fv1, fv2));
//		trustValue = trustValue + (tempEffectWeight * trustworthiness.getTemporalEffect().validateTrustworthiness(fv1, fv2));
		
		trustworthiness.setValue(trustValue);
		trustworthiness.setComputedAt(fv2.getIsValidFrom());
		
		foundation.create(trustworthiness, graph);
		
		return true;
	}

}
