package foundation;

import java.util.ArrayList;

import model.MFeatureVersion;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
/**
 * All class methods works only if there exists a pair of class <mclass,fclass> such that
 * mclass belongs to Model package, fclass belongs to Foundation package and 
 * the names of the two classes differs only for the first letter.
 * @author fausto
 */
public class FFoundationFacade {
	
	FFoundationFactory ffactory;
	
	public FFoundationFacade() {
		ffactory = new FFoundationFactory();
	}
	
	public String convertToRDF(Object modelObj)
	{
		FFoundationAbstract foundation = ffactory.getFFoundation(modelObj);		
		return foundation.convertToRDF(modelObj);
	}
	
	/*************************
	 * 
	 * Graph FUNCTIONS
	 *
	 *************************/	
	
	public boolean importJenaModel(Model jenaModel){
		FInstallation finstall = new FInstallation();
		return finstall.importJenaModel(jenaModel,"");
	}
	
	public boolean importJenaModel(Model jenaModel, String graphName){
		FInstallation finstall = new FInstallation();
		return finstall.importJenaModel(jenaModel, graphName);
	}
	
	public boolean createGraph(String graphName, String namespace) {
		FInstallation finstall = new FInstallation();
		return finstall.createGraph(graphName, namespace);
	}
	
	public boolean deleteGraph(String graphName, String namespace) {
		FInstallation finstall = new FInstallation();
		return finstall.deleteGraph(graphName, namespace);
	}
	
	public boolean createGraph(String graphName) {
		FInstallation finstall = new FInstallation();
		return finstall.createGraph(graphName, "");
	}
	
	public boolean deleteGraph(String graphName) {
		FInstallation finstall = new FInstallation();
		return finstall.deleteGraph(graphName, "");
	}
	
	public boolean checkGraphExists(String graphName, String namespace) {
		FInstallation finstall = new FInstallation();
		return finstall.graphExists(graphName, namespace);
	}
	
	public boolean enableGraphIndexes(String graphName,String namespaceUri){
		FInstallation finstall = new FInstallation();
		return finstall.enableGraphIndexes(graphName, namespaceUri);
	}
	
	/*************************
	 * 
	 * Model CrUD FUNCTIONS
	 *
	 *************************/	
	
	public boolean create(Object modelObj) 
	{
		FFoundationAbstract foundation = ffactory.getFFoundation(modelObj);		
		return foundation.create(modelObj);
	}
	
	public boolean update(Object oldModelObj, Object updModelObj) {
		FFoundationAbstract foundation = ffactory.getFFoundation(updModelObj);
		return foundation.update(oldModelObj, updModelObj);
	}
	
	public boolean delete(Object modelObj) {
		FFoundationAbstract foundation = ffactory.getFFoundation(modelObj);		
		return foundation.delete(modelObj);
	}
	
	/*************************
	 * 
	 * Model Retriving FUNCTIONS
	 *
	 *************************/	
	
	/************* Retrive All *************/
	
	public ArrayList<String> retrieveAll(String mclass, String graphUri)
	{
		FFoundationAbstract ffoundation = this.ffactory.getFFoundation(mclass);
		return ffoundation.retrieveAll(graphUri);
	}
	@SuppressWarnings("rawtypes")
	public ArrayList<String> retrieveAll(Class mclass, String graphUri)
	{
		FFoundationAbstract ffoundation = this.ffactory.getFFoundation(mclass);
		return ffoundation.retrieveAll(graphUri);
	}
	
	public ArrayList<String> retrieveAll(String mclass)
	{
		FFoundationAbstract ffoundation = this.ffactory.getFFoundation(mclass);
		return ffoundation.retrieveAll();
	}
	@SuppressWarnings("rawtypes")
	public ArrayList<String> retrieveAll(Class mclass)
	{
		FFoundationAbstract ffoundation = this.ffactory.getFFoundation(mclass);
		return ffoundation.retrieveAll();
	}
	
	/************* Retrive By URI (Lazy) *************/
	
