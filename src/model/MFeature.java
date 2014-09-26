package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import foundation.FFoundationFacade;
import utility.UConfig;
import utility.UDebug;

public class MFeature {

	private String uri;
	private Map<String, MFeatureVersion> versionsByUri;
	private Map<String, String> versionsByVersion;
	
	private FFoundationFacade foundation;
		
	public MFeature() {
		super();
		this.foundation = new FFoundationFacade(); 
		this.versionsByUri = new TreeMap<String, MFeatureVersion>();
		this.versionsByVersion = new TreeMap<String, String>();
	}
	
	public MFeatureVersion getFirstVersion() {
		String minFVuri = versionsByVersion.get( Collections.min( versionsByVersion.keySet() ));
		UDebug.print("\nFirst Version: " + minFVuri.toString(), 10);
		return this.getFeatureVersionByURI(minFVuri, 0);
	}
	
	public MFeatureVersion getLastVersion() {
		String maxFVuri = versionsByVersion.get( Collections.max( versionsByVersion.keySet() ));
		UDebug.print("\nLast Version: " + maxFVuri.toString(), 10);
		return this.getFeatureVersionByURI(maxFVuri, 0);
	}
	
	public ArrayList<MFeatureVersion> getPreviousVersions(String version, int lazyDepth) {
		
		ArrayList<MFeatureVersion> versions = new ArrayList<MFeatureVersion>();
		Iterator<Entry<String, String>> verIterator = versionsByVersion.entrySet().iterator();
		Entry<String, String> versionEntry;
		
		boolean beforeLimit = true;
		while ( verIterator.hasNext() && beforeLimit) {
			versionEntry = verIterator.next();
			if (version.compareTo( versionEntry.getKey() ) > 0)
				versions.add( this.getFeatureVersionByVersion(versionEntry.getKey(), 0));
			else beforeLimit = false;
		}
		
		return versions;
	}
	
	public String toString(String rowPrefix)
	{
		String featureString = "";
		
		featureString +=  rowPrefix + "Feature :" + "\n"
				      +   rowPrefix + "\t uri = \""+ this.getUri() +"\"\n";
		
		featureString += rowPrefix + "\t feature versions : \n";
		for ( Entry<String, MFeatureVersion> version : this.versionsByUri.entrySet())
		{
			featureString += rowPrefix + "\t\t uri = \""+ version.getKey() + "\"\n";
			if (version.getValue() != null)
			featureString += version.getValue().toString(rowPrefix + "\t\t ");
		}
		
		return featureString;
	}

	
	public String getUri() {
		if (this.uri == null) this.uri = "";
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public Map<String, MFeatureVersion> getVersions() {
		
			for (Entry<String, MFeatureVersion> version : versionsByUri.entrySet())
				if (version.getValue() == null)
					versionsByUri.put( version.getKey() , (MFeatureVersion) foundation.retrieveByUri(version.getKey(), UConfig.getVGIHGraphURI(), 0, MFeatureVersion.class) );
		
		return versionsByUri;
	}
	public void setVersions(Map<String, MFeatureVersion> versions) {
		this.versionsByUri = versions;
	}
	public Map<String, String> getVersionsByVersion() {
		return versionsByVersion;
	}	
	public void setVersionsByVersion(Map<String, String> versionsByVersion) {
		this.versionsByVersion = versionsByVersion;
	}

	public void addVersion(String fversionuri, MFeatureVersion fversion)
	{
		this.versionsByUri.put(fversionuri, fversion);
	}
	public void addVersion(String fversionUri, String version, MFeatureVersion fversion)
	{
		this.versionsByUri.put(fversionUri, fversion);
		this.versionsByVersion.put(version, fversionUri);
	}

	public MFeatureVersion getFeatureVersionByURI(String uri, int lazyDepth) {
		MFeatureVersion featureVersion;
		String vgihGraphUri = UConfig.getVGIHGraphURI();
		
		if ( this.versionsByUri.get(uri) != null )
			featureVersion = this.versionsByUri.get(uri);
		else {
			featureVersion = (MFeatureVersion) foundation.retrieveByUri(uri, vgihGraphUri, lazyDepth, MFeatureVersion.class);
			this.versionsByUri.put(uri, featureVersion);
		}
		return featureVersion;
	}
	public MFeatureVersion getFeatureVersionByVersion(String version, int lazyDepth) {
		String uri = this.versionsByVersion.get(version);
		return this.getFeatureVersionByURI(uri, lazyDepth);
	}

	
}
