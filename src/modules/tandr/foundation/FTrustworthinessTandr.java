package modules.tandr.foundation;

import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;

import utility.UDebug;
import model.MFeature;
import model.MFeatureVersion;
import model.MTrustworthiness;
import modules.tandr.foundation.RDFconverter.xml.FTrustworthiness2XML;
import modules.tandr.model.MFDirectEffect;
import modules.tandr.model.MFEffect;
import modules.tandr.model.MFIndirectEffect;
import modules.tandr.model.MFTemporalEffect;
import modules.tandr.model.MTrustworthinessTandr;
import foundation.FFoundationAbstract;
import foundation.FTrustworthinessExport;
import foundation.FFoundationFacade;

public class FTrustworthinessTandr extends FFoundationAbstract implements FTrustworthinessExport {

	private FTrustworthinessEffect feffect;
	
	public FTrustworthinessTandr() {
		super();
		this.feffect = new FTrustworthinessEffect( super.triplestore );
	}
	
	@Override
	protected String getClassUri(){
		return "tandr:Trustworthiness";
	}
	
	@Override
	public String convertToRDFXML(Object obj) {
		return this.convertToRDFXML((MTrustworthinessTandr) obj);
	}

	@Override
	public String convertToRDFXML(MTrustworthiness trust) {
		
		FTrustworthiness2XML trustXML = new FTrustworthiness2XML(triplestore);
		Document trustworthinessDoc = trustXML.convertToRDFXML(trust);
		
		String trustworthinessTriples = this.writeRDFXML(trustworthinessDoc);
		return trustworthinessTriples;
	}
	
	@Override 	//TODO: correct value subgraph
	public MTrustworthiness retrieveByURI(String trustworthinessUri, String graphUri, int lazyDepth) {
		
		MTrustworthinessTandr trustworthiness = new MTrustworthinessTandr();
		
		String queryString = ""
				+ "\tSELECT * \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString = ""
				+ "\t\tOPTIONAL { <"+trustworthinessUri+">" + " tandr:refersToFeatureState    ?featureVersionUri }    \n"
				+ "\t\tOPTIONAL { <"+trustworthinessUri+">" + " tandr:hasTrustworhtinessValue ?trustworthinessValue } \n"
				+ "\t\tOPTIONAL { <"+trustworthinessUri+">" + " tandr:computedAt              ?coumputedAt }          \n" ;
						
		if (!graphUri.equals("")) queryString += "\t}\n";
				
		queryString = ""
				+ "\t}";	
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", 3);
		
		ResultSet rawResults = this.triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",6);
		queryRawResults.reset();
		
		QuerySolution generalQueryResults = queryRawResults.next();
		
		trustworthiness = this.setTrustworthinessAttributes(trustworthiness, graphUri, generalQueryResults, lazyDepth);
		trustworthiness = this.setTrustworthinessEffects(trustworthiness, graphUri);
		
		return trustworthiness;
	}
	
	public Map<String,MFEffect> retrieveTrustworthinessEffectList(MTrustworthiness trustworthiness) {
		return feffect.retrieveTrustworthinessEffectList(trustworthiness, "");
	}
	
	public Map<String,MFEffect> retrieveTrustworthinessEffectList(MTrustworthiness trustworthiness, String graphUri) {
		return feffect.retrieveTrustworthinessEffectList(trustworthiness, graphUri);
	}
	
	private MTrustworthinessTandr setTrustworthinessEffects(MTrustworthinessTandr trustworthiness,String graphUri) {
		Map<String,MFEffect> effects = new HashMap<String,MFEffect>();
		effects = feffect.retrieveTrustworthinessEffectList(trustworthiness,graphUri);
		trustworthiness.setDirectEffect((MFDirectEffect) effects.get("direct"));
		trustworthiness.setIndirectEffect((MFIndirectEffect) effects.get("indirect"));
		trustworthiness.setTemporalEffect((MFTemporalEffect) effects.get("temporal"));
		return trustworthiness;
	}

	private MTrustworthinessTandr setTrustworthinessAttributes(MTrustworthinessTandr trustworthiness, String graphUri, QuerySolution generalQueryResults, int lazyDepth){
		
		FFoundationFacade ffacade = new FFoundationFacade();
		
		RDFNode refersToFeatureState = generalQueryResults.getResource("featureVersionUri");
		RDFNode trustworthinessValue   = generalQueryResults.getLiteral("hasTrustworthinessValue");
		RDFNode computedAt  = generalQueryResults.getLiteral("computedAt");
		
		if (refersToFeatureState != null){
			trustworthiness.setFeatureVersionUri(refersToFeatureState.toString());
			if ( lazyDepth > 0 ) {
				MFeatureVersion featureVersion = (MFeatureVersion) ffacade.retrieveByUri( refersToFeatureState.toString(), graphUri, lazyDepth-1, MFeature.class); 
				trustworthiness.setFeatureVersion(featureVersion);
			}			
		}
		if (trustworthinessValue != null)
			trustworthiness.setValue( Double.parseDouble(trustworthinessValue.toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")));
		if (computedAt != null)
			trustworthiness.setComputedAt(computedAt.toString().replace("^^http://www.w3.org/2001/XMLSchema#dateTime", ""));		
		
		return trustworthiness;
	}
	
}
