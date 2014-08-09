package model;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public class MFeature {

	private String uri;
	private Map<String, MFeatureVersion> versions;
		
	public MFeature() {
		super();
		this.versions = new TreeMap<String, MFeatureVersion>();
	}
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public Map<String, MFeatureVersion> getVersions() {
		return versions;
	}
	public void setVersions(Map<String, MFeatureVersion> versions) {
		this.versions = versions;
	}
	
	public void addVersion(String fversionuri, MFeatureVersion fversion)
	{
		this.versions.put(fversionuri, fversion);
	}
	
	public String toString(String rowPrefix)
	{
		String featureString = "";
		
		featureString +=  rowPrefix + "Feature :" + "\n"
				      +   rowPrefix + "\t uri = \""+ this.getUri() +"\"\n";
		
		featureString += rowPrefix + "\t feature versions : \n";
		for ( Entry<String, MFeatureVersion> version : this.versions.entrySet())
		{
			featureString += rowPrefix + "\t\t uri = \""+ version.getKey() + "\"\n";
			if (version.getValue() != null)
			featureString += version.getValue().toString(rowPrefix + "\t\t ");
		}
		
		return featureString;
	}
	
}
