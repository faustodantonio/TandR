package controller;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import utility.UConfig;
import utility.UDebug;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import foundation.FFoundationFacade;
import foundation.FTandRImport;

public class CInstallation {
	
	private FFoundationFacade foundation;
	
	public static FilenameFilter filter(final String regex) {
	    // Creation of anonymous inner class:*|*
	    return new FilenameFilter() { 
	      
	    	private Pattern pattern = Pattern.compile(regex);
	    	
	    	public boolean accept(File dir, String name) {
	    		return pattern.matcher( new File(name).getName() ).matches();
	    	}
	    };
	}
	    
	public CInstallation() {
		this.foundation = new FFoundationFacade();
	}

	/**
	 * check if the graphs are created. If not, create them. Otherwise delete and then create them
	 * 
	 * Populate the hvgi graph with the rdf files.
	 * 		For every file to be imported
	 *		* Create Jena Model
	 *		* Populate with the file 
	 *		* Insert the jena model in a graph using Parliament
	 * 
	 */
	public void install()	{
		
		switch (UConfig.installation_mode) {
		case 0:		;
			break;
		case 1:	this.checkInstall();
			break;
		case 2:	this.forceInstall();
			break;
		case 3:	this.restoreTandR();
			break;
		case 4:	this.restoreVGIH();
			break;
		default:
			break;
		}
	}

	/**
	 * If the graphs are not created it performs the installation
	 */
	private void checkInstall() {
		
		boolean hvgiExists  = foundation.checkGraphExists("hvgi" , UConfig.graphURI);
		boolean tandrExists = foundation.checkGraphExists("tandr" , UConfig.graphURI);
		
		if (!hvgiExists) {
			foundation.createGraph("hvgi" , "graphs");
			this.importVGIHTriples();
		}
		if (!tandrExists)
			foundation.createGraph("tandr" , "graphs");
	}

	private void forceInstall() {
		
		boolean hvgiExists  = foundation.checkGraphExists("hvgi" , UConfig.graphURI);
		boolean tandrExists = foundation.checkGraphExists("tandr" , UConfig.graphURI);
		
		UDebug.print("\nDoes hvgi graph exists? ANSWER: "  + hvgiExists  + "\n", 3);
		UDebug.print("Does tandr graph exists? ANSWER: " + tandrExists + "\n\n", 3);
		
		if (hvgiExists) 
			foundation.deleteGraph("hvgi" , "graphs");
		if (tandrExists)
			foundation.deleteGraph("tandr" , "graphs");

		foundation.createGraph("hvgi" , "graphs");
		foundation.createGraph("tandr" , "graphs");
		this.importVGIHTriples();
		this.importTandRTriples();
		this.createSpatialIndexes();
	}

	private void restoreVGIH() {
		boolean hvgiExists  = foundation.checkGraphExists("hvgi" , UConfig.graphURI);
		UDebug.print("\nDoes hvgi graph exists? ANSWER: "  + hvgiExists  + "\n", 3);
		if (hvgiExists) 
			foundation.deleteGraph("hvgi" , "graphs");
		foundation.createGraph("hvgi" , "graphs");
		this.importVGIHTriples();
		this.createSpatialIndexes();
	}

	private void restoreTandR() {
		boolean tandrExists = foundation.checkGraphExists("tandr" , UConfig.graphURI);
		UDebug.print("Does tandr graph exists? ANSWER: " + tandrExists + "\n", 6);
		if (tandrExists)
			foundation.deleteGraph("tandr" , "graphs");

		foundation.createGraph("tandr" , "graphs");
		this.importTandRTriples();
	}
	
	private void importVGIHTriples() {
		
		ArrayList<String> files = this.getFileList(UConfig.inputRDFfilesDirectory);
		int fileNo=0;
		UDebug.print("Start to import triples from files in "+ UConfig.inputRDFfilesDirectory +" 0/"+files.size()+"\n",3);
		for(String file : files) {
			fileNo++;
			UDebug.print("\t Importing " + file + " ...",3);
			this.importFile(UConfig.inputRDFfilesDirectory + file, UConfig.graphURI + UConfig.hvgiGraph);
			UDebug.print("\t\t Ended " + fileNo+"/"+files.size()+" ||\n",3);
		}
		UDebug.print("Imports ended\n",3);
	}
	
	private boolean importTandRTriples() {
		FTandRImport TRImport = this.getTandRImport();
		return TRImport.importTandRTriples();
	}
	
	private FTandRImport getTandRImport()
	{
		FTandRImport TRImport;
		String importClass = "modules." + UConfig.module_trustworthiness_calculus +"."+ UConfig.tandr_import;
		try {
			TRImport = (FTandRImport) Class.forName(importClass).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.out.print("Unable to instantiate the class " + importClass + "\n"
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			TRImport = new modules.tandr.foundation.FTandR();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.out.print("Unable to access the class " + importClass + ". "
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			TRImport = new modules.tandr.foundation.FTandR();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.print("Unable to locate the class " + importClass + ". "
					+importClass + " do not exists \n"
					+ e.toString() + "\nWill be used the default module: \"tandr\"");
			TRImport = new modules.tandr.foundation.FTandR(); 
		}
		return TRImport;
	}

	private void createSpatialIndexes() {
		
		UDebug.print("\nSpatial indexes creation ... \n",3);
		
		UDebug.print("\t Indexing " + "urn:x-arq:DefaultGraph" + " ...",3);
		foundation.enableGraphIndexes("","");
		UDebug.print("\t\t Ended \t ||\n",3);
		
		UDebug.print("\t Indexing " + UConfig.graphURI + UConfig.hvgiGraph + " ...",3);
		foundation.enableGraphIndexes(UConfig.hvgiGraph, UConfig.graphURI);
		UDebug.print("\t\t Ended \t ||\n",3);
		
		UDebug.print("\t Indexing " + UConfig.graphURI + UConfig.tandrGraph + " ...",3);
		foundation.enableGraphIndexes(UConfig.tandrGraph, UConfig.graphURI);
		UDebug.print("\t\t Ended \t ||\n",3);
		
		UDebug.print("Indexing ended " +"\n",3);
	}
	
	private ArrayList<String> getFileList(String inputDirectory) {
		
		ArrayList<String> files = new ArrayList<String>();
		File directory = new File(inputDirectory);
		
		String[] fileList = directory.list(
			new FilenameFilter() {
				String inputRdfFileRegex = UConfig.inputRDFfileRegex;
				
		        private Pattern pattern = Pattern.compile(inputRdfFileRegex);
		        
		        public boolean accept(File dir, String name) {
		          return pattern.matcher(
		            new File(name).getName()).matches();  
		        }        
			}
		);
		
		for(String file : fileList)
			files.add(file);
		
		return files;
	}
	
	private boolean importFile(String filePath, String graphName)	{	
		boolean result = true;
		
		Model jenaModel = ModelFactory.createDefaultModel();
		jenaModel.read(filePath);
		
		if (graphName.equals(""))
			foundation.importJenaModel(jenaModel);
		else
			foundation.importJenaModel(jenaModel,graphName);
		
		return result;
	}
	
}
