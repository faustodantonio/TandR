package controller;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.jdom2.Namespace;

import model.MAuthor;
import model.MEdit;
import model.MFeature;
import model.MFeatureVersion;
import utility.UConfig;
import utility.UDebug;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import foundation.FFoundationFacade;

public class CTest {
	
	private String graphUri = "<http://parliament.semwebcentral.org/parliament#hvgi>";
	
	@SuppressWarnings("deprecation")
	public void printAllAuthorURIs()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		
		ResultSet uris = ffactory.retrieveAllAsResultSet("MAuthor");
		
	    ResultSetFormatter.out( uris );	    	  
	}
	
	public void printFirstAuthorInfos()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		
		ArrayList<String> uris = ffactory.retrieveAll("MAuthor", graphUri);
		
		UDebug.print( "Author URI: "+ uris.get(0) +"\n\n" , 2);
		
		MAuthor author = (MAuthor) ffactory.retrieveByUri( uris.get(0), graphUri, 0, "MAuthor");
		
		UDebug.print(author.toString(""),1);    	  
	}

	public void printFirstEditInfos()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		ArrayList<String> uris = ffactory.retrieveAll("MEdit");
		
		UDebug.print( "Edit ID: "+ uris.get(0) +"\n\n" , 2);
		
		MEdit edit = (MEdit) ffactory.retrieveByUri(uris.get(0), graphUri, 0, "MEdit");
		
		UDebug.print(edit.toString(""),1);   	  
	}
	
	public void printEditInfos()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		String id = "http://semantic.web/data/hvgi/edits.rdf#wayEdit5105728_5.0";
		
		UDebug.print( "Edit URI: "+ id +"\n\n" , 2);
		
		MEdit edit = (MEdit) ffactory.retrieveByUri(id, graphUri, 1, "MEdit");
		
		UDebug.print(edit.toString(""),1);   	  
	}
	
	public void printFeatureInfos()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		String id = "http://semantic.web/data/hvgi/ways.rdf#way4271855";
		
		MFeature feature = (MFeature) ffactory.retrieveByUri(id, graphUri, 0, "MFeature");
		
		UDebug.print(feature.toString(""),1);   	  
	}
	
	public void printFeatureVersionInfos()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
//		String id = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion199732_1";
		String id = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion198467_2";
		
		MFeatureVersion fversion = (MFeatureVersion) ffactory.retrieveByUri(id, graphUri, 1, "MFeatureVersion");
		
		UDebug.print(fversion.toString(""),1);   	  
	}
	
	public void printQuery_1()
	{
		boolean graphUri = true;
		String authorUri = "author0";
		String effect = "effect";
		String aspect = "aspect";
		
		String queryString = ""
				+ "\tSELECT DISTINCT ?fvUri ?aspectValue ?aspectComputedAt  \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
		
		if (graphUri) queryString += "\t GRAPH graphs:hvgi \n\t {\n";
		queryString += ""
				+ "\t\t ?fvUri dcterms:contributor <"+authorUri+"> .\n";
		if (graphUri) queryString += "\t }\n";
		
		queryString += "\t\t \n";
		
		if (graphUri) queryString += "\t GRAPH graphs:tandr \n\t {\n";
		queryString += ""
					+ "\t\t ?trust tandr:refersToFeatureVersion     ?fvUri  .\n"
					+ "\t\t ?trust tandr:hasTrustworthinessEffect ?effect   .\n"
				    + "\t\t         ?effect       tandr:hasEffectDescription  ?EDescription                .\n"
					+ "\t\t         ?EDescription tandr:effectNameIs          \""+ effect +"\"^^xsd:string .\n"
					+ "\t\t \n"
					+ "\t\t         ?effect       tandr:hasTrustworthinessAspect ?aspect                      .\n"
					+ "\t\t         ?aspect       tandr:hasAspectDescription     ?ADescription                .\n"
					+ "\t\t         ?ADescription tandr:aspectNameIs             \""+ aspect +"\"^^xsd:string .\n"
					+ "\t\t         ?aspect       tandr:hasAspectValue           ?AValue                      .\n"
					+ "\t\t         ?AValue       tandr:aspectValueIs            ?aspectValue                 .\n"
					+ "\t\t         { \n"
					+ "\t\t             SELECT  ?aspectValue (MAX(?aspectTimeStamp) AS ?aspectComputedAt)\n"
					+ "\t\t             WHERE { \n" ;
					if (graphUri) queryString += "\t\t              GRAPH graphs:tandr {\n";
		queryString += ""
					+ "\t\t  		?AValue       tandr:computedAt               ?aspectTimeStamp            .\n";
					if (graphUri) queryString += "\t\t              }\n";
		queryString += ""
					+ "\t\t              FILTER( ?aspectTimeStamp  < \""+UConfig.getMaxDateTimeAsString()+"\"^^xsd:dateTime )      \n"
					+ "\t\t             }\n"
					+ "\t\t         } \n"
					;
		if (graphUri) queryString += "\t }\n";
		queryString += ""
					+ "\t FILTER( !isblank(?aspectComputedAt) )  \n"
					+ "\t}";

		UDebug.print("SPARQL query: \n" + queryString + "\n\n", 1);
	}
	
	public void printQuery_2()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		String id = "http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion14955295_21.10";
		MFeatureVersion featureVersion = (MFeatureVersion) ffactory.retrieveByUri(id, UConfig.getVGIHGraphURI(), 0, "MFeatureVersion");

