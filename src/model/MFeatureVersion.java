package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import utility.UConfig;
import utility.UDebug;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTReader;

public class MFeatureVersion {

	private String uri;
	
	private String featureUri;
	private MFeature feature;
	
	private String prevFVersionUri;
	private MFeatureVersion prevFVersion;
	
	private MTrustworthiness trustworthiness;
	
	private String  authorUri;
	private MAuthor author;
	
	private String  editUri;
	private MEdit   edit;
	
	private String versionNo;
	private boolean isDeleted;
	private Date   isValidFrom;
	private Date   isValidTo;
	
	private String wktGeometry;
	private Geometry geometry;
	
	private HashMap<String,String> tags;
	
	private SimpleDateFormat sdf;

	public MFeatureVersion() {
		super();
		this.tags = new HashMap<String, String>();

		this.sdf = UConfig.sdf;
	}
	
	public String toString(String rowPrefix)
	{
		String fversionString = "";
		
		fversionString +=  rowPrefix + "FeatureVersion :" + "\n"
				+  rowPrefix +         "\t uri               = \""+ this.getUri() +"\"\n";
		//referenced feature (hvgi:isVersionOf)
		fversionString +=  rowPrefix + "\t reference feature = \""+ this.getFeatureUri() +"\"\n";
		if (this.getFeature() != null) 
			fversionString += rowPrefix + this.getFeature().toString(rowPrefix + "\t "); 
		else fversionString += rowPrefix + "\t Feature: null" + "\n"; 
		//geometry
		fversionString +=  rowPrefix + "\t wkt geometry = \""+ this.getWktGeometry() +"\"\n";
		if (this.getGeometry() != null) 
			fversionString += rowPrefix + "\t Geometry: " + this.getGeometry().toString() + "\n"; 
		else fversionString += rowPrefix + "\t Geometry: null" + "\n"; 
		//version (versionNo)
		fversionString +=  rowPrefix + "\t version = \""+ this.getVersionNo() +"\"\n";
		//precedent fv (prv:precedeedBy)
		fversionString +=  rowPrefix + "\t previous version = \""+ this.getPrevFVersionUri() +"\"\n";
		if (this.getPrevFVersion() != null) 
			fversionString += rowPrefix + this.getPrevFVersion().toString(rowPrefix + "\t "); 
		else fversionString += rowPrefix + "\t FeatureVersion: null" + "\n"; 
		//edit (osp:createdBy)
		fversionString +=  rowPrefix + "\t edit          = \""+ this.getEditUri() +"\"\n";
		if (this.getEdit() != null) 
			fversionString += rowPrefix + this.getEdit().toString(rowPrefix + "\t "); 
		else fversionString += rowPrefix + "\t Edit: null" + "\n"; 		
		//author (hvgi:hasAuthor)
		fversionString +=  rowPrefix + "\t author          = \""+ this.getAuthorUri() +"\"\n";
		if (this.getAuthor() != null) 
			fversionString += rowPrefix + this.getAuthor().toString(rowPrefix + "\t "); 
		else fversionString += rowPrefix + "\t Author: null" + "\n"; 
		//validity
		fversionString +=  rowPrefix + "\t is valid from = \""+ this.getIsValidFromString() +"\"\n";
		fversionString +=  rowPrefix + "\t is valid to   = \""+ this.getIsValidToString() +"\"\n";
		//deleted
		fversionString +=  rowPrefix + "\t version = \""+ this.getVersionNo() +"\"\n";
		//tags
		fversionString += rowPrefix + "\t tags : \n";
		for (Entry<String, String> tag : tags.entrySet())
			fversionString += rowPrefix + "\t\t "+ tag.getKey() + " => \"" + tag.getValue() +"\"\n";
		
		return fversionString;
	}
	
	public String getGeometryBuffer()
	{
		Double radius = UConfig.featureInfluenceRadius;
		String wktGeometryBuffered = null;
		
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(  ), Integer.parseInt(UConfig.epsg_crs));
		WKTReader reader = new WKTReader( geometryFactory );
		
