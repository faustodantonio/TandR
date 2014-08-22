package foundation;

import java.util.ArrayList;

import utility.UConfig;
import utility.UDebug;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

abstract class FFoundationAbstract {

	protected FTripleStore triplestore;
	protected String classUri;
	
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
		
		this.classUri = this.getClassUri();
	}
	
	/*************************
	 * 
	 * Abstract FUNCTIONS
	 *
	 *************************/	
	
	public    abstract Object retrieveByURI(String uri, String graphUri, int lazyDepth);
	public    abstract String convertToRDF(Object obj);
	protected abstract String getClassUri();
	
	/*************************
	 * 
	 * Retrive FUNCTIONS
	 *
	 *************************/
	
	public Object retrieveByURI(String uri){
		return this.retrieveByURI(uri, 0);
	};
	
	public Object retrieveByURI(String uri, int lazyDepth){
		return this.retrieveByURI(uri, "", 0);
	};
	
	/*************************
	 * 
	 * Retrive List FUNCTIONS
	 *
	 *************************/	
	
	@Deprecated
	public ResultSet retrieveAllAsResultSet()
	{
		return this.getURIsOfClassAsResultSet(this.classUri);
	}
	
	public ArrayList<String> retrieveAll()	{
		return this.getURIsOfClass(this.classUri);
	}

	public ArrayList<String> retrieveAll(String graphUri) {
		return this.getURIsOfClass(this.classUri,graphUri);
	}
	
	ResultSet getURIsOfClassAsResultSet( String vocabClass ) {
		return this.getURIsOfClassAsResultSet(vocabClass, "");
	}
	
	public ArrayList<String> getURIsOfClass( String vocabClass )	{
		return this.getURIsOfClass(vocabClass, "");
	}
	
	ResultSet getURIsOfClassAsResultSet( String vocabClass, String graphUri )	{
		String queryString;
		if (graphUri.equals("")) 
			queryString = "SELECT ?uri { ?uri rdf:type "+ vocabClass +" }";
		else 
			queryString = "SELECT ?uri { GRAPH "+ graphUri +" {?uri rdf:type "+ vocabClass +" }}"; 
		
		return triplestore.sparqlSelectHandled(queryString);
	}
	
	public ArrayList<String> getURIsOfClass( String vocabClass, String graphUri )
	{
		ArrayList<String> uris = new ArrayList<String>();
		
		ResultSet queryRawResults = getURIsOfClassAsResultSet( vocabClass, graphUri );
		
		while ( queryRawResults.hasNext() )
		{
			QuerySolution generalQueryResults = queryRawResults.next();
			RDFNode classuri = generalQueryResults.getResource("uri");
			uris.add(classuri.toString());
		}
		
		return uris;
	}
	
	/*************************
	 * 
	 * Create FUNCTIONS
	 *
	 *************************/	
	
	private boolean create(String rdfTriples, String graphUri) {
		String updateQueryString;
		if (graphUri.equals(""))
			updateQueryString = "INSERT DATA { "+ rdfTriples +" }";
		else
			updateQueryString = "INSERT DATA { GRAPH "+ graphUri +" {"+ rdfTriples +" } }";
		boolean result = this.triplestore.sparqlUpdateHandled(updateQueryString);
		
		return result;
	}
	
	boolean create(Object modelObj) {
		return this.create(modelObj, "");
	}
	
	boolean create(Object modelObj, String graphUri) 	{
		boolean creation = false;
		if (this.checkObjectModel(modelObj)) {
			String rdfTriples = this.convertToRDF(modelObj);
			creation = this.create(rdfTriples,graphUri);
		}
		// TODO: RAISE EXCEPTION at create(), the object to be "created" is not a Model object.
		return creation;
	}
	
	/*************************
	 * 
	 * Update FUNCTIONS
	 *
	 *************************/	

	private boolean update(String oldRdfTriples, String updatedRdfTriples, String graphUri) {
		boolean result = false;
		if ( this.delete(oldRdfTriples,graphUri) )
			result = this.create(updatedRdfTriples,graphUri);
		
		return result;
	}
	
	boolean update(Object oldObj, Object updObj) {
		return this.update(oldObj, updObj, "");
	}
	
	boolean update(Object oldObj, Object updObj, String graphUri) {
		boolean update = false;
		if ( this.checkObjectModel(oldObj) && this.checkObjectModel(updObj) ) {
			String oldRdfTriples =  this.convertToRDF(oldObj);
			String updatedRdfTriples = this.convertToRDF(updObj);
			update = this.update(oldRdfTriples, updatedRdfTriples, graphUri);
		}
		// TODO: RAISE EXCEPTION at update(), the object to be "updated" is not a Model object.
		return update;
	}
	
	/*************************
	 * 
	 * Delete FUNCTIONS
	 *
	 *************************/	
	
	private boolean delete(String rdfTriples, String graphUri) {
		String updateQueryString;
		
		if (graphUri.equals(""))
			updateQueryString = "DELETE DATA { "+ rdfTriples +" }";
		else updateQueryString = "DELETE DATA { GRAPH "+ graphUri +" {"+ rdfTriples +"} }";
		
		boolean result = this.triplestore.sparqlUpdateHandled(updateQueryString);
		return result;
	}
	
	boolean delete(Object modelObj) {
		return this.delete(modelObj, "");
	}
	
	boolean delete(Object modelObj, String graphUri) {
		boolean deletion = false;
		if (this.checkObjectModel(modelObj)) {
			String rdfTriples = this.convertToRDF(modelObj);
			deletion = this.delete(rdfTriples, graphUri);
		}
		// TODO: RAISE EXCEPTION at delete(), the object to be "deleted" is not a Model object.
		return deletion;
	}
	
	/*************************
	 * 
	 * Miscellaneous FUNCTIONS
	 *
	 *************************/	
	
	@SuppressWarnings("rawtypes")
	private boolean checkObjectModel(Object obj)	{
		boolean isModelObject = false;
		Class objClass = obj.getClass();
		if (objClass.getPackage().toString().contains("model"))
			isModelObject = true;
		return isModelObject;
	}
	
}