//		String fv_dateFrom = featureVersion.getIsValidFromString();
//		String fv_dateTo = featureVersion.getIsValidToString();		
		String fv_dateFrom = "2009-01-12T15:59:30Z";
		String fv_dateTo = "2009-02-13T15:59:51Z";
//		String fv_dateFrom = UConfig.getMinDateTimeAsString();
//		String fv_dateTo = UConfig.getMaxDateTimeAsString();
		double radius = 50.0;
		

		
		String fv_wkt_buffered = featureVersion.getGeometryBuffer(radius);
		
		String queryString = ""
				+ "\tSELECT ?uri \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\tOPTIONAL { ?uri      rdf:type             osp:FeatureState } \n"
				// Join on TEMPORAL subgraph
				+ "\t\tOPTIONAL { ?uri      hvgi:valid           ?valid       . \n"
				+ "  			  ?valid     hvgi:validFrom      _:timeFrom   . \n"
				+ "	     		  _:timeFrom time:inXSDDateTime  ?dateFrom      \n"		
				+ "\t\t\t} \n"
				// Join on SPATIAL subgraph
				+ "\t\tOPTIONAL { ?uri      geosparql:hasGeometry    _:geom       . \n"
				+ "	     		  _:geom    geosparql:asWKT          ?wktString   } \n";
				
		if (!graphUri.equals("")) queryString += "\t\t}\n";
		
		// TODO: manage if dateTo do not exists
		
		queryString += ""				
				+ "\t\tFILTER(                                                               \n"
				+ "\t\t\t \"" + fv_dateFrom + "\"^^xsd:dateTime < ?dateFrom  &&  			 \n"
				+ "\t\t\t?dateFrom < \"" + fv_dateTo + "\"^^xsd:dateTime    &&               \n"
				+ "\t\t\tgeof:sfWithin(?wktString, \""+ fv_wkt_buffered +"\"^^sf:wktLiteral) \n"
				+ "\t\t\t)                                                                   \n"
				+ "\t}																         \n"
				+ "\tORDER BY DESC(?dateFrom) 										         \n"
//				+ "\tLIMIT 10 										                         \n"
				;

		UDebug.print("SPARQL query: \n" + printPrefixes(queryString) + "\n\n", 1);
	}
	
	public void printQuery_3()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		String id = "http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion14955295_21.10";
		MFeatureVersion featureVersion = (MFeatureVersion) ffactory.retrieveByUri(id, UConfig.getVGIHGraphURI(), 0, "MFeatureVersion");

		String fv_dateFrom = featureVersion.getIsValidFromString();
