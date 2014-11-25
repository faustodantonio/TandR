package modules.tandr.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import utility.UConfig;
import utility.UDebug;
import model.MFeature;
import model.MFeatureVersion;
import modules.tandr.foundation.FTandrFacade;
import modules.tandr.model.MReputationTandr;
import modules.tandr.model.MTrustworthinessTandr;
import modules.tandr.view.VReputationTandr;
import modules.tandr.view.VTrustworthinessTandr;
import controller.CCalculusAbstract;

public class CTandR extends CCalculusAbstract{

	private FTandrFacade ffacade;
	private VTrustworthinessTandr TView = new VTrustworthinessTandr();
	private VReputationTandr RView = new VReputationTandr();
		
	private static double dirEffectWeight  = 0.3333333;
	private static double indEffectWeight  = 0.3333333;
	private static double tempEffectWeight = 0.3333333;
	
//	private static double dirEffectWeight  = 0.5;
//	private static double tempEffectWeight = 0.5;
	
	public CTandR() {
		
		ffacade = new FTandrFacade();
		
	}

	@Override
	public boolean computeTW(MFeatureVersion featureVersion) {
		boolean result = true;
		int dbgLevel = 100;
		
		String tandrGraphUri = UConfig.getTANDRGraphURI();
		String vgihGraphUri = UConfig.getVGIHGraphURI();
		
		MTrustworthinessTandr trustworthiness;
		
		MFeature feature = featureVersion.getFeature();
		ArrayList<MFeatureVersion> neighborVersions = new ArrayList<MFeatureVersion>();
		ArrayList<MFeatureVersion> prevVersions = new ArrayList<MFeatureVersion>();
		Map<String,Double> prevAverages = new HashMap<String, Double>();
		
		neighborVersions = this.getNeighboursUris(featureVersion, vgihGraphUri);
		
		if ( featureVersion.isFirst() ) {
			trustworthiness = this.calculateFirstVersionTrustworthiness(featureVersion);
		} else {
//			prevVersions = feature.getPreviousVersions(featureVersion.getVersionNo(), 0);			
//			prevAverages = this.calculateWeightedAvgsMap(prevVersions);
			prevVersions = feature.getCleanedPreviousVersions(featureVersion, 0);
			prevAverages = this.calculateAvgsMap(prevVersions);
//			prevNeighRelations = this
			trustworthiness = this.calculateSuccVersionTrustworthiness(featureVersion, prevAverages, prevVersions, neighborVersions);
		}
		
		this.debugTrust(trustworthiness);
		
		//save and debug Trustworthiness
		ffacade.create(trustworthiness, tandrGraphUri);
		
//		UDebug.print("\n\n" + ffacade.convertToRDFTTL(trustworthiness),4);
		UDebug.log("\nCREATE TRUSTWORTHINESS (new): " + trustworthiness.getUri() + " - validity time: " + trustworthiness.getComputedAtString(),dbgLevel);
		UDebug.log("\n\t" + TView.getTrustworthinessString(featureVersion) + "\n",dbgLevel);
		
		//update Reputation
		this.updateUserReputation(featureVersion,featureVersion.getIsValidFromString());
		
		//propagate to dependencies
		if ( ! featureVersion.isFirst() )
			this.updatePrevVersionsTrustworthiness(prevVersions, neighborVersions, prevAverages, featureVersion);
		this.confirmNeighbours(neighborVersions, featureVersion, vgihGraphUri);
		
		return result;
	}

