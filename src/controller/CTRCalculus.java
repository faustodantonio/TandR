package controller;


import java.util.ArrayList;

import utility.UConfig;
import utility.UDebug;
import foundation.FFoundationFacade;
import model.MFeatureVersion;
import model.MTrustworthiness;

public class CTRCalculus {

	private FFoundationFacade ffacade;
	private CCalculusAbstract cfactor;
//	private HashMap<MFFactor, Double> effects;

	public CTRCalculus()
	{
		this.ffacade = new FFoundationFacade();
		
		String factorClass = "modules." + UConfig.module_trustworthiness_calculus +"."+ UConfig.main_trustworthiness_calculus;
		
		try {
			cfactor = (CCalculusAbstract) Class.forName(factorClass).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.out.print("Unable to instantiate the class " + factorClass + "\n"
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			cfactor = new modules.tandr.controller.CTandR();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.out.print("Unable to access the class " + factorClass + ". "
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			cfactor = new modules.tandr.controller.CTandR(); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.print("Unable to locate the class " + factorClass + ". "
					+factorClass + " do not exists \n"
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			cfactor = new modules.tandr.controller.CTandR(); 
		}
		
	}
	
	/**
	 * Get the dates list
	 * 
	 * Until all the dates are processed
	 * 
	 * 		Get the next date
	 * 		get all the features versions starts from that date
	 * 		for each of those features versions
	 * 			Calculate the fv TW value
	 * 			Save the TW values in Parliament 
	 * 			Get all others fvs it confirms 
	 * 			Recompute confirmed fv TW
	 * 
	 */
	public void computeAll()
	{
		String vgihGraphUri = UConfig.getVGIHGraphURI();
		
		ArrayList<String> dates = new ArrayList<String>();
		dates = ffacade.retrieveDateList(vgihGraphUri);
		
		int countDate = 1;
		int countFvs = 0;
		int totalFvs = ffacade.countClassSubject("MFeatureVersion", "graphs:hvgi");
		
		this.debugGeneralInformations(totalFvs, dates.size(), 3);
		
		for (String date : dates) {
			UDebug.print("Retriving features versions for date " + date + " (" + countDate + " of " + dates.size() + ").", 3);
			ArrayList<MFeatureVersion> fvs = new ArrayList<MFeatureVersion>();
			fvs = ffacade.retrieveByDate(date, vgihGraphUri, 1);
			for (MFeatureVersion featureVersion : fvs)
				this.compute(featureVersion);
			countFvs = countFvs + fvs.size();
			UDebug.print(" # features versions processed "+fvs.size()+" for a total of " + countFvs +"/"+ totalFvs, 3);
			UDebug.print(".\n", 3);
			countDate++;
		}
	}

	public MTrustworthiness compute(MFeatureVersion featureVersion)
	{
		MTrustworthiness trustworthiness = new MTrustworthiness(featureVersion);
		
		cfactor.computeTW(featureVersion);
		
		return trustworthiness;
	}

	public void debugGeneralInformations(int totalFvs, int totalDates, int dbgLevel) {
		int totalFs = ffacade.countClassSubject("MFeature", "graphs:hvgi");
		
		UDebug.print("Total number of Features: " + totalFs +".\n", dbgLevel);
		UDebug.print("Total number of Feature Versions: " + totalFvs +".\n", dbgLevel);
		UDebug.print("Total number of Dates: " + totalDates +".\n", dbgLevel);
		UDebug.print("\n", dbgLevel);
		
	}

}
