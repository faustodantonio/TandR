package view;

import java.util.ArrayList;
import java.util.Date;

import foundation.FFoundationFacade;
import model.MFeatureVersion;
import utility.UConfig;
import utility.UDebug;

public class VShow {

	private VTrustworthiness trust;
	private FFoundationFacade ffacade;
	
	public VShow() {
		super();
		
		String viewClass = "modules." + UConfig.module_trustworthiness_calculus +"."+ UConfig.view_trustworthiness;
		
		try {
			this.trust = (VTrustworthiness) Class.forName(viewClass).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.out.print("Unable to instantiate the class " + viewClass + "\n"
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			this.trust = new modules.tandr.view.VTrustworthinessTandr();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.out.print("Unable to access the class " + viewClass + ". "
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			this.trust = new modules.tandr.view.VTrustworthinessTandr();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.print("Unable to locate the class " + viewClass + ". "
					+viewClass + " do not exists \n"
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			this.trust = new modules.tandr.view.VTrustworthinessTandr();
		}
		
		this.ffacade = new FFoundationFacade();
	}
	
	/**
	 * for each date
	 * 		print the date
	 * 		get the fvs
	 * 		for each fv get T
	 * 		print T (ask to module)
	 * 
	 */
	public void showAll(ArrayList<String> dates) {
		
		String trustInfo;
		String vgihGraphUri = UConfig.getVGIHGraphURI();
		
		UDebug.output(this.getOutputInfo(),10);
		
		for (String date:dates) {
			UDebug.output("\tDate: " + date +"\n",4);
			
			ArrayList<MFeatureVersion> fvs = new ArrayList<MFeatureVersion>();
			fvs = ffacade.retrieveFVByDate(date, vgihGraphUri, 1);
			for (MFeatureVersion featureVersion : fvs) {
				trustInfo = trust.getTrustworthinessString(featureVersion);
				UDebug.output("\t\t Feature Version "+ featureVersion.getUriID() +" " + trustInfo +"\n",4);
			}	
		}
		UDebug.output("\n" ,10);
	}

	private String getOutputInfo() {
		String info = "";
		
		String vgihGraphUri = UConfig.getVGIHGraphURI();
		String module = UConfig.module_trustworthiness_calculus;
		
		info = 	  
				  "********** Main Output Info **********" 	+ "\n"
				+ "\n" 
				+ "Computed At: " 			+ new Date() 				+ "\n"
				+ "Graph Uri: "  			+ vgihGraphUri 			+ "\n"
				+ "Calculation Module: "	+ module		 			+ "\n"
				+ "\n"
				+ "**************************************" 	+ "\n"
				+ "\n";
		
		return info;
	}
	
}