	private ArrayList<MFeatureVersion> getNeighboursUris(MFeatureVersion featureVersion, String vgihGraphUri) {
		Map<String,Map<String,MFeatureVersion>> cleanedMap = new HashMap<String, Map<String,MFeatureVersion>>();
		ArrayList<MFeatureVersion> neighborVersions = new ArrayList<MFeatureVersion>();
		ArrayList<String> neighboursUris = ffacade.retrieveFVPreviousesNeighbours(featureVersion, vgihGraphUri, featureVersion.getGeometryBuffer());
		
		for ( String neighbourUri : neighboursUris) {
			MFeatureVersion neighbour = (MFeatureVersion) ffacade.retrieveByUri(neighbourUri, UConfig.getVGIHGraphURI(), 1, MFeatureVersion.class);
			
			Map<String,MFeatureVersion> authorFVS = new HashMap<String, MFeatureVersion>();
			
			if ( cleanedMap.containsKey(neighbour.getAuthorUri()) )
				authorFVS = cleanedMap.get(neighbour.getAuthorUri());
			else cleanedMap.put(neighbour.getAuthorUri(), authorFVS);
			
			if ( authorFVS.containsKey(neighbour.getFeatureUri()) ) {
				if( MFeature.compareVersions( authorFVS.get(neighbour.getFeatureUri()).getVersionNo(), neighbour.getVersionNo()) == -1 ) {
					neighborVersions.remove( authorFVS.get(neighbour.getFeatureUri()) );
					authorFVS.put(neighbour.getFeatureUri(), neighbour);
				}
			}
			else authorFVS.put(neighbour.getFeatureUri(), neighbour);
			
			if(! featureVersion.getAuthorUri().equals(neighbour.getAuthorUri()))
				neighborVersions.add( neighbour );			
			
		}
		return neighborVersions;
	}
	
//		Map<String,Map<String,MFeatureVersion>> cleanedMap = new HashMap<String, Map<String,MFeatureVersion>>();
//		
//		ArrayList<MFeatureVersion> versions = new ArrayList<MFeatureVersion>();
//		Iterator<Entry<String, String>> verIterator = versionsByVersion.entrySet().iterator();
//		Entry<String, String> versionEntry;
//		
//		boolean beforeLimit = true;
//		while ( verIterator.hasNext() && beforeLimit) {
//			versionEntry = verIterator.next();
//			
//			if (compareVersions(version,versionEntry.getKey()) > 0 ) {
//				MFeatureVersion fvEntry = this.getFeatureVersionByVersion(versionEntry.getKey(), 0);
//				
//				Map<String,MFeatureVersion> authorFVS = new HashMap<String, MFeatureVersion>();
//				
//				if ( cleanedMap.containsKey(fvEntry.getAuthorUri()) )
//					authorFVS = cleanedMap.get(fvEntry.getAuthorUri());
//				else cleanedMap.put(fvEntry.getAuthorUri(), authorFVS);
//				
//				if ( authorFVS.containsKey(fvEntry.getFeatureUri()) ) {
//					if( compareVersions( authorFVS.get(fvEntry.getFeatureUri()).getVersionNo(), fvEntry.getVersionNo()) == -1 ) {
//						versions.remove( authorFVS.get(fvEntry.getFeatureUri()) );
//						authorFVS.put(fvEntry.getFeatureUri(), fvEntry);
//					}
//				}
//				else authorFVS.put(fvEntry.getFeatureUri(), fvEntry);
//				
//				if(! fversion.getAuthorUri().equals(fvEntry.getAuthorUri()))
//					versions.add( fvEntry );
//			}
//			else beforeLimit = false;
//		}

	private MTrustworthinessTandr calculateFirstVersionTrustworthiness(MFeatureVersion featureVersion) {
		
		MTrustworthinessTandr trustworthiness = (MTrustworthinessTandr) featureVersion.getTrustworthiness();
		MReputationTandr reputation = (MReputationTandr) featureVersion.getAuthor().getReputation();
		
		trustworthiness.getDirectEffect().getGeometricAspect().setValue(   reputation.getDirectEffect().getGeometricAspect().getValue()   );
		trustworthiness.getDirectEffect().getQualitativeAspect().setValue( reputation.getDirectEffect().getQualitativeAspect().getValue() );
		trustworthiness.getDirectEffect().getSemanticAspect().setValue(    reputation.getDirectEffect().getSemanticAspect().getValue()    );
		trustworthiness.getDirectEffect().setValue( reputation.getDirectEffect().getValue() );
		
		trustworthiness.getIndirectEffect().getGeometricAspect().setValue(   reputation.getIndirectEffect().getGeometricAspect().getValue()   );
		trustworthiness.getIndirectEffect().getQualitativeAspect().setValue( reputation.getIndirectEffect().getQualitativeAspect().getValue() );
		trustworthiness.getIndirectEffect().getSemanticAspect().setValue(    reputation.getIndirectEffect().getSemanticAspect().getValue()    );
		trustworthiness.getIndirectEffect().setValue( reputation.getIndirectEffect().getValue() );
		
//		trustworthiness.getTemporalEffect().setValue( reputation.getTemporalEffect().getValue() );
		trustworthiness.getTemporalEffect().calculateTrustworthiness(featureVersion,featureVersion.getIsValidFrom());
		
		trustworthiness.setValue( reputation.getValue() );
		trustworthiness.setComputedAt(featureVersion.getIsValidFrom());
		
		return trustworthiness;
	}

