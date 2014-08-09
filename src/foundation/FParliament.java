package foundation;

import java.io.IOException;

import org.apache.jena.atlas.logging.LogCtl;

import utility.UConfig;

import com.hp.hpl.jena.query.ResultSet;
import com.bbn.parliament.jena.joseki.client.RemoteModel;

class FParliament extends FTripleStore{

	private RemoteModel parliament;
	
	private String sparqlEndpointUrl = UConfig.datasetSPARQLQueryURI_Parliment;
	private String bulkEndpointUrl = UConfig.datasetBULKQueryURI_Parliment;
	
	public FParliament() { 
		
		parliament = new RemoteModel(sparqlEndpointUrl, bulkEndpointUrl);
	}
	
	@Override
	public ResultSet sparqlSelect(String selectQueryString)
	{
		LogCtl.setCmdLogging();
		selectQueryString = this.AddPrefixes(selectQueryString);
		
//		Query query = QueryFactory.create(selectQueryString);
//		QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpointUrl, query);
//		ResultSet rs = qexec.execSelect() ;
		
		ResultSet rs = null;
		try {
			rs = parliament.selectQuery(selectQueryString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	    return rs;
	}
	
	@Override
	public boolean sparqlUpdate(String updateQueryString) {
		LogCtl.setCmdLogging();
		boolean execFine = true;
		
		updateQueryString = this.AddPrefixes(updateQueryString);
		try {
			parliament.updateQuery(updateQueryString);
		} catch (IOException e) {
			execFine = false;
			e.printStackTrace();
		}

		return execFine;
	}

}
