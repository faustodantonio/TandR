package foundation;

import model.MReputation;
import model.MTrustworthiness;

public interface FReputationExport {

//	protected FTripleStore triplestore;
//	
//	public FTrustworthinessExport() {	}

	public abstract String convertToRDFXML(MReputation trustworthiness);
	public abstract MReputation retrieveByURI(String reputationUri,String graphUri, int lazyDepth);
	public abstract MReputation getMaximumReputation(String computedAt);
	public abstract boolean create(MReputation reputation, String  graph);
	
}
