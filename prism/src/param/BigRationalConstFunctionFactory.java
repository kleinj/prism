package param;

public class BigRationalConstFunctionFactory implements FunctionFactory
{
	private static BigRationalConstFunctionFactory instance = null;

	public static FunctionFactory getInstance()
	{
		if (instance == null) {
			instance = new BigRationalConstFunctionFactory();
		}
		return instance;
	}

	private BigRationalConstFunctionFactory()
	{
	}

	@Override
	public Function getOne()
	{
		return new BigRationalConstFunction(BigRational.ONE);
	}

	@Override
	public Function getZero()
	{
		return new BigRationalConstFunction(BigRational.ZERO);
	}

	@Override
	public Function getNaN()
	{
		return new BigRationalConstFunction(BigRational.NAN);
	}

	@Override
	public Function getInf()
	{
		return new BigRationalConstFunction(BigRational.INF);
	}

	@Override
	public Function getMInf()
	{
		return new BigRationalConstFunction(BigRational.MINF);
	}

	@Override
	public Function fromBigRational(BigRational bigRat)
	{
		return new BigRationalConstFunction(bigRat);
	}

	@Override
	public Function getVar(int var)
	{
		throw new IllegalArgumentException("No variables for big rational constant functions");
	}

	@Override
	public int getVarIndex(String var)
	{
		throw new IllegalArgumentException("No variables for big rational constant functions");
	}

	@Override
	public String getFunctionTypeName()
	{
		return "big rational constant";
	}

	@Override
	public int getNumVariables()
	{
		return 0;
	}

	@Override
	public String getParameterName(int var)
	{
		throw new IllegalArgumentException("No variables for big rational constant functions");
	}

	@Override
	public BigRational getLowerBound(int var)
	{
		throw new IllegalArgumentException("No variables for big rational constant functions");
	}

	@Override
	public BigRational getUpperBound(int var)
	{
		throw new IllegalArgumentException("No variables for big rational constant functions");
	}

}
	