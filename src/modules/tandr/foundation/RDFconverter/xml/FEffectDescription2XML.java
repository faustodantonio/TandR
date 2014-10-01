package modules.tandr.foundation.RDFconverter.xml;

import java.util.HashMap;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;

import utility.UConfig;

public class FEffectDescription2XML {

	private HashMap<String, Namespace> namespaces;
	
	public FEffectDescription2XML() {
		super();
		this.namespaces = UConfig.namespaces;
	}
	
	public Element createEffectDescriptionElement(String name, String classEffectUri, String description){
		Element effectElement = null, effectType = null, effectname = null, effectDescription = null, definedBy1 = null, definedBy2 = null;
		
		effectElement = new Element("Description",this.namespaces.get("rdf"));
		effectElement.setAttribute("about", "http://parliament.semwebcentral.org/parliament#" + "tandrEffect" + classEffectUri, namespaces.get("rdf"));
		
		effectType = new Element("type", namespaces.get("rdf"));
		effectType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/tandr_assessment/tandr#EffectDescription", namespaces.get("rdf")) );
		
		effectname = this.setEffectElementNameIs(name);
		effectDescription = this.setEffectElementDescriptionIs(description);
		
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
		aspectElement.setAttribute("about", "http://parliament.semwebcentral.org/parliament#" + "tandrAspect" + classAspectUri, namespaces.get("rdf"));
		
		aspectType = new Element("type", namespaces.get("rdf"));
		aspectType.setAttribute(new Attribute("resource", "http://semantic.web/vocabs/tandr_assessment/tandr#AspectDescription", namespaces.get("rdf")) );
		
		aspectName = this.setAspectElementNameIs(name);
		aspectDescription = this.setAspectElementDescriptionIs(description);
		
		definedBy1 = this.setElementDefinedBy("Fausto D'Antonio");
		definedBy2 = this.setElementDefinedBy("Paolo Fogliaroni");
		
		aspectElement.addContent(aspectType);
		aspectElement.addContent(aspectName);
		aspectElement.addContent(aspectDescription);	
		aspectElement.addContent(definedBy1);
		aspectElement.addContent(definedBy2);
		
		return aspectElement;
	}
	
	private Element setEffectElementNameIs(String name)
	{
		Element nameIs = new Element("effectNameIs",namespaces.get("tandr"));
		nameIs.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#string",this.namespaces.get("rdf")));
		nameIs.setText(name);
		return nameIs;
	}
	
	private Element setEffectElementDescriptionIs(String description)
	{
		Element descriptionIs = new Element("effectDescriptionIs",namespaces.get("tandr"));
		descriptionIs.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#string",this.namespaces.get("rdf")));
		descriptionIs.setText(description);
		return descriptionIs;
	}
	
	private Element setAspectElementNameIs(String name)
	{
		Element nameIs = new Element("aspectNameIs",namespaces.get("tandr"));
		nameIs.setAttribute(new Attribute("datatype", "http://www.w3.org/2001/XMLSchema#string",this.namespaces.get("rdf")));
		nameIs.setText(name);
		return nameIs;
	}
	
	private Element setAspectElementDescriptionIs(String description)
	{
		Element descriptionIs = new Element("aspectDescriptionIs",namespaces.get("tandr"));
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
	
}
