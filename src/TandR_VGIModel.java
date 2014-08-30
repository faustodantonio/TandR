import controller.CInstallation;
import controller.CTRCalculus;
import utility.UConfig;


public class TandR_VGIModel {

	public static void main(String[] args) {
		// TODO complete application start 
		UConfig.instance();
		
		CTRCalculus trust   = new CTRCalculus();
		CInstallation  install = new CInstallation();
		
		install.install();
		trust.computeAll();
	}
	
	

}
