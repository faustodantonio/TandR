package modules.tandr.foundation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;

import utility.UConfig;
import utility.UDebug;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;

import foundation.FTripleStore;
import model.MTrustworthiness;
import modules.tandr.model.*;

public class FEffect {

	private FTripleStore triplestore;
	private HashMap<String, Namespace> namespaces;
	
	public FEffect() {	}
	
	public FEffect(FTripleStore triplestore) {	
		this.triplestore = triplestore; 
		this.namespaces = UConfig.namespaces;
	}

	public Map<String,MFEffect> retrieveTrustworthinessEffectList(MTrustworthiness trustworthiness) {

		String trustworthinessUri = trustworthiness.getUri();
		Map<String,MFEffect> effects = new HashMap<String, MFEffect>();
		
		ResultSetRewindable directEffectRawResults;
		ResultSetRewindable indirectEffectRawResults;
		ResultSetRewindable temporalEffectRawResults;
		
		directEffectRawResults   = this.retrieveDirectEffectRawResult(trustworthinessUri);
		indirectEffectRawResults = this.retrieveIndirectEffectRawResult(trustworthinessUri);
		temporalEffectRawResults = this.retrieveTemporalEffectRawResult(trustworthinessUri);
		
		effects.put("direct", this.elaborateDirectEffects(directEffectRawResults) );
		effects.put("indirect", this.elaborateIndirectEffects(indirectEffectRawResults) );
		effects.put("temporal", this.elaborateTemporalEffects(temporalEffectRawResults) );	
		
		return effects;
	}
	
	/*************************
	 * 
	 * Get Query Result FUNCTIONS
	 *
	 *************************/	
	
	private ResultSetRewindable retrieveDirectEffectRawResult(String trustworthinessUri) {
		
		String queryString = ""
				+ "\tSELECT ?directEffectValue, ?geomDirAspectValue, ?qualDirAspectValue, ?semDirAspectValue   \n"
				+ "\tWHERE \n"
				+ "\t{ \n"
					+ "\t\t <"+trustworthinessUri+">" + " hvgi:hasTrustworthinessEffect       ?effect  .\n"
					+ "\t\t  		?effect      tandr:effectNameIs      \"Direct Effect\"             .\n"
					+ "\t\t  		?effect      tandr:effectValueIs     ?directEffectValue            .\n"
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
					+ "      }                                                                                         \n"
				+ "\t}";	
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", 5);
		
		ResultSet rawResults = this.triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",6);
		queryRawResults.reset();
		
		return queryRawResults;
	}
	
	private ResultSetRewindable retrieveIndirectEffectRawResult(	String trustworthinessUri) {
		String queryString = ""
				+ "\tSELECT ?indirectEffectValue, ?geomIndAspectValue, ?qualIndAspectValue, ?semIndAspectValue \n"
				+ "\tWHERE \n"
				+ "\t{ \n"
					+ "\t\t <"+trustworthinessUri+">" + " hvgi:hasTrustworthinessEffect       ?effect  .\n"
					+ "\t\t  		?effect      tandr:effectNameIs      \"Indirect Effect\"           .\n"
					+ "\t\t  		?effect      tandr:effectValueIs     ?indirectEffectValue          .\n"
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
					+ "\t\t                  }                                                                           \n"
					+ "      }                                                                                           \n"
				+ "\t}";
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", 5);
		
		ResultSet rawResults = this.triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",6);
		queryRawResults.reset();
		
		return queryRawResults;
	}

