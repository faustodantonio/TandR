package modules.tandr.model;

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
	@Override
	public double calculateTrustworthiness(MFeatureVersion featureVersion) {
		
		double TTemporalEffect = 0.0;
		TTemporalEffect = this.calculateTemporalEffect(featureVersion);
		
		super.value = TTemporalEffect;
		UDebug.print("\t Temporal Trust : " + super.value + "\n",1);
		return super.value;
	}
	
	public double confirmTrustworthiness(MFeatureVersion featureVersion) {
		
		double TTemporalEffect = 0.0;
		TTemporalEffect = this.calculateTemporalEffect(featureVersion);
		
		super.value = TTemporalEffect;
		return super.value;
	}
	
	public double  calculateTemporalEffect(MFeatureVersion featureVersion) {
		
		MFeature feature = featureVersion.getFeature();
		MFeatureVersion featureFirst = feature.getFirstVersion();
		MFeatureVersion featureLast = feature.getLastVersion();
		
		Double versionLifetime = 0.0, featureLifetime = 0.0;
		Double curveSlopeParameter = 10000000.0;
//		Double curveSlopeParameter = 10.0;
		
		if (featureVersion.getIsValidTo() != null)
			versionLifetime = (double) (featureVersion.getIsValidTo().getTime() - featureVersion.getIsValidFrom().getTime());
		else versionLifetime = (double) UConfig.getMaxDateTime().getTime() - featureVersion.getIsValidFrom().getTime();
		
		if (featureLast.getIsValidTo() != null)
			featureLifetime = (double) (featureLast.getIsValidTo().getTime() - featureFirst.getIsValidFrom().getTime());
		else featureLifetime = (double) (UConfig.getMaxDateTime().getTime() - featureFirst.getIsValidFrom().getTime());
		
		Double temporalEffectValue = (double) (versionLifetime / (featureLifetime + curveSlopeParameter));
		
		if (featureVersion.getIsValidTo() != null)
			UDebug.print("\n\n to("+ featureVersion.getIsValidTo().getTime() + ") - from ("+ featureVersion.getIsValidFrom().getTime() +") = " + UConfig.getDoubleAsString(versionLifetime) +"\n",10);
		else UDebug.print("\n\n to("+ UConfig.getMaxDateTime().getTime() + ") - from ("+ featureVersion.getIsValidFrom().getTime() +") = " + UConfig.getDoubleAsString(versionLifetime) +"\n",10);
		UDebug.print(""
				+ "versionLifetime("	+UConfig.getDoubleAsString(versionLifetime)	+") / ["
				+ "featureLifetime("	+UConfig.getDoubleAsString(featureLifetime)	+") + "
				+ "curveslope("			+UConfig.getDoubleAsString(curveSlopeParameter)	+") ] = "
				+ "TTemporalEffect("	+UConfig.getDoubleAsString(temporalEffectValue)	+") \n\n",10);
		
		return Math.abs( temporalEffectValue );
	}
	
	/*
	 * (non-Javadoc)
	 * @see modules.tandr.model.MFEffect#calculateReputation(model.MAuthor)
	 * get all trustwirthiness temporal effects and compute the average
	 * the moment 
	 */
	@Override
	public double calculateReputation(MAuthor author, String untilDate) {
		
		super.value = 0.0;
		
		// TODO: implement reputation calculus for temporal effect		
		
		return super.value;
	}
	
	public double getTempValue() {
		return super.value;
	}
	
}
