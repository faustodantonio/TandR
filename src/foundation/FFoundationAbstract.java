package foundation;

import java.util.ArrayList;

import utility.UConfig;
import utility.UDebug;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

abstract class FFoundationAbstract {

	protected FTripleStore triplestore;
	
	protected FFoundationAbstract() 
	{	
		try {
			this.triplestore = (FTripleStore) Class.forName("foundation."+UConfig.triplestoreConnectionClass).newInstance();
		} catch (ClassNotFoundException e) {
			UDebug.print("triplestoreConnectionClass NOT FOUND. \nException: " + e.getMessage() + "\n\n"
					+ "The Parliament (default) one will be loaded", 1);
			this.triplestore = new FParliament();
		} catch (InstantiationException e) {
			UDebug.print("Cannot instantiate triplestoreConnectionClass. \nException: " + e.getMessage() + "\n\n"
					+ "The Parliament (default) one will be loaded", 1);
			this.triplestore = new FParliament();
		} catch (IllegalAccessException e) {
			UDebug.print("Cannot access to triplestoreConnectionClass. \nException: " + e.getMessage() + "\n\n"
					+ "The Parliament (default) one will be loaded", 1);
		}		
	}
	
	public abstract ArrayList<String> retrieveAll();
	public abstract Object            retrieveByURI(String uri,int lazyDepth);
	public abstract String			  convertToRDF(Object obj);

	@Deprecated
	public abstract ResultSet retrieveAllAsResultSet();
	
	ResultSet getURIsOfClassAsResultSet( String vocabClass )
	{
		String queryString = "SELECT ?uri { ?uri rdf:type "+ vocabClass +" }";  
		return triplestore.sparqlSelect(queryString);
	}
	
	public ArrayList<String> getURIsOfClass( String vocabClass )
	{
		ArrayList<String> uris = new ArrayList<String>();
		
		ResultSet queryRawResults = getURIsOfClassAsResultSet( vocabClass );
		
		while ( queryRawResults.hasNext() )
		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode classuri = generalQueryResults.getResource("uri");
			uris.add(classuri.toString());
		}
		
		return uris;
	}
	
	private boolean create(String rdfTriples) {
		
		String updateQueryString = "INSERT DATA { "+ rdfTriples +" }";
		boolean result = this.triplestore.sparqlUpdate(updateQueryString);
		
		return result;
	}

	private boolean update(String oldRdfTriples, String updatedRdfTriples) {
		boolean result = false;
		if ( this.delete(oldRdfTriples) )
			result = this.create(updatedRdfTriples);
		
		return result;
	}
	
	private boolean delete(String rdfTriples) {
		String updateQueryString = "DELETE DATA { "+ rdfTriples +" }";
		boolean result = this.triplestore.sparqlUpdate(updateQueryString);
		
		return result;
	}
	
	boolean create(Object modelObj) 	{
		boolean creation = false;
		if (this.checkObjectModel(modelObj)) {
			String rdfTriples = this.convertToRDF(modelObj);
			creation = this.create(rdfTriples);
		}
		// TODO: RAISE EXCEPTION at create(), the object to be "created" is not a Model object.
		return creation;
	}
	
	boolean update(Object oldObj, Object updObj) {
		boolean update = false;
		if ( this.checkObjectModel(oldObj) && this.checkObjectModel(updObj) ) {
			String oldRdfTriples =  this.convertToRDF(oldObj);
			String updatedRdfTriples = this.convertToRDF(updObj);
			update = this.update(oldRdfTriples, updatedRdfTriples);
		}
		// TODO: RAISE EXCEPTION at update(), the object to be "updated" is not a Model object.
		return update;
	}
	
	boolean delete(Object modelObj) {
		boolean deletion = false;
		if (this.checkObjectModel(modelObj)) {
			String rdfTriples = this.convertToRDF(modelObj);
			deletion = this.delete(rdfTriples);
		}
		// TODO: RAISE EXCEPTION at delete(), the object to be "deleted" is not a Model object.
		return deletion;
	}
	
	@SuppressWarnings("rawtypes")
	private boolean checkObjectModel(Object obj)	{
		boolean isModelObject = false;
		Class objClass = obj.getClass();
		if (objClass.getPackage().toString().contains("model"))
			isModelObject = true;
		return isModelObject;
	}
	
}
