package foundation;

import model.MTrustworthiness;

public abstract class FTrustworthinessExport {

	protected FTripleStore triplestore;
	
	public FTrustworthinessExport() {	}
	
	public FTrustworthinessExport(FTripleStore triplestore) {
		this.setTriplestore(triplestore);
	}

	public abstract String convertToRDF(MTrustworthiness trustworthiness);
	public abstract MTrustworthiness retrieveByURI(String trustworthinessUri,String graphUri, int lazyDepth);
	
	public void setTriplestore(FTripleStore triplestore)
	{
		this.triplestore = triplestore;
	}
	public FTripleStore getTriplestore()
	{
		return this.triplestore;
	}
}