	private ResultSetRewindable retrieveTemporalEffectRawResult(	String trustworthinessUri) {
		String queryString = ""
				+ "\tSELECT * \n"
				+ "\tWHERE \n"
				+ "\t{ \n"
					+ "\t\t <"+trustworthinessUri+">" + " hvgi:hasTrustworthinessEffect ?temporalEffect  .\n"
					+ "\t\t ?temporalEffect      tandr:effectNameIs      \"Temporal Effect\"               .\n"
					+ "\t\t ?temporalEffect      tandr:effectValueIs     ?temporalEffectValue              .\n"						
				+ "\t}";	
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", 5);
		
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
		QuerySolution generalQueryResults = directEffectRawResults.next();
		
		RDFNode directEffectValue   = generalQueryResults.getLiteral("directEffectValue");
		RDFNode geomDirAspectValue  = generalQueryResults.getLiteral("geomDirAspectValue");
		RDFNode qualDirAspectValue  = generalQueryResults.getLiteral("qualDirAspectValue");
		RDFNode semDirAspectValue   = generalQueryResults.getLiteral("semDirAspectValue");
		
		MFDirectEffect directEffect = new MFDirectEffect( Double.parseDouble(directEffectValue.toString()) );
		MFDirectGeomAspect geometricAspect = new MFDirectGeomAspect( Double.parseDouble(geomDirAspectValue.toString()) );
		MFDirectQualAspect qualitativeAspect = new MFDirectQualAspect( Double.parseDouble(qualDirAspectValue.toString())  );
		MFDirectSemAspect semanticAspect = new MFDirectSemAspect( Double.parseDouble(semDirAspectValue.toString()) );
		
		directEffect.setGeometricAspect(geometricAspect);
		directEffect.setQualitativeAspect(qualitativeAspect);
		directEffect.setSemanticAspect(semanticAspect);
		
		return directEffect;
	}

	private MFIndirectEffect elaborateIndirectEffects(ResultSetRewindable indirectEffectRawResults) {
		indirectEffectRawResults.reset();
		QuerySolution generalQueryResults = indirectEffectRawResults.next();
		
		RDFNode directEffectValue   = generalQueryResults.getLiteral("indirectEffectValue");
		RDFNode geomIndAspectValue  = generalQueryResults.getLiteral("geomIndAspectValue");
		RDFNode qualIndAspectValue  = generalQueryResults.getLiteral("qualIndAspectValue");
		RDFNode semIndAspectValue   = generalQueryResults.getLiteral("semIndAspectValue");
		
		MFIndirectEffect indirectEffect = new MFIndirectEffect( Double.parseDouble(directEffectValue.toString()) );
		MFIndirectGeomAspect geometricAspect = new MFIndirectGeomAspect( Double.parseDouble(geomIndAspectValue.toString()) );
		MFIndirectQualAspect qualitativeAspect = new MFIndirectQualAspect( Double.parseDouble(qualIndAspectValue.toString())  );
		MFIndirectSemAspect semanticAspect = new MFIndirectSemAspect( Double.parseDouble(semIndAspectValue.toString()) );
		
		indirectEffect.setGeometricAspect(geometricAspect);
		indirectEffect.setQualitativeAspect(qualitativeAspect);
		indirectEffect.setSemanticAspect(semanticAspect);
		
		return indirectEffect;
	}

	private MFTemporalEffect elaborateTemporalEffects(ResultSetRewindable temporalEffectRawResults) {
		temporalEffectRawResults.reset();
		QuerySolution generalQueryResults = temporalEffectRawResults.next();
		
		RDFNode directEffectValue  = generalQueryResults.getLiteral("temporalEffectValue");
		MFTemporalEffect temporalEffect = new MFTemporalEffect( Double.parseDouble(directEffectValue.toString()) );
		return temporalEffect;
	}
	
	Element createEffectElement(String classEffectUri, String value){
		Element effectElement = null, effectType = null, effectDescription = null, effectValue = null;
		
		effectElement = new Element("Description",this.namespaces.get("rdf"));
		
		effectType = new Element("type", namespaces.get("rdf"));
		effectType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/tandr_assessment/tandr#TrustworthinessEffect", namespaces.get("rdf")) );
		
		effectDescription = this.setElementEffectDescription(classEffectUri);
		effectValue = this.setValue(value);
		
		effectElement.addContent(effectType);
		effectElement.addContent(effectDescription);
		effectElement.addContent(effectValue);
		
		return effectElement;
	}
	
	Element createAspectElement(String classAspectUri, String value){
		Element aspectElement = null, aspectType = null, aspectDescription = null, aspectValue = null;
		
		aspectElement = new Element("Description",this.namespaces.get("rdf"));
		
		aspectType = new Element("type", namespaces.get("rdf"));
		aspectType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/tandr_assessment/tandr#TrustworthinessAspect", namespaces.get("rdf")) );
		
		aspectDescription = this.setElementAspectDescription(classAspectUri);
		aspectValue = this.setValue(value);
		
		aspectElement.addContent(aspectType);
		aspectElement.addContent(aspectDescription);
		aspectElement.addContent(aspectValue);
		
		return aspectElement;
	}
	
