package foundation;

import model.MFeature;
import model.MFeatureVersion;
import utility.UDebug;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class FFeature extends FFoundationAbstract{
	
	public FFeature()
	{
		super();
	}
	@Override
	protected String getClassUri(){
		return"hvgi:VGIFeature";
	}
	@Override
	public MFeature retrieveByURI(String featureURI, String graphUri, int lazyDepth)
	{
		MFeature feature = new MFeature();
		FFeatureVersion ffversion = new FFeatureVersion();
		
		String queryString = ""
				+ "\tSELECT ?versionuri \n"
				+ "\tWHERE \n"
				+ "\t{ \n";
				
		if (!graphUri.equals("")) queryString += "\t GRAPH " +graphUri+ "{\n";
		
		queryString += ""
				+ "\t\t <"+featureURI+">" + " hvgi:hasVersion ?versionuri \n";
		
		if (!graphUri.equals("")) queryString += "\t}\n";
				
		queryString += ""
				+ "\t}";
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", 5);
		
		ResultSet rawResults = triplestore.sparqlSelectHandled(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",6);
		queryRawResults.reset();
		
		feature.setUri( featureURI );
		
		while ( queryRawResults.hasNext() )
		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode fversionuri = generalQueryResults.getResource("versionuri");
			MFeatureVersion fversion = null;
			
			if ( lazyDepth > 0 )
				fversion = ffversion.retrieveByURI(fversionuri.toString(), graphUri, lazyDepth-1);
			
			feature.addVersion(fversionuri.toString(), fversion);
		}
		
		return feature;
	}

	@Override
	public String convertToRDF(Object obj) {
		// TODO Implement Feature's convertToRDF() method
		return null;
	}
	
}
