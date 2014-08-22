package foundation;

import utility.UConfig;
import model.MTrustworthiness;

public class FTrustworthiness extends FFoundationAbstract {

	public FTrustworthiness() {	}

	@Override
	protected String getClassUri(){
		return "tandr:Trustworthiness";
	}

	@Override
	public Object retrieveByURI(String trustworthinessUri, String graphUri, int lazyDepth) {
		FTrustworthinessExport fTrustExport = this.getFTrustworthinessExport();		
		fTrustExport.setTriplestore(triplestore);
		return fTrustExport.retrieveByURI(trustworthinessUri, graphUri, lazyDepth);
	}

	public String convertToRDF(Object obj) {
		FTrustworthinessExport fTrustExport = this.getFTrustworthinessExport();
		fTrustExport.setTriplestore(triplestore);
		return fTrustExport.convertToRDF((MTrustworthiness) obj);
	}
	
	private FTrustworthinessExport getFTrustworthinessExport(){
		
		FTrustworthinessExport fTrustExport;
		String exportClass = "modules." + UConfig.module_trustworthiness_calculus +"."+ UConfig.trustworthiness_export;
		
		try {
			fTrustExport = (FTrustworthinessExport) Class.forName(exportClass).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.out.print("Unable to instantiate the class " + exportClass + "\n"
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			fTrustExport = new modules.tandr.foundation.FTrustworthiness();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.out.print("Unable to access the class " + exportClass + ". "
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			fTrustExport = new modules.tandr.foundation.FTrustworthiness(); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.print("Unable to locate the class " + exportClass + ". "
					+exportClass + " do not exists \n"
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			fTrustExport = new modules.tandr.foundation.FTrustworthiness(); 
		}
		
		return fTrustExport;
	}

}
