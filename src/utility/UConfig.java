package utility;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.jdom2.Namespace;

public class UConfig {
	
	private static UConfig instance = null;
	
	/*************************
	 * 
	 * Triplestore connection PARAMETERS
	 *
	 *************************/	
	
	public static String triplestoreConnectionClass = "FParliament";
//	public static String triplestoreConnectionClass = "FFuseki";
	
	public static String datasetQueryURI_Fuseki = "http://semantic.net:3030/TandR_Model/query";
	public static String datasetSPARQLQueryURI_Parliment = "http://semantic.net:8080/parliament/sparql";
	public static String datasetBULKQueryURI_Parliment = "http://semantic.net:8080/parliament/bulk";
		
	/*************************
	 * 
	 * Module selection PARAMETERS
	 *
	 *************************/	

	public static String module_trustworthiness_calculus = "tandr";
	
	/**
	 * the referenced class has to extend controller.CMainFactor class
	 */
	public static String main_trustworthiness_calculus = "controller.CTandR";
	/**
	 * the referenced class has to implement foundaiton.FTrustworthinessExport interface
	 */
	public static String trustworthiness_export = "foundation.FTrustworthinessTandr";
	/**
	 * the referenced class has to implement foundaiton.FReputationExport interface
	 */
	public static String reputation_export = "foundation.FReputationTandr";
	/**
	 * the referenced class has to extend foundaiton.FTrustworthinessExport class
	 */
	public static String tandr_import = "foundation.FTandR";
	
	/*************************
	 * 
	 * Installation and Named Graph PARAMETERS
	 *
	 *************************/
	
	/**
	 * 0 -> Never perform installation (DEFAULT)                            *|*  
	 * 1 -> Perform installation whether it has not yet been performed      *|*  
	 * 2 -> Perform installation, it deletes the previouses installed data  *|*
	 * 3 -> Leave previous dataset, but deletes the computed T and R values *|*
	 * 4 -> Restore dataset, leaving the computed T and R values            *|*
	 */
	public static int installation_mode = 3;
	
	public static boolean graph_usage = true;
	
	public static String graphURI = "http://parliament.semwebcentral.org/parliament#";
	public static String hvgiGraph = "hvgi";
	public static String tandrGraph = "tandr";
	
	public static String inputRDFfilesDirectory = "/opt/lampp/htdocs/data/hvgi/";
	public static String inputRDFfileRegex = ".*.rdf";
	
	/*************************
	 * 
	 * Miscellaneous PARAMETERS
	 *
	 *************************/	
	
	/**
	 * 0 -> Raw	     *|*  
	 * 1 -> Compact  *|*  
	 * 2 -> Pretty   *|*
	 */
	public static int rdf_output_format = 2 ;
	
	public static int debugLevel = 3;
	
	public static Map< Map.Entry<String, Map<String,Double>>, Double> effects_hierarchy = 
			new HashMap< Map.Entry<String, Map<String,Double>>, Double>();
	
	/*************************
	 *
	 * Temporal and Spatial PARAMETERS
	 *
	 *************************/
	
	public static SimpleDateFormat sdf;
	public static String epsg_crs = "900913";
	public static double featureInfluenceRadius = 50;
	
	public static HashMap<String, Namespace> namespaces;

	//***************************************************************************************//
	//*************************************** METHODS ***************************************//
	//***************************************         ***************************************//
	
	public static UConfig instance()
	{
		if (instance == null)
			instance = new UConfig();
		
		return instance;
	}
	
	private UConfig()
	{
		initEffectsHierarchy();
		initNamespaces();
		UConfig.sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	}
	
	public static String getVGIHGraphURI(){
		// return "graphs:" + UConfig.hvgiGraph;
		return "<" + UConfig.graphURI + UConfig.hvgiGraph + ">";
	}
	public static String getTANDRGraphURI(){
		// return "graphs:" + UConfig.tandrGraph;
		return "<" + UConfig.graphURI + UConfig.tandrGraph + ">";
	}
	
	private void initNamespaces()
	{
		namespaces = new HashMap<String, Namespace>();
		
		Namespace rdf       = Namespace.getNamespace("rdf",       "http://www.w3.org/1999/02/22-rdf-syntax-ns#"        );
		Namespace xsd       = Namespace.getNamespace("xsd",       "http://www.w3.org/2001/XMLSchema#"                  );
		Namespace dcterms   = Namespace.getNamespace("dcterms",   "http://purl.org/dc/terms/"                          );
		Namespace foaf      = Namespace.getNamespace("foaf",      "http://xmlns.com/foaf/0.1/"                         );
		Namespace time      = Namespace.getNamespace("time",      "http://www.w3.org/2006/time#"                       );		
		Namespace geosparql = Namespace.getNamespace("geosparql", "http://www.opengis.net/ont/geosparql#"              );
		Namespace sf        = Namespace.getNamespace("sf",        "http://www.opengis.net/ont/sf#"                     );
		Namespace units     = Namespace.getNamespace("units",     "http://www.opengis.net/def/uom/OGC/1.0/"            );
		Namespace prv       = Namespace.getNamespace("prv",       "http://purl.org/net/provenance/ns#"                 );
		Namespace osp       = Namespace.getNamespace("osp",       "http://semantic.web/vocabs/osm_provenance/osp#"     );
		Namespace hvgi      = Namespace.getNamespace("hvgi",      "http://semantic.web/vocabs/history_vgi/hvgi#"       );
		Namespace tandr     = Namespace.getNamespace("tandr",     "http://semantic.web/vocabs/tandr_assessment/tandr#" );
		Namespace graphs    = Namespace.getNamespace("graphs",    "http://parliament.semwebcentral.org/parliament#"    );
				
		namespaces.put(rdf.getPrefix()       , rdf       );
		namespaces.put(xsd.getPrefix()       , xsd       );
		namespaces.put(dcterms.getPrefix()   , dcterms   );
		namespaces.put(foaf.getPrefix()      , foaf      );
		namespaces.put(time.getPrefix()      , time      );
		namespaces.put(geosparql.getPrefix() , geosparql );
		namespaces.put(sf.getPrefix()        , sf        );
		namespaces.put(units.getPrefix()     , units     );
		namespaces.put(prv.getPrefix()       , prv       );		
		namespaces.put(osp.getPrefix()       , osp       );
		namespaces.put(hvgi.getPrefix()      , hvgi      );
		namespaces.put(tandr.getPrefix()     , tandr     );
		namespaces.put(graphs.getPrefix()    , graphs    );
	}
	
	private void initEffectsHierarchy()
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
		
		TreeMap< String, Map<String,Double> > factor_direct   = new TreeMap<String, Map<String,Double>>();
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
