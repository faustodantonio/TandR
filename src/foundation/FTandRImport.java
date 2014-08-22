package foundation;

import model.MTrustworthiness;

public abstract class FTandRImport {

	protected FTripleStore triplestore;
	
	public FTandRImport() {	}
	
	public FTandRImport(FTripleStore triplestore) {
		this.setTriplestore(triplestore);
	}

	public abstract boolean importTandRTriples();
	
	public void setTriplestore(FTripleStore triplestore)	{
		this.triplestore = triplestore;
	}
	public FTripleStore getTriplestore()	{
		return this.triplestore;
	}
}
