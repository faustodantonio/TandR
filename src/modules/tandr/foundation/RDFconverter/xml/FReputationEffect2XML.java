package modules.tandr.foundation.RDFconverter.xml;

import java.util.HashMap;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;

import utility.UConfig;

public class FReputationEffect2XML {

	private HashMap<String, Namespace> namespaces;
//	private FEffect feffect;
	
	public FReputationEffect2XML() {
		super();
		this.namespaces = UConfig.namespaces;
	}
	
	public Element createReputationEffectElement(String classEffectUri, String value, String computedAt){
		Element effectElement = null, effectType = null, effectDescription = null, effectValue = null;
		
		effectElement = new Element("Description",this.namespaces.get("rdf"));
		
		effectType = new Element("type", namespaces.get("rdf"));
		effectType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/tandr_assessment/tandr#ReputationEffect", namespaces.get("rdf")) );
		
		effectDescription = this.setElementEffectDescription(classEffectUri);
		effectValue = this.setEffectValue(value, computedAt);
		
		effectElement.addContent(effectType);
		effectElement.addContent(effectDescription);
		effectElement.addContent(effectValue);
		
		return effectElement;
	}
	
	public Element createReputationAspectElement(String classAspectUri, String value, String computedAt){
		Element hasAspectElement = null, aspectElement = null, aspectType = null, aspectDescription = null, aspectValue = null;
		
		hasAspectElement = new Element("hasReputationAspect",this.namespaces.get("tandr"));
		
		aspectElement = new Element("Description",this.namespaces.get("rdf"));
		
		aspectType = new Element("type", namespaces.get("rdf"));
		aspectType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/tandr_assessment/tandr#ReputationAspect", namespaces.get("rdf")) );
		
		aspectDescription = this.setElementAspectDescription(classAspectUri);
		aspectValue = this.setAspectValue(value, computedAt);
		
		aspectElement.addContent(aspectType);
		aspectElement.addContent(aspectDescription);
		aspectElement.addContent(aspectValue);
		
		hasAspectElement = aspectElement;
		
		return hasAspectElement;
	}
	
	private Element setElementAspectDescription(String classAspectUri)
	{
		Element hasAspectDescription = new Element("hasAspectDescription",this.namespaces.get("tandr"));
		hasAspectDescription.setAttribute("resource", "http://parliament.semwebcentral.org/parliament#" + "tandrAspect_" + classAspectUri, namespaces.get("rdf"));
		return hasAspectDescription;
	}
	
	private Element setAspectValue(String value, String computedAt)
	{
		Element hasValue, valueEl, valueElType,valueIs, computedAtEl;
		hasValue = new Element("hasAspectValue",namespaces.get("tandr"));
		valueEl = new Element("Description",namespaces.get("rdf"));
		valueElType = new Element("type", namespaces.get("rdf"));
		valueElType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/tandr_assessment/tandr#AspectValue", namespaces.get("rdf")) );
		
		valueIs = new Element("aspectValueIs",namespaces.get("tandr"));
		valueIs.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#decimal",this.namespaces.get("rdf")));
		valueIs.setText(value);
		
		computedAtEl = new Element("computedAt",namespaces.get("tandr"));
		computedAtEl.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#dateTime",this.namespaces.get("rdf")));
		computedAtEl.setText(computedAt);
		
		valueEl.addContent(valueElType);
		valueEl.addContent(valueIs);
		valueEl.addContent(computedAtEl);
		
		hasValue.addContent(valueEl);
		return hasValue;
	}	
	
	private Element setElementEffectDescription(String classEffectUri)
	{
		Element hasEffectDescription = new Element("hasEffectDescription",this.namespaces.get("tandr"));
		hasEffectDescription.setAttribute("resource", "http://parliament.semwebcentral.org/parliament#" + "tandrEffect_" + classEffectUri, namespaces.get("rdf"));
		return hasEffectDescription;
	}
	
	private Element setEffectValue(String value, String computedAt)
	{
		Element hasValue, valueEl, valueElType,valueIs, computedAtEl;
		hasValue = new Element("hasEffectValue",namespaces.get("tandr"));
		valueEl = new Element("Description",namespaces.get("rdf"));
		valueElType = new Element("type", namespaces.get("rdf"));
		valueElType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/tandr_assessment/tandr#EffectValue", namespaces.get("rdf")) );
		
		valueIs = new Element("effectValueIs",namespaces.get("tandr"));
		valueIs.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#decimal",this.namespaces.get("rdf")));
		valueIs.setText(value);
		
		computedAtEl = new Element("computedAt",namespaces.get("tandr"));
		computedAtEl.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#dateTime",this.namespaces.get("rdf")));
		computedAtEl.setText(computedAt);
		
		valueEl.addContent(valueElType);
		valueEl.addContent(valueIs);
		valueEl.addContent(computedAtEl);
		
		hasValue.addContent(valueEl);
		
		return hasValue;
	}
	
}
