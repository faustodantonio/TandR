package modules.tandr.foundation;

import foundation.FFoundationAbstract;
import foundation.FFoundationFactory;

/**
 * All class methods works only if there exists a pair of class <mclass,fclass> such that
 * mclass belongs to the Model package, fclass belongs to th Foundation package and 
 * the names of the two classes differs only for the first letter.
 * @author fausto
 */
class FTandrFactory extends FFoundationFactory{
	
	public FTandrFactory() {
		this.ffoundation = null;
	}
	
	protected FFoundationAbstract getFFoundation(String mclass)
	{
		String fclass = "modules.tandr.foundation.F" + mclass.substring(1);
		return this.getFFoundation(fclass, mclass);
	}
	
	@SuppressWarnings("rawtypes")
	protected FFoundationAbstract getFFoundation(Class mclass)
	{
		String mclassStr = mclass.getSimpleName();;
		return this.getFFoundation(mclassStr);
	}
	
	protected FFoundationAbstract getFFoundation(Object modelObj)
	{	
		String mclass = modelObj.getClass().getSimpleName();
		return this.getFFoundation(mclass);
	}
	
	protected FFoundationAbstract getFFoundation(String fclass,String mclass)
	{
		try {
			ffoundation = (FFoundationAbstract) Class.forName(fclass).newInstance();
		} catch (InstantiationException e) {
			super.getFFoundation(mclass);
		} catch (IllegalAccessException e) {
			super.getFFoundation(mclass);
		} catch (ClassNotFoundException e) {
			super.getFFoundation(mclass);
		}
		return ffoundation;
	}
	
}
