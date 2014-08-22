package controller;


import utility.UConfig;
import foundation.FFoundationFacade;
import model.MFeatureVersion;
import model.MTrustworthiness;

public class CTrustworthinessCalculusLazy {

	private FFoundationFacade ffacade;
	private CMainFactor cfactor;
//	private HashMap<MFFactor, Double> effects;

	public CTrustworthinessCalculusLazy()
	{
		this.ffacade = new FFoundationFacade();
		
		String factorClass = "modules." + UConfig.module_trustworthiness_calculus +"."+ UConfig.main_trustworthiness_calculus;
		
		try {
			cfactor = (CMainFactor) Class.forName(factorClass).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.out.print("Unable to instantiate the class " + factorClass + "\n"
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			cfactor = new modules.tandr.controller.CTrustworthiness();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.out.print("Unable to access the class " + factorClass + ". "
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			cfactor = new modules.tandr.controller.CTrustworthiness(); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.print("Unable to locate the class " + factorClass + ". "
					+factorClass + " do not exists \n"
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			cfactor = new modules.tandr.controller.CTrustworthiness(); 
		}
		
	}
	
	/**
	 * Get the first fv 
	 * Calculate the TW value
	 * Save the TW value in Parliament
	 * Until all the fvs are processed
	 * 
	 * 		Get the next fv
	 * 		Calculate the fv TW
	 * 		Save the TW value in Parliament 
	 * 
	 * 		Get all others fvs it confirms 
	 * 		Recompute confirmed fv TW
	 *  
	 * 		Update the TW value in Parliament
	 */
	public void computeAllTrustworthinessValues()
	{
		String firstUri = ffacade.retrieveFirstFVUri();
		MFeatureVersion firstFV = (MFeatureVersion) ffacade.retrieveByUri(firstUri, MFeatureVersion.class);
		
//		MTrustworthiness firstTw = this.computeFVTrustworthiness(firstFV);
//		ffacade.create(firstTw); // The trustworthiness has to be saved from modules controllers
				
		this.computeFVTrustworthiness(firstFV);
		String nextUri = ffacade.retrieveNextFVUri(firstFV);
		MFeatureVersion nextFV = (MFeatureVersion) ffacade.retrieveByUri(nextUri, MFeatureVersion.class);
		while (nextFV != null) {
			
//			MTrustworthiness TwFvNext = this.computeFVTrustworthiness(nextFV); // Compute and cross assign reference
//			ffacade.create(TwFvNext);
			
			this.computeFVTrustworthiness(nextFV);
			nextUri = ffacade.retrieveNextFVUri(firstFV);
			nextFV = (MFeatureVersion) ffacade.retrieveByUri(nextUri, MFeatureVersion.class);
		}
	}

	public MTrustworthiness computeFVTrustworthiness(MFeatureVersion featureVersion)
	{
		MTrustworthiness trustworthiness = new MTrustworthiness();
		
		featureVersion.setTrustworthiness(trustworthiness);
		trustworthiness.setFeatureVersion(featureVersion);
		
		cfactor.computeTW(featureVersion);
		
		return trustworthiness;
	}


}
