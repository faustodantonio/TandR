package modules.tandr.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import utility.UConfig;
import utility.UDebug;
import model.MFeatureVersion;

public abstract class MFFactor {

	protected double value;
	protected Date computedAt;
	
	protected SimpleDateFormat sdf;
	
	public MFFactor ()	{
		this.value = 0.0;
		this.sdf = UConfig.sdf;
		this.setComputedAt("2005-09-15T21:42:44Z");
	}

	public MFFactor (Double value)	{
		this.setValue(value);
	}
	
	//	public abstract double calculate();
	public abstract double calculate(MFeatureVersion featureVersion);
	
	public String toString()	{
		return this.getClass().getName();
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
