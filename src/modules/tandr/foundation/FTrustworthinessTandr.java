package modules.tandr.foundation;

import org.jdom2.Document;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;

import utility.UConfig;
import utility.UDebug;
import model.MFeatureVersion;
import model.MTrustworthiness;
import modules.tandr.foundation.RDFconverter.xml.FTrustworthiness2XML;
import modules.tandr.model.*;
import foundation.FFoundationAbstract;
import foundation.FTrustworthinessExport;
import foundation.FFoundationFacade;

public class FTrustworthinessTandr extends FFoundationAbstract implements FTrustworthinessExport {

	private int dbgLevel = 100;
	
	public FTrustworthinessTandr() {
		super();
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
		Document trustworthinessDoc = trustXML.convertToRDFXML((MTrustworthinessTandr)trust);
		
		String trustworthinessTriples = this.writeRDFXML(trustworthinessDoc);
		return trustworthinessTriples;
	}

	@Override
	public MTrustworthiness retrieveByURI(String trustworthinessUri, String graphUri, int lazyDepth) {
		return this.retrieveByURI_prod(trustworthinessUri, graphUri, lazyDepth);
	}

	
	public String retrieveTrustworthinessUri(String versionUri,String graphUri)
	{	
		String queryString = ""
				+ "\tSELECT ?trustworthinessUri\n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "\n\t {\n";
		
		queryString += ""
				+ "\t\tOPTIONAL { ?trustworthinessUri    tandr:refersToFeatureState    <"+versionUri+">     }\n"
				;
		
		if (!graphUri.equals("")) queryString += "\t }\n";
		
		queryString += ""
				+ "\t}																\n"
				;
		
		UDebug.print("Retriving date List \n", dbgLevel);
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", dbgLevel+1 );
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2);
		queryRawResults.reset();
		
		queryRawResults.hasNext();
		QuerySolution generalQueryResults = queryRawResults.next();
		RDFNode trustUri = generalQueryResults.getLiteral("trustworthinessUri");

		return trustUri.toString();
	}

	
	public MTrustworthiness retrieveByURI_prod(String trustworthinessUri, String graphUri, int lazyDepth) {
		
		MTrustworthinessTandr trustworthiness = new MTrustworthinessTandr();
		trustworthiness.setUri(trustworthinessUri);
		
		String queryString = ""
				+ "SELECT \n"
				+ "#Trustworthiness Info \n"
				+ " ?tUri ?fvUri (str(?computedAt) AS ?timeStamp) (str(?trustworthinessValue) AS ?TrustValue)\n"
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
				+ "\t ?tUri  tandr:refersToFeatureState    ?fvUri                .\n"
				+ "\t ?tUri  tandr:hasTrustworthinessValue ?value                .\n"
				+ "\t ?value tandr:trustworthinessValueIs  ?trustworthinessValue .\n"
				+ "\t ?value tandr:computedAt              ?computedAt           .\n" 
				;
		
		queryString += this.getEffectsQueryString();
		queryString += this.getDirectAspectsQueryString();
		queryString += this.getIndirectAspectsQueryString();
						
		if (!graphUri.equals("")) queryString += "\t}\n";
				
		queryString += ""
				+ " FILTER (?tUri = <"+trustworthinessUri+">)"
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
			trustworthiness = this.setTrustworthinessAttributes_prod(trustworthiness, graphUri, generalQueryResults, lazyDepth);
			trustworthiness = this.setTrustworthinessEffects_prod(trustworthiness, graphUri, generalQueryResults);
		}
		
