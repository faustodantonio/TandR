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
		return fTrustExport.retrieveByURI(trustworthinessUri, graphUri, lazyDepth);
	}

	@Override
	public String convertToRDFXML(Object obj) {
		FTrustworthinessExport fTrustExport = this.getFTrustworthinessExport();
		return fTrustExport.convertToRDFXML((MTrustworthiness) obj);
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
			fTrustExport = new modules.tandr.foundation.FTrustworthinessTandr();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.out.print("Unable to access the class " + exportClass + ". "
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			fTrustExport = new modules.tandr.foundation.FTrustworthinessTandr(); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.print("Unable to locate the class " + exportClass + ". "
					+exportClass + " do not exists \n"
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			fTrustExport = new modules.tandr.foundation.FTrustworthinessTandr(); 
		}
		
		return fTrustExport;
	}

}
