package modules.tandr.foundation;

import java.util.HashMap;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import utility.UConfig;
import foundation.FTandRImport;

public class FTandR extends FTandRImport{
	
	private Document factors;
	private HashMap<String, Namespace> namespaces;
	private FEffect feffect;
	
	public FTandR() {	
		this.namespaces = UConfig.namespaces;
		feffect = new FEffect();
	}

	@Override
	public boolean importTandRTriples() {

		boolean result = true;
		factors = new Document();
		
		Element directEffect  = null, indirectEffect = null, temporalEffect = null,
				dirGeomAspect = null, dirQualAspect  = null, dirSemAspect   = null, 
				indGeomAspect = null, indQualAspect  = null, indSemAspect   = null;
		
		directEffect   = feffect.createEffectDescriptionElement("Direct Effect", "direct", "This effect take into account the feature version story");
		indirectEffect = feffect.createEffectDescriptionElement("Indirect Effect", "indirect", "This effect take into account confirmations");
		temporalEffect = feffect.createEffectDescriptionElement("Temporal Effect", "temporal", "This effect take into account the feature version life time");
		
		dirGeomAspect = feffect.createAspectDescriptionElement("Geometric Direct Aspect", "geomDir", "Geometric Aspect for Direct Effect");
		dirQualAspect = feffect.createAspectDescriptionElement("Qualitative Direct Aspect", "qualDir", "Qualitative Aspect for Direct Effect");
		dirSemAspect  = feffect.createAspectDescriptionElement("Semantic Direct Aspect", "semDir", "Semantic Aspect for Direct Effect");
		
		indGeomAspect = feffect.createAspectDescriptionElement("Geometric Indirect Aspect", "geomInd", "Geometric Aspect for Indirect Effect");
		indQualAspect = feffect.createAspectDescriptionElement("Qualitative Indirect Aspect", "qualInd", "Qualitative Aspect for Indirect Effect");
		indSemAspect  = feffect.createAspectDescriptionElement("Semantic Indirect Aspect", "semInd", "Semantic Aspect for Indirect Effect");

		factors.addContent(directEffect);
		factors.addContent(indirectEffect);
		factors.addContent(temporalEffect);
		
		factors.addContent(dirGeomAspect);
		factors.addContent(dirQualAspect);
		factors.addContent(dirSemAspect);
		
		factors.addContent(indGeomAspect);
		factors.addContent(indQualAspect);
		factors.addContent(indSemAspect);
		
		String rdfTriples = this.writeDocument(factors);
		String graphUri = UConfig.graphURI + UConfig.tandrGraph;
		
		result = this.importTriples(rdfTriples, graphUri);
		
		return result;
	}
	

	
	private boolean importTriples(String rdfTriples, String graphUri) {
		String updateQueryString;
		if (graphUri.equals(""))
			updateQueryString = "INSERT DATA { "+ rdfTriples +" }";
		else
			updateQueryString = "INSERT DATA { GRAPH "+ graphUri +" {"+ rdfTriples +" } }";
		boolean result = this.triplestore.sparqlUpdateHandled(updateQueryString);
		
		return result;
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