	private MTrustworthinessTandr calculateSuccVersionTrustworthiness(MFeatureVersion featureVersion, Map<String,Double> averages, ArrayList<MFeatureVersion> prevVersions, ArrayList<MFeatureVersion> neighbors) {
		
		MTrustworthinessTandr trustworthiness = (MTrustworthinessTandr) featureVersion.getTrustworthiness();
		
		double trustValue = 0.0;
		
		trustValue = trustValue + (dirEffectWeight  * trustworthiness.getDirectEffect().calculateTrustworthiness(prevVersions, neighbors, averages, featureVersion)) ;
//		At insertion moment (featureVersion.getIsValidFrom()), featureVersion trustworthiness indirect and temporal values are 0.0
		trustValue = trustValue + (indEffectWeight  * trustworthiness.getIndirectEffect().calculateTrustworthiness(featureVersion,featureVersion.getIsValidFrom()));
		trustValue = trustValue + (tempEffectWeight * trustworthiness.getTemporalEffect().calculateTrustworthiness(featureVersion,featureVersion.getIsValidFrom()));
		
		trustworthiness.setValue(trustValue);
		trustworthiness.setComputedAt(featureVersion.getIsValidFrom());
		
		return trustworthiness;
	}

	public boolean updateUserReputation(MFeatureVersion featureVersion, String untilDate) {
		boolean result = true;
		
		MReputationTandr reputation = (MReputationTandr) featureVersion.getAuthor().getReputation();
		
//		UDebug.print("\n\n" + ffacade.convertToRDFTTL(reputation),10);
		
		reputation = ffacade.getCalculatedReputation(featureVersion.getAuthorUri(), untilDate, true);
		reputation.setAuthor(featureVersion.getAuthor());
		featureVersion.getAuthor().setReputation(reputation);
		
		this.debugRep(reputation);
		
//		UDebug.print("\n\n" + ffacade.convertToRDFXML(reputation),10);
		
		ffacade.create(reputation, UConfig.getTANDRGraphURI());
		UDebug.log("\nCREATE REPUTATION (new): " + reputation.getUri() + " - validity time: " + reputation.getComputedAtString(),10);
		UDebug.log("\n\t" + RView.getReputationString(featureVersion.getAuthor()) + "\n",10);
		
		return result;
	}
	

