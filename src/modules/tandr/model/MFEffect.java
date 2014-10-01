package modules.tandr.model;

public abstract class MFEffect extends MFFactor{
	
	public MFEffect ()	{
		this.value = 0.0;
	}
	
	public MFEffect (Double value)	{
		super(value);
	}
	
	public String toString()	{
		return this.getClass().getName();
	}

	
}
