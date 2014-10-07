package controller;

import java.util.ArrayList;

import utility.UConfig;
import utility.UDebug;
import foundation.FFoundationFacade;
import model.MFeatureVersion;

public class CTRCalculus {

	private FFoundationFacade ffacade;
	private CCalculusAbstract cfactor;
//	private HashMap<MFFactor, Double> effects;
	private ArrayList<String> dates;

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
		
		this.dates = new ArrayList<String>();
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
		
		this.dates = ffacade.retrieveDateList(vgihGraphUri);
		
		int countDate = 1;
		int countFvs = 0;
//		int totalFvs = ffacade.countClassSubject("MFeatureVersion", "graphs:hvgi");
		int totalFvs = ffacade.countClassSubject("MFeatureVersion", "graphs:"+UConfig.hvgiGraph);
		int totalFs = ffacade.countClassSubject("MFeature", "graphs:"+UConfig.hvgiGraph);
		this.debugGeneralInformations(totalFs, totalFvs, dates.size(), 3);
		
		for (String date : dates) {
			if (countDate % 100 == 0) UDebug.print("Features versions retrieved for " + countDate + " dates of " + dates.size() + ".", 3);
			UDebug.print("Retriving features versions for date " + date + " (" + countDate + " of " + dates.size() + ").", 3);
			ArrayList<MFeatureVersion> fvs = new ArrayList<MFeatureVersion>();
			fvs = ffacade.retrieveFVByDate(date, vgihGraphUri, 1);
			UDebug.print("\t\t # features versions retrieved "+fvs.size()+"\n",1);
			for (MFeatureVersion featureVersion : fvs) {
				UDebug.print("\t * feature version "+ featureVersion.getUriID() +"",1);
				UDebug.print("\t * author "+ featureVersion.getAuthor().getAccountName() +"\n",1);
				this.compute(featureVersion);
			}
			countFvs = countFvs + fvs.size();
			UDebug.print("# features versions processed "+fvs.size()+" for a total of " + countFvs +"/"+ totalFvs, 3);
			UDebug.print(".\n\n", 3);
			countDate++;
		}
	}

	public void compute(MFeatureVersion featureVersion)	{		
		cfactor.computeTW(featureVersion);
	}

	public void debugGeneralInformations(int totalFs, int totalFvs, int totalDates, int dbgLevel) {
		
		UDebug.print("Total number of Features: " + totalFs +".\n", dbgLevel);
		UDebug.print("Total number of Feature Versions: " + totalFvs +".\n", dbgLevel);
		UDebug.print("Total number of Dates: " + totalDates +".\n", dbgLevel);
		UDebug.print("\n", dbgLevel);
		
	}
	
	public ArrayList<String> getDates() {
		return dates;
	}
	public void setDates(ArrayList<String> dates) {
		this.dates = dates;
	}

}
