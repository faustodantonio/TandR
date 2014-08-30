package modules.tandr.foundation.RDFconverter.xml;

import java.util.ArrayList;
import java.util.HashMap;

import model.MTrustworthiness;
import modules.tandr.model.MTrustworthinessTandr;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import foundation.FTripleStore;
import utility.UConfig;

public class FTrustworthiness2XML {
	
	private HashMap<String, Namespace> namespaces;
	private FTrustworthinessEffect2XML feffect;
	
	public FTrustworthiness2XML(FTripleStore triplestore) {
		super();
		this.namespaces = UConfig.namespaces;
		this.feffect = new FTrustworthinessEffect2XML();
	}
	
	public Document convertToRDFXML(MTrustworthiness trust) {
		
		MTrustworthinessTandr trustworthiness = new MTrustworthinessTandr(trust);
		Document trustworthinessDoc = new Document();
		Element root = new  Element("RDF",namespaces.get("rdf"));
		ArrayList<Element> effectElementList = new ArrayList<Element>();
		
		Element trust_el = null, trust_type = null, 
				refersToFeatureVersion = null, computedAt = null, hasTrustworthinessValue = null, trustworthinessValueIs = null;
		
		trust_el = new Element("Description",namespaces.get("rdf"));
//		trust_el.setAttribute("about", "http://semantic.web/data/hvgi/#trustworthiness" + trustworthiness.getFeatureVersion().getVersionNo(), namespaces.get("rdf"));
		trust_el.setAttribute("about", trustworthiness.getUri(), namespaces.get("rdf"));
		
		trust_type = new Element("type", namespaces.get("rdf"));
		trust_type.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/tandr_assessment/tandr#Trustworthiness", namespaces.get("rdf")) );
		
		refersToFeatureVersion = new Element("refersToFeatureVersion", this.namespaces.get("tandr"));
		refersToFeatureVersion.setAttribute(new Attribute("resource", trustworthiness.getFeatureVersion().getUri(), namespaces.get("rdf")) );
		
		hasTrustworthinessValue = new Element("hasTrustworthinessValue",namespaces.get("tandr"));
		Element trustworthinessValue = new Element("Description",namespaces.get("rdf"));
		
		computedAt = new Element("computedAt",namespaces.get("tandr"));
		computedAt.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#dateTime",this.namespaces.get("rdf")));
		computedAt.setText(trustworthiness.getComputedAtString());
		
		trustworthinessValueIs = new Element("trustworthinessValueIs",namespaces.get("tandr"));
		trustworthinessValueIs.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#decimal",this.namespaces.get("rdf")));
		trustworthinessValueIs.setText(trustworthiness.getValue()+"");
		
		hasTrustworthinessValue.addContent(trustworthinessValue);
		
		trustworthinessValue.addContent(trustworthinessValueIs);
		trustworthinessValue.addContent(computedAt);
		
		trust_el.addContent(trust_type);
		trust_el.addContent(refersToFeatureVersion);
		trust_el.addContent(hasTrustworthinessValue);
		
		effectElementList = this.createEffectsElements(trust_el, trustworthiness);
		
		for (Element effectElement : effectElementList)
			trust_el.addContent(effectElement);
		
		root.addContent(trust_el);
		
		trustworthinessDoc.addContent(root);

		return trustworthinessDoc;
	}
	
	private ArrayList<Element> createEffectsElements(Element trust_el, MTrustworthinessTandr trustworthiness) {

		ArrayList<Element> effectElementList = new ArrayList<Element>(); 
		Element directEffect  = null, indirectEffect = null, temporalEffect = null,
				hasDirectEffect  = null, hasIndirectEffect = null, hasTemporalEffect = null;
		
		directEffect   = this.createDirectEffectElement(trustworthiness);
		indirectEffect = this.createIndirectEffectElement(trustworthiness);
		temporalEffect = feffect.createTrustworthinessEffectElement("temporal" , trustworthiness.getTemporalEffect().getValue() + "",trustworthiness.getTemporalEffect().getComputedAtString() + "");
		
		hasDirectEffect   = new Element("hasTrustworthinessEffect",this.namespaces.get("tandr"));
		hasIndirectEffect = new Element("hasTrustworthinessEffect",this.namespaces.get("tandr"));
		hasTemporalEffect = new Element("hasTrustworthinessEffect",this.namespaces.get("tandr"));
		
		hasDirectEffect.addContent(directEffect);
		hasIndirectEffect.addContent(indirectEffect);
		hasTemporalEffect.addContent(temporalEffect);
		
		effectElementList.add(hasDirectEffect);
		effectElementList.add(hasIndirectEffect);
		effectElementList.add(hasTemporalEffect);
		
		return effectElementList;
	}
	
