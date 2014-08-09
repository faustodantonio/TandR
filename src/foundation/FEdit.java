package foundation;

import java.util.ArrayList;
import java.util.Map.Entry;

import model.MEdit;
import utility.UDebug;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class FEdit extends FFoundationAbstract {
	
	public FEdit()
	{
		super();
	}

	@Override
	@Deprecated
	public ResultSet retrieveAllAsResultSet()
	{
		return super.getURIsOfClassAsResultSet("osp:Edit");
	}
	@Override
	public ArrayList<String> retrieveAll()
	{
		return super.getURIsOfClass("osp:Edit");
	}
	@Override
	public MEdit retrieveByURI(String editURI, int lazyDepth)
	{
		MEdit edit = new MEdit();
		
		String queryString = ""
				+ "\tSELECT ?author ?changesGeometry ?addtag ?changevalueofkey ?removetag \n"
				+ "\tWHERE \n"
				+ "\t{ \n"
				+ "\t\tOPTIONAL { <"+editURI+">" + " prv:performedBy ?author } \n"
				+ "\t\tOPTIONAL { <"+editURI+">" + " osp:changesGeometry ?changesGeometry } \n"
				+ "\t\tOPTIONAL { <"+editURI+">" + " osp:addTags ?addtag } \n"
				+ "\t\tOPTIONAL { <"+editURI+">" + " osp:changesValuesOfKey ?changevalueofkey } \n"
				+ "\t\tOPTIONAL { <"+editURI+">" + " osp:removeTags ?removetag } \n"
				+ "\t}";
		
		UDebug.print("SPARQL query: \n" + queryString + "\n\n", 5);
		
		ResultSet rawResults = triplestore.sparqlSelect(queryString);
		
		ResultSetRewindable queryRawResults = ResultSetFactory.copyResults(rawResults);
		UDebug.print("SPARQL query results: \n" + ResultSetFormatter.asText(queryRawResults) + "\n\n",6);
		queryRawResults.reset();
		
		QuerySolution generalQueryResults = queryRawResults.next();
		
		RDFNode changesGeometry = generalQueryResults.getLiteral("changesGeometry");
		RDFNode performedBy = generalQueryResults.getResource("author");
		
		edit.setUri( editURI );
		if (changesGeometry != null)
			edit.setChangesGeometry( Boolean.parseBoolean(changesGeometry.toString().replace("^^http://www.w3.org/2001/XMLSchema#boolean", "")) );
		if (performedBy != null)
			edit.setAuthorUri(performedBy.toString());
		
		FAuthor fauthor = new FAuthor();
		
		if ( lazyDepth > 0 && performedBy != null)
			edit.setAuthor(fauthor.retrieveByURI(performedBy.toString(),lazyDepth-1));
		
		this.retriveEditTags(edit, queryRawResults);
		
		return edit;
	}	

	private void retriveEditTags(MEdit edit, ResultSetRewindable rawResults) {
		
		FTag ftag = new FTag();
		rawResults.reset();
		ResultSet queryRawResults = ResultSetFactory.copyResults(rawResults);

		for (int i = 0; i < rawResults.size(); i++ )
		{
			QuerySolution generalQuery = queryRawResults.next();
			
			if ( generalQuery.getResource("addtag") != null )
			{
				String tagUri = generalQuery.getResource("addtag").toString();
				Entry<String, String> tag = ftag.retrieveTagByURI(tagUri);
				if (tag != null && tag.getKey() != null && tag.getValue() != null) 
					edit.addAddedTag(tag.getKey(), tag.getValue());
			}
			if ( generalQuery.getResource("changevalueofkey") != null )
			{
				String tagUri = generalQuery.getResource("changevalueofkey").toString();
				Entry<String, String> tag = ftag.retrieveTagByURI(tagUri);
				if (tag != null && tag.getKey() != null && tag.getValue() != null) 
					edit.addChangedTag(tag.getKey(), tag.getValue());
			}
			if ( generalQuery.getResource("removetag") != null )
			{
				String tagUri = generalQuery.getResource("removetag").toString();
				Entry<String, String> tag = ftag.retrieveTagByURI(tagUri);
				if (tag != null && tag.getKey() != null && tag.getValue() != null) 
					edit.addRemovedTag(tag.getKey(), tag.getValue());
			}
		}
		
	}

	@Override
	public String convertToRDF(Object obj) {
		// TODO Implement Edit's convertToRDF() method
		return null;
	}

}
