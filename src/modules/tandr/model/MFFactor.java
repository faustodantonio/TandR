package modules.tandr.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import utility.UConfig;
import utility.UDebug;
import model.MAuthor;
import model.MFeatureVersion;
import modules.tandr.foundation.FTandrFacade;

public abstract class MFFactor {

	protected double value;
	protected Date computedAt;
	
	protected FTandrFacade foundation;
	
	protected SimpleDateFormat sdf;
	
	public MFFactor ()	{
		this.foundation = new FTandrFacade();
		this.sdf = UConfig.sdf;
		
		this.value = 0.0;
//		this.setComputedAt(UConfig.getMinDateTime());
	}

	public MFFactor (Double value)	{
		this.foundation = new FTandrFacade();
		this.sdf = UConfig.sdf;
		
		this.setValue(value);
//		this.setComputedAt(UConfig.getMinDateTime());
	}
	
	/**
	 * Calculate the trustworthiness, this method is invoked only once per feature version.
	 * It is invoked only when there is no trustworthiness associated to feature version. 
	 * @param featureVersion the feature version which Trustworthiness needs to bew calculated 
	 * @return
	 */
//	public abstract double calculateTrustworthiness(MFeatureVersion featureVersion);
//	public abstract double calculateConfirmation(MFeatureVersion featureVersion);
	/**
	 * Calculate and update the reputation associated to a user
	 * @param author the author which Reputation needs to be calculated
	 * @return
	 */
//	public abstract double calculateReputation(MAuthor author, String untillDate);
	
	public String toString()	{
		return this.getClass().getName();
	}
	
	public String getValueString() {
		return UConfig.getDoubleAsString(value);
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
}
