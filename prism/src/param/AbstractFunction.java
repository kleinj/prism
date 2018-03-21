package param;

public abstract class AbstractFunction implements Function
{
	/** function factory for this function */
	protected FunctionFactory factory;

	/**
	 * Creates a new function.
	 * For internal use.
	 * 
	 * @param factory factory used for this function
	 */
	protected AbstractFunction(FunctionFactory factory)
	{
		this.factory = factory;
	}

	@Override
	public FunctionFactory getFactory()
	{
		return factory;
	}

}