	public Object retrieveByUri(String uri, String mclass)
	{		
		return this.retrieveByUri(uri, 0, mclass);
	}	
	@SuppressWarnings("rawtypes")
	public Object retrieveByUri(String uri, Class mclass)
	{
		return this.retrieveByUri(uri, 0, mclass);
	}
	public Object retrieveByUri(String uri, String graphUri, String mclass)
	{		
		return this.retrieveByUri(uri, graphUri, 0, mclass);
	}	
	@SuppressWarnings("rawtypes")
	public Object retrieveByUri(String uri, String graphUri, Class mclass)
	{
		return this.retrieveByUri(uri, graphUri, 0, mclass);
	}
	
	/************* Retrive By URI *************/
	
	public Object retrieveByUri(String uri, int lazyDepth, String mclass)
	{
		FFoundationAbstract ffoundation = this.ffactory.getFFoundation(mclass);
		return ffoundation.retrieveByURI(uri, lazyDepth);
	}
	@SuppressWarnings("rawtypes")
	public Object retrieveByUri(String uri, int lazyDepth, Class mclass)
	{
		FFoundationAbstract ffoundation = this.ffactory.getFFoundation(mclass);
		return ffoundation.retrieveByURI(uri, lazyDepth);
	}
	
	public Object retrieveByUri(String uri, String graphUri, int lazyDepth, String mclass)
	{
		FFoundationAbstract ffoundation = this.ffactory.getFFoundation(mclass);
		return ffoundation.retrieveByURI(uri, graphUri,lazyDepth);
	}
	@SuppressWarnings("rawtypes")
	public Object retrieveByUri(String uri, String graphUri, int lazyDepth, Class mclass)
	{
		FFoundationAbstract ffoundation = this.ffactory.getFFoundation(mclass);
		return ffoundation.retrieveByURI(uri, graphUri, lazyDepth);
	}
	
	/*************************
	 * 
	 * FeatureVersion FUNCTIONS
	 *
	 *************************/	
	
	public ArrayList<String> retrieveFVPreviousesNeighbours(MFeatureVersion featureVersion, String fv_wkt_buffered)
	{
		FFeatureVersion ffoundation = new FFeatureVersion();
		return ffoundation.retrievePreviousesNeighbours(featureVersion.getIsValidFromString(), fv_wkt_buffered);
	}
	
	public String retrieveFirstFVUri()
	{
		FFeatureVersion ffoundation = new FFeatureVersion();
		return ffoundation.retrieveNext(null);
	}
	
	public String retrieveNextFVUri(MFeatureVersion fv)
	{
		FFeatureVersion ffoundation = new FFeatureVersion();
		return ffoundation.retrieveNext(fv.getIsValidFromString());
	}
	
	public ArrayList<String> retrieveFVPreviousesNeighbours(MFeatureVersion featureVersion, String graphUri, String fv_wkt_buffered)
	{
		FFeatureVersion ffoundation = new FFeatureVersion();
		return ffoundation.retrievePreviousesNeighbours(featureVersion.getIsValidFromString(), fv_wkt_buffered, graphUri);
	}
	
	public String retrieveFirstFVUri(String graphUri)
	{
		FFeatureVersion ffoundation = new FFeatureVersion();
		return ffoundation.retrieveNext(null,graphUri);
	}
	
	public String retrieveNextFVUri(MFeatureVersion fv, String graphUri)
	{
		FFeatureVersion ffoundation = new FFeatureVersion();
		return ffoundation.retrieveNext(fv.getIsValidFromString(), graphUri);
	}
	
	/*************************
	 * xxxxxxxxxxxxxxxxxxxxxxx
	 * x
	 * x DEPRECATED FUNCTIONS
	 * x
	 * xxxxxxxxxxxxxxxxxxxxxxx
	 *************************/
	@Deprecated
	public ResultSet retrieveAllAsResultSet(String mclass)
	{
		FFoundationAbstract ffoundation = this.ffactory.getFFoundation(mclass);
		return ffoundation.retrieveAllAsResultSet();
	}
	@Deprecated
	@SuppressWarnings("rawtypes")
	public ResultSet retrieveAllAsResultSet(Class mclass)
	{
		FFoundationAbstract ffoundation = this.ffactory.getFFoundation(mclass);
		return ffoundation.retrieveAllAsResultSet();
	}
	
}