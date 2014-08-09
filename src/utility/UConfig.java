package utility;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class UConfig {
	
	private static UConfig instance = null;
	
	public static String datasetQueryURI_Fuseki = "http://semantic.net:3030/TandR_Model/query";
	public static String datasetSPARQLQueryURI_Parliment = "http://semantic.net:8080/parliament/sparql";
	public static String datasetBULKQueryURI_Parliment = "http://semantic.net:8080/parliament/bulk";
		
	public static int debugLevel = 3;
	
	public static String triplestoreConnectionClass = "FParliament";
//	public static String triplestoreConnectionClass = "FFuseki";

	public static String module_trustworthiness_calculus = "tandr";
	public static String main_trustworthiness_calculus = "controller.CTrustworthiness"; //the referenced class has to extend controller.CMainFactor class
	public static Map< Map.Entry<String, Map<String,Double>>, Double> effects_hierarchy = 
			new HashMap< Map.Entry<String, Map<String,Double>>, Double>();
	
	public static SimpleDateFormat sdf;
	public static String epsg_crs = "900913";
	public static double featureInfluenceRadius = 50;

	//*************************************** Methods ***************************************//
	
	public static UConfig instance()
	{
		if (instance == null)
			instance = new UConfig();
		
		return instance;
	}
	
	private UConfig()
	{
		initEffectsHierarchy();		
		UConfig.sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	}
	
	private static void initEffectsHierarchy()
	{
		TreeMap<String,Double> component_X_direct = new TreeMap<String,Double>();
		component_X_direct.put("MFCompGeomDir", 0.33);
		component_X_direct.put("MFCompQualDir", 0.33);
		component_X_direct.put("MFCompSemDir" , 0.33);
		
		TreeMap<String,Double> component_X_indirect = new TreeMap<String,Double>();
		component_X_indirect.put("MFCompGeomInd", 0.33);
		component_X_indirect.put("MFCompQualInd", 0.33);
		component_X_indirect.put("MFCompSemInd" , 0.33);
		
		TreeMap<String,Double> component_X_temporal = null;
		
		TreeMap< String, Map<String,Double> > factor_direct = new TreeMap<String, Map<String,Double>>();
		TreeMap< String, Map<String,Double> > factor_indirect = new TreeMap<String, Map<String,Double>>();
		TreeMap< String, Map<String,Double> > factor_temporal = new TreeMap<String, Map<String,Double>>();
		
		factor_direct.put("MFDepDirect" , component_X_direct);
		factor_indirect.put("MFDepIndirect" , component_X_indirect);
		factor_temporal.put("MFIndTemporal" , component_X_temporal);
		
		UConfig.effects_hierarchy.put(factor_direct.firstEntry(), 0.33);
		UConfig.effects_hierarchy.put(factor_indirect.firstEntry(), 0.33);
		UConfig.effects_hierarchy.put(factor_temporal.firstEntry(), 0.33);
	}
	
}
