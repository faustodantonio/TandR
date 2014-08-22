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

//	private Map<MFEffect, Double> effects;
//	private Map<String,Double> effectsHierarchy;

	private SimpleDateFormat sdf;
	
	public MTrustworthiness() {
//		this.effects = new HashMap<MFEffect, Double>();
//		this.buildEffectHierarchy();
		this.sdf = UConfig.sdf;
	}
	
	public String toString(String rowPrefix)
	{
		String trustworthinessString = "";
		//TODO: implement conversion from MTrustworthiness to String
		return trustworthinessString;
	}
	
	public boolean compute()
	{
		boolean result = true;
//		Double trustworthiness = 0.0;
		
		//TODO: check and implement the logic of the trustworthiness compute method
//		for (Entry<MFEffect, Double> effect : this.effects.entrySet())
//			trustworthiness = trustworthiness + effect.getValue() * effect.getKey().updateValue(featureVersion,prevNeighborsFV);
		
		return result;
	}

//	public void buildEffectHierarchy(){
//		UDebug.print("\t effect hierarchy elements: "+UConfig.effects_hierarchy.entrySet().size() +"\n", 10);
//		for (  Entry<Entry<String, Map<String, Double>>, Double> config_effect: UConfig.effects_hierarchy.entrySet()) {
//			Double effect_weight = config_effect.getValue();
//			MFEffect effect = null;
//			String effect_str = "model.factor." + config_effect.getKey().getKey();
//			try {
//				effect = (MFEffect) Class.forName(effect_str).newInstance();
//			} catch (InstantiationException e) {
//				effect = null;
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				effect = null;
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				effect = null;
//				e.printStackTrace();
//			}
//			if (effect instanceof MFDependent) 
//				for (Entry<String, Double> aspect : config_effect.getKey().getValue().entrySet())
//					((MFDependent)effect).addAspect(aspect.getKey(),aspect.getValue());
//			effects.put(effect, effect_weight);
//		}	
//	}
	
	public MFeatureVersion getFeatureVersion() {
		return featureVersion;
	}
	public void setFeatureVersion(MFeatureVersion featureVersion) {
		this.featureVersion = featureVersion;
	}	
//	public Map<MFEffect, Double> getEffects() {
//		if (this.effects.isEmpty())
//			this.buildEffectHierarchy();
//		return effects;
//	}
//	public void setEffects(Map<MFEffect, Double> effects) {
//		this.effects = effects;
//	}

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