	Element createEffectDescriptionElement(String name, String classEffectUri, String description){
		Element effectElement = null, effectType = null, effectname = null, effectDescription = null, definedBy1 = null, definedBy2 = null;
		
		effectElement = new Element("Description",this.namespaces.get("rdf"));
		effectElement.setAttribute("about", "http://parliament.semwebcentral.org/parliament#" + "tandrEffect_" + classEffectUri, namespaces.get("rdf"));
		
		effectType = new Element("type", namespaces.get("rdf"));
		effectType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/tandr_assessment/tandr#EffectDescription", namespaces.get("rdf")) );
		
		effectname = this.setElementNameIs(name);
		effectDescription = this.setElementDescriptionIs(description);
		
		definedBy1 = this.setElementDefinedBy("Fausto D'Antonio");
		definedBy2 = this.setElementDefinedBy("Paolo Fogliaroni");
		
		effectElement.addContent(effectType);
		effectElement.addContent(effectname);
		effectElement.addContent(effectDescription);
		effectElement.addContent(definedBy1);
		effectElement.addContent(definedBy2);
		
		return effectElement;
	}
	
	Element createAspectDescriptionElement(String name, String classAspectUri, String description){
		Element aspectElement = null, aspectType = null,
				aspectName = null, aspectDescription = null, definedBy1 = null, definedBy2 = null;
		
		aspectElement = new Element("Description",this.namespaces.get("rdf"));
		aspectElement.setAttribute("about", "http://parliament.semwebcentral.org/parliament#" + "tandrAspect_" + classAspectUri, namespaces.get("rdf"));
		
		aspectType = new Element("type", namespaces.get("rdf"));
		aspectType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/tandr_assessment/tandr#AspectDescription", namespaces.get("rdf")) );
		
		aspectName = this.setElementNameIs(name);
		aspectDescription = this.setElementDescriptionIs(description);
		
		definedBy1 = this.setElementDefinedBy("Fausto D'Antonio");
		definedBy2 = this.setElementDefinedBy("Paolo Fogliaroni");
		
		aspectElement.addContent(aspectType);
		aspectElement.addContent(aspectName);
		aspectElement.addContent(aspectDescription);	
		aspectElement.addContent(definedBy1);
		aspectElement.addContent(definedBy2);
		
		return aspectElement;
	}
	
	private Element setElementEffectDescription(String classEffectUri)
	{
		Element descriptionEffect = new Element("Description",this.namespaces.get("rdf"));
		descriptionEffect.setAttribute("resource", "http://parliament.semwebcentral.org/parliament#" + "tandrEffect_" + classEffectUri, namespaces.get("rdf"));
		return descriptionEffect;
	}
	
	private Element setElementAspectDescription(String classAspectUri)
	{
		Element descriptionEffect = new Element("Description",this.namespaces.get("rdf"));
		descriptionEffect.setAttribute("resource", "http://parliament.semwebcentral.org/parliament#" + "tandrEffect_" + classAspectUri, namespaces.get("rdf"));
		return descriptionEffect;
	}
	
	private Element setElementNameIs(String name)
	{
		Element name_el = new Element("effectNameIs",namespaces.get("tandr"));
		name_el.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#string",this.namespaces.get("rdf")));
		name_el.setText(name);
		return name_el;
	}
	
	private Element setElementDescriptionIs(String description)
	{
		Element description_el = new Element("effectDescriptionIs",namespaces.get("tandr"));
		description_el.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#string",this.namespaces.get("rdf")));
		description_el.setText(description);
		return description_el;
	}
	
	private Element setElementDefinedBy(String authorName)
	{
		Element definedBy = new Element("definedBy",namespaces.get("tandr"));
		Element foafPerson = new Element("Description",this.namespaces.get("rdf"));
		
		Element foafName = new Element("name",this.namespaces.get("foaf"));
		foafName.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#string",this.namespaces.get("rdf")));
		foafName.setText(authorName);
		
		foafPerson.addContent(foafName);
		definedBy.addContent(foafPerson);
		return definedBy;
	}
	
	private Element setValue(String value)
	{
		Element value_el = new Element("effectValueIs",namespaces.get("tandr"));
		value_el.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#decimal",this.namespaces.get("rdf")));
		value_el.setText(value);
		return value_el;
	}

}
