package foundation;

import model.MReputation;
import utility.UConfig;

public class FReputation extends FFoundationAbstract {

	public FReputation() {	}

	@Override
	protected String getClassUri(){
		return "tandr:Reputation";
	}

	@Override
	public Object retrieveByURI(String trustworthinessUri, String graphUri, int lazyDepth) {
		FReputationExport fReputationExport = this.getFReputationExport();		
		return fReputationExport.retrieveByURI(trustworthinessUri, graphUri, lazyDepth);
	}

	@Override
	public String convertToRDFXML(Object obj) {
		FReputationExport fReputationExport = this.getFReputationExport();
		return fReputationExport.convertToRDFXML((MReputation) obj);
	}
	
	private FReputationExport getFReputationExport(){
		
		FReputationExport fReputationExport;
		String exportClass = "modules." + UConfig.module_trustworthiness_calculus +"."+ UConfig.reputation_export;
		
		try {
			fReputationExport = (FReputationExport) Class.forName(exportClass).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.out.print("Unable to instantiate the class " + exportClass + "\n"
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			fReputationExport = new modules.tandr.foundation.FReputationTandr();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.out.print("Unable to access the class " + exportClass + ". "
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			fReputationExport = new modules.tandr.foundation.FReputationTandr(); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.print("Unable to locate the class " + exportClass + ". "
					+exportClass + " do not exists \n"
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			fReputationExport = new modules.tandr.foundation.FReputationTandr(); 
		}
		
		return fReputationExport;
	}

	
}
