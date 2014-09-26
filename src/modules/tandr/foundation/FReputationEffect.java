package modules.tandr.foundation;

import java.util.HashMap;
import java.util.Map;

import utility.UDebug;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;

import foundation.FTripleStore;
import model.MReputation;
import modules.tandr.model.*;

public class FReputationEffect {

	private FTripleStore triplestore;
	
	public FReputationEffect() {	}
	
	public FReputationEffect(FTripleStore triplestore) {	
		this.triplestore = triplestore;
	}

	public Map<String,MFEffect> retrieveReputationEffectList(MReputation reputation, String graphUri) {

		String reputationUri = reputation.getUri();
		UDebug.print("Reputation URI \n\t"+reputation.getUri() + "\n\n",10);
		Map<String,MFEffect> effects = new HashMap<String, MFEffect>();
		
		ResultSetRewindable directEffectRawResults;
		ResultSetRewindable indirectEffectRawResults;
		ResultSetRewindable temporalEffectRawResults;
		
		directEffectRawResults   = this.retrieveDirectEffectRawResult(reputationUri,graphUri);
		indirectEffectRawResults = this.retrieveIndirectEffectRawResult(reputationUri,graphUri);
		temporalEffectRawResults = this.retrieveTemporalEffectRawResult(reputationUri,graphUri);
		
		if (directEffectRawResults == null)
			effects.put("direct", null );
		else effects.put("direct", this.elaborateDirectEffects(directEffectRawResults) );
		
		if (indirectEffectRawResults == null)
			effects.put("indirect", null );
		else effects.put("indirect", this.elaborateIndirectEffects(indirectEffectRawResults) );
		
		if (temporalEffectRawResults == null)
			effects.put("temporal", null );
		else effects.put("temporal", this.elaborateTemporalEffects(temporalEffectRawResults) );
			
		
		return effects;
	}
	