	//	save fvs Trustworthiness affected by direct evaluation 
	private void updatePrevVersionsTrustworthiness(ArrayList<MFeatureVersion> prevVersions, ArrayList<MFeatureVersion> neighbors, Map<String,Double> averages, MFeatureVersion featureVersion) {
		
		int dbgLevel = 1;
		
		ArrayList<MFeatureVersion> completeVersions = new ArrayList<MFeatureVersion>();
		completeVersions.addAll(prevVersions);
		completeVersions.add(featureVersion);
		
		for ( MFeatureVersion fv : prevVersions) {	
			if( ! fv.getAuthorUri().equals(featureVersion.getAuthorUri())) {
				MTrustworthinessTandr trust = (MTrustworthinessTandr) fv.getTrustworthiness();
				
				double trustValue = 0.0;		
				
				trustValue = trustValue + (dirEffectWeight  * trust.getDirectEffect().calculateTrustworthiness(completeVersions, neighbors, averages,fv));
				trustValue = trustValue + (indEffectWeight  * trust.getIndirectEffect().getValue());
				trustValue = trustValue + (tempEffectWeight  * trust.getTemporalEffect().calculateTrustworthiness(fv,featureVersion.getIsValidFrom()));
	
				trust.setValue(trustValue);
				trust.setComputedAt( featureVersion.getIsValidFromString() );
				
				UDebug.print("\t * (UPD) * feature version "+ fv.getUriID() +"",dbgLevel+2);
				UDebug.print("\t * author "+ fv.getAuthor().getAccountName() +" * (UPD)\n",dbgLevel+2);
				this.debugTrust(trust);
				
				ffacade.create(trust, UConfig.getTANDRGraphURI());
				UDebug.log("\nCREATE TRUSTWORTHINESS (upd): " + trust.getUri() + " - validity time: " + trust.getComputedAtString(),10);
				UDebug.log("\n\t" + TView.getTrustworthinessString(fv) + "\n",10);
				
				this.updateUserReputation(fv,featureVersion.getIsValidFromString());
			}
		}
		
	}
	
	//	expand confirmation
	private void confirmNeighbours(ArrayList<MFeatureVersion> neighbours,MFeatureVersion featureVersion, String vgihGraphUri) {
//		ArrayList<String> neighbours = ffacade.retrieveFVPreviousesNeighbours(featureVersion, vgihGraphUri, featureVersion.getGeometryBuffer());
		for ( MFeatureVersion neighbour : neighbours)
			this.confirm(neighbour,featureVersion);
	}
	
	//	expand confirmation
//	private void confirmNeighbours(MFeatureVersion featureVersion, String vgihGraphUri) {
//		ArrayList<String> neighbours = ffacade.retrieveFVPreviousesNeighbours(featureVersion, vgihGraphUri, featureVersion.getGeometryBuffer());
//		for ( String neighbour : neighbours)
//			this.confirm((MFeatureVersion) ffacade.retrieveByUri(neighbour, UConfig.getVGIHGraphURI(), 1, MFeatureVersion.class),featureVersion);
//	}

	public boolean confirm(MFeatureVersion fvToConfirm, MFeatureVersion fvConfirmer) {
		boolean result = true;
		
		MTrustworthinessTandr trustworthiness = (MTrustworthinessTandr) fvToConfirm.getTrustworthiness();
//		if (trustworthiness == null)
////			trustworthiness = new MTrustworthinessTandr(fvToConfirm);
//			trustworthiness = (MTrustworthinessTandr) ffacade.retrieveByUri(fvToConfirm.generateTrustworthinessUri(), UConfig.getVGIHGraphURI(), 0, MTrustworthinessTandr.class);
//		double trustValue = trustworthiness.getValue();
		double trustValue = 0.0;
		
		trustValue = trustValue + (dirEffectWeight  * trustworthiness.getDirectEffect().getValue()) ;
		trustValue = trustValue + (indEffectWeight  * trustworthiness.getIndirectEffect().confirmTrustworthiness(fvToConfirm,fvConfirmer.getIsValidFromString()) );
		trustValue = trustValue + (tempEffectWeight * trustworthiness.getTemporalEffect().confirmTrustworthiness(fvToConfirm)); 
		
		trustworthiness.setValue(trustValue);
		trustworthiness.setComputedAt(fvConfirmer.getIsValidFrom());
		
		this.debugTrust(trustworthiness);
		
		ffacade.create(trustworthiness, UConfig.getTANDRGraphURI());
		UDebug.log("\nCREATE TRUSTWORTHINESS (cnf): " + trustworthiness.getUri() + " - validity time: " + trustworthiness.getComputedAtString(),4);
		UDebug.log("\n\t" + TView.getTrustworthinessString(fvToConfirm) + "\n",4);
//		this.updateUserReputation(fvToConfirm);
		
		return result;
	}

