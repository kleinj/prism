package param;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Parameters
{
	/** names of parameters */
	private ArrayList<String> paramNames = new ArrayList<String>();
	/** lower bounds of parameters */
	private ArrayList<BigRational> lowerBounds = new ArrayList<BigRational>();
	/** upper bounds of parameters */
	private ArrayList<BigRational> upperBounds = new ArrayList<BigRational>();

	public Parameters()
	{
	}

	public void addParameter(String name, BigRational lower, BigRational upper)
	{
		paramNames.add(name);
		lowerBounds.add(lower);
		upperBounds.add(upper);
	}
	
	public void addParameter(String name, String lower, String upper)
	{
		addParameter(name, new BigRational(lower), new BigRational(upper));
	}

	public int size()
	{
		return paramNames.size();
	}

	public List<String> getParameterNames()
	{
		return Collections.unmodifiableList(paramNames);
	}

	public String getParameterName(int i)
	{
		return paramNames.get(i);
	}

	public BigRational getLowerBound(int i)
	{
		return lowerBounds.get(i);
	}

	public BigRational getUpperBound(int i)
	{
		return upperBounds.get(i);
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;

		if (other instanceof Parameters) {
			Parameters otherP = (Parameters) other;
			return paramNames.equals(otherP.paramNames) &&
					lowerBounds.equals(otherP.lowerBounds) &&
					upperBounds.equals(otherP.upperBounds);
		}
		return false;
	}
	
}
