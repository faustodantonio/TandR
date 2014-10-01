package modules.tandr.foundation;

import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;

import utility.UDebug;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;

import model.MAuthor;
import model.MReputation;
import modules.tandr.foundation.RDFconverter.xml.FReputation2XML;
import modules.tandr.model.MFDirectEffect;
import modules.tandr.model.MFEffect;
import modules.tandr.model.MFIndirectEffect;
import modules.tandr.model.MFTemporalEffect;
import modules.tandr.model.MReputationTandr;
import foundation.FFoundationAbstract;
import foundation.FFoundationFacade;
import foundation.FReputationExport;

public class FReputationTandr extends FFoundationAbstract implements FReputationExport {

	private FReputationEffect feffect;
	
	public FReputationTandr() {
		super();
		this.feffect = new FReputationEffect( super.triplestore );
	}
	
	@Override
	protected String getClassUri(){
		return "tandr:Reputation";
	}
	
	@Override
	public String convertToRDFXML(Object obj) {
		return this.convertToRDFXML((MReputationTandr) obj);
	}

	@Override
	public String convertToRDFXML(MReputation reputation) {
		
		FReputation2XML reputationXML = new FReputation2XML(triplestore);
		Document trustworthinessDoc = reputationXML.convertToRDFXML(reputation);
		
		String reputationTriples = this.writeRDFXML(trustworthinessDoc);
		return reputationTriples;
	}
	
	public MReputation retrieveByURI(String reputationUri, String graphUri, int lazyDepth) {
		
		MReputationTandr reputation = new MReputationTandr();
		reputation.setUri(reputationUri);
		
		String queryString = ""
				+ "\tSELECT * \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\tOPTIONAL { <"+reputationUri+">" + " tandr:refersToAuthor     ?authorUri       }\n"
				+ "\t\tOPTIONAL { <"+reputationUri+">" + " tandr:hasReputationValue ?value           .\n"
				+ "\t\t           ?value                   tandr:ReputationsValueIs ?reputationValue .\n"
				+ "\t\t           ?value                   tandr:computedAt         ?coumputedAt     }\n" 
				;
						
		if (!graphUri.equals("")) queryString += "\t}\n";
				
		queryString += ""
				+ "\t}";	
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", 3);
		
		ResultSet rawResults = this.triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",6);
		queryRawResults.reset();
		
		QuerySolution generalQueryResults = queryRawResults.next();
		
		reputation = this.setReputationAttributes(reputation, graphUri, generalQueryResults, lazyDepth);
		reputation = this.setReputationEffects(reputation, graphUri);
		
		return reputation;
	}
	
	public Map<String,MFEffect> retrieveReputationEffectList(MReputation reputation) {
		return feffect.retrieveReputationEffectList(reputation, "");
	}
	
	public Map<String,MFEffect> retrieveReputationEffectList(MReputation reputation, String graphUri) {
		return feffect.retrieveReputationEffectList(reputation, graphUri);
	}
	
	private MReputationTandr setReputationEffects(MReputationTandr reputation,String graphUri) {
		Map<String,MFEffect> effects = new HashMap<String,MFEffect>();
		effects = feffect.retrieveReputationEffectList(reputation,graphUri);
		
		System.out.print("\n\n Direct Effect Retrieved Value" + ((MFDirectEffect) effects.get("direct")).getValue() ) ;
		
		reputation.setDirectEffect((MFDirectEffect) effects.get("direct"));
		reputation.setIndirectEffect((MFIndirectEffect) effects.get("indirect"));
		reputation.setTemporalEffect((MFTemporalEffect) effects.get("temporal"));
		return reputation;
	}

	private MReputationTandr setReputationAttributes(MReputationTandr reputation, String graphUri, QuerySolution generalQueryResults, int lazyDepth){
		
		FFoundationFacade ffacade = new FFoundationFacade();
		
		RDFNode refersToAuthor = generalQueryResults.getResource("authorUri");
		RDFNode reputationValue   = generalQueryResults.getLiteral("reputationValue");
		RDFNode computedAt  = generalQueryResults.getLiteral("computedAt");
		
		if (refersToAuthor != null){
			reputation.setAuthorUri(refersToAuthor.toString());
			if ( lazyDepth > 0 ) {
				MAuthor author = (MAuthor) ffacade.retrieveByUri( refersToAuthor.toString(), graphUri, lazyDepth-1, MAuthor.class); 
				reputation.setAuthor(author);
			}			
		}
		if (reputationValue != null)
			reputation.setValue( Double.parseDouble(reputationValue.toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")));
		if (computedAt != null && ! computedAt.toString().equals(""))
			reputation.setComputedAt(computedAt.toString().replace("^^http://www.w3.org/2001/XMLSchema#dateTime", ""));		
		
		return reputation;
	}

}
