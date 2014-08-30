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
import model.MTrustworthiness;
import modules.tandr.model.*;

public class FTrustworthinessEffect {

	private FTripleStore triplestore;
	
	public FTrustworthinessEffect() {	}
	
	public FTrustworthinessEffect(FTripleStore triplestore) {	
		this.triplestore = triplestore;
	}

	public Map<String,MFEffect> retrieveTrustworthinessEffectList(MTrustworthiness trustworthiness, String graphUri) {

		String trustworthinessUri = trustworthiness.getUri();
		UDebug.print("Trustworthiness URI \n\t"+trustworthiness.getUri() + "\n\n",10);
		Map<String,MFEffect> effects = new HashMap<String, MFEffect>();
		
		ResultSetRewindable directEffectRawResults;
		ResultSetRewindable indirectEffectRawResults;
		ResultSetRewindable temporalEffectRawResults;
		
		directEffectRawResults   = this.retrieveDirectEffectRawResult(trustworthinessUri,graphUri);
		indirectEffectRawResults = this.retrieveIndirectEffectRawResult(trustworthinessUri,graphUri);
		temporalEffectRawResults = this.retrieveTemporalEffectRawResult(trustworthinessUri,graphUri);
		
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
	//TODO: correct value subgraph
	private ResultSetRewindable retrieveDirectEffectRawResult(String trustworthinessUri, String graphUri) {
		
		String queryString = ""
				+ "\tSELECT *  \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "\n\t {\n";
		queryString += ""
					+ "\t\t <"+trustworthinessUri+">" + " hvgi:hasTrustworthinessEffect       ?effect  .\n"
					+ "\t\t  		?effect      tandr:effectNameIs      \"Direct Effect\"             .\n"
					+ "\t\t  		?effect      tandr:hasEffectValue    ?effectValue                  .\n"
					+ "\t\t         ?effectValue tandr:effectValueIs     ?directEffectValue            .\n"
					+ "\t\t         OPTIONAL { ?effect      hvgi:hasTrustworthinessAspect  ?geomAspect                 .\n"
					+ "\t\t  		           ?geomAspect  tandr:aspectNameIs             \"Geometric Direct Aspect\" .\n"
					+ "\t\t  		           ?geomAspect  tandr:aspectValueIs            ?geomDirAspectValue         .\n"
					+ "\t\t                  }                                                                          \n"
					+ "\t\t         OPTIONAL { ?effect      hvgi:hasTrustworthinessAspect  ?qualAspect                   .\n"
					+ "\t\t  		           ?qualAspect  tandr:aspectNameIs             \"Qualitative Direct Aspect\" .\n"
					+ "\t\t  		           ?qualAspect  tandr:aspectValueIs            ?qualDirAspectValue           .\n"
					+ "\t\t                  }                                                                            \n"
					+ "\t\t         OPTIONAL { ?effect      hvgi:hasTrustworthinessAspect  ?semAspect                 .\n"
					+ "\t\t  		           ?semAspect   tandr:aspectNameIs             \"Semantic Direct Aspect\" .\n"
					+ "\t\t  		           ?semAspect   tandr:aspectValueIs            ?semDirAspectValue         .\n"
					+ "\t\t                  }                                                                         \n"
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
	//TODO: correct value subgraph
	private ResultSetRewindable retrieveIndirectEffectRawResult(String trustworthinessUri, String graphUri) {
		String queryString = ""
				+ "\tSELECT * \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		queryString += ""
					+ "\t\t <"+trustworthinessUri+">" + " hvgi:hasTrustworthinessEffect       ?effect  .\n"
					+ "\t\t  		?effect      tandr:effectNameIs      \"Indirect Effect\"           .\n"
					+ "\t\t  		?effect      tandr:hasEffectValue    ?effectValue                  .\n"
					+ "\t\t         ?effectValue tandr:effectValueIs     ?indirectEffectValue          .\n"
					+ "\t\t         OPTIONAL { ?effect      hvgi:hasTrustworthinessAspect  ?geomAspect                   .\n"
					+ "\t\t  		           ?geomAspect  tandr:aspectNameIs             \"Geometric Indirect Aspect\" .\n"
					+ "\t\t  		           ?geomAspect  tandr:aspectValueIs            ?geomIndAspectValue           .\n"
					+ "\t\t                  }                                                                            \n"
					+ "\t\t         OPTIONAL { ?effect      hvgi:hasTrustworthinessAspect  ?qualAspect                     .\n"
					+ "\t\t  		           ?qualAspect  tandr:aspectNameIs             \"Qualitative Indirect Aspect\" .\n"
					+ "\t\t  		           ?qualAspect  tandr:aspectValueIs            ?qualIndAspectValue             .\n"
					+ "\t\t                  }                                                                              \n"
					+ "\t\t         OPTIONAL { ?effect      hvgi:hasTrustworthinessAspect  ?semAspect                   .\n"
					+ "\t\t  		           ?semAspect   tandr:aspectNameIs             \"Semantic Indirect Aspect\" .\n"
					+ "\t\t  		           ?semAspect   tandr:aspectValueIs            ?semIndAspectValue           .\n"
					+ "\t\t                  }                                                                           \n";
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
	//TODO: correct value subgraph
	private ResultSetRewindable retrieveTemporalEffectRawResult(String trustworthinessUri, String graphUri) {
		String queryString = ""
				+ "\tSELECT * \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		queryString += ""
					+ "\t\t <"+trustworthinessUri+">" + " hvgi:hasTrustworthinessEffect ?temporalEffect  .\n"
					+ "\t\t ?temporalEffect      tandr:effectNameIs      \"Temporal Effect\"               .\n"
					+ "\t\t  		?effect      tandr:hasEffectValue    ?effectValue                  .\n"
					+ "\t\t         ?effectValue tandr:effectValueIs     ?temporalEffectValue            .\n";
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
		
		if (directEffectRawResults.hasNext())
		{
			generalQueryResults = directEffectRawResults.next();
		
			directEffectValue   = Double.parseDouble( generalQueryResults.getLiteral("directEffectValue").toString() );
			geomDirAspectValue  = Double.parseDouble( generalQueryResults.getLiteral("geomDirAspectValue").toString() );
			qualDirAspectValue  = Double.parseDouble( generalQueryResults.getLiteral("qualDirAspectValue").toString() );
			semDirAspectValue   = Double.parseDouble( generalQueryResults.getLiteral("semDirAspectValue").toString() );
		}
		
		MFDirectEffect directEffect = new MFDirectEffect( directEffectValue );
		MFDirectGeomAspect geometricAspect = new MFDirectGeomAspect( geomDirAspectValue );
		MFDirectQualAspect qualitativeAspect = new MFDirectQualAspect( qualDirAspectValue  );
		MFDirectSemAspect semanticAspect = new MFDirectSemAspect( semDirAspectValue );
		
		directEffect.setGeometricAspect(geometricAspect);
		directEffect.setQualitativeAspect(qualitativeAspect);
		directEffect.setSemanticAspect(semanticAspect);
		
		return directEffect;
	}

	private MFIndirectEffect elaborateIndirectEffects(ResultSetRewindable indirectEffectRawResults) {
		indirectEffectRawResults.reset();
		QuerySolution generalQueryResults = null;
		
		Double indirectEffectValue = 0.0, geomIndAspectValue = 0.0, qualIndAspectValue = 0.0, semIndAspectValue = 0.0;
		
		if (indirectEffectRawResults.hasNext())
		{
			generalQueryResults = indirectEffectRawResults.next();
		
			indirectEffectValue = Double.parseDouble( generalQueryResults.getLiteral("indirectEffectValue").toString() );
			geomIndAspectValue  = Double.parseDouble( generalQueryResults.getLiteral("geomIndAspectValue").toString() );
			qualIndAspectValue  = Double.parseDouble( generalQueryResults.getLiteral("qualIndAspectValue").toString() );
			semIndAspectValue   = Double.parseDouble( generalQueryResults.getLiteral("semIndAspectValue").toString() );
		}
		
		MFIndirectEffect indirectEffect = new MFIndirectEffect( indirectEffectValue );
		MFIndirectGeomAspect geometricAspect = new MFIndirectGeomAspect( geomIndAspectValue );
		MFIndirectQualAspect qualitativeAspect = new MFIndirectQualAspect( qualIndAspectValue  );
		MFIndirectSemAspect semanticAspect = new MFIndirectSemAspect( semIndAspectValue );
		
		indirectEffect.setGeometricAspect(geometricAspect);
		indirectEffect.setQualitativeAspect(qualitativeAspect);
		indirectEffect.setSemanticAspect(semanticAspect);
		
		return indirectEffect;
	}

	private MFTemporalEffect elaborateTemporalEffects(ResultSetRewindable temporalEffectRawResults) {
		temporalEffectRawResults.reset();
		QuerySolution generalQueryResults = null;
		Double temporalEffectValue = 0.0;
		if (temporalEffectRawResults.hasNext()){
			generalQueryResults = temporalEffectRawResults.next();
			temporalEffectValue  = Double.parseDouble( generalQueryResults.getLiteral("temporalEffectValue").toString() );
		}
		
		MFTemporalEffect temporalEffect = new MFTemporalEffect( temporalEffectValue );
		
		return temporalEffect;
	}

}
