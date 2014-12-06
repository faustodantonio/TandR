package modules.tandr.foundation;

import org.jdom2.Document;

import utility.UConfig;
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
import modules.tandr.model.*;
import foundation.FFoundationAbstract;
import foundation.FFoundationFacade;
import foundation.FReputationExport;

public class FReputationTandr extends FFoundationAbstract implements FReputationExport {
	
	private int dbgLevel = 100;
	
	public FReputationTandr() {
		super();
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
		return this.retrieveByURI_prod(reputationUri, graphUri, lazyDepth);
	}

	@Override
	public MReputationTandr getMaximumReputation(String computedAt) {
		MReputationTandr maxR = new MReputationTandr();
		
		maxR.getDirectEffect().getGeometricAspect().setValue(1);
		maxR.getDirectEffect().getQualitativeAspect().setValue(1);
		maxR.getDirectEffect().getSemanticAspect().setValue(1);
		maxR.getDirectEffect().setValue(1);
		
		maxR.getIndirectEffect().getGeometricAspect().setValue(1);
		maxR.getIndirectEffect().getQualitativeAspect().setValue(1);
		maxR.getIndirectEffect().getSemanticAspect().setValue(1);
		maxR.getIndirectEffect().setValue(1);
		
		maxR.getTemporalEffect().setValue(1);
		
		maxR.setValue(1);
		maxR.setComputedAt(computedAt);
		
		return maxR;
	}
	@Override
	public boolean create(MReputation reputation, String graph) {
		FTandrFacade ffoundation = new FTandrFacade();
		return ffoundation.create(reputation, graph);
	}
	
	public MReputation retrieveByURI_prod(String reputationUri, String graphUri, int lazyDepth) {
		
		MReputationTandr reputation = new MReputationTandr();
		reputation.setUri(reputationUri);
		
		String queryString = ""
				+ "SELECT \n"
				+ "#Reputation Info \n"
				+ " ?repUri ?authorUri (str(?computedAt) AS ?timeStamp) (str(?reputationValue) AS ?RepValue)\n"
				+ "# Effects Values \n"
				+ " (str(?directEffectValue) AS ?DirValue) (str(?indirectEffectValue) AS ?IndValue) (str(?temporalEffectValue) AS ?TempValue)\n"
				+ "#Direct Aspects Values\n"
				+ " (str(?dirGeomAspectValue) AS ?GeomDirValue) (str(?dirQualAspectValue) AS ?QualDirValue) (str(?dirSemAspectValue) AS ?SemDirValue)\n"
				+ "#Indirect Aspect Values\n"
				+ " (str(?indGeomAspectValue) AS ?GeomIndValue) (str(?indQualAspectValue) AS ?QualIndValue) (str(?indSemAspectValue) AS ?SemIndValue)\n"
				+ "\n"
				+ "WHERE \n"
				+ "{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t ?repUri  tandr:refersToAuthor     ?authorUri       .\n"
				+ "\t ?repUri  tandr:hasReputationValue ?value           .\n"
				+ "\t ?value   tandr:reputationValueIs  ?reputationValue .\n"
				+ "\t ?value   tandr:computedAt         ?computedAt      .\n" 
				;
		
		queryString += this.getEffectsQueryString();
		queryString += this.getDirectAspectsQueryString();
		queryString += this.getIndirectAspectsQueryString();
						
		if (!graphUri.equals("")) queryString += "\t}\n";
				
		queryString += ""
				+ " FILTER (?repUri = <"+reputationUri+">)"
				+ "\n"
				;
		
		queryString += ""
				+ "}"
				+ "\nORDER BY DESC(?computedAt) \n"
				+ "LIMIT 1 \n";			
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", dbgLevel+1);
		
		ResultSet rawResults = this.triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2);
		queryRawResults.reset();
		
		QuerySolution generalQueryResults;
		
		if (queryRawResults.hasNext()) {
			generalQueryResults = queryRawResults.next();
			reputation = this.setTrustworthinessAttributes_prod(reputation, graphUri, generalQueryResults, lazyDepth);
			reputation = this.setTrustworthinessEffects_prod(reputation, graphUri, generalQueryResults);
		}
		
		return reputation;
	}
		
