package foundation;

import java.util.TreeMap;

import utility.UDebug;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class FTag extends FFoundationAbstract{
		
	public FTag()	{
		super();
	}

	@Override
	protected String getClassUri(){
		return "osp:Tag";
	}

	@Override
	public Object retrieveByURI(String uri, String graphUri, int lazyDepth) {
		TreeMap<String, String> tags = new TreeMap<String, String>();
		
		String queryString = ""
				+ "SELECT ?key ?value \n"
				+ "\tWHERE \n"
				+ "\t{\n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\tOPTIONAL {<"+uri+">" + " osp:hasKey "   + " _:tagKey   .  "
						+ "		_:tagKey "     + " hvgi:isKey "   + "?key        }\n"
				+ "\t\tOPTIONAL {<"+uri+">" + " osp:hasValue " + " _:tagValue .  "
						+ "		_:tagValue "   + " hvgi:isValue " + "?value      }\n";
						
		if (!graphUri.equals("")) queryString += "\t}\n";
				
		queryString += ""
				+ "\t}";
		
		UDebug.print("SPARQL query: \n\t" + queryString + "\n\n", 6);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",7);
		queryRawResults.reset();
		QuerySolution queryResults = queryRawResults.next();
		
		RDFNode tagKey   = queryResults.getLiteral("key");
		RDFNode tagValue = queryResults.getLiteral("value");
		
		if (tagKey != null && tagValue != null)
			tags.put(
					tagKey.toString().replace("^^http://www.w3.org/2001/XMLSchema#string", ""), 
					tagValue.toString().replace("^^http://www.w3.org/2001/XMLSchema#string", ""));
		
		return tags.firstEntry();
	}

	@Override
	public String convertToRDF(Object obj) {
		// TODO Implement Tag's convertToRDF() method
		return null;
	}
	
}