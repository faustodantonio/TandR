package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import utility.UConfig;
import utility.UDebug;

public class MTrustworthiness {

	private String uri;
	private double value;
	private Date computedAt;
	
	private String featureVersionUri;
	private MFeatureVersion featureVersion;	

	private SimpleDateFormat sdf;
	
	public MTrustworthiness() {
		this.sdf = UConfig.sdf;
	}
	
	public MTrustworthiness(MFeatureVersion featureVersion) {
		this.sdf = UConfig.sdf;
		
		featureVersion.setTrustworthiness(this);
		this.setFeatureVersion(featureVersion);
	}
	
	public String toString(String rowPrefix)
	{
		String trustworthinessString = "";
		//TODO: implement conversion from MTrustworthiness to String
		return trustworthinessString;
	}
	
	public MFeatureVersion getFeatureVersion() {
		return featureVersion;
	}
	public void setFeatureVersion(MFeatureVersion featureVersion) {
		this.featureVersion = featureVersion;
		this.setUri(""+UConfig.graphURI + "Trustworthiness_" + UConfig.module_trustworthiness_calculus + "_" + featureVersion.getUriID());
		this.setComputedAt(featureVersion.getIsValidFromString());
	}	

	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	public Date getComputedAt() {
		return computedAt;
	}
	public void setComputedAt(Date isValidFrom) {
		this.computedAt = isValidFrom;
	}
    public void setComputedAt(String isValidFrom) {
    	try {
			this.computedAt = sdf.parse(isValidFrom);
		} catch (ParseException e) {
			UDebug.print("\n *** ERROR: IsValidFrom field not formatted\n",5);
			e.printStackTrace();	}
    }
	public String getComputedAtString(){
		String date = "";
		if (this.computedAt != null)
			date = this.sdf.format(this.computedAt);
		return date;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getFeatureVersionUri() {
		return featureVersionUri;
	}
	public void setFeatureVersionUri(String featureVersionUri) {
		this.featureVersionUri = featureVersionUri;
	}	
	
}
