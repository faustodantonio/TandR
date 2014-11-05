package modules.tandr.controller;

import java.util.ArrayList;

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
		ArrayList<MFeatureVersion> prevVersions = new ArrayList<MFeatureVersion>();
		
		if ( featureVersion.isFirst() ) {
			trustworthiness = this.calculateFirstVersionTrustworthiness(featureVersion);
		} else {
			prevVersions = feature.getPreviousVersions(featureVersion.getVersionNo(), 0);			
			trustworthiness = this.calculateSuccVersionTrustworthiness(featureVersion, prevVersions);
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
			this.updatePrevVersionsTrustworthiness(prevVersions, featureVersion);
		this.confirmNeighbours(featureVersion, vgihGraphUri);
		
		return result;
	}

	private MTrustworthinessTandr calculateFirstVersionTrustworthiness(MFeatureVersion featureVersion) {
		
		MTrustworthinessTandr trustworthiness = new MTrustworthinessTandr( featureVersion );
//		MTrustworthinessTandr trustworthiness = (MTrustworthinessTandr) featureVersion.getTrustworthiness();
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

	private MTrustworthinessTandr calculateSuccVersionTrustworthiness(MFeatureVersion featureVersion, ArrayList<MFeatureVersion> prevVersions) {
		
		MTrustworthinessTandr trustworthiness = new MTrustworthinessTandr( featureVersion );
//		MTrustworthinessTandr trustworthiness = (MTrustworthinessTandr) featureVersion.getTrustworthiness();
		
		double trustValue = 0.0;
		
		trustValue = trustValue + (dirEffectWeight  * trustworthiness.getDirectEffect().calculateTrustworthiness(prevVersions,featureVersion)) ;
//		At insertion moment (featureVersion.getIsValidFrom()), featureVersion trustworthiness indirect and temporal values are 0.0
		trustValue = trustValue + (indEffectWeight  * trustworthiness.getIndirectEffect().calculateTrustworthiness(featureVersion,featureVersion.getIsValidFrom()));
		trustValue = trustValue + (tempEffectWeight * trustworthiness.getTemporalEffect().calculateTrustworthiness(featureVersion,featureVersion.getIsValidFrom()));
		
		trustworthiness.setValue(trustValue);
		trustworthiness.setComputedAt(featureVersion.getIsValidFrom());
		
		return trustworthiness;
	}

	public boolean updateUserReputation(MFeatureVersion featureVersion, String untilDate) {
		boolean result = true;
		
		MReputationTandr reputation;
		if ( featureVersion.getAuthor().getReputation() == null && (featureVersion.getAuthor().getReputationUri() == null || featureVersion.getAuthor().getReputationUri() .equals("")) ) 
			reputation = new MReputationTandr( featureVersion.getAuthor() );
		else reputation = (MReputationTandr) featureVersion.getAuthor().getReputation();
		
//		UDebug.print("\n\n" + ffacade.convertToRDFTTL(reputation),10);
		
//		double repValue = 0.0;
//		repValue = repValue + (dirEffectWeight  * reputation.getDirectEffect().calculateReputation(featureVersion.getAuthor(),untilDate)) ;
//		repValue = repValue + (indEffectWeight  * reputation.getIndirectEffect().calculateReputation(featureVersion.getAuthor(),untilDate));
//		repValue = repValue + (tempEffectWeight * reputation.getTemporalEffect().calculateReputation(featureVersion.getAuthor(),untilDate));
//		reputation.setValue(repValue);
//		reputation.setComputedAt(untilDate);
		
		reputation = ffacade.getCalculatedReputation(featureVersion.getAuthorUri(), untilDate, true);
		reputation.setAuthor(featureVersion.getAuthor());
		
		this.debugRep(reputation);
		
//		UDebug.print("\n\n" + ffacade.convertToRDFXML(reputation),10);
		
		ffacade.create(reputation, UConfig.getTANDRGraphURI());
		UDebug.log("\nCREATE REPUTATION (new): " + reputation.getUri() + " - validity time: " + reputation.getComputedAtString(),10);
		UDebug.log("\n\t" + RView.getReputationString(featureVersion.getAuthor()) + "\n",10);
		
		return result;
	}
	

	//	save fvs Trustworthiness affected by direct evaluation 
	private void updatePrevVersionsTrustworthiness(ArrayList<MFeatureVersion> prevVersions, MFeatureVersion featureVersion) {
		
		//TODO: optimize: the avarage calculus must be done once per featureVersion
		
		int dbgLevel = 1;
		
		for ( MFeatureVersion fv : prevVersions) {			
			MTrustworthinessTandr trust;
			if ( fv.getTrustworthiness() == null && (fv.getTrustworthinessUri() == null || fv.getTrustworthinessUri().equals("")) ) 
				trust = new MTrustworthinessTandr( fv );
			else trust = (MTrustworthinessTandr) fv.getTrustworthiness();
			
			ArrayList<MFeatureVersion> complementaryVersions = new ArrayList<MFeatureVersion>();
			complementaryVersions.addAll(prevVersions);
			complementaryVersions.add(featureVersion);
			complementaryVersions.remove(fv);
			
			double trustValue = 0.0;
			trustValue = trustValue + (dirEffectWeight  * trust.getDirectEffect().calculateTrustworthiness(complementaryVersions,fv));
			trustValue = trustValue + (dirEffectWeight  * trust.getIndirectEffect().getValue());
			trustValue = trustValue + (dirEffectWeight  * trust.getTemporalEffect().calculateTrustworthiness(fv,featureVersion.getIsValidFrom()));
			
//			MFeatureVersion featureFirst = featureVersion.getFeature().getFirstVersion();
						
			trust.setValue(trustValue);
			trust.setComputedAt( featureVersion.getIsValidFromString() );
			
			UDebug.print("\t * feature version "+ fv.getUriID() +"",dbgLevel+2);
			UDebug.print("\t * author "+ fv.getAuthor().getAccountName() +"\n",dbgLevel+2);
			this.debugTrust(trust);
			
			ffacade.create(trust, UConfig.getTANDRGraphURI());
			UDebug.log("\nCREATE TRUSTWORTHINESS (upd): " + trust.getUri() + " - validity time: " + trust.getComputedAtString(),10);
			UDebug.log("\n\t" + TView.getTrustworthinessString(fv) + "\n",10);
			
			this.updateUserReputation(fv,featureVersion.getIsValidFromString());
		}
		
	}
	
	//	expand confirmation
	private void confirmNeighbours(MFeatureVersion featureVersion, String vgihGraphUri) {
		ArrayList<String> neighbours = ffacade.retrieveFVPreviousesNeighbours(featureVersion, vgihGraphUri, featureVersion.getGeometryBuffer());
		for ( String neighbour : neighbours)
			this.confirm((MFeatureVersion) ffacade.retrieveByUri(neighbour, UConfig.getVGIHGraphURI(), 1, MFeatureVersion.class),featureVersion);
	}

	public boolean confirm(MFeatureVersion fvToConfirm, MFeatureVersion fvConfirmer) {
		boolean result = true;
		
		MTrustworthinessTandr trustworthiness = (MTrustworthinessTandr) fvToConfirm.getTrustworthiness();
		if (trustworthiness == null)
			trustworthiness = new MTrustworthinessTandr(fvToConfirm);
		double trustValue = trustworthiness.getValue();
		
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

}
