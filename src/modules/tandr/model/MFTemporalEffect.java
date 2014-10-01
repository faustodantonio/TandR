package modules.tandr.model;

import java.util.Date;

import utility.UConfig;
import utility.UDebug;
import model.MAuthor;
import model.MFeature;
import model.MFeatureVersion;

public class MFTemporalEffect extends MFEffect{

	public MFTemporalEffect() { }
	
	public MFTemporalEffect(Double value) { 
		super(value);
		this.setComputedAt(UConfig.getMinDateTime());
	}
	
	/**
	 * compute the trustworthiness temporal effect as the ratio between
	 * the feature version living time (difference between date from and date to, in seconds)
	 * and the total feature lifetime (difference between first "date from" and last "date to" or actual time, in seconds)
	 */
	public double calculateTrustworthiness(MFeatureVersion featureVersion) {
		return this.calculateTrustworthiness(featureVersion, null); 
	}
	
	/**
	 * compute the trustworthiness temporal effect as the ratio between
	 * the feature version living time (difference between date from and date to, in seconds)
	 * and the total feature lifetime (difference between first "date from" and last "date to" or actual time, in seconds)
	 */
	public double calculateTrustworthiness(MFeatureVersion featureVersion, Date untilDate) {
		
		double TTemporalEffect = 0.0;
		TTemporalEffect = this.calculateTemporalEffect(featureVersion, untilDate);
		
		super.value = TTemporalEffect;
		UDebug.print("\t Temporal Trust : " + super.value + "\n",1);
		return super.value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see modules.tandr.model.MFEffect#calculateReputation(model.MAuthor)
	 * get all trustwirthiness temporal effects and compute the average
	 * the moment 
	 */
	public double calculateReputation(MAuthor author, String untilDate) {
		
		super.value = 0.0;
		
		// TODO: implement reputation calculus for temporal effect		
		
		return super.value;
	}
	
	public double getTempValue() {
		return super.value;
	}
	
	public double confirmTrustworthiness(MFeatureVersion featureVersion) {
		
		double TTemporalEffect = 0.0;
		TTemporalEffect = this.calculateTemporalEffect(featureVersion);
		
		super.value = TTemporalEffect;
		return super.value;
	}
	
	public double  calculateTemporalEffect(MFeatureVersion featureVersion) {
		return this.calculateTemporalEffect(featureVersion, null);
	}
	
	public double  calculateTemporalEffect(MFeatureVersion featureVersion, Date untilDate) {
		
		MFeature feature = featureVersion.getFeature();
		MFeatureVersion featureFirst = feature.getFirstVersion(), featureLast = feature.getLastVersion();
		long versionFrom = 0, versionTo = 0, featureFrom = 0, featureTo = 0; 
		Double versionLifetime = 0.0, featureLifetime = 0.0, curveSlopeParameter = 10000000.0;
		
		versionFrom = featureVersion.getIsValidFrom().getTime();
		featureFrom = featureFirst.getIsValidFrom().getTime();
		
		if (untilDate == null) untilDate = UConfig.getMaxDateTime(); 
		if ( untilDate.before(featureVersion.getIsValidFrom()) || untilDate.before(featureFirst.getIsValidFrom()) )
			UDebug.error("untilDate preceds feature or feature version validity interval");
		else {
			if ( featureVersion.getIsValidTo() != null && featureVersion.getIsValidTo().before(untilDate) )
				versionTo = featureVersion.getIsValidTo().getTime();
			else versionTo = untilDate.getTime();
			
			if ( featureLast.getIsValidTo() != null && featureLast.getIsValidTo().before(untilDate) )
				featureTo = featureLast.getIsValidTo().getTime();
			else featureTo = untilDate.getTime();
		}
		
		versionLifetime = (double) (versionTo - versionFrom);
		featureLifetime = (double) (featureTo - featureFrom);
		
		Double temporalEffectValue = (double) (versionLifetime / (featureLifetime + curveSlopeParameter));
		
		debugTTemporalInfo(featureVersion, versionLifetime, featureLifetime, curveSlopeParameter, temporalEffectValue);
		
		return Math.abs( temporalEffectValue );
	}
	
    private void debugTTemporalInfo(MFeatureVersion featureVersion, double versionLifetime, double featureLifetime, double curveSlopeParameter, double temporalEffectValue) {

		if (featureVersion.getIsValidTo() != null)
			UDebug.print("\n\n to("+ featureVersion.getIsValidTo().getTime() + ") - from ("+ featureVersion.getIsValidFrom().getTime() +") = " + UConfig.getDoubleAsString(versionLifetime) +"\n",10);
		else UDebug.print("\n\n to("+ UConfig.getMaxDateTime().getTime() + ") - from ("+ featureVersion.getIsValidFrom().getTime() +") = " + UConfig.getDoubleAsString(versionLifetime) +"\n",10);
		UDebug.print(""
				+ "versionLifetime("	+UConfig.getDoubleAsString(versionLifetime)	+") / ["
				+ "featureLifetime("	+UConfig.getDoubleAsString(featureLifetime)	+") + "
				+ "curveslope("			+UConfig.getDoubleAsString(curveSlopeParameter)	+") ] = "
				+ "TTemporalEffect("	+UConfig.getDoubleAsString(temporalEffectValue)	+") \n\n",10);
		
    }
}