		return trustworthiness;
	}
	
	private String getEffectsQueryString() {
		String queryString = "";
		
		queryString += ""
				+ "\t ?tUri           tandr:hasTrustworthinessEffect ?dirEffect  .\n"
			    + "\t ?dirEffect      tandr:hasEffectDescription     <http://parliament.semwebcentral.org/parliament#tandrEffectDirect> .\n"
				+ "\t ?dirEffect      tandr:hasEffectValue           ?dirEffectValue     .\n"
				+ "\t ?dirEffectValue tandr:effectValueIs            ?directEffectValue  .\n"
				+ "\t ?dirEffectValue tandr:computedAt               ?computedAt .\n"
				+ "\n";
				
		queryString += ""
				+ "\t ?tUri           tandr:hasTrustworthinessEffect  ?indEffect        .\n"
			    + "\t ?indEffect      tandr:hasEffectDescription      <http://parliament.semwebcentral.org/parliament#tandrEffectIndirect>  .\n"
				+ "\t ?indEffect      tandr:hasEffectValue            ?indEffectValue      .\n"
				+ "\t ?indEffectValue tandr:effectValueIs             ?indirectEffectValue .\n"
				+ "\t ?indEffectValue tandr:computedAt                ?computedAt          .\n"
				+ "\n";
				
		queryString += ""
				+ "\t ?tUri            tandr:hasTrustworthinessEffect ?tempEffect       .\n"
				+ "\t ?tempEffect      tandr:hasEffectDescription     <http://parliament.semwebcentral.org/parliament#tandrEffectTemporal>  .\n"
				+ "\t ?tempEffect      tandr:hasEffectValue           ?tempEffectValue     .\n"
				+ "\t ?tempEffectValue tandr:effectValueIs            ?temporalEffectValue .\n"
				+ "\t ?tempEffectValue tandr:computedAt               ?computedAt          .\n"
				+ "\n"
				;
		
		return queryString;
	}
	
	private String getDirectAspectsQueryString() {

		String queryString = "";
		
		queryString += ""
				+ "\t OPTIONAL { \n"
				+ "\t\t ?dirEffect          tandr:hasTrustworthinessAspect ?dirGeomAspect      .\n"
				+ "\t\t ?dirGeomAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectGeomDir> .\n"	
				+ "\t\t ?dirGeomAspect      tandr:hasAspectValue           ?dirGeomValue       .\n"
				+ "\t\t ?dirGeomValue       tandr:aspectValueIs            ?dirGeomAspectValue .\n"
				+ "\t\t ?dirGeomValue       tandr:computedAt               ?computedAt         .\n"
				+ "\t }\n"
				+ ""
				+ "\t OPTIONAL { \n"
				+ "\t\t ?dirEffect          tandr:hasTrustworthinessAspect ?dirQualAspect      .\n"
				+ "\t\t ?dirQualAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectQualDir> .\n"
				+ "\t\t ?dirQualAspect      tandr:hasAspectValue           ?dirQualValue       .\n"
				+ "\t\t ?dirQualValue       tandr:aspectValueIs            ?dirQualAspectValue .\n"
				+ "\t\t ?dirQualValue       tandr:computedAt               ?computedAt         .\n"
				+ "\t }\n"
				+ ""
				+ "\t OPTIONAL { \n"
				+ "\t\t ?dirEffect         tandr:hasTrustworthinessAspect ?dirSemAspect      .\n"
				+ "\t\t ?dirSemAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectSemDir> .\n"
				+ "\t\t ?dirSemAspect      tandr:hasAspectValue           ?dirSemValue       .\n"
				+ "\t\t ?dirSemValue       tandr:aspectValueIs            ?dirSemAspectValue .\n"
				+ "\t\t ?dirSemValue       tandr:computedAt               ?computedAt        .\n"
				+ "\t }\n"
				+ "";
		
		return queryString;
	}

	private String getIndirectAspectsQueryString() {

		String queryString = "";
		
		queryString += ""
				+ "\t OPTIONAL { \n"
				+ "\t\t ?indEffect          tandr:hasTrustworthinessAspect ?indGeomAspect      .\n"
				+ "\t\t ?indGeomAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectGeomInd> .\n"
				+ "\t\t ?indGeomAspect      tandr:hasAspectValue           ?indGeomValue       .\n"
				+ "\t\t ?indGeomValue       tandr:aspectValueIs            ?indGeomAspectValue .\n"
				+ "\t\t ?indGeomValue       tandr:computedAt               ?computedAt         .\n"
				+ "\t }\n"
				+ "\t OPTIONAL { \n"
				+ "\t\t ?indEffect          tandr:hasTrustworthinessAspect ?indQualAspect      .\n"
				+ "\t\t ?indQualAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectQualInd> .\n"
				+ "\t\t ?indQualAspect      tandr:hasAspectValue           ?indQualValue       .\n"
				+ "\t\t ?indQualValue       tandr:aspectValueIs            ?indQualAspectValue .\n"
				+ "\t\t ?indQualValue       tandr:computedAt               ?computedAt         .\n"
				+ "\t }\n"
				+ "\t OPTIONAL { \n"
				+ "\t\t ?indEffect         tandr:hasTrustworthinessAspect ?indSemAspect      .\n"
				+ "\t\t ?indSemAspect      tandr:hasAspectDescription     <http://parliament.semwebcentral.org/parliament#tandrAspectSemInd> .\n"
				+ "\t\t ?indSemAspect      tandr:hasAspectValue           ?indSemValue       .\n"
				+ "\t\t ?indSemValue       tandr:aspectValueIs            ?indSemAspectValue .\n"
				+ "\t\t ?indSemValue       tandr:computedAt               ?computedAt        .\n"
				+ "\t }\n";
		
		return queryString;
	}
	
	private MTrustworthinessTandr setTrustworthinessAttributes_prod(MTrustworthinessTandr trustworthiness, String graphUri, QuerySolution generalQueryResults, int lazyDepth){
		
		FFoundationFacade ffacade = new FFoundationFacade();
		
		RDFNode refersToFeatureState = generalQueryResults.getResource("fvUri");
		RDFNode trustworthinessValue   = generalQueryResults.getLiteral("TrustValue");
		RDFNode computedAt  = generalQueryResults.getLiteral("timeStamp");
		
		if (refersToFeatureState != null){
			trustworthiness.setFeatureVersionUri(refersToFeatureState.toString());
			if ( lazyDepth > 0 ) {
				MFeatureVersion featureVersion = (MFeatureVersion) ffacade.retrieveByUri( refersToFeatureState.toString(), graphUri, lazyDepth-1, MFeatureVersion.class); 
				trustworthiness.setFeatureVersion(featureVersion);
			}			
		}
		if (trustworthinessValue != null)
			trustworthiness.setValue( Double.parseDouble(trustworthinessValue.toString()));
		if (computedAt != null && ! computedAt.toString().equals(""))
			trustworthiness.setComputedAt(computedAt.toString());		
		
		return trustworthiness;
	}
	
	private MTrustworthinessTandr setTrustworthinessEffects_prod(MTrustworthinessTandr trustworthiness,String graphUri, QuerySolution effectRawResults) {
		
		trustworthiness.setDirectEffect(this.elaborateDirectEffects(effectRawResults));
		trustworthiness.setIndirectEffect(this.elaborateIndirectEffects(effectRawResults));
		trustworthiness.setTemporalEffect(this.elaborateTemporalEffects(effectRawResults));
		
		return trustworthiness;
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
