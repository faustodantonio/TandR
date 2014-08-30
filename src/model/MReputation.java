package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import utility.UConfig;
import utility.UDebug;

public class MReputation {

	private String uri;
	private double value;
	private Date computedAt;
	
	private String  authorUri;
	private MAuthor author;	

	private SimpleDateFormat sdf;
	
	public MReputation() {
		this.sdf = UConfig.sdf;
	}
	
	public MReputation(MAuthor author) {
		this.sdf = UConfig.sdf;
		
		author.setReputation(this);
		this.setAuthor(author);
	}
	
	public MReputation(MAuthor author, String computedAt) {
		this.sdf = UConfig.sdf;
		
		author.setReputation(this);
		this.setAuthor(author);
		this.setComputedAt(computedAt);
	}
	
	public String toString(String rowPrefix)
	{
		String reputationString = "";
		//TODO: implement conversion from MReputation to String
		return reputationString;
	}

	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	public Date getComputedAt() {
		return computedAt;
	}
	public void setComputedAt(Date isValidFrom) {
		this.computedAt = isValidFrom;
	}
    public void setComputedAt(String isValidFrom) {
    	try {
			this.computedAt = sdf.parse(isValidFrom);
		} catch (ParseException e) {
			UDebug.print("\n *** ERROR: IsValidFrom field not formatted\n",5);
			e.printStackTrace();	}
    }
	public String getComputedAtString(){
		String date = "";
		if (this.computedAt != null)
			date = this.sdf.format(this.computedAt);
		return date;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public MAuthor getAuthor() {
		return author;
	}
	public void setAuthor(MAuthor author) {
		this.setUri(""+UConfig.graphURI + "Reputation" + UConfig.module_trustworthiness_calculus + "_" + author.getAccountName());
		this.author = author;
	}
	public String getAuthorUri() {
		return authorUri;
	}
	public void setAuthorUri(String authorUri) {
		this.authorUri = authorUri;
	}
	
	
}
