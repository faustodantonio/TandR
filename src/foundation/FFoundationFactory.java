package foundation;

/**
 * All class methods works only if there exists a pair of class <mclass,fclass> such that
 * mclass belongs to the Model package, fclass belongs to th Foundation package and 
 * the names of the two classes differs only for the first letter.
 * @author fausto
 */
class FFoundationFactory {

	FFoundationAbstract ffoundation;
	
	public FFoundationFactory() {
		this.ffoundation = null;
	}
	
	FFoundationAbstract getFFoundation(String mclass)
	{
		String fclass = "foundation.F" + mclass.substring(1);
		return this.getFFoundation(fclass, mclass);
	}
	
	@SuppressWarnings("rawtypes")
	FFoundationAbstract getFFoundation(Class mclass)
	{		
		String mclassStr = mclass.getSimpleName();;
		return this.getFFoundation(mclassStr);
	}
	
	FFoundationAbstract getFFoundation(Object modelObj)
	{	
		String mclass = modelObj.getClass().getSimpleName();
		return this.getFFoundation(mclass);
	}
	
	FFoundationAbstract getFFoundation(String fclass,String mclass)
	{		
		try {
			ffoundation = (FFoundationAbstract) Class.forName(fclass).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.print("Unable to load elements of the class " + mclass + "\n"
					+fclass + " do not exists \n");
			e.printStackTrace();
		}
		return ffoundation;
	}
	
}
