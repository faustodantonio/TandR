package foundation;

import java.util.ArrayList;

import utility.UDebug;
import model.MAuthor;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class FAuthor extends FFoundationAbstract{
	
	public FAuthor()
	{
		super();
	}

	@Override
	@Deprecated
	public ResultSet retrieveAllAsResultSet()
	{
		return super.getURIsOfClassAsResultSet("osp:User");
	}
	@Override
	public ArrayList<String> retrieveAll()
	{
		return super.getURIsOfClass("osp:User");
	}
	
	@Override
	public MAuthor retrieveByURI(String authorURI, int lazyDepth)
	{
		MAuthor author = new MAuthor();
		
		String queryString = ""
				+ "\tSELECT ?accountName ?accountServerHomepage \n"
				+ "\tWHERE \n"
				+ "\t{ \n"
				+ "\t\t<"+authorURI+">" + " ?p ?o .\n"
				+ "\t\t?o foaf:accountName ?accountName . \n"
				+ "\t\t?o foaf:accountServerHomepage ?accountServerHomepage . \n"
				+ "\t} \n";
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", 5);
		
		ResultSet rawResults = triplestore.sparqlSelect(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",6);
		queryRawResults.reset();
		
		QuerySolution queryResults = queryRawResults.next();
		
		RDFNode accountName = queryResults.getLiteral("accountName");
		RDFNode accountServerHomepage = queryResults.getLiteral("accountServerHomepage");
		
		author.setUri( authorURI );
		author.setAccountName(accountName.toString().replace("^^http://www.w3.org/2001/XMLSchema#string", ""));
		author.setAccountServerHomepage(accountServerHomepage.toString());
		
		return author;
	}

	@Override
	public String convertToRDF(Object obj) {
		// TODO Implement Author's convertToRDF() method
		return null;
	}
	
}
