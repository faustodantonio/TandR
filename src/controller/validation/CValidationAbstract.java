package controller.validation;

import model.MFeatureVersion;

public abstract class CValidationAbstract {

	public CValidationAbstract() {	}
	
	public abstract boolean validateTrustworthiness(MFeatureVersion fv1, MFeatureVersion fv2, String graph);	

}