	private void debugTrust(MTrustworthinessTandr trustworthiness) {
		
		int dbgLevel = 1;
		UDebug.print("\t Trust :"+ trustworthiness.getValueString(), dbgLevel+2);
		
		UDebug.print("\t [ ", dbgLevel+3);
		trustworthiness.getDirectEffect().debugTDirectInfo(dbgLevel+3);
		trustworthiness.getIndirectEffect().debugTIndirectInfo(dbgLevel+3);
		trustworthiness.getTemporalEffect().debugTTemporalInfo(dbgLevel+3);
		UDebug.print(" ]", dbgLevel+3);
		
		UDebug.print("\n", dbgLevel+2);
	}
	
	private void debugRep(MReputationTandr reputation) {
		
		int dbgLevel = 1;
		UDebug.print("\t Rep   :"+ reputation.getValueString(), dbgLevel+2);
		
		UDebug.print("\t [ ", dbgLevel+3);
		reputation.getDirectEffect().debugRDirectInfo(dbgLevel+3);
		reputation.getIndirectEffect().debugRIndirectInfo(dbgLevel+3);
		reputation.getTemporalEffect().debugRTemporalInfo(dbgLevel+3);
		UDebug.print(" ]", dbgLevel+3);
		
		UDebug.print("\n", dbgLevel+2);
	}

	private Map<String,Double> calculateWeightedAvgsMap(ArrayList<MFeatureVersion> prevVersions) {
		
		Map<String,Double> averages = new HashMap<String, Double>();
		double totArea = 0.0, totPerimeter  = 0.0, totNoVertices = 0.0;
		double avgArea = 0.0, avgPerimeter  = 0.0, avgNoVertices = 0.0;
		
		double totReputations = 0.0;

		if (prevVersions.size() != 0) {
			
			for ( MFeatureVersion fv : prevVersions) {
				MReputationTandr reputation = (MReputationTandr) fv.getAuthor().getReputation();
				totArea       += fv.getGeometry().getArea() * reputation.getValue();
				totPerimeter  += fv.getGeometry().getLength() * reputation.getValue();
				totNoVertices += fv.getGeometry().getNumPoints() * reputation.getValue();
				totReputations += reputation.getValue();
			}
			
			if (totReputations != 0.0) {
				avgArea       = totArea       / totReputations;
				avgPerimeter  = totPerimeter  / totReputations;
				avgNoVertices = totNoVertices / totReputations;
			} else {
				avgArea       = 0.0;
				avgPerimeter  = 0.0;
				avgNoVertices = 0.0;
			}
			
		} else {
			avgArea       = 0.0;
			avgPerimeter  = 0.0;
			avgNoVertices = 0.0;
		}
		
		averages.put("avgArea", avgArea);
		averages.put("avgPerimeter", avgPerimeter);
		averages.put("avgNoVertices", avgNoVertices);
		return averages;
	}

	public Map<String,Double> calculateAvgsMap(ArrayList<MFeatureVersion> prevVersions) {
	
		Map<String,Double> averages = new HashMap<String, Double>();
		double totArea = 0.0, totPerimeter  = 0.0, totNoVertices = 0.0;
		double avgArea = 0.0, avgPerimeter  = 0.0, avgNoVertices = 0.0;
		
		if (prevVersions.size() != 0) {
			
			for ( MFeatureVersion fv : prevVersions) {
				totArea       += fv.getGeometry().getArea();
				totPerimeter  += fv.getGeometry().getLength();
				totNoVertices += fv.getGeometry().getNumPoints();
			}
			
			avgArea       = totArea       / prevVersions.size();
			avgPerimeter  = totPerimeter  / prevVersions.size();
			avgNoVertices = totNoVertices / prevVersions.size();
		} else {
			avgArea       = 0.0;
			avgPerimeter  = 0.0;
			avgNoVertices = 0.0;
		}
		
		averages.put("avgArea", avgArea);
		averages.put("avgPerimeter", avgPerimeter);
		averages.put("avgNoVertices", avgNoVertices);
		return averages;		
	}
	
}