	/*************************
	 * 
	 * Get Query Result FUNCTIONS
	 *
	 *************************/	
	
	
	private ResultSetRewindable retrieveDirectEffectRawResult(String reputationUri, String graphUri) {
		
		String queryString = ""
				+ "\tSELECT *  \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "\n\t {\n";
		queryString += ""
					+ "\t\t <"+reputationUri+">" + " hvgi:hasReputationEffect       ?effect     .\n"
				    + "\t\t  		?effect      tandr:hasEffectDescription  <http://parliament.semwebcentral.org/parliament#tandrEffectDirect> .\n"
					+ "\t\t  		?effect      tandr:hasEffectValue        ?effectValue       .\n"
					+ "\t\t         ?effectValue tandr:effectValueIs         ?directEffectValue .\n"
					+ "\t\t         ?effectValue tandr:computedAt            ?effectComputedAt   \n"
					+ "\t\t         OPTIONAL { ?effect          tandr:hasReputationAspect  ?geomAspect        .\n"
					+ "\t\t  		           ?geomAspect      tandr:hasAspectValue       ?geomValue         .\n"
					+ "\t\t  		           ?geomValue       tandr:aspectValueIs        ?geomAspectValue   .\n"
					+ "\t\t                    ?geomValue       tandr:computedAt           ?geomComputedAt    .\n"
					+ "\t\t  		           ?geomAspect      tandr:hasAspectDescription <http://parliament.semwebcentral.org/parliament#tandrAspectGeomDir> .\n"
					+ "\t\t                  }\n"
					+ "\t\t         OPTIONAL { ?effect          tandr:hasReputationAspect  ?qualAspect        .\n"
					+ "\t\t  		           ?qualAspect      tandr:hasAspectValue       ?qualValue         .\n"
					+ "\t\t  		           ?qualValue       tandr:aspectValueIs        ?qualAspectValue   .\n"
					+ "\t\t                    ?qualValue       tandr:computedAt           ?qualComputedAt    .\n"
					+ "\t\t  		           ?qualAspect      tandr:hasAspectDescription <http://parliament.semwebcentral.org/parliament#tandrAspectQualDir> .\n"
					+ "\t\t                  }\n"
					+ "\t\t         OPTIONAL { ?effect         tandr:hasReputationAspect  ?semAspect          .\n"
					+ "\t\t  		           ?semAspect      tandr:hasAspectValue       ?semValue           .\n"
					+ "\t\t  		           ?semValue       tandr:aspectValueIs        ?semAspectValue     .\n"
					+ "\t\t                    ?semValue       tandr:computedAt           ?semComputedAt      .\n"
					+ "\t\t  		           ?semAspect      tandr:hasAspectDescription <http://parliament.semwebcentral.org/parliament#tandrAspectSemDir> .\n"
					+ "\t\t                  }\n"
					+ "";
		if (!graphUri.equals("")) queryString += "\t }\n";
		queryString += ""
				+ "\t}";
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", 4);
		
		ResultSet rawResults = this.triplestore.sparqlSelectHandled(queryString);
		ResultSetRewindable queryRawResults = null;
		
		if (rawResults != null){
			queryRawResults = ResultSetFactory.copyResults(rawResults);
			UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",6);
			queryRawResults.reset();
		} 
		
		return queryRawResults;
	}
	
	private ResultSetRewindable retrieveIndirectEffectRawResult(String reputationUri, String graphUri) {
		String queryString = ""
				+ "\tSELECT * \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		queryString += ""
					+ "\t\t <"+reputationUri+">" + " tandr:hasReputationEffect       ?effect  .\n"
				    + "\t\t  		?effect      tandr:hasEffectDescription  <http://parliament.semwebcentral.org/parliament#tandrEffectIndirect>  .\n"
					+ "\t\t  		?effect      tandr:hasEffectValue        ?effectValue         .\n"
					+ "\t\t         ?effectValue tandr:effectValueIs         ?indirectEffectValue .\n"
					+ "\t\t         ?effectValue tandr:computedAt            ?effectComputedAt     \n"
					+ "\t\t         OPTIONAL { ?effect          tandr:hasReputationAspect  ?geomAspect        .\n"
					+ "\t\t  		           ?geomAspect      tandr:hasAspectValue       ?geomValue         .\n"
					+ "\t\t  		           ?geomValue       tandr:aspectValueIs        ?geomAspectValue   .\n"
					+ "\t\t                    ?geomValue       tandr:computedAt           ?geomComputedAt    .\n"
					+ "\t\t  		           ?geomAspect      tandr:hasAspectDescription <http://parliament.semwebcentral.org/parliament#tandrAspectGeomInd> .\n"
					+ "\t\t                  }\n"
					+ "\t\t         OPTIONAL { ?effect          tandr:hasReputationAspect  ?qualAspect        .\n"
					+ "\t\t  		           ?qualAspect      tandr:hasAspectValue       ?qualValue         .\n"
					+ "\t\t  		           ?qualValue       tandr:aspectValueIs        ?qualAspectValue   .\n"
					+ "\t\t                    ?qualValue       tandr:computedAt           ?qualComputedAt    .\n"
					+ "\t\t  		           ?qualAspect      tandr:hasAspectDescription <http://parliament.semwebcentral.org/parliament#tandrAspectQualInd> .\n"
					+ "\t\t                  }\n"
					+ "\t\t         OPTIONAL { ?effect         tandr:hasReputationAspect  ?semAspect          .\n"
					+ "\t\t  		           ?semAspect      tandr:hasAspectValue       ?semValue           .\n"
					+ "\t\t  		           ?semValue       tandr:aspectValueIs        ?semAspectValue     .\n"
					+ "\t\t                    ?semValue       tandr:computedAt           ?semComputedAt      .\n"
					+ "\t\t  		           ?semAspect      tandr:hasAspectDescription <http://parliament.semwebcentral.org/parliament#tandrAspectSemInd> .\n"
					+ "\t\t                  }\n";
		if (!graphUri.equals("")) queryString += "\t }\n";
		queryString += ""
				+ "\t}";
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", 4);
		
		ResultSet rawResults = this.triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",6);
		queryRawResults.reset();
		
		return queryRawResults;
	}
	
	private ResultSetRewindable retrieveTemporalEffectRawResult(String reputationUri, String graphUri) {
		String queryString = ""
				+ "\tSELECT * \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		queryString += ""
					+ "\t\t <"+reputationUri+">" + " tandr:hasReputationEffect ?effect .\n"
					+ "\t\t ?effect      tandr:hasEffectDescription <http://parliament.semwebcentral.org/parliament#tandrEffectTemporal>  .\n"
					+ "\t\t ?effect      tandr:hasEffectValue ?value                  .\n"
					+ "\t\t ?value       tandr:effectValueIs  ?temporalEffectValue    .\n"
					+ "\t\t ?value       tandr:computedAt     ?computedAt              \n"
					;
		if (!graphUri.equals("")) queryString += "\t }\n";
		queryString += ""
				+ "\t}";

		UDebug.print("SPARQL query: \n" + queryString + "\n\n", 4);
		
		ResultSet rawResults = this.triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",6);
		queryRawResults.reset();
		
		return queryRawResults;
	}


	
	/*************************
	 * 
	 * elaborate effect query result FUNCTIONS
	 *
	 *************************/	
	
	private MFDirectEffect elaborateDirectEffects(ResultSetRewindable directEffectRawResults) {
		
		directEffectRawResults.reset();
		QuerySolution generalQueryResults = null;
		
		Double directEffectValue = 0.0, geomDirAspectValue = 0.0, qualDirAspectValue = 0.0, semDirAspectValue = 0.0;
		String computedAt = "2005-09-15T21:42:44Z";
		String directEffectComputed = computedAt, geomDirAspectComputed = computedAt, 
				qualDirAspectComputed = computedAt, semDirAspectComputed = computedAt;
		
		if (directEffectRawResults.hasNext())
		{
			generalQueryResults = directEffectRawResults.next();
		
			directEffectValue  = Double.parseDouble( generalQueryResults.getLiteral("directEffectValue").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "") );
			geomDirAspectValue = Double.parseDouble( generalQueryResults.getLiteral("geomAspectValue").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "") );
			qualDirAspectValue = Double.parseDouble( generalQueryResults.getLiteral("qualAspectValue").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "") );
			semDirAspectValue  = Double.parseDouble( generalQueryResults.getLiteral("semAspectValue").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "") );
			
			directEffectComputed  = generalQueryResults.getLiteral("effectComputedAt").toString().replace("^^http://www.w3.org/2001/XMLSchema#dateTime", "");
			geomDirAspectComputed = generalQueryResults.getLiteral("geomComputedAt").toString().replace("^^http://www.w3.org/2001/XMLSchema#dateTime", "");
			qualDirAspectComputed = generalQueryResults.getLiteral("qualComputedAt").toString().replace("^^http://www.w3.org/2001/XMLSchema#dateTime", "");
			semDirAspectComputed  = generalQueryResults.getLiteral("semComputedAt").toString().replace("^^http://www.w3.org/2001/XMLSchema#dateTime", "");
		}
		
		MFDirectEffect directEffect = new MFDirectEffect( directEffectValue );
		MFDirectGeomAspect geometricAspect = new MFDirectGeomAspect( geomDirAspectValue );
		MFDirectQualAspect qualitativeAspect = new MFDirectQualAspect( qualDirAspectValue  );
		MFDirectSemAspect semanticAspect = new MFDirectSemAspect( semDirAspectValue );
		
		directEffect.setComputedAt(directEffectComputed);
		geometricAspect.setComputedAt(geomDirAspectComputed);
		qualitativeAspect.setComputedAt(qualDirAspectComputed);
		semanticAspect.setComputedAt(semDirAspectComputed);
		
		directEffect.setGeometricAspect(geometricAspect);
		directEffect.setQualitativeAspect(qualitativeAspect);
		directEffect.setSemanticAspect(semanticAspect);
		
		return directEffect;
	}
	
	private MFIndirectEffect elaborateIndirectEffects(ResultSetRewindable indirectEffectRawResults) {
		indirectEffectRawResults.reset();
		QuerySolution generalQueryResults = null;
		
		Double indirectEffectValue = 0.0, geomIndAspectValue = 0.0, qualIndAspectValue = 0.0, semIndAspectValue = 0.0;
		String computedAt = "2005-09-15T21:42:44Z";
		String indirectEffectComputed = computedAt, geomIndAspectComputed = computedAt, 
				qualIndAspectComputed = computedAt, semIndAspectComputed = computedAt;
		
		if (indirectEffectRawResults.hasNext())
		{
			generalQueryResults = indirectEffectRawResults.next();
		
			indirectEffectValue = Double.parseDouble( generalQueryResults.getLiteral("indirectEffectValue").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "") );
			geomIndAspectValue  = Double.parseDouble( generalQueryResults.getLiteral("geomAspectValue").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "") );
			qualIndAspectValue  = Double.parseDouble( generalQueryResults.getLiteral("qualAspectValue").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "") );
			semIndAspectValue   = Double.parseDouble( generalQueryResults.getLiteral("semAspectValue").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "") );
			
			indirectEffectComputed  = generalQueryResults.getLiteral("effectComputedAt").toString().replace("^^http://www.w3.org/2001/XMLSchema#dateTime", "");
			geomIndAspectComputed = generalQueryResults.getLiteral("geomComputedAt").toString().replace("^^http://www.w3.org/2001/XMLSchema#dateTime", "");;
			qualIndAspectComputed = generalQueryResults.getLiteral("qualComputedAt").toString().replace("^^http://www.w3.org/2001/XMLSchema#dateTime", "");;
			semIndAspectComputed  = generalQueryResults.getLiteral("semComputedAt").toString().replace("^^http://www.w3.org/2001/XMLSchema#dateTime", "");;
		}
		
		MFIndirectEffect indirectEffect = new MFIndirectEffect( indirectEffectValue );
		MFIndirectGeomAspect geometricAspect = new MFIndirectGeomAspect( geomIndAspectValue );
		MFIndirectQualAspect qualitativeAspect = new MFIndirectQualAspect( qualIndAspectValue  );
		MFIndirectSemAspect semanticAspect = new MFIndirectSemAspect( semIndAspectValue );
		
		indirectEffect.setComputedAt(indirectEffectComputed);
		geometricAspect.setComputedAt(geomIndAspectComputed);
		qualitativeAspect.setComputedAt(qualIndAspectComputed);
		semanticAspect.setComputedAt(semIndAspectComputed);
		
		indirectEffect.setGeometricAspect(geometricAspect);
		indirectEffect.setQualitativeAspect(qualitativeAspect);
		indirectEffect.setSemanticAspect(semanticAspect);
		
		return indirectEffect;
	}
	
	private MFTemporalEffect elaborateTemporalEffects(ResultSetRewindable temporalEffectRawResults) {
		temporalEffectRawResults.reset();
		QuerySolution generalQueryResults = null;
		Double temporalEffectValue = 0.0;
		String temporalEffectComputed = "2005-09-15T21:42:44Z";
		if (temporalEffectRawResults.hasNext()){
			generalQueryResults = temporalEffectRawResults.next();
			temporalEffectValue  = Double.parseDouble( generalQueryResults.getLiteral("temporalEffectValue").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "") );
			temporalEffectComputed  = generalQueryResults.getLiteral("effectComputedAt").toString().replace("^^http://www.w3.org/2001/XMLSchema#dateTime", "");
		}
		
		MFTemporalEffect temporalEffect = new MFTemporalEffect( temporalEffectValue );
		temporalEffect.setComputedAt(temporalEffectComputed);
		
		return temporalEffect;
	}

}
