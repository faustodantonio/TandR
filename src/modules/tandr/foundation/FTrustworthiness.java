package modules.tandr.foundation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;

import utility.UConfig;
import utility.UDebug;
import model.MFeature;
import model.MFeatureVersion;
import model.MTrustworthiness;
import modules.tandr.model.MFDirectEffect;
import modules.tandr.model.MFEffect;
import modules.tandr.model.MFIndirectEffect;
import modules.tandr.model.MFTemporalEffect;
import modules.tandr.model.MTrustworthinessTandr;
import foundation.FTrustworthinessExport;
import foundation.FFoundationFacade;

public class FTrustworthiness extends FTrustworthinessExport {

	private HashMap<String, Namespace> namespaces;
	private FEffect feffect;
	
	public FTrustworthiness() {	
		this.namespaces = UConfig.namespaces;
		this.feffect = new FEffect();
	}
	
	@Override
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
				+ "\t\tOPTIONAL { <"+trustworthinessUri+">" + " tandr:computedAt              ?coumputedAt }          \n"
				
				+ "\t\tOPTIONAL { <"+trustworthinessUri+">" + " hvgi:hasTrustworthinessEffect       ?effect  ."
//						+ "  		?effect      tandr:effectNameIs      ?effectName                         ."
						+ "  		?effect      tandr:effectValueIs     ?effectValue                        ."
						+ "  		?effect      tandr:hasEffectDescription      ?description                ."
						+ "  		?description tandr:effectNameIs      ?effectName                         ."
						+ "\t\t\t   OPTIONAL { ?effect   hvgi:hasTrustworthinessAspect   ?aspect             ."
						+ "  		           ?aspect   tandr:aspectNameIs              ?aspectName         ."
						+ "  		           ?aspect   tandr:aspectValueIs             ?aspectValue        ."
						+ "                }                                                                \n"
						+ "      }                                                                          \n";
						
		if (!graphUri.equals("")) queryString += "\t}\n";
				
		queryString = ""
				+ "\t}";	
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", 5);
		
		ResultSet rawResults = super.triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",6);
		queryRawResults.reset();
		
		QuerySolution generalQueryResults = queryRawResults.next();
		
		trustworthiness = this.setTrustworthinessAttributes(trustworthiness, graphUri, generalQueryResults, lazyDepth);
		trustworthiness = this.setTrustworthinessEffects(trustworthiness);
		