	private Element createDirectEffectElement(MTrustworthinessTandr trustworthiness){
	Element dirGeomAspect = null, dirQualAspect  = null, dirSemAspect   = null,
			hasDirGeomAspect = null, hasDirQualAspect  = null, hasDirSemAspect   = null;
	Element directEffect   = feffect.createTrustworthinessEffectElement("direct"   , trustworthiness.getDirectEffect().getValue() + "", trustworthiness.getDirectEffect().getComputedAtString() + "");
	
	dirGeomAspect = feffect.createTrustworthinessAspectElement("geomDir" , trustworthiness.getDirectEffect().getGeometricAspect().getValue()   + "", trustworthiness.getDirectEffect().getGeometricAspect().getComputedAtString()   + "");
	dirQualAspect = feffect.createTrustworthinessAspectElement("qualDir" , trustworthiness.getDirectEffect().getQualitativeAspect().getValue() + "", trustworthiness.getDirectEffect().getQualitativeAspect().getComputedAtString() + "");
	dirSemAspect  = feffect.createTrustworthinessAspectElement("semDir"  , trustworthiness.getDirectEffect().getSemanticAspect().getValue()    + "", trustworthiness.getDirectEffect().getSemanticAspect().getComputedAtString()    + "");

	hasDirGeomAspect = new Element("hasTrustworthinessAspect",this.namespaces.get("tandr"));
	hasDirQualAspect = new Element("hasTrustworthinessAspect",this.namespaces.get("tandr"));
	hasDirSemAspect  = new Element("hasTrustworthinessAspect",this.namespaces.get("tandr"));
	
	hasDirGeomAspect.addContent(dirGeomAspect);
	hasDirQualAspect.addContent(dirQualAspect);
	hasDirSemAspect.addContent(dirSemAspect);
	
	directEffect.addContent(hasDirGeomAspect);
	directEffect.addContent(hasDirQualAspect);
	directEffect.addContent(hasDirSemAspect);
	
	return directEffect;
}
	
	private Element createIndirectEffectElement(MTrustworthinessTandr trustworthiness){
		Element indGeomAspect = null, indQualAspect  = null, indSemAspect   = null,
				hasIndGeomAspect = null, hasIndQualAspect  = null, hasIndSemAspect   = null;
		Element indirectEffect = feffect.createTrustworthinessEffectElement("indirect" , trustworthiness.getIndirectEffect().getValue() + "", trustworthiness.getIndirectEffect().getComputedAtString() + "");
		
		indGeomAspect = feffect.createTrustworthinessAspectElement("geomInd" , trustworthiness.getIndirectEffect().getGeometricAspect().getValue()   + "", trustworthiness.getIndirectEffect().getGeometricAspect().getComputedAtString()   + "");
		indQualAspect = feffect.createTrustworthinessAspectElement("qualInd" , trustworthiness.getIndirectEffect().getQualitativeAspect().getValue() + "", trustworthiness.getIndirectEffect().getQualitativeAspect().getComputedAtString() + "");
		indSemAspect  = feffect.createTrustworthinessAspectElement("semInd"  , trustworthiness.getIndirectEffect().getSemanticAspect().getValue()    + "", trustworthiness.getIndirectEffect().getSemanticAspect().getComputedAtString()    + "");
		
		hasIndGeomAspect = new Element("hasTrustworthinessAspect",this.namespaces.get("tandr"));
		hasIndQualAspect = new Element("hasTrustworthinessAspect",this.namespaces.get("tandr"));
		hasIndSemAspect  = new Element("hasTrustworthinessAspect",this.namespaces.get("tandr"));
		
		hasIndGeomAspect.addContent(indGeomAspect);
		hasIndQualAspect.addContent(indQualAspect);
		hasIndSemAspect.addContent(indSemAspect);
		
		indirectEffect.addContent(hasIndGeomAspect);
		indirectEffect.addContent(hasIndQualAspect);
		indirectEffect.addContent(hasIndSemAspect);
		
		return indirectEffect;
	}
		
	
}
