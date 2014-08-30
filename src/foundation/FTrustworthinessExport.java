package foundation;

import model.MTrustworthiness;

public interface FTrustworthinessExport {

//	protected FTripleStore triplestore;
//	
//	public FTrustworthinessExport() {	}

	public abstract String convertToRDFXML(MTrustworthiness trustworthiness);
	public abstract MTrustworthiness retrieveByURI(String trustworthinessUri,String graphUri, int lazyDepth);
	
}
