package controller;

import model.MFeatureVersion;

public abstract class CCalculusAbstract {

	public CCalculusAbstract() {	}
	
	public abstract boolean computeTW(MFeatureVersion featureVersion);
	public abstract boolean updateLastFeatureVersion(MFeatureVersion featureVersion);	

}
