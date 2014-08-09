package foundation;

import com.hp.hpl.jena.query.ResultSet;

abstract class FTripleStore {

	public FTripleStore() {	}
	
	public abstract ResultSet sparqlSelect(String selectQueryString);
	public abstract boolean sparqlUpdate(String updateQueryString);
	
	protected String AddPrefixes(String queryString) {		
		
		String prefixes = "\n"
				+ "PREFIX rdf: "       + "\t\t" +"<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"     + "\n"
				+ "PREFIX xsd: "       + "\t\t" +"<http://www.w3.org/2001/XMLSchema#>"               + "\n"
				+ "PREFIX dcterms: "   + "\t"   +"<http://purl.org/dc/terms/>" 						 + "\n"
		  		+ "PREFIX foaf: "      + "\t\t" +"<http://xmlns.com/foaf/0.1/>" 					 + "\n"
		  		+ "PREFIX geosparql: " + "\t"   +"<http://www.opengis.net/ont/geosparql#>" 			 + "\n"
		  		+ "PREFIX geof: "      + "\t\t" +"<http://www.opengis.net/def/function/geosparql/>"  + "\n"
		  		+ "PREFIX sf: "        + "\t\t" +"<http://www.opengis.net/ont/sf#>" 				 + "\n"
		  		+ "PREFIX units: "     + "\t\t" +"<http://www.opengis.net/def/uom/OGC/1.0/>" 		 + "\n"		  		
		  		+ "PREFIX hvgi: "      + "\t\t" +"<http://semantic.web/vocabs/history_vgi/hvgi#>" 	 + "\n"
		  		+ "PREFIX osp: "       + "\t\t" +"<http://semantic.web/vocabs/osm_provenance/osp#> " + "\n"
		  		+ "PREFIX prv: "       + "\t\t" +"<http://purl.org/net/provenance/ns#>" 			 + "\n"
		  		+ "PREFIX time: "      + "\t\t" +"<http://www.w3.org/2006/time#>" 			  + "\n" + "\n"
		  		+ "";
		
		return prefixes + queryString;
		
//PREFIX rdf: 		<http://www.w3.org/1999/02/22-rdf-syntax-ns#>
//PREFIX xsd: 		<http://www.w3.org/2001/XMLSchema#>
//PREFIX dcterms: 	<http://purl.org/dc/terms/>
//PREFIX foaf: 		<http://xmlns.com/foaf/0.1/>
//PREFIX geosparql: <http://www.opengis.net/ont/geosparql#>
//PREFIX geof: 		<http://www.opengis.net/def/function/geosparql/>
//PREFIX sf: 		<http://www.opengis.net/ont/sf#>
//PREFIX units: 	<http://www.opengis.net/def/uom/OGC/1.0/>
//PREFIX hvgi: 		<http://semantic.web/vocabs/history_vgi/hvgi#>
//PREFIX osp: 		<http://semantic.web/vocabs/osm_provenance/osp#> 
//PREFIX prv: 		<http://purl.org/net/provenance/ns#>
//PREFIX time: 		<http://www.w3.org/2006/time#>

		
	}

}
