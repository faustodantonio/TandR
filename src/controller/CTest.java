package controller;

import java.util.ArrayList;

import model.MAuthor;
import model.MEdit;
import model.MFeature;
import model.MFeatureVersion;
import modules.tandr.controller.CTrustworthiness;
import utility.UConfig;
import utility.UDebug;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import foundation.FFoundationFacade;

public class CTest {
	
	private String graphUri = "<http://parliament.semwebcentral.org/parliament#hvgi>";
	
	@SuppressWarnings("deprecation")
	public void printAllAuthorURIs()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		
		ResultSet uris = ffactory.retrieveAllAsResultSet("MAuthor");
		
	    ResultSetFormatter.out( uris );	    	  
	}
	
	public void printFirstAuthorInfos()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		
		ArrayList<String> uris = ffactory.retrieveAll("MAuthor", graphUri);
		
		UDebug.print( "Author URI: "+ uris.get(0) +"\n\n" , 2);
		
		MAuthor author = (MAuthor) ffactory.retrieveByUri( uris.get(0), graphUri, 0, "MAuthor");
		
		UDebug.print(author.toString(""),1);    	  
	}

	public void printFirstEditInfos()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		ArrayList<String> uris = ffactory.retrieveAll("MEdit");
		
		UDebug.print( "Edit ID: "+ uris.get(0) +"\n\n" , 2);
		
		MEdit edit = (MEdit) ffactory.retrieveByUri(uris.get(0), graphUri, 0, "MEdit");
		
		UDebug.print(edit.toString(""),1);   	  
	}
	
	public void printEditInfos()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		String id = "http://semantic.web/data/hvgi/edits.rdf#wayEdit5105728_5.0";
		
		UDebug.print( "Edit URI: "+ id +"\n\n" , 2);
		
		MEdit edit = (MEdit) ffactory.retrieveByUri(id, graphUri, 1, "MEdit");
		
		UDebug.print(edit.toString(""),1);   	  
	}
	
	public void printFeatureInfos()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		String id = "http://semantic.web/data/hvgi/ways.rdf#way4271855";
		
		MFeature feature = (MFeature) ffactory.retrieveByUri(id, graphUri, 0, "MFeature");
		
		UDebug.print(feature.toString(""),1);   	  
	}
	
	public void printFeatureVersionInfos()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
//		String id = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion199732_1";
		String id = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion198467_2";
		
		MFeatureVersion fversion = (MFeatureVersion) ffactory.retrieveByUri(id, graphUri, 1, "MFeatureVersion");
		
		UDebug.print(fversion.toString(""),1);   	  
	}
	
	public void printTrustworthiness()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		String id = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion198467_2";
		
		MFeatureVersion featureVersion = (MFeatureVersion) ffactory.retrieveByUri(id, graphUri, 1, "MFeatureVersion");
		UDebug.print(featureVersion.toString(""),3); 
		
		CTrustworthinessCalculusLazy controller = new CTrustworthinessCalculusLazy();
		controller.computeFVTrustworthiness(featureVersion);
	}

//	public void buildEffectHierarchy() {
//		
//		CTrustworthinessCalculus trust = new CTrustworthinessCalculus();
//		trust.buildEffectHierarchy();
//		
//		Map<MFEffect, Double> effects = trust.getEffects();
//		
//		UDebug.print("Elements: "+effects.size()+"\n", 10);
//		
//		for (Entry<MFEffect, Double> effect : effects.entrySet())	{
//			
//			UDebug.print("Effect: \n\t" + effect.getKey().toString() 
//							+" weighted: "+ effect.getValue()+"\n", 2);
//			
//			if (effect.getKey() instanceof MFDependent)
//				for ( Map.Entry<MFAspect, Double> aspect : ((MFDependent)effect.getKey()).getComponents().entrySet())
//					UDebug.print("\tAspect: \n\t\t" + aspect.getKey().toString() 
//							+" weighted: "+ aspect.getValue()+"\n", 2);
//		}
//	}
	
//	public void getTrustworthinessCalculation()
//	{
//		CTrustworthinessCalculus trust = new CTrustworthinessCalculus();
//		UDebug.print(trust.computeTrustworthiness()+"",2);
//	}
	
	public void retreiveNextFV()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		String id = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion198467_2";
		
		MFeatureVersion fversion = (MFeatureVersion) ffactory.retrieveByUri(id, graphUri, 1, "MFeatureVersion");
		
		UDebug.print(fversion.toString(""),2);
		
		UDebug.print("\n\nDATA ESTRATTA: " + fversion.getIsValidFromString() + "\n\n",2);  
		
		String nextUri = ffactory.retrieveNextFVUri( fversion, graphUri );
		
		UDebug.print("Prossima feature version: \n\t" + nextUri , 2);
	}
	
	public void retreiveSuccNeighboursFVs()
	{
		FFoundationFacade ffactory = new FFoundationFacade();
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(  ), Integer.parseInt(UConfig.epsg_crs));
		
		Double radius = UConfig.featureInfluenceRadius;
		String wktGeometryBuffered = null;
		String id = "http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion198467_2";
//		String id = "http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion8179208_3.0";
		
		MFeatureVersion fversion = (MFeatureVersion) ffactory.retrieveByUri(id,1,"MFeatureVersion");
		
		UDebug.print(fversion.toString(""),2);
		
		WKTReader reader = new WKTReader( geometryFactory );
		Geometry geometryBuffered;
		try {
			geometryBuffered = ((Geometry) reader.read(fversion.getWktGeometry())).buffer( radius );
			wktGeometryBuffered = geometryBuffered.toText();
		} catch (ParseException e) {
			wktGeometryBuffered = null;
			e.printStackTrace();
		}
		
		ArrayList<String> uris = ffactory.retrieveFVPreviousesNeighbours(fversion, wktGeometryBuffered);
		
		for (String uri : uris)
			UDebug.print("\n\t feature version: \t" + uri , 2);
	}	
	
}