		return trustworthiness;
	}
	
	private MTrustworthinessTandr setTrustworthinessEffects(MTrustworthinessTandr trustworthiness) {
		Map<String,MFEffect> effects = new HashMap<String,MFEffect>();
		effects = feffect.retrieveTrustworthinessEffectList(trustworthiness);
		trustworthiness.setDirect((MFDirectEffect) effects.get("direct"));
		trustworthiness.setIndirect((MFIndirectEffect) effects.get("indirect"));
		trustworthiness.setTemporal((MFTemporalEffect) effects.get("temporal"));
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

	@Override
	public String convertToRDF(MTrustworthiness trust) {
		
		MTrustworthinessTandr trustworthiness = new MTrustworthinessTandr(trust);
		String trustworthinessTriples = "";
		Document trustworthinessDoc = new Document();
		ArrayList<Element> effectElementList = new ArrayList<Element>();
		
		Element trust_el = null, trust_type = null, 
				refersToFeatureVersion = null, computedAt = null, hasTrustworthinessValue = null;
		
		trust_el = new Element("Description",namespaces.get("rdf"));
		trust_el.setAttribute("about", "http://semantic.web/data/hvgi/#trustworthiness" + trustworthiness.getFeatureVersion().getVersionNo(), namespaces.get("rdf"));
		
		trust_type = new Element("type", namespaces.get("rdf"));
		trust_type.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/tandr_assessment/tandr#Trustworthiness", namespaces.get("rdf")) );
		
		refersToFeatureVersion = new Element("refersToFeatureVersion", this.namespaces.get("tandr"));
		refersToFeatureVersion.setAttribute(new Attribute("resource", trustworthiness.getFeatureVersion().getUri(), namespaces.get("rdf")) );
		
		computedAt = new Element("computedAt",namespaces.get("tandr"));
		computedAt.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#dateTime",this.namespaces.get("rdf")));
		computedAt.setText(trustworthiness.getComputedAtString());
		
		hasTrustworthinessValue = new Element("hasTrustworthinessValue",namespaces.get("tandr"));
		hasTrustworthinessValue.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#decimal",this.namespaces.get("rdf")));
		hasTrustworthinessValue.setText(trustworthiness.getValue()+"");
		
		trust_el.addContent(trust_type);
		trust_el.addContent(refersToFeatureVersion);
		trust_el.addContent(computedAt);
		trust_el.addContent(hasTrustworthinessValue);
		
		effectElementList = this.createEffectsElements(trust_el, trustworthiness);
		
		for (Element effectElement : effectElementList)
			trust_el.addContent(effectElement);
		
		trustworthinessDoc.addContent(trust_el);
		
		trustworthinessTriples = this.writeDocument(trustworthinessDoc);
		return trustworthinessTriples;
	}
	
	private ArrayList<Element> createEffectsElements(Element trust_el, MTrustworthinessTandr trustworthiness) {
		
		ArrayList<Element> effectElementList = new ArrayList<Element>(); 
		Element directEffect  = null, indirectEffect = null, temporalEffect = null,
				dirGeomAspect = null, dirQualAspect  = null, dirSemAspect   = null, 
				indGeomAspect = null, indQualAspect  = null, indSemAspect   = null;
		
		directEffect   = feffect.createEffectElement("direct"   , trustworthiness.getDirect().getValue()   + "");
		indirectEffect = feffect.createEffectElement("indirect" , trustworthiness.getIndirect().getValue() + "");
		temporalEffect = feffect.createEffectElement("temporal" , trustworthiness.getTemporal().getValue() + "");
		
		dirGeomAspect = feffect.createAspectElement("geomDir" , trustworthiness.getDirect().getGeometricAspect().getValue()   + "");
		dirQualAspect = feffect.createAspectElement("qualDir" , trustworthiness.getDirect().getQualitativeAspect().getValue() + "");
		dirSemAspect  = feffect.createAspectElement("semDir"  , trustworthiness.getDirect().getSemanticAspect().getValue()    + "");
		
		indGeomAspect = feffect.createAspectElement("geomInd" , trustworthiness.getIndirect().getGeometricAspect().getValue()   + "");
		indQualAspect = feffect.createAspectElement("qualInd" , trustworthiness.getIndirect().getQualitativeAspect().getValue() + "");
		indSemAspect  = feffect.createAspectElement("semInd"  , trustworthiness.getIndirect().getSemanticAspect().getValue()    + "");
		
		directEffect.addContent(dirGeomAspect);
		directEffect.addContent(dirQualAspect);
		directEffect.addContent(dirSemAspect);
		
		indirectEffect.addContent(indGeomAspect);
		indirectEffect.addContent(indQualAspect);
		indirectEffect.addContent(indSemAspect);
		
		effectElementList.add(directEffect);
		effectElementList.add(indirectEffect);
		effectElementList.add(temporalEffect);
		
		return effectElementList;
	}
	
	private String writeDocument(Document document)
	{
		String output ="";
		
		switch ( UConfig.rdf_output_format )
		{
			case 0 : output = new XMLOutputter(Format.getRawFormat()    ).outputString(document); break;
			case 1 : output = new XMLOutputter(Format.getCompactFormat()).outputString(document); break;
			case 2 : output = new XMLOutputter(Format.getPrettyFormat() ).outputString(document); break;
			default: output = new XMLOutputter(Format.getRawFormat()    ).outputString(document); break;
		}
		 
		output = output.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
//		output = output.substring( output.indexOf('\n')+1 );
		
		return output;	
	}
}