//		String fv_dateTo = featureVersion.getIsValidToString();		
//		String fv_dateFrom = "2009-01-12T15:59:30Z";
//		String fv_dateTo = "2009-02-13T15:59:51Z";
//		String fv_dateFrom = UConfig.getMinDateTimeAsString();
//		String fv_dateTo = UConfig.getMaxDateTimeAsString();
		double radius = 50.0;
		
		String fv_wkt_buffered = featureVersion.getGeometryBuffer(radius);
		
		String queryString = ""
				+ "\tSELECT ?uri ?dateFrom ?dateTo ?wktString \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\tOPTIONAL { ?uri      rdf:type             osp:FeatureState } \n"
				// Join on TEMPORAL subgraph
				+ "\t\tOPTIONAL { ?uri      hvgi:valid           ?valid               . \n"
				+ "  			  OPTIONAL {?valid     hvgi:validFrom      _:timeFrom . \n"
				+ "	     		            _:timeFrom time:inXSDDateTime  ?dateFrom  } \n"
				+ "  			  OPTIONAL {?valid     hvgi:validTo        _:timeTo   . \n"
				+ "	     		            _:timeTo   time:inXSDDateTime  ?dateTo    } \n"				
				+ "\t\t\t} \n"
				// Join on SPATIAL subgraph
				+ "\t\tOPTIONAL { ?uri      geosparql:hasGeometry    _:geom       . \n"
				+ "	     		  _:geom    geosparql:asWKT          ?wktString   } \n";
				
		if (!graphUri.equals("")) queryString += "\t\t}\n";
		
		// TODO: manage if dateTo do not exists
		
		queryString += ""				
				+ "\t\tFILTER(                                                               \n"
				+ "\t\t\t?dateFrom < \"" + fv_dateFrom + "\"^^xsd:dateTime  &&  			 \n"
				+ "\t\t\t?dateTo > \"" + fv_dateFrom + "\"^^xsd:dateTime    &&               \n"
				+ "\t\t\tgeof:sfWithin(?wktString, \""+ fv_wkt_buffered +"\"^^sf:wktLiteral) \n"
				+ "\t\t\t)                                                                   \n"
				+ "\t}																         \n"
				+ "\tORDER BY DESC(?dateFrom) 										         \n"
//				+ "\tLIMIT 10 										                         \n"
				;

		UDebug.print("SPARQL query: \n" + printPrefixes(queryString) + "\n\n", 1);
	}
	
	public void printTrustworthiness()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
//		String id = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion198467_2";
//		
		String id = "http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion14955295_21.10";
		
		
		MFeatureVersion featureVersion = (MFeatureVersion) ffactory.retrieveByUri(id, graphUri, 1, "MFeatureVersion");
		UDebug.print(featureVersion.toString(""),3); 
		
		CTRCalculus controller = new CTRCalculus();
		controller.compute(featureVersion);
	}
	
	public void retreiveNextFV()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		String id = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion198467_2";
		
		MFeatureVersion fversion = (MFeatureVersion) ffactory.retrieveByUri(id, graphUri, 1, "MFeatureVersion");
		
		UDebug.print(fversion.toString(""),2);
		
		UDebug.print("\n\nDATA ESTRATTA: " + fversion.getIsValidFromString() + "\n\n",2);  
		
		String nextUri = ffactory.retrieveNextFVUri( fversion, graphUri );
		
		UDebug.print("Prossima feature version: \n\t" + nextUri , 2);
	}
	
	public void retreiveSuccNeighboursFVs()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(  ), Integer.parseInt(UConfig.epsg_crs));
		
		Double radius = UConfig.featureInfluenceRadius;
		String wktGeometryBuffered = null;
		String id = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion198467_2";
//		String id = "http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion8179208_3.0";
		
		MFeatureVersion fversion = (MFeatureVersion) ffactory.retrieveByUri(id,1,"MFeatureVersion");
		
		UDebug.print(fversion.toString(""),2);
		
		WKTReader reader = new WKTReader( geometryFactory );
		Geometry geometryBuffered;
		try {
			geometryBuffered = ((Geometry) reader.read(fversion.getWktGeometry())).buffer( radius );
			wktGeometryBuffered = geometryBuffered.toText();
		} catch (ParseException e) {
			wktGeometryBuffered = null;
			e.printStackTrace();
		}
		
		ArrayList<String> uris = ffactory.retrieveFVPreviousesNeighbours(fversion, wktGeometryBuffered);
		
		for (String uri : uris)
			UDebug.print("\n\t feature version: \t" + uri , 2);
	}	
	
	private String printPrefixes(String query) {
		String prefixes = "\n";
		
		for (Entry<String, Namespace> namespace : UConfig.namespaces.entrySet())
			prefixes += "PREFIX " + namespace.getKey() + ": \t\t <" + namespace.getValue().getURI() +">\n" ;
		
		UDebug.print(prefixes, 6);
		
		query = prefixes + "\n" + query; 
		
		return query;
	}
}