//	public MReputationTandr computeReputationValues(String authorUri,String untilDate,Boolean graphUri) {
//
//		MReputationTandr reputation = new MReputationTandr();
//		
//		String queryString = ""
//				+ "\tSELECT \n"
//				+ "\t#Reputation Info \n"
//				+ "\t (( <"+ authorUri +">) AS ?author) (STR(AVG(?trustworthinessValue)) AS ?repValue) (STR(MAX(?computedAt)) AS ?timestamp)\n"
//				+ "\t# Effects Values \n"
//				+ "\t (STR(AVG(?directEffectValue)) AS ?directRepValue) (STR(AVG(?indirectEffectValue)) AS ?indirectRepValue) (STR(AVG(?temporalEffectValue)) AS ?temporalRepValue)\n"
//				+ "\t#Direct Aspects Values\n"
//				+ "\t (STR(AVG(?dirGeomAspectValue)) AS ?DirGeomRepValue) (STR(AVG(?dirQualAspectValue)) AS ?DirQualRepValue) (STR(AVG(?dirSemAspectValue)) AS ?DirSemRepValue)\n"
//				+ "\t#Indirect Aspect Values\n"
//				+ "\t (STR(AVG(?indGeomAspectValue)) AS ?IndGeomRepValue) (STR(AVG(?indQualAspectValue)) AS ?IndQualRepValue) (STR(AVG(?indSemAspectValue)) AS ?IndSemRepValue)\n"
//				+ "\n"
//				+ "\tWHERE \n"
//				+ "\t{ \n";
//		
//		if (graphUri) queryString += "\t GRAPH " +UConfig.getVGIHGraphURI()+ "\n\t {\n";
//		
//		queryString += ""
//				+ "\t ?fvUri dcterms:contributor <"+ authorUri +"> .\n"
//				;
//		if (graphUri) queryString += "\t }\n";
//		
//		//***//
//				
//		if (graphUri) queryString += "\t GRAPH " +UConfig.getTANDRGraphURI()+ "\n\t {\n";
//		
//		queryString += ""
//				+ "\t  ?tUri  tandr:refersToFeatureVersion  ?fvUri                .\n"
//				+ "\t  ?tUri  tandr:hasTrustworthinessValue ?value                .\n"
//				+ "\t  ?value tandr:trustworthinessValueIs  ?trustworthinessValue .\n"
//				+ "\t  ?value tandr:computedAt              ?computedAt           .\n"
//				+ "\n" 
//				;
//		
//		queryString += this.getTrustEffectsQueryString();
//		queryString += this.getDirectTrustAspectsQueryString();
//		queryString += this.getIndirectTrustAspectsQueryString();
//		queryString += this.getComputedAtQueryString(untilDate, graphUri);
//		
//		queryString += "\t }\n";
//		
//		if (graphUri) queryString += "\t  FILTER( ?computedAt <= ?maxComputedAt  )\n";
//		
//		queryString += ""
//				+ "\t} \n"
//				;
//	
//		
//		UDebug.print("SPARQL query: \n" + queryString + "\n\n", dbgLevel+1);
//		
//		ResultSet rawResults = this.triplestore.sparqlSelectHandled(queryString);
//		
//		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
//		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2);
//		queryRawResults.reset();
//		
//		QuerySolution generalQueryResults;
//		
//		if (queryRawResults.hasNext()) {
//			generalQueryResults = queryRawResults.next();
//			
//			String computedAt = generalQueryResults.getLiteral("timestamp").toString();
//			
//			reputation.setValue( Double.parseDouble(generalQueryResults.getLiteral("repValue").toString()) );
//			reputation.getDirectEffect().setValue( Double.parseDouble(generalQueryResults.getLiteral("directRepValue").toString()) );
//			reputation.getIndirectEffect().setValue( Double.parseDouble(generalQueryResults.getLiteral("indirectRepValue").toString()) );
//			reputation.getTemporalEffect().setValue( Double.parseDouble(generalQueryResults.getLiteral("temporalRepValue").toString()) );
//			
//			reputation.getDirectEffect().getGeometricAspect().setValue( Double.parseDouble(generalQueryResults.getLiteral("DirGeomRepValue").toString()) );
//			reputation.getDirectEffect().getQualitativeAspect().setValue( Double.parseDouble(generalQueryResults.getLiteral("DirQualRepValue").toString()) );
//			reputation.getDirectEffect().getSemanticAspect().setValue( Double.parseDouble(generalQueryResults.getLiteral("DirSemRepValue").toString()) );
//			
//			reputation.getIndirectEffect().getGeometricAspect().setValue( Double.parseDouble(generalQueryResults.getLiteral("IndGeomRepValue").toString()) );
//			reputation.getIndirectEffect().getQualitativeAspect().setValue( Double.parseDouble(generalQueryResults.getLiteral("IndQualRepValue").toString()) );
//			reputation.getIndirectEffect().getSemanticAspect().setValue( Double.parseDouble(generalQueryResults.getLiteral("IndSemRepValue").toString()) );
//			
//			reputation.setComputedAt(computedAt);
//			reputation.getDirectEffect().setComputedAt(computedAt);
//			reputation.getIndirectEffect().setComputedAt(computedAt);
//			reputation.getTemporalEffect().setComputedAt(computedAt);
//			
//			reputation.getDirectEffect().getGeometricAspect().setComputedAt(computedAt);
//			reputation.getDirectEffect().getQualitativeAspect().setComputedAt(computedAt);
//			reputation.getDirectEffect().getSemanticAspect().setComputedAt(computedAt);
//			
//			reputation.getIndirectEffect().getGeometricAspect().setComputedAt(computedAt);
//			reputation.getIndirectEffect().getQualitativeAspect().setComputedAt(computedAt);
//			reputation.getIndirectEffect().getSemanticAspect().setComputedAt(computedAt);
//			
//		} else {
//			reputation = null;
//		}
//		
//		return reputation;
//	}
	
	public MReputationTandr computeReputationValues(String authorUri,String untilDate,Boolean graphUri) {

		MReputationTandr reputation = new MReputationTandr();
		
		String queryString = ""
				+ "\tSELECT \n"
				+ "\t#Reputation Info \n"
				+ "\t (( <"+ authorUri +">) AS ?author) (STR(AVG(?trustworthinessValue)) AS ?repValue) (STR(MAX(?computedAt)) AS ?timestamp)\n"
				+ "\t# Effects Values \n"
				+ "\t (STR(AVG(?directEffectValue)) AS ?directRepValue) (STR(AVG(?indirectEffectValue)) AS ?indirectRepValue) (STR(AVG(?temporalEffectValue)) AS ?temporalRepValue)\n"
				+ "\t#Direct Aspects Values\n"
				+ "\t (STR(AVG(?dirGeomAspectValue)) AS ?DirGeomRepValue) (STR(AVG(?dirQualAspectValue)) AS ?DirQualRepValue) (STR(AVG(?dirSemAspectValue)) AS ?DirSemRepValue)\n"
				+ "\t#Indirect Aspect Values\n"
				+ "\t (STR(AVG(?indGeomAspectValue)) AS ?IndGeomRepValue) (STR(AVG(?indQualAspectValue)) AS ?IndQualRepValue) (STR(AVG(?indSemAspectValue)) AS ?IndSemRepValue)\n"
				+ "\n"
				+ "\tWHERE \n"
				+ "\t{ \n";
		
		if (graphUri) queryString += "\t GRAPH " +UConfig.getVGIHGraphURI()+ "\n\t {\n";
		
		queryString += ""
				+ "\t ?fvUri dcterms:contributor <"+ authorUri +"> .\n"
				;
		if (graphUri) queryString += "\t }\n";
		
		//***//
				
		if (graphUri) queryString += "\t GRAPH " +UConfig.getTANDRGraphURI()+ "\n\t {\n";
		
		queryString += ""
				+ "\t  ?tUri  tandr:refersToFeatureVersion  ?fvUri                .\n"
				+ "\t  ?tUri  tandr:hasTrustworthinessValue ?value                .\n"
				+ "\t  ?value tandr:trustworthinessValueIs  ?trustworthinessValue .\n"
				+ "\t  ?value tandr:computedAt              ?computedAt           .\n"
				+ "\n" 
				;
		
		queryString += this.getTrustEffectsQueryString();
		queryString += this.getDirectTrustAspectsQueryString();
		queryString += this.getIndirectTrustAspectsQueryString();
		queryString += this.getComputedAtQueryString(untilDate, graphUri);
		
		queryString += "\t }\n";
		
		if (graphUri) queryString += "\t  FILTER( ?computedAt <= ?maxComputedAt  )\n";
		
		queryString += ""
				+ "\t} \n"
				;
	
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", dbgLevel+1);
		
		ResultSet rawResults = this.triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2);
		queryRawResults.reset();
		
		QuerySolution generalQueryResults;
		
		if (queryRawResults.hasNext()) {
			generalQueryResults = queryRawResults.next();
			
			double rep, dir, ind, temp, dirGeom, dirQual, dirSem, indGeom, indQual, indSem;
			double dirWeight = 0.3333333, indWeight = 0.3333333, tempWeight  = 0.3333333;
			double dirGeomWeight = 0.3333333, dirQualWeight = 0.3333333, dirSemWeight  = 0.3333333;
			double indGeomWeight = 0.3333333, indQualWeight = 0.3333333, indSemWeight  = 0.3333333;
			
//			dirWeight*dirGeomWeight*dirGeom + indWeight*indGeomWeight*indGeom + tempWeight*temp/3;
			
			rep = Double.parseDouble(generalQueryResults.getLiteral("repValue").toString());
			dir =  Double.parseDouble(generalQueryResults.getLiteral("directRepValue").toString());
			ind = Double.parseDouble(generalQueryResults.getLiteral("indirectRepValue").toString());
			temp = Double.parseDouble(generalQueryResults.getLiteral("temporalRepValue").toString());
			dirGeom = Double.parseDouble(generalQueryResults.getLiteral("DirGeomRepValue").toString());
			dirQual = Double.parseDouble(generalQueryResults.getLiteral("DirQualRepValue").toString());
			dirSem = Double.parseDouble(generalQueryResults.getLiteral("DirSemRepValue").toString());
			indGeom = Double.parseDouble(generalQueryResults.getLiteral("IndGeomRepValue").toString());
			indQual = Double.parseDouble(generalQueryResults.getLiteral("IndQualRepValue").toString());
			indSem = Double.parseDouble(generalQueryResults.getLiteral("IndSemRepValue").toString());
			
			String computedAt = generalQueryResults.getLiteral("timestamp").toString();
			
			reputation.setValue( rep );
			reputation.getDirectEffect().setValue( dir );
			reputation.getIndirectEffect().setValue( ind );
			reputation.getTemporalEffect().setValue( temp );
			
			reputation.getDirectEffect().getGeometricAspect().setValue( dirGeom );
			reputation.getDirectEffect().getQualitativeAspect().setValue( dirQual );
			reputation.getDirectEffect().getSemanticAspect().setValue( dirSem );
			
			reputation.getIndirectEffect().getGeometricAspect().setValue( indGeom );
			reputation.getIndirectEffect().getQualitativeAspect().setValue( indQual );
			reputation.getIndirectEffect().getSemanticAspect().setValue( indSem );
			
			reputation.setComputedAt(computedAt);
			reputation.getDirectEffect().setComputedAt(computedAt);
			reputation.getIndirectEffect().setComputedAt(computedAt);
			reputation.getTemporalEffect().setComputedAt(computedAt);
			
			reputation.getDirectEffect().getGeometricAspect().setComputedAt(computedAt);
			reputation.getDirectEffect().getQualitativeAspect().setComputedAt(computedAt);
			reputation.getDirectEffect().getSemanticAspect().setComputedAt(computedAt);
			
			reputation.getIndirectEffect().getGeometricAspect().setComputedAt(computedAt);
			reputation.getIndirectEffect().getQualitativeAspect().setComputedAt(computedAt);
			reputation.getIndirectEffect().getSemanticAspect().setComputedAt(computedAt);
			
		} else {
			reputation = null;
		}
		
		return reputation;
	}	
	
	//*******************************//
	
	private String getEffectsQueryString() {
		String queryString = "";
		
		queryString += ""
				+ "\t ?repUri         tandr:hasReputationEffect   ?dirEffect         .\n"
			    + "\t ?dirEffect      tandr:hasEffectDescription  <http://parliament.semwebcentral.org/parliament#tandrEffectDirect> .\n"
				+ "\t ?dirEffect      tandr:hasEffectValue        ?dirEffectValue    .\n"
				+ "\t ?dirEffectValue tandr:effectValueIs         ?directEffectValue .\n"
				+ "\t ?dirEffectValue tandr:computedAt            ?computedAt        .\n"
				+ "\n";
				
		queryString += ""
				+ "\t ?repUri         tandr:hasReputationEffect   ?indEffect           .\n"
			    + "\t ?indEffect      tandr:hasEffectDescription  <http://parliament.semwebcentral.org/parliament#tandrEffectIndirect>  .\n"
				+ "\t ?indEffect      tandr:hasEffectValue        ?indEffectValue      .\n"
				+ "\t ?indEffectValue tandr:effectValueIs         ?indirectEffectValue .\n"
				+ "\t ?indEffectValue tandr:computedAt            ?computedAt          .\n"
				+ "\n";
				
		queryString += ""
				+ "\t ?repUri          tandr:hasReputationEffect  ?tempEffect          .\n"
				+ "\t ?tempEffect      tandr:hasEffectDescription <http://parliament.semwebcentral.org/parliament#tandrEffectTemporal>  .\n"
				+ "\t ?tempEffect      tandr:hasEffectValue       ?tempEffectValue     .\n"
				+ "\t ?tempEffectValue tandr:effectValueIs        ?temporalEffectValue .\n"
				+ "\t ?tempEffectValue tandr:computedAt           ?computedAt          .\n"
				+ "\n"
				;
		
		return queryString;
	}
	
	private String getDirectAspectsQueryString() {

		String queryString = "";
		
		queryString += ""
				+ "\t OPTIONAL { \n"
				+ "\t\t ?dirEffect          tandr:hasReputationAspect  ?dirGeomAspect      .\n"
				+ "\t\t ?dirGeomAspect      tandr:hasAspectDescription <http://parliament.semwebcentral.org/parliament#tandrAspectGeomDir> .\n"	
				+ "\t\t ?dirGeomAspect      tandr:hasAspectValue       ?dirGeomValue       .\n"
				+ "\t\t ?dirGeomValue       tandr:aspectValueIs        ?dirGeomAspectValue .\n"
				+ "\t\t ?dirGeomValue       tandr:computedAt           ?computedAt         .\n"
				+ "\t }\n"
				+ ""
				+ "\t OPTIONAL { \n"
				+ "\t\t ?dirEffect          tandr:hasReputationAspect  ?dirQualAspect      .\n"
				+ "\t\t ?dirQualAspect      tandr:hasAspectDescription <http://parliament.semwebcentral.org/parliament#tandrAspectQualDir> .\n"
				+ "\t\t ?dirQualAspect      tandr:hasAspectValue       ?dirQualValue       .\n"
				+ "\t\t ?dirQualValue       tandr:aspectValueIs        ?dirQualAspectValue .\n"
				+ "\t\t ?dirQualValue       tandr:computedAt           ?computedAt         .\n"
				+ "\t }\n"
				+ ""
				+ "\t OPTIONAL { \n"
				+ "\t\t ?dirEffect         tandr:hasReputationAspect  ?dirSemAspect      .\n"
				+ "\t\t ?dirSemAspect      tandr:hasAspectDescription <http://parliament.semwebcentral.org/parliament#tandrAspectSemDir> .\n"
				+ "\t\t ?dirSemAspect      tandr:hasAspectValue       ?dirSemValue       .\n"
				+ "\t\t ?dirSemValue       tandr:aspectValueIs        ?dirSemAspectValue .\n"
				+ "\t\t ?dirSemValue       tandr:computedAt           ?computedAt        .\n"
				+ "\t }\n"
				+ "";
		
		return queryString;
	}

	private String getIndirectAspectsQueryString() {

		String queryString = "";
		
		queryString += ""
				+ "\t OPTIONAL { \n"
				+ "\t\t ?indEffect          tandr:hasReputationAspect  ?indGeomAspect      .\n"
				+ "\t\t ?indGeomAspect      tandr:hasAspectDescription <http://parliament.semwebcentral.org/parliament#tandrAspectGeomInd> .\n"
				+ "\t\t ?indGeomAspect      tandr:hasAspectValue       ?indGeomValue       .\n"
				+ "\t\t ?indGeomValue       tandr:aspectValueIs        ?indGeomAspectValue .\n"
				+ "\t\t ?indGeomValue       tandr:computedAt           ?computedAt         .\n"
				+ "\t }\n"
				+ "\t OPTIONAL { \n"
				+ "\t\t ?indEffect          tandr:hasReputationAspect  ?indQualAspect      .\n"
				+ "\t\t ?indQualAspect      tandr:hasAspectDescription <http://parliament.semwebcentral.org/parliament#tandrAspectQualInd> .\n"
				+ "\t\t ?indQualAspect      tandr:hasAspectValue       ?indQualValue       .\n"
				+ "\t\t ?indQualValue       tandr:aspectValueIs        ?indQualAspectValue .\n"
				+ "\t\t ?indQualValue       tandr:computedAt           ?computedAt         .\n"
				+ "\t }\n"
				+ "\t OPTIONAL { \n"
				+ "\t\t ?indEffect         tandr:hasReputationAspect  ?indSemAspect      .\n"
				+ "\t\t ?indSemAspect      tandr:hasAspectDescription <http://parliament.semwebcentral.org/parliament#tandrAspectSemInd> .\n"
				+ "\t\t ?indSemAspect      tandr:hasAspectValue       ?indSemValue       .\n"
				+ "\t\t ?indSemValue       tandr:aspectValueIs        ?indSemAspectValue .\n"
				+ "\t\t ?indSemValue       tandr:computedAt           ?computedAt        .\n"
				+ "\t }\n";
		
		return queryString;
	}
	
	//****************//
	
	private String getTrustEffectsQueryString() {
		String queryString = "";
		
		queryString += ""
				+ "\t  ?tUri           tandr:hasTrustworthinessEffect ?dirEffect  .\n"
			    + "\t  ?dirEffect      tandr:hasEffectDescription     <http://parliament.semwebcentral.org/parliament#tandrEffectDirect> .\n"
				+ "\t  ?dirEffect      tandr:hasEffectValue           ?dirEffectValue     .\n"
				+ "\t  ?dirEffectValue tandr:effectValueIs            ?directEffectValue  .\n"
				+ "\t  ?dirEffectValue tandr:computedAt               ?computedAt .\n"
				+ "\n"
				+ "\t  ?tUri           tandr:hasTrustworthinessEffect  ?indEffect        .\n"
			    + "\t  ?indEffect      tandr:hasEffectDescription      <http://parliament.semwebcentral.org/parliament#tandrEffectIndirect>  .\n"
				+ "\t  ?indEffect      tandr:hasEffectValue            ?indEffectValue      .\n"
				+ "\t  ?indEffectValue tandr:effectValueIs             ?indirectEffectValue .\n"
				+ "\t  ?indEffectValue tandr:computedAt                ?computedAt          .\n"
				+ "\n"
				+ "\t  ?tUri            tandr:hasTrustworthinessEffect ?tempEffect       .\n"
				+ "\t  ?tempEffect      tandr:hasEffectDescription     <http://parliament.semwebcentral.org/parliament#tandrEffectTemporal>  .\n"
				+ "\t  ?tempEffect      tandr:hasEffectValue           ?tempEffectValue     .\n"
				+ "\t  ?tempEffectValue tandr:effectValueIs            ?temporalEffectValue .\n"
				+ "\t  ?tempEffectValue tandr:computedAt               ?computedAt          .\n"
				+ "\n"
				;
		
		return queryString;
	}
	
	private String getDirectTrustAspectsQueryString() {

		String queryString = "";
		
		queryString += ""
				+ "\t  OPTIONAL { \n"
				+ "\t\t ?dirEffect          tandr:hasTrustworthinessAspect ?dirGeomAspect      .\n"
				+ "\t\t ?dirGeomAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectGeomDir> .\n"	
				+ "\t\t ?dirGeomAspect      tandr:hasAspectValue           ?dirGeomValue       .\n"
				+ "\t\t ?dirGeomValue       tandr:aspectValueIs            ?dirGeomAspectValue .\n"
				+ "\t\t ?dirGeomValue       tandr:computedAt               ?computedAt         .\n"
				+ "\t  }\n"
				+ ""
				+ "\t  OPTIONAL { \n"
				+ "\t\t ?dirEffect          tandr:hasTrustworthinessAspect ?dirQualAspect      .\n"
				+ "\t\t ?dirQualAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectQualDir> .\n"
				+ "\t\t ?dirQualAspect      tandr:hasAspectValue           ?dirQualValue       .\n"
				+ "\t\t ?dirQualValue       tandr:aspectValueIs            ?dirQualAspectValue .\n"
				+ "\t\t ?dirQualValue       tandr:computedAt               ?computedAt         .\n"
				+ "\t  }\n"
				+ ""
				+ "\t  OPTIONAL { \n"
				+ "\t\t ?dirEffect         tandr:hasTrustworthinessAspect ?dirSemAspect      .\n"
				+ "\t\t ?dirSemAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectSemDir> .\n"
				+ "\t\t ?dirSemAspect      tandr:hasAspectValue           ?dirSemValue       .\n"
				+ "\t\t ?dirSemValue       tandr:aspectValueIs            ?dirSemAspectValue .\n"
				+ "\t\t ?dirSemValue       tandr:computedAt               ?computedAt        .\n"
				+ "\t  }\n"
				+ "";
		
		return queryString;
	}

	private String getIndirectTrustAspectsQueryString() {

		String queryString = "";
		
		queryString += ""
				+ "\t  OPTIONAL { \n"
				+ "\t\t ?indEffect          tandr:hasTrustworthinessAspect ?indGeomAspect      .\n"
				+ "\t\t ?indGeomAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectGeomInd> .\n"
				+ "\t\t ?indGeomAspect      tandr:hasAspectValue           ?indGeomValue       .\n"
				+ "\t\t ?indGeomValue       tandr:aspectValueIs            ?indGeomAspectValue .\n"
				+ "\t\t ?indGeomValue       tandr:computedAt               ?computedAt         .\n"
				+ "\t  }\n"
				+ "\t  OPTIONAL { \n"
				+ "\t\t ?indEffect          tandr:hasTrustworthinessAspect ?indQualAspect      .\n"
				+ "\t\t ?indQualAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectQualInd> .\n"
				+ "\t\t ?indQualAspect      tandr:hasAspectValue           ?indQualValue       .\n"
				+ "\t\t ?indQualValue       tandr:aspectValueIs            ?indQualAspectValue .\n"
				+ "\t\t ?indQualValue       tandr:computedAt               ?computedAt         .\n"
				+ "\t  }\n"
				+ "\t  OPTIONAL { \n"
				+ "\t\t ?indEffect         tandr:hasTrustworthinessAspect ?indSemAspect      .\n"
				+ "\t\t ?indSemAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectSemInd> .\n"
				+ "\t\t ?indSemAspect      tandr:hasAspectValue           ?indSemValue       .\n"
				+ "\t\t ?indSemValue       tandr:aspectValueIs            ?indSemAspectValue .\n"
				+ "\t\t ?indSemValue       tandr:computedAt               ?computedAt        .\n"
				+ "\t  }\n";
		
		return queryString;
	}
	
	private String getComputedAtQueryString(String untilDate, boolean graphUri) {
		String queryString = "";
		queryString += ""
				+ "\t  { \n"
				+ "\t   SELECT (MAX(?aspectTimeStamp) AS ?maxComputedAt)\n"
				+ "\t   WHERE \n"
				+ "\t   { \n";
		
		if (graphUri) queryString += "\t    GRAPH " +UConfig.getTANDRGraphURI()+ "\n\t   {\n";
		
		queryString += ""
				+ "\t     ?tUri1  tandr:refersToFeatureVersion  ?fvUri .\n"
				+ "\t     ?tUri1  tandr:hasTrustworthinessValue ?value1 .\n"
				+ "\t     ?value1 tandr:trustworthinessValueIs  ?trustworthinessValue1 .\n"
				+ "\t     ?value1 tandr:computedAt  ?aspectTimeStamp  .\n"
				;
		
		if (graphUri) queryString += "\t    }\n";
		
		queryString += "\t    FILTER( ?aspectTimeStamp <= \""+untilDate+"\"^^xsd:dateTime  )";
		
		queryString += ""
				+ "\n\t   } \n"
				+ "\t  } \n"
				;
		return queryString;
	}
	
	//***************//
	
	private MReputationTandr setTrustworthinessAttributes_prod(MReputationTandr reputation, String graphUri, QuerySolution generalQueryResults, int lazyDepth){
		
		FFoundationFacade ffacade = new FFoundationFacade();
		
		RDFNode refersToUserState = generalQueryResults.getResource("authorUri");
		RDFNode reputationValue   = generalQueryResults.getLiteral("RepValue");
		RDFNode computedAt  = generalQueryResults.getLiteral("timeStamp");
		
		if (refersToUserState != null){
			reputation.setAuthorUri(refersToUserState.toString());
			if ( lazyDepth > 0 ) {
				MAuthor author = (MAuthor) ffacade.retrieveByUri( refersToUserState.toString(), graphUri, lazyDepth-1, MAuthor.class); 
				reputation.setAuthor(author);
			}			
		}
		if (reputationValue != null)
			reputation.setValue( Double.parseDouble( reputationValue.toString() ));
		if (computedAt != null && ! computedAt.toString().equals(""))
			reputation.setComputedAt(computedAt.toString());		
		
		return reputation;
	}
	
	private MReputationTandr setTrustworthinessEffects_prod(MReputationTandr reputation,String graphUri, QuerySolution effectRawResults) {
		
		reputation.setDirectEffect(this.elaborateDirectEffects(effectRawResults));
		reputation.setIndirectEffect(this.elaborateIndirectEffects(effectRawResults));
		reputation.setTemporalEffect(this.elaborateTemporalEffects(effectRawResults));
		
		return reputation;
	}
	
	private MFDirectEffect elaborateDirectEffects(QuerySolution directEffectRawResults) {
				
		Double directEffectValue = 0.0, geomDirAspectValue = 0.0, qualDirAspectValue = 0.0, semDirAspectValue = 0.0;
		String computedAt = UConfig.getMinDateTimeAsString();
				
		directEffectValue   = Double.parseDouble( directEffectRawResults.getLiteral("DirValue").toString() );
		geomDirAspectValue  = Double.parseDouble( directEffectRawResults.getLiteral("GeomDirValue").toString() );
		qualDirAspectValue  = Double.parseDouble( directEffectRawResults.getLiteral("QualDirValue").toString() );
		semDirAspectValue   = Double.parseDouble( directEffectRawResults.getLiteral("SemDirValue").toString() );
		
		computedAt = directEffectRawResults.getLiteral("timeStamp").toString();
				
		MFDirectEffect directEffect = new MFDirectEffect( directEffectValue );
		MFDirectGeomAspect geometricAspect = new MFDirectGeomAspect( geomDirAspectValue );
		MFDirectQualAspect qualitativeAspect = new MFDirectQualAspect( qualDirAspectValue  );
		MFDirectSemAspect semanticAspect = new MFDirectSemAspect( semDirAspectValue );
		
		directEffect.setComputedAt(computedAt);
		geometricAspect.setComputedAt(computedAt);
		qualitativeAspect.setComputedAt(computedAt);
		semanticAspect.setComputedAt(computedAt);
		
		directEffect.setGeometricAspect(geometricAspect);
		directEffect.setQualitativeAspect(qualitativeAspect);
		directEffect.setSemanticAspect(semanticAspect);
		
		return directEffect;
	}
	
	private MFIndirectEffect elaborateIndirectEffects(QuerySolution indirectEffectRawResults) {
		
		Double indirectEffectValue = 0.0, geomIndAspectValue = 0.0, qualIndAspectValue = 0.0, semIndAspectValue = 0.0;
		String computedAt = UConfig.getMinDateTimeAsString();
		
		indirectEffectValue = Double.parseDouble( indirectEffectRawResults.getLiteral("IndValue").toString() );
		geomIndAspectValue  = Double.parseDouble( indirectEffectRawResults.getLiteral("GeomIndValue").toString() );
		qualIndAspectValue  = Double.parseDouble( indirectEffectRawResults.getLiteral("QualIndValue").toString() );
		semIndAspectValue   = Double.parseDouble( indirectEffectRawResults.getLiteral("SemIndValue").toString() );
		
		computedAt = indirectEffectRawResults.getLiteral("timeStamp").toString();
		
		MFIndirectEffect indirectEffect = new MFIndirectEffect( indirectEffectValue );
		MFIndirectGeomAspect geometricAspect = new MFIndirectGeomAspect( geomIndAspectValue );
		MFIndirectQualAspect qualitativeAspect = new MFIndirectQualAspect( qualIndAspectValue  );
		MFIndirectSemAspect semanticAspect = new MFIndirectSemAspect( semIndAspectValue );
		
		indirectEffect.setComputedAt(computedAt);
		geometricAspect.setComputedAt(computedAt);
		qualitativeAspect.setComputedAt(computedAt);
		semanticAspect.setComputedAt(computedAt);
		
		indirectEffect.setGeometricAspect(geometricAspect);
		indirectEffect.setQualitativeAspect(qualitativeAspect);
		indirectEffect.setSemanticAspect(semanticAspect);
		
		return indirectEffect;
	}
	
	private MFTemporalEffect elaborateTemporalEffects(QuerySolution temporalEffectRawResults) {

		Double temporalEffectValue = 0.0;
		String computedAt = UConfig.getMinDateTimeAsString();

		temporalEffectValue  = Double.parseDouble( temporalEffectRawResults.getLiteral("TempValue").toString() );
		computedAt = temporalEffectRawResults.getLiteral("timeStamp").toString();
		
		MFTemporalEffect temporalEffect = new MFTemporalEffect( temporalEffectValue );
		temporalEffect.setComputedAt(computedAt);
		
		return temporalEffect;
	}

}
