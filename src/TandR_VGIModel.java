import controller.CInstallation;
import controller.CTRCalculus;
import controller.validation.CValidation;
import utility.UConfig;
import view.VShow;


public class TandR_VGIModel {

	public static void main(String[] args) {
		// TODO complete application start 
		UConfig.instance();
		
		CTRCalculus trust   = new CTRCalculus();
		CInstallation  install = new CInstallation();
		CValidation validation = new CValidation();
		
		VShow view = new VShow();
		
		install.install();
		trust.computeAll();
		validation.validate();
		
		view.showAll(trust.getDates());
	}
	
	

}
