package controller;

import model.MFeatureVersion;

public abstract class CMainFactor {

	public CMainFactor() {	}
	
	public abstract boolean computeTW(MFeatureVersion featureVersion);	

}
