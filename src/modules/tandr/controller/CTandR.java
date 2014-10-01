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
		
	private static double dirEffectWeight = 0.33;
	private static double indEffectWeight = 0.33;
	private static double tempEffectWeight = 0.33;
	
	public CTandR() {
		
		ffacade = new FTandrFacade();
		
	}

	@Override
	public boolean computeTW(MFeatureVersion featureVersion) {
		boolean result = true;
		
		String tandrGraphUri = UConfig.getTANDRGraphURI();
		String vgihGraphUri = UConfig.getVGIHGraphURI();
		
		MTrustworthinessTandr trustworthiness = new MTrustworthinessTandr( featureVersion );
		
		MFeature feature = featureVersion.getFeature();
		ArrayList<MFeatureVersion> prevVersions = feature.getPreviousVersions(featureVersion.getVersionNo(), 0);
		
		double trustValue = 0.0;
		trustValue = trustValue + (dirEffectWeight  * trustworthiness.getDirectEffect().calculateTrustworthiness(prevVersions,featureVersion)) ;
//		At the moment of the insertion of featureVersion (featureVersion.getIsValidFrom()) trustworthiness indirect and temporal are 0.0
		trustValue = trustValue + (indEffectWeight  * trustworthiness.getIndirectEffect().calculateTrustworthiness(featureVersion,featureVersion.getIsValidFrom()));
		trustValue = trustValue + (tempEffectWeight * trustworthiness.getTemporalEffect().calculateTrustworthiness(featureVersion,featureVersion.getIsValidFrom()));
		trustworthiness.setValue(trustValue);
		
		//save and debug Trustworthiness
		ffacade.create(trustworthiness, tandrGraphUri);
		
		UDebug.print("\n\n" + ffacade.convertToRDFTTL(trustworthiness),4);
		UDebug.log("\nCREATE Trustworthiness (new): " + trustworthiness.getUri() + " - validity time: " + trustworthiness.getComputedAtString(),4);
		UDebug.log("\n\t" + TView.getTrustworthinessString(featureVersion) + "\n",4);
		
		//update Reputation
		this.updateUserReputation(featureVersion);
		
		//propagate to dependancies
		this.updatePrevVersionsTrustworthiness(prevVersions, featureVersion);
		this.confirmNeighbours(featureVersion, vgihGraphUri);
		
		return result;
	}

	public boolean updateUserReputation(MFeatureVersion featureVersion) {
		boolean result = true;
		
//		MReputationTandr reputation = (MReputationTandr) featureVersion.getAuthor().getReputation();
//		if (reputation == null)
//			reputation = new MReputationTandr(featureVersion.getAuthor());
		
		MReputationTandr reputation;
		if ( featureVersion.getAuthor().getReputation() == null && (featureVersion.getAuthor().getReputationUri() == null || featureVersion.getAuthor().getReputationUri() .equals("")) ) 
			reputation = new MReputationTandr( featureVersion.getAuthor() );
		else reputation = (MReputationTandr) featureVersion.getAuthor().getReputation();
		
		UDebug.print("\n\n" + ffacade.convertToRDFTTL(reputation),4);
		
		double repValue = 0.0;
		
		repValue = repValue + (dirEffectWeight  * reputation.getDirectEffect().calculateReputation(featureVersion.getAuthor(),featureVersion.getIsValidFromString())) ;
		repValue = repValue + (indEffectWeight  * reputation.getIndirectEffect().calculateReputation(featureVersion.getAuthor(),featureVersion.getIsValidFromString()));
		repValue = repValue + (tempEffectWeight * reputation.getTemporalEffect().calculateReputation(featureVersion.getAuthor(),featureVersion.getIsValidFromString())); 
		
		reputation.setValue(repValue);
		reputation.setComputedAt(featureVersion.getIsValidFrom());
		
		UDebug.print("\n\n" + ffacade.convertToRDFTTL(reputation),4);
		
		ffacade.create(reputation, UConfig.getTANDRGraphURI());
		UDebug.log("\nCREATE Reputation (new): " + reputation.getUri() + " - validity time: " + reputation.getComputedAtString(),4);
		UDebug.log("\n\t" + RView.getReputationString(featureVersion.getAuthor()) + "\n",4);
		
		return result;
	}
	
	//	save fvs Trustworthiness affected by direct evaluation 
	private void updatePrevVersionsTrustworthiness(ArrayList<MFeatureVersion> prevVersions, MFeatureVersion featureVersion) {
		
		for ( MFeatureVersion fv : prevVersions) {			
			MTrustworthinessTandr trust;
			if ( fv.getTrustworthiness() == null && (fv.getTrustworthinessUri() == null || fv.getTrustworthinessUri().equals("")) ) 
				trust = new MTrustworthinessTandr( fv );
			else trust = (MTrustworthinessTandr) fv.getTrustworthiness();
			
			ArrayList<MFeatureVersion> complementaryVersions = new ArrayList<MFeatureVersion>();
			complementaryVersions.addAll(prevVersions);
			complementaryVersions.add(featureVersion);
			complementaryVersions.remove(fv);
			
			UDebug.print("v: " + fv.getUriID(), 1);
			
			double trustValue = 0.0;
			trustValue = trustValue + (dirEffectWeight  * trust.getDirectEffect().calculateTrustworthiness(complementaryVersions,fv));
			trustValue = trustValue + (dirEffectWeight  * trust.getIndirectEffect().getValue());
			trustValue = trustValue + (dirEffectWeight  * trust.getTemporalEffect().calculateTrustworthiness(fv,featureVersion.getIsValidFrom()));
			
			trust.setValue(trustValue);
			trust.setComputedAt( featureVersion.getIsValidFromString() );
			
			ffacade.create(trust, UConfig.getTANDRGraphURI());
			UDebug.log("\nCREATE Trustworthiness (upd): " + trust.getUri() + " - validity time: " + trust.getComputedAtString(),4);
			UDebug.log("\n\t" + TView.getTrustworthinessString(fv) + "\n",4);
			
			this.updateUserReputation(fv);
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
		
		ffacade.create(trustworthiness, UConfig.getTANDRGraphURI());
		UDebug.log("\nCREATE Trustworthiness (cnf): " + trustworthiness.getUri() + " - validity time: " + trustworthiness.getComputedAtString(),4);
		UDebug.log("\n\t" + TView.getTrustworthinessString(fvToConfirm) + "\n",4);
//		this.updateUserReputation(fvToConfirm);
		
		return result;
	}

}
