package controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import foundation.FFoundationFacade;
import utility.UConfig;
import utility.UDebug;
import model.MFeature;
import model.MFeatureVersion;
import modules.tandr.model.MFDependent;
import modules.tandr.model.MFEffect;

public class CTrustworthinessCalculus {

	FFoundationFacade ffactory;
	
	private Map<MFEffect, Double> effects;
//	private Map<String, Map<String,MFeatureVersion>> featureVersions;
	private Map<Date, ArrayList<MFeatureVersion>> featureVersionsByDate;
	private Map<String, MFeature> features;

	public CTrustworthinessCalculus()
	{
		this.effects = new HashMap<MFEffect, Double>();
		this.features = new HashMap<String, MFeature>();
//		this.featureVersions = new HashMap<String, Map<String,MFeatureVersion>>();
		this.featureVersionsByDate = new TreeMap<Date, ArrayList<MFeatureVersion>>();
		this.ffactory = new FFoundationFacade();
		this.buildEffectHierarchy();
	}
	
//	public Double computeTrustworthiness()
//	{
//		Double trustworthiness = 0.0;
//		
//		for (Entry<MFFactor, Double> effect : this.effects.entrySet())
//			trustworthiness = trustworthiness + effect.getValue() * effect.getKey().calculate();
//		
//		return trustworthiness;
//	}
	
	public void buildMaps(){
		this.buildEffectHierarchy();
		
		UDebug.print("\nEnter into buildFeatureMap()\n", 2);
		this.buildFeatureMap();
		UDebug.print("\nExit from buildFeatureMap()\n", 2);
//		this.buildFeatureVersionMap();
		UDebug.print("\nEnter into buildFeatureversionByDateMap()\n", 2);
		this.buildFeatureVersionByDateMap();
		UDebug.print("\nExit from buildFeatureversionByDateMap()\n", 2);
	}

	public void buildEffectHierarchy(){
		UDebug.print("\t effect hierarchy elements: "+UConfig.effects_hierarchy.entrySet().size() +"\n", 10);
		for (  Entry<Entry<String, Map<String, Double>>, Double> config_effect: UConfig.effects_hierarchy.entrySet()) {
			Double effect_weight = config_effect.getValue();
			MFEffect effect = null;
			String effect_str = "model.factor." + config_effect.getKey().getKey();
			try {
				effect = (MFEffect) Class.forName(effect_str).newInstance();
			} catch (InstantiationException e) {
				effect = null;
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				effect = null;
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				effect = null;
				e.printStackTrace();
			}
			if (effect instanceof MFDependent) 
				for (Entry<String, Double> aspect : config_effect.getKey().getValue().entrySet())
					((MFDependent)effect).addAspect(aspect.getKey(),aspect.getValue());
			effects.put(effect, effect_weight);
		}	
	}
	
	private void buildFeatureMap() {
		
		ArrayList<String> feature_uris = this.ffactory.retrieveAll("MFeature");
		
		UDebug.print("# of RDFFeatures: "+feature_uris.size(), 3);
		
		for (String uri : feature_uris)
		{
			this.features.put(uri, (MFeature) ffactory.retrieveByUri(uri, 0,"MFeature")); 
		}
	}
	
	private void buildFeatureVersionByDateMap() {
		
		for ( Entry<String, MFeature> f_entry : this.features.entrySet()) {
			for (Entry<String, MFeatureVersion> fv_entry : f_entry.getValue().getVersions().entrySet()) {
				
				MFeatureVersion fv_value = fv_entry.getValue();
				
				if(fv_value == null)
					fv_value = (MFeatureVersion) this.ffactory.retrieveByUri(fv_entry.getKey(), 0, "MFeatureVersion");
				
				Date keyDate = fv_value.getIsValidFrom();
				ArrayList<MFeatureVersion> fvsByDate = this.featureVersionsByDate.get( keyDate );
				
				if(fvsByDate == null){
					fvsByDate = new ArrayList<MFeatureVersion>();
					this.featureVersionsByDate.put(keyDate, fvsByDate);
				}
				
				fvsByDate.add(fv_value);
			}

		}
	}
	
	public Map<MFEffect, Double> getEffects() {
		if (this.effects.isEmpty())
			this.buildEffectHierarchy();
		return effects;
	}
	public void setEffects(Map<MFEffect, Double> effects) {
		this.effects = effects;
	}
	public Map<Date, ArrayList<MFeatureVersion>> getFeatureVersionsByDate() {
		if (this.featureVersionsByDate.isEmpty() )
			this.buildMaps();
		return featureVersionsByDate;
	}
	public void setFeatureVersionsByDate(
			Map<Date, ArrayList<MFeatureVersion>> featureVersionsByDate) {
		this.featureVersionsByDate = featureVersionsByDate;
	}
	public Map<String, MFeature> getFeatures() {
		if (this.features.isEmpty() )
			this.buildMaps();
		return features;
	}
	public void setFeatures(Map<String, MFeature> features) {
		this.features = features;
	}
}
