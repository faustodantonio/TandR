package modules.tandr.foundation.RDFconverter.xml;

import java.util.ArrayList;
import java.util.HashMap;

import model.MReputation;
import model.MTrustworthiness;
import modules.tandr.model.MReputationTandr;
import modules.tandr.model.MTrustworthinessTandr;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import foundation.FTripleStore;
import utility.UConfig;

public class FReputation2XML {
	
	private HashMap<String, Namespace> namespaces;
	private FReputationEffect2XML feffectXML;
	
	public FReputation2XML(FTripleStore triplestore) {
		super();
		this.namespaces = UConfig.namespaces;
		this.feffectXML = new FReputationEffect2XML();
	}
	
	public Document convertToRDFXML(MReputation rep) {
		
		MReputationTandr reputation = new MReputationTandr(rep);
		Document reputationDoc = new Document();
		Element root = new  Element("RDF",namespaces.get("rdf"));
		ArrayList<Element> effectElementList = new ArrayList<Element>();
		
		Element reputation_el = null, reputation_type = null, 
				refersToAuthor = null, computedAt = null, hasReputationValue = null, reputationValueIs = null;
		
		reputation_el = new Element("Description",namespaces.get("rdf"));
		reputation_el.setAttribute("about", reputation.getUri(), namespaces.get("rdf"));
		
		reputation_type = new Element("type", namespaces.get("rdf"));
		reputation_type.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/tandr_assessment/tandr#Reputation", namespaces.get("rdf")) );
		
		refersToAuthor = new Element("refersToAuthor", this.namespaces.get("tandr"));
		refersToAuthor.setAttribute(new Attribute("resource", reputation.getAuthorUri(), namespaces.get("rdf")) );
		
		hasReputationValue = new Element("hasReputationValue",namespaces.get("tandr"));
		Element reputationValue = new Element("Description",namespaces.get("rdf"));
		
		computedAt = new Element("computedAt",namespaces.get("tandr"));
		computedAt.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#dateTime",this.namespaces.get("rdf")));
		computedAt.setText(reputation.getComputedAtString());
		
		reputationValueIs = new Element("trustworthinessValueIs",namespaces.get("tandr"));
		reputationValueIs.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#decimal",this.namespaces.get("rdf")));
		reputationValueIs.setText(reputation.getValue()+"");
		
		hasReputationValue.addContent(reputationValue);
		
		reputationValue.addContent(reputationValueIs);
		reputationValue.addContent(computedAt);
		
		reputation_el.addContent(reputation_type);
		reputation_el.addContent(refersToAuthor);
		reputation_el.addContent(hasReputationValue);
		
		effectElementList = this.createEffectsElements(reputation_el, reputation);
		
		for (Element effectElement : effectElementList)
			reputation_el.addContent(effectElement);
		
		root.addContent(reputation_el);
		
		reputationDoc.addContent(root);

		return reputationDoc;
	}
	
	private ArrayList<Element> createEffectsElements(Element trust_el, MReputationTandr reputation) {

		ArrayList<Element> effectElementList = new ArrayList<Element>(); 
		Element directEffect  = null, indirectEffect = null, temporalEffect = null,
				hasDirectEffect  = null, hasIndirectEffect = null, hasTemporalEffect = null;
		
		directEffect   = this.createDirectEffectElement(reputation);
		indirectEffect = this.createIndirectEffectElement(reputation);
		temporalEffect = feffectXML.createReputationEffectElement("temporal" , reputation.getTemporalEffect().getValue() + "",reputation.getTemporalEffect().getComputedAtString() + "");
		
		hasDirectEffect   = new Element("hasReputationEffect",this.namespaces.get("tandr"));
		hasIndirectEffect = new Element("hasReputationEffect",this.namespaces.get("tandr"));
		hasTemporalEffect = new Element("hasReputationEffect",this.namespaces.get("tandr"));
		
		hasDirectEffect.addContent(directEffect);
		hasIndirectEffect.addContent(indirectEffect);
		hasTemporalEffect.addContent(temporalEffect);
		
		effectElementList.add(hasDirectEffect);
		effectElementList.add(hasIndirectEffect);
		effectElementList.add(hasTemporalEffect);
		
		return effectElementList;
	}
	
