package foundation;

import java.util.ArrayList;

import com.hp.hpl.jena.query.ResultSet;

public class FTrustworthiness extends FFoundationAbstract {

	public FTrustworthiness() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<String> retrieveAll() {
		return super.getURIsOfClass("tandr:Trustworthiness");
	}

	@Override
	public Object retrieveByURI(String uri, int lazyDepth) {
		// TODO implement FTrustworthiness retriveByURI method
		return null;
	}

	@Override
	public String convertToRDF(Object obj) {
		// TODO implement FTrustworthiness convertToRDF method
		return null;
	}

	@Deprecated
	@Override
	public ResultSet retrieveAllAsResultSet() {
		return super.getURIsOfClassAsResultSet("tandr:Trustworthiness");
	}

}
