package controller;

import java.util.ArrayList;
import java.util.Date;

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
		
		int dbgLevel = 1;
		
		int countDate = 1;
		int countFvs = 0;
//		int totalFvs = ffacade.countClassSubject("MFeatureVersion", "graphs:hvgi");
		int totalFvs = ffacade.countClassSubject("MFeatureVersion", UConfig.getVGIHGraphURI());
		int totalFs = ffacade.countClassSubject("MFeature", UConfig.getVGIHGraphURI());
		this.debugGeneralInformations(totalFs, totalFvs, dates.size(), dbgLevel);
		
		Date startedAt = new Date();
		Date startedDate = startedAt;
		
		for (String date : dates) {
			if (countDate % 100 == 0) UDebug.print("Features versions retrieved for " + countDate + " dates of " + dates.size() + ".\t", dbgLevel);
			UDebug.print("Retriving features versions for date " + date + " (" + countDate + " of " + dates.size() + ").\t", dbgLevel+1);
			ArrayList<MFeatureVersion> fvs = new ArrayList<MFeatureVersion>();
			fvs = ffacade.retrieveFVByDate(date, vgihGraphUri, 1);
			UDebug.print("\t # features versions retrieved "+fvs.size()+"\n",dbgLevel+2);
			
//			fvs = this.cleanFVS(fvs);
			
			for (MFeatureVersion featureVersion : fvs) {
				UDebug.print("\t * feature version "+ featureVersion.getUriID() +"",dbgLevel+2);
				UDebug.print("\t * author "+ featureVersion.getAuthor().getAccountName() +"\n",dbgLevel+2);
				this.compute(featureVersion);
			}
			countFvs = countFvs + fvs.size();
			
			Date endedDate = new Date();
			
			UDebug.print("\n# features versions processed "+fvs.size()+" for a total of " + countFvs +"/"+ totalFvs, dbgLevel+1);
			UDebug.print("\n# PARTIAL elapsed time: " + UDebug.formatInterval(endedDate.getTime() - startedDate.getTime()) +", "
							+ "TOTAL elapsed time: "  + UDebug.formatInterval(endedDate.getTime() - startedAt.getTime()) + ". ", dbgLevel+1);
			UDebug.print("\n\n", dbgLevel+1);
			countDate++;
			startedDate = endedDate;
		}
		
//		for (String date : dates) {
//			ArrayList<MFeatureVersion> fvs = ffacade.retrieveFVByDate(date, vgihGraphUri, 1);
//			for (MFeatureVersion featureVersion : fvs) {
//				this.cfactor.updateLastFeatureVersion(featureVersion);
//			}
//		}
		
		ArrayList<MFeatureVersion> nonEndedVersions = ffacade.retrieveNonEndedVersions(UConfig.getVGIHGraphURI());
		for (MFeatureVersion featureVersion : nonEndedVersions) {
			this.cfactor.updateLastFeatureVersion(featureVersion);
		}
	}

	public void compute(MFeatureVersion featureVersion)	{		
		cfactor.computeTW(featureVersion);
	}

	public void updateLast(MFeatureVersion featureVersion)	{		
		cfactor.updateLastFeatureVersion(featureVersion);
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