	private Element createDirectEffectElement(MReputationTandr reputation){
	Element dirGeomAspect = null, dirQualAspect  = null, dirSemAspect   = null,
			hasDirGeomAspect = null, hasDirQualAspect  = null, hasDirSemAspect   = null;
	Element directEffect   = feffectXML.createReputationEffectElement("direct"   , reputation.getDirectEffect().getValue() + "", reputation.getDirectEffect().getComputedAtString() + "");
	
	dirGeomAspect = feffectXML.createReputationAspectElement("geomDir" , reputation.getDirectEffect().getGeometricAspect().getValue()   + "", reputation.getDirectEffect().getGeometricAspect().getComputedAtString()   + "");
	dirQualAspect = feffectXML.createReputationAspectElement("qualDir" , reputation.getDirectEffect().getQualitativeAspect().getValue() + "", reputation.getDirectEffect().getQualitativeAspect().getComputedAtString() + "");
	dirSemAspect  = feffectXML.createReputationAspectElement("semDir"  , reputation.getDirectEffect().getSemanticAspect().getValue()    + "", reputation.getDirectEffect().getSemanticAspect().getComputedAtString()    + "");

	hasDirGeomAspect = new Element("hasReputationAspect",this.namespaces.get("tandr"));
	hasDirQualAspect = new Element("hasReputationAspect",this.namespaces.get("tandr"));
	hasDirSemAspect  = new Element("hasReputationAspect",this.namespaces.get("tandr"));
	
	hasDirGeomAspect.addContent(dirGeomAspect);
	hasDirQualAspect.addContent(dirQualAspect);
	hasDirSemAspect.addContent(dirSemAspect);
	
	directEffect.addContent(hasDirGeomAspect);
	directEffect.addContent(hasDirQualAspect);
	directEffect.addContent(hasDirSemAspect);
	
	return directEffect;
}
	
	private Element createIndirectEffectElement(MReputationTandr reputation){
		Element indGeomAspect = null, indQualAspect  = null, indSemAspect   = null,
				hasIndGeomAspect = null, hasIndQualAspect  = null, hasIndSemAspect   = null;
		Element indirectEffect = feffectXML.createReputationEffectElement("indirect" , reputation.getIndirectEffect().getValue() + "", reputation.getIndirectEffect().getComputedAtString() + "");
		
		indGeomAspect = feffectXML.createReputationAspectElement("geomInd" , reputation.getIndirectEffect().getGeometricAspect().getValue()   + "", reputation.getIndirectEffect().getGeometricAspect().getComputedAtString()   + "");
		indQualAspect = feffectXML.createReputationAspectElement("qualInd" , reputation.getIndirectEffect().getQualitativeAspect().getValue() + "", reputation.getIndirectEffect().getQualitativeAspect().getComputedAtString() + "");
		indSemAspect  = feffectXML.createReputationAspectElement("semInd"  , reputation.getIndirectEffect().getSemanticAspect().getValue()    + "", reputation.getIndirectEffect().getSemanticAspect().getComputedAtString()    + "");
		
		hasIndGeomAspect = new Element("hasReputationAspect",this.namespaces.get("tandr"));
		hasIndQualAspect = new Element("hasReputationAspect",this.namespaces.get("tandr"));
		hasIndSemAspect  = new Element("hasReputationAspect",this.namespaces.get("tandr"));
		
		hasIndGeomAspect.addContent(indGeomAspect);
		hasIndQualAspect.addContent(indQualAspect);
		hasIndSemAspect.addContent(indSemAspect);
		
		indirectEffect.addContent(hasIndGeomAspect);
		indirectEffect.addContent(hasIndQualAspect);
		indirectEffect.addContent(hasIndSemAspect);
		
		return indirectEffect;
	}
		
	
}
