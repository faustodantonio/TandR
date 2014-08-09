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
	
	public FFeatureVersion()
	{
		super();
	}

	@Deprecated
	public ResultSet retrieveAllAsResultSet()
	{
		return super.getURIsOfClassAsResultSet("osp:FeatureState");
	}

	public ArrayList<String> retrieveAll()
	{
		return super.getURIsOfClass("osp:FeatureState");
	}
	public MFeatureVersion retrieveByURI(String fversionURI)
	{
		return this.retrieveByURI(fversionURI, 0);
	}
	@Override
	public MFeatureVersion retrieveByURI(String fversionURI, int lazyDepth)
	{
		MFeatureVersion fversion = new MFeatureVersion();
		FEdit fedit = new FEdit();
		FAuthor fauthor = new FAuthor();
		FTag ftag = new FTag();
		FFeature ffeature = new FFeature();
		
		String queryString = ""
				+ "\tSELECT * \n"
				+ "\tWHERE \n"
				+ "\t{ \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " hvgi:isVersionOf      ?isVersionOf } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " hvgi:hasVersion       _:ver           ."
						+ "  		_:ver      hvgi:versionNo     ?versionNo                } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " prv:precededBy        ?precededBy  } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " osp:createdBy         ?createdBy   } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " dcterms:contributor   ?contributor } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " hvgi:valid            _:valid        ."
						+ "			_:valid    hvgi:validFrom     _:timeFrom                  ."
						+ "			_:valid    hvgi:validTo       _:timeTo                    ."
						+ "			_:timeFrom time:inXSDDateTime ?validFrom                  ."
						+ "			_:timeTo   time:inXSDDateTime ?validTo		   	        } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " hvgi:isDeleted        ?isDeleted   } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " osp:hasTag            ?hasTag      } \n"
				+ "\t\tOPTIONAL { <"+fversionURI+">" + " geosparql:hasGeometry _:geom         . "
						+ "       _:geom       geosparql:asWKT    ?wktGeom                  } \n"
				+ "\t}";	
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", 5);
		
		ResultSet rawResults = triplestore.sparqlSelect(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",6);
		queryRawResults.reset();
		
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
				MFeature feature = ffeature.retrieveByURI( isVersionOf.toString(), lazyDepth-1); 
				fversion.setFeature(feature);
			}
		}
		
		if (versionNo != null)   fversion.setVersionNo(versionNo.toString().replace("^^http://www.w3.org/2001/XMLSchema#int", ""));
		if (precededBy != null)  {
			fversion.setPrevFVersionUri( precededBy.toString() );
			if ( lazyDepth > 0 ) {
				MFeatureVersion precededfv = this.retrieveByURI( precededBy.toString(), lazyDepth-1); 
				fversion.setPrevFVersion( precededfv );
			}
		}
		if (createdBy != null) {
			fversion.setEditUri(createdBy.toString());
			if ( lazyDepth > 0 ) {
				MEdit edit = fedit.retrieveByURI(createdBy.toString(), lazyDepth-1); 
				fversion.setEdit( edit );
			}
		}
		if (hasAuthor != null) {
			fversion.setAuthorUri(hasAuthor.toString());
			if ( lazyDepth > 0 ) {
				MAuthor author = fauthor.retrieveByURI(hasAuthor.toString(), lazyDepth-1); 
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
				Entry<String, String> tag = ftag.retrieveTagByURI(tagUri);
				if (tag != null && tag.getKey() != null && tag.getValue() != null) 
					fversion.addTag(tag.getKey(), tag.getValue());
			}
		}
		
		return fversion;
	}
	
	public String retrieveFirst()
	{
		return this.retrieveNext(null);
	}
	
	public String retrieveNext(String fv_dateFrom)
	{		
		String queryString = ""
				+ "\tSELECT ?uri ?dateFrom\n"
				+ "\tWHERE \n"
				+ "\t{ \n"
				+ "\t\tOPTIONAL { ?uri      rdf:type             osp:FeatureState } \n"
				+ "\t\tOPTIONAL { ?uri      hvgi:valid           _:valid          . \n"
				+ "			  _:valid   hvgi:validFrom       _:time            	  . \n"
				+ "			  _:time    time:inXSDDateTime   ?dateFrom}          	\n";
		if (fv_dateFrom != null)
		queryString +=  ""
				+ "\t\tFILTER( ?dateFrom > \"" + fv_dateFrom + "\"^^xsd:dateTime )  \n";
		
		queryString += ""
				+ "\t}																\n"
				+ "\tORDER BY ASC(?dateFrom) 										\n"
				+ "\tLIMIT 1 \n\n"
				;
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n",4);
		
		ResultSet rawResults = triplestore.sparqlSelect(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",3);
		queryRawResults.reset();
		
		QuerySolution generalQueryResults = queryRawResults.next();
		
		RDFNode uri = generalQueryResults.getResource("uri");
		
		return uri.toString();
	}
	
	public ArrayList<String> retrievePreviousesNeighbours(String fv_dateFrom,String fv_wkt_buffered)
	{	
		ArrayList<String> uris = new ArrayList<String>();		
		
		String queryString = ""
				+ "\tSELECT ?uri ?dateFrom ?dateTo ?wktString \n"
				+ "\tWHERE \n"
				+ "\t{ \n"
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
				+ "	     		  _:geom    geosparql:asWKT          ?wktString   } \n"
				
				+ "\t\tFILTER(                                                               \n"
				+ "\t\t\t?dateFrom < \"" + fv_dateFrom + "\"^^xsd:dateTime  &&  			 \n"
				+ "\t\t\t?dateTo > \"" + fv_dateFrom + "\"^^xsd:dateTime    &&               \n"
				+ "\t\t\tgeof:sfWithin(?wktString, \""+ fv_wkt_buffered +"\"^^sf:wktLiteral) \n"
				+ "\t\t\t)                                                                   \n"
				+ "\t}																         \n"
				+ "\tORDER BY DESC(?dateFrom) 										         \n"
//				+ "\tLIMIT 10 										                         \n"
				;
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n",3);
		
		ResultSet rawResults = triplestore.sparqlSelect(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",2);
		queryRawResults.reset();
		
		while ( queryRawResults.hasNext() )
		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode uri = generalQueryResults.getResource("uri");		
			uris.add(uri.toString());
		}
		
		return uris;
	}

	@Override
	public String convertToRDF(Object obj) {
		// TODO Implement FeatureVersion's convertToRDF() method
		return null;
	}	
	
}