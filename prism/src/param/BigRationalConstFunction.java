package param;

public class BigRationalConstFunction implements Function
{
	private BigRational value;

	protected BigRationalConstFunction(BigRational value)
	{
		this.value = value;
	}

	@Override
	public Function add(Function other)
	{
		return new BigRationalConstFunction(value.add(other.asBigRational()));
	}

	@Override
	public Function negate()
	{
		return new BigRationalConstFunction(value.negate());
	}

	@Override
	public Function multiply(Function other)
	{
		return new BigRationalConstFunction(value.multiply(other.asBigRational()));
	}

	@Override
	public Function divide(Function other)
	{
		return new BigRationalConstFunction(value.divide(other.asBigRational()));
	}

	@Override
	public Function star()
	{
		if (this.isNaN()) {
			return getFactory().getNaN();
		}
		BigRational oneMinusValue = BigRational.ONE.subtract(value);
		BigRational result = BigRational.ONE.divide(oneMinusValue);
		return new BigRationalConstFunction(result);
	}

	@Override
	public Function toConstraint()
	{
		// simplified = this
		return this;
	}

	@Override
	public BigRational evaluate(Point point, boolean cancel)
	{
		// evaluation = constant value
		return value;
	}

	@Override
	public BigRational asBigRational()
	{
		return value;
	}

	@Override
	public boolean isNaN()
	{
		return value.isNaN();
	}

	@Override
	public boolean isInf()
	{
		return value.isInf();
	}

	@Override
	public boolean isMInf()
	{
		return value.isMInf();
	}

	@Override
	public boolean isOne()
	{
		return value.isOne();
	}

	@Override
	public boolean isZero()
	{
		return value.isZero();
	}

	@Override
	public boolean isConstant()
	{
		return true;
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BigRationalConstFunction other = (BigRationalConstFunction) obj;
		return value.equals(other.value);
	}

	@Override
	public FunctionFactory getFactory()
	{
		return BigRationalConstFunctionFactory.getInstance();
	}

}
