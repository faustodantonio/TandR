package foundation;

import java.util.ArrayList;
import java.util.Map.Entry;

import model.MAuthor;
import model.MEdit;
import model.MFeature;
import model.MFeatureVersion;
import utility.UDebug;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;

class FFeatureVersion extends FFoundationAbstract{
	
	int dbgLevel = 4;
	
	public FFeatureVersion()	{
		super();
	}
	
	@Override
	protected String getClassUri(){
		return "osp:FeatureState";
	}
	
	@Override
	public MFeatureVersion retrieveByURI(String fversionURI, String graphUri, int lazyDepth)
	{	
		String queryString = ""
				+ "\tSELECT * \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
		
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " hvgi:isVersionOf      ?isVersionOf } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " hvgi:hasVersion       _:ver         .\n"
				+ "\t\t          		_:ver      hvgi:versionNo     ?versionNo                } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " prv:precededBy        ?precededBy  } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " osp:createdBy         ?createdBy   } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " dcterms:contributor   ?contributor } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " hvgi:valid            ?valid       .\n"
				+ "\t\t\tOPTIONAL { 	?valid     hvgi:validFrom     _:timeFrom                 .\n"
				+ "\t		        	_:timeFrom time:inXSDDateTime ?validFrom                 }\n"
				+ "\t\t\tOPTIONAL {     ?valid     hvgi:validTo       _:timeTo                   .\n"
				+ "\t			        _:timeTo   time:inXSDDateTime ?validTo		   	         }\n"
				+ "\t\t         }\n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " hvgi:isDeleted        ?isDeleted   } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " osp:hasTag            ?hasTag      } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " geosparql:hasGeometry _:geom        .\n"
				+ "\t\t\t                 _:geom       geosparql:asWKT    ?wktGeom                } \n";
		
		if (!graphUri.equals("")) queryString += "\t\t}\n";
		
		queryString += ""
				+ "\t}";	
		
		UDebug.print("Retriving feature version: "+ fversionURI +" \n", dbgLevel);
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", dbgLevel+1);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2);
		queryRawResults.reset();
	
		return this.setFVAttributes(queryRawResults, fversionURI, graphUri, lazyDepth);
	}
	
	@Override
	public String convertToRDFXML(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String retrieveFirst()	{
		return this.retrieveNext(null);
	}
	
	public String retrieveFirst(String graphUri)	{
		return this.retrieveNext(null,graphUri);
	}
	
	public String retrieveNext(String fv_dateFrom)	{
		return this.retrieveNext(fv_dateFrom, "");
	}
	
	public String retrieveNext(String fv_dateFrom,String graphUri)
	{		
		String queryString = ""
				+ "\tSELECT ?uri ?dateFrom\n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "\n\t {\n";
		
		queryString += ""
				+ "\t\tOPTIONAL { ?uri      rdf:type             osp:FeatureState } \n"
				+ "\t\tOPTIONAL { ?uri      hvgi:valid           _:valid          . \n"
				+ "			  _:valid   hvgi:validFrom       _:time            	  . \n"
				+ "			  _:time    time:inXSDDateTime   ?dateFrom}          	\n";
		
		if (fv_dateFrom != null)
			queryString += "\t\tFILTER( ?dateFrom > \"" + fv_dateFrom + "\"^^xsd:dateTime )  \n";
		
		if (!graphUri.equals("")) queryString += "\t }\n";
		
		queryString += ""
				+ "\t}																\n"
				+ "\tORDER BY ASC(?dateFrom) 										\n"
				+ "\tLIMIT 1 \n\n"
				;
		
		UDebug.print("Retriving the next feature version wrt date: "+ fv_dateFrom +" \n", dbgLevel);
		UDebug.print("SPARQL query: \n" + queryString + "\n\n",dbgLevel+1);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2 );
		queryRawResults.reset();
		
		QuerySolution generalQueryResults = queryRawResults.next();
		
		RDFNode uri = generalQueryResults.getResource("uri");
		
		return uri.toString();
	}
	
	public ArrayList<String> retrieveLivingNeighbours(String fv_dateFrom,String fv_wkt_buffered) {
		return this.retrieveLivingNeighbours(fv_dateFrom, fv_wkt_buffered, "");
	}
	
	public ArrayList<String> retrieveLivingNeighbours(String fv_dateFrom,String fv_wkt_buffered,String graphUri)
	{	
		ArrayList<String> uris = new ArrayList<String>();		
		
		String queryString = ""
				+ "\tSELECT ?uri ?dateFrom ?dateTo ?wktString \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\tOPTIONAL { ?uri      rdf:type             osp:FeatureState } \n"
				// Join on TEMPORAL subgraph
				+ "\t\tOPTIONAL { ?uri      hvgi:valid           ?valid               . \n"
				+ "  			  ?valid     hvgi:validFrom      _:timeFrom           . \n"
				+ "	     		            _:timeFrom time:inXSDDateTime  ?dateFrom  . \n"
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
		
		UDebug.print("Retriving features valid at " +fv_dateFrom+" in "+ fv_wkt_buffered +" \n", dbgLevel+1);
		UDebug.print("SPARQL query: \n" + queryString + "\n\n",dbgLevel+2);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+3);
		queryRawResults.reset();
		
		while ( queryRawResults.hasNext() )
		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode uri = generalQueryResults.getResource("uri");		
			uris.add(uri.toString());
		}
		
		return uris;
	}
	
	public ArrayList<MFeatureVersion> retrieveConfirmers(MFeatureVersion featureVersion, String dateTo, String graphUri, int lazyDepth) {
		
		ArrayList<MFeatureVersion> versions = new ArrayList<MFeatureVersion>();
		ArrayList<String> uris = this.retrieveConfirmersUris(featureVersion, dateTo, graphUri, lazyDepth);
		
		for (String uri : uris) 
			versions.add( this.retrieveByURI(uri, graphUri, 1) );
		
		return versions;
	}
	
	public ArrayList<String> retrieveConfirmersUris(MFeatureVersion featureVersion, String dateTo, String graphUri, int lazyDepth) {
		
		ArrayList<String> uris = new ArrayList<String>();
		double radius = 10.0;
		
		String fv_dateFrom = featureVersion.getIsValidFromString();
//		String fv_dateTo = featureVersion.getIsValidToString(); //CHECK su fv_dateTo > fv_dateFrom???
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
		
		queryString += ""				
				+ "\t\tFILTER(                                                               \n"
				+ "\t\t\t \"" + fv_dateFrom + "\"^^xsd:dateTime < ?dateFrom  &&  			 \n"
				+ "\t\t\t?dateFrom < \"" + dateTo + "\"^^xsd:dateTime    &&               \n"
				+ "\t\t\tgeof:sfWithin(?wktString, \""+ fv_wkt_buffered +"\"^^sf:wktLiteral) \n"
				+ "\t\t\t)                                                                   \n"
				+ "\t}																         \n"
				+ "\tORDER BY DESC(?dateFrom) 										         \n"
				;
		
		UDebug.print("Retriving features valid at " + fv_dateFrom +" in "+ fv_wkt_buffered +" \n", dbgLevel+1);
		UDebug.print("SPARQL query: \n" + queryString + "\n\n",dbgLevel+2);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+3);
		queryRawResults.reset();
		
		while ( queryRawResults.hasNext() )
		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode uri = generalQueryResults.getResource("uri");		
			uris.add(uri.toString());
		}
		
		return uris;
	}
	
	@SuppressWarnings("unchecked")
	private MFeatureVersion setFVAttributes(ResultSetRewindable queryRawResults, String fversionURI, String graphUri, int lazyDepth)
	{
		MFeatureVersion fversion = new MFeatureVersion();
		FEdit fedit = new FEdit();
		FAuthor fauthor = new FAuthor();
		FTag ftag = new FTag();
		FFeature ffeature = new FFeature();
		
		QuerySolution generalQueryResults = queryRawResults.next();
		
		RDFNode isVersionOf = generalQueryResults.getResource("isVersionOf");
		RDFNode versionNo   = generalQueryResults.getLiteral("versionNo");
		RDFNode precededBy  = generalQueryResults.getResource("precededBy");
		RDFNode createdBy   = generalQueryResults.getResource("createdBy");
		RDFNode hasAuthor   = generalQueryResults.getResource("contributor");
		RDFNode validFrom   = generalQueryResults.getLiteral("validFrom");
		RDFNode validTo     = generalQueryResults.getLiteral("validTo");
		RDFNode isDeleted   = generalQueryResults.getLiteral("isDeleted");
		RDFNode wktGeom     = generalQueryResults.getLiteral("wktGeom");
		
		fversion.setUri( fversionURI );
		
		if (isVersionOf != null)  {
			fversion.setFeatureUri(isVersionOf.toString());
			if ( lazyDepth > 0 ) {
				MFeature feature = ffeature.retrieveByURI( isVersionOf.toString(), graphUri, lazyDepth-1); 
				fversion.setFeature(feature);
			}
		}
		
		if (versionNo != null)   fversion.setVersionNo(versionNo.toString().replace("^^http://www.w3.org/2001/XMLSchema#string", ""));
		if (precededBy != null)  {
			fversion.setPrevFVersionUri( precededBy.toString() );
			if ( lazyDepth > 0 ) {
				MFeatureVersion precededfv = this.retrieveByURI( precededBy.toString(), graphUri, lazyDepth-1); 
				fversion.setPrevFVersion( precededfv );
			}
		}
		if (createdBy != null) {
			fversion.setEditUri(createdBy.toString());
			if ( lazyDepth > 0 ) {
				MEdit edit = fedit.retrieveByURI(createdBy.toString(), graphUri, lazyDepth-1); 
				fversion.setEdit( edit );
			}
		}
		if (hasAuthor != null) {
			fversion.setAuthorUri(hasAuthor.toString());
			if ( lazyDepth > 0 ) {
				MAuthor author = fauthor.retrieveByURI(hasAuthor.toString(), graphUri, lazyDepth-1); 
				fversion.setAuthor( author );
			}
		}
		if (validFrom != null)   fversion.setIsValidFrom(validFrom.toString());
		if (validTo != null)     fversion.setIsValidTo(validTo.toString());
		if (isDeleted != null)   fversion.setIsDeleted( Boolean.parseBoolean(isDeleted.toString()) );
		
		if (wktGeom != null) { 
			fversion.setWktGeometry(wktGeom.toString().replace("^^http://www.opengis.net/ont/sf#wktLiteral", ""));
		 	fversion.setGeometry(wktGeom.toString());
		}
		
		queryRawResults.reset();

		for (int i = 0; i < queryRawResults.size(); i++ )
		{
			QuerySolution generalQuery = queryRawResults.next();
			
			if ( generalQuery.getResource("hasTag") != null )
			{
				String tagUri = generalQuery.getResource("hasTag").toString();
				Entry<String, String> tag = (Entry<String, String>) ftag.retrieveByURI(tagUri, graphUri, 0);
				if (tag != null && tag.getKey() != null && tag.getValue() != null) 
					fversion.addTag(tag.getKey(), tag.getValue());
			}
		}
		
		return fversion;
	}
	
	public ArrayList<String> retrieveDateList(String graphUri)
	{	
		ArrayList<String> dates = new ArrayList<String>();
		String queryString = ""
				+ "\tSELECT DISTINCT ?dateFrom\n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "\n\t {\n";
		
		queryString += ""
				+ "\t\t?uri      rdf:type             osp:FeatureState . \n"
				+ "\t\t?uri      hvgi:valid           _:valid          . \n"
				+ "\t\t_:valid   hvgi:validFrom       _:time           . \n"
				+ "\t\t_:time    time:inXSDDateTime   ?dateFrom          \n";
		
		if (!graphUri.equals("")) queryString += "\t }\n";
		
		queryString += ""
				+ "\t}																\n"
				+ "\tORDER BY ASC(?dateFrom) 										\n"
				;
		
		UDebug.print("Retriving date List \n", dbgLevel);
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", dbgLevel+1 );
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+2);
		queryRawResults.reset();
		
		while (queryRawResults.hasNext()){
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode date = generalQueryResults.getLiteral("dateFrom");
			dates.add(date.toString().replace("^^http://www.w3.org/2001/XMLSchema#dateTime", ""));
		}

		return dates;
	}
	
	public ArrayList<String> retrieveURIByDate(String dateFrom, String graphUri)
	{	
		ArrayList<String> uris = new ArrayList<String>();
		String queryString = ""
				+ "\tSELECT ?uri\n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "\n\t {\n";
		
		queryString += ""
				+ "\t\t ?uri      rdf:type             osp:FeatureState                  . \n"
				+ "\t\t ?uri      hvgi:valid           _:valid                           . \n"
				+ "\t\t _:valid   hvgi:validFrom       _:time                            . \n"
				+ "\t\t _:time    time:inXSDDateTime   \""+ dateFrom +"\"^^xsd:dateTime    \n";
		
		if (!graphUri.equals("")) queryString += "\t }\n";
		
		queryString += ""
				+ "\t}																\n"
				+ "\tORDER BY ASC(?dateFrom) 										\n"
				;
		
		UDebug.print("Retriving features versions start in date: "+ dateFrom +" \n", dbgLevel+1);
		UDebug.print("SPARQL query: \n" + queryString + "\n\n",dbgLevel+2);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",dbgLevel+3);
		queryRawResults.reset();
		
		while (queryRawResults.hasNext()){
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode uri = generalQueryResults.getResource("uri");
			uris.add(uri.toString());
		}

		return uris;
	}
	
	public ArrayList<MFeatureVersion> retrieveByDate(String dateFrom, String graphUri, int lazyDepth) {	
		ArrayList<MFeatureVersion> fvs = new ArrayList<MFeatureVersion>();
		ArrayList<String> uris = new ArrayList<String>();
		uris = this.retrieveURIByDate(dateFrom, graphUri);
		
		for (String fversionURI : uris)
			fvs.add( this.retrieveByURI(fversionURI, graphUri, lazyDepth) );
		
		return fvs;
	}
	
}
