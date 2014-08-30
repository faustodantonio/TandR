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
	
	public Element createEffectDescriptionElement(String name, String classEffectUri, String description){
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
	
	public Element createAspectDescriptionElement(String name, String classAspectUri, String description){
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
		Element hasEffectDescription = new Element("hasEffectDescription",this.namespaces.get("tandr"));
		hasEffectDescription.setAttribute("resource", "http://parliament.semwebcentral.org/parliament#" + "tandrEffect_" + classEffectUri, namespaces.get("rdf"));
		return hasEffectDescription;
	}
	
	private Element setElementAspectDescription(String classAspectUri)
	{
		Element hasAspectDescription = new Element("hasAspectDescription",this.namespaces.get("tandr"));
		hasAspectDescription.setAttribute("resource", "http://parliament.semwebcentral.org/parliament#" + "tandrAspect_" + classAspectUri, namespaces.get("rdf"));
		return hasAspectDescription;
	}
	
	private Element setElementNameIs(String name)
	{
		Element nameIs = new Element("effectNameIs",namespaces.get("tandr"));
		nameIs.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#string",this.namespaces.get("rdf")));
		nameIs.setText(name);
		return nameIs;
	}
	
	private Element setElementDescriptionIs(String description)
	{
		Element descriptionIs = new Element("effectDescriptionIs",namespaces.get("tandr"));
		descriptionIs.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#string",this.namespaces.get("rdf")));
		descriptionIs.setText(description);
		return descriptionIs;
	}
	
	private Element setElementDefinedBy(String authorName)
	{
		Element definedBy = new Element("definedBy",namespaces.get("tandr"));
		Element foafPerson = new Element("Description",this.namespaces.get("rdf"));
		Element foafPersonType = new Element("type", namespaces.get("rdf")); 
		foafPersonType.setAttribute(new Attribute("resource", "http://xmlns.com/foaf/0.1/Person", namespaces.get("rdf")) );
		
		Element foafName = new Element("name",this.namespaces.get("foaf"));
		foafName.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#string",this.namespaces.get("rdf")));
		foafName.setText(authorName);
		
		foafPerson.addContent(foafName);
		foafPerson.addContent(foafPersonType);
		definedBy.addContent(foafPerson);
		return definedBy;
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
	
}