		Geometry geometryBuffered;
		try {
			geometryBuffered = ((Geometry) reader.read(this.getWktGeometry())).buffer( radius );
			wktGeometryBuffered = geometryBuffered.toText();
		} catch (com.vividsolutions.jts.io.ParseException e) {
			wktGeometryBuffered = null;
			e.printStackTrace();
		}
		return wktGeometryBuffered;
	}
	
	public String getUriID(){
		String uriID = this.getUri();
		
		uriID = uriID.replace("http://semantic.web/data/hvgi/nodeVersions.rdf#nodeVersion", "");
		uriID = uriID.replace("http://semantic.web/data/hvgi/wayVersions.rdf#wayVersion", "");
		uriID = uriID.replace("http://semantic.web/data/hvgi/featureVersions.rdf#featureVersion", "");
		
		return uriID;
	}
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getFeatureUri() {
		return featureUri;
	}
	public void setFeatureUri(String featureUri) {
		this.featureUri = featureUri;
	}
	public MFeature getFeature() {
		return feature;
	}
	public void setFeature(MFeature feature) {
		this.feature = feature;
	}
	public String getPrevFVersionUri() {
		return prevFVersionUri;
	}
	public void setPrevFVersionUri(String prevFVersionUri) {
		this.prevFVersionUri = prevFVersionUri;
	}
	public MFeatureVersion getPrevFVersion() {
		return prevFVersion;
	}
	public void setPrevFVersion(MFeatureVersion prevFVersion) {
		this.prevFVersion = prevFVersion;
	}
	public String getAuthorUri() {
		return authorUri;
	}
	public void setAuthorUri(String authorUri) {
		this.authorUri = authorUri;
	}
	public MAuthor getAuthor() {
		return author;
	}
	public void setAuthor(MAuthor author) {
		this.author = author;
	}
	public String getEditUri() {
		return editUri;
	}
	public void setEditUri(String editUri) {
		this.editUri = editUri;
	}
	public MEdit getEdit() {
		return edit;
	}
	public void setEdit(MEdit edit) {
		this.edit = edit;
	}
	public String getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	public boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public Date getIsValidFrom() {
		return isValidFrom;
	}
	public void setIsValidFrom(Date isValidFrom) {
		this.isValidFrom = isValidFrom;
	}
	public Date getIsValidTo() {
		return isValidTo;
	}
	public void setIsValidTo(Date isValidTo) {
		this.isValidTo = isValidTo;
	}
	public String getIsValidFromString(){
		String date = "";
		if (this.isValidFrom != null)
			date = this.sdf.format(this.isValidFrom);
		return date;
	}
    public void setIsValidFrom(String isValidFrom) {
    	try {
			this.isValidFrom = sdf.parse(isValidFrom);
		} catch (ParseException e) {
			UDebug.print("\n *** ERROR: IsValidFrom field not formatted\n",5);
			e.printStackTrace();	}
    }
	public String getIsValidToString(){
		String date = "";
		if (this.isValidTo != null)
			date = this.sdf.format(this.isValidTo);
		return date;
	}
    public void setIsValidTo(String isValidTo) {
    	try {
			this.isValidTo = sdf.parse(isValidTo);
		} catch (ParseException e) {
			UDebug.print("\n *** ERROR: IsValidTo field not formatted\n",5);
			e.printStackTrace();	}
    }
	public String getWktGeometry() {
		return wktGeometry;
	}
	public void setWktGeometry(String wktGeometry) {
		this.wktGeometry = wktGeometry;
	}
	public Geometry getGeometry() {
		return geometry;
	}
	public void setGeometry(Geometry the_geom) {
		this.geometry = the_geom;
	}
	public void setGeometry(String wktGeometry) {
		try {
			this.geometry = new WKTReader().read(wktGeometry);
		} catch (com.vividsolutions.jts.io.ParseException e1) {
			UDebug.print("\n*** ERROR: wkt geometry bad formatted\nCorrection Attempt #1",1);
			UDebug.print("\n*** ERROR: " + e1.getMessage(),2);
			wktGeometry.replace("( ", "(");
			wktGeometry.replace(" )", ")");
				try {
					this.geometry = new WKTReader().read(wktGeometry);
					UDebug.print("\n****** ERROR: Correction Attempt #1 Succeded",1);
				} catch (com.vividsolutions.jts.io.ParseException e2) {
					UDebug.print("\n****** ERROR: Correction Attempt #1 Failed \n The geometry will be set as null",1);
					UDebug.print("\n****** ERROR: " + e2.getMessage(),2);
					this.geometry = null;
				}
		}
	}
	public HashMap<String, String> getTags() {
		return tags;
	}
	public void setTags(HashMap<String, String> tags) {
		this.tags = tags;
	}
	public void addTag(String key, String value)	{
		this.tags.put(key, value);
	}
	public MTrustworthiness getTrustworthiness() {
		return trustworthiness;
	}
	public void setTrustworthiness(MTrustworthiness trustworthiness) {
		this.trustworthiness = trustworthiness;
	}
	
}
