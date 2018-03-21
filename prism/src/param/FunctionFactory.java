package param;

public interface FunctionFactory
{

	/**
	 * Returns number of variables used in this function factory.
	 * @return
	 */
	int getNumVariables();

	/**
	 * Returns the underlying function type name
	 */
	String getFunctionTypeName();

	/**
	 * Returns a function representing the number one.
	 * @return function representing the number one
	 */
	Function getOne();
	
	/**
	 * Returns a function representing the number zero.
	 * @return function representing the number zero
	 */
	Function getZero();
	
	/**
	 * Returns a function representing not-a-number.
	 * @return function representing not-a-number
	 */
	Function getNaN();

	/**
	 * Returns a function representing positive infinity.
	 * @return function representing the positive infinity
	 */
	Function getInf();

	/**
	 * Returns a function representing negative infinity.
	 * @return function representing the negative infinity
	 */
	Function getMInf();

	/**
	 * Returns a new function which represents the same value as the
	 * {@code BigRational} {@code bigRat}.
	 * 
	 * @param bigRat value to create a function of
	 * @return function representing the same value as {@code bigRat}
	 */
	Function fromBigRational(BigRational bigRat);

	/**
	 * Returns a function representing a single variable. 
	 * 
	 * @param var the variable to create a function of
	 * @return function consisting only in one variable
	 */
	Function getVar(int var);

	int getVarIndex(String var);

	/**
	 * Returns a function representing a single variable. 
	 * 
	 * @param var name of the variable to create a function of
	 * @return function consisting only in one variable
	 */
	default Function getVar(String var) {
		return getVar(getVarIndex(var));
	}

	/**
	 * Returns name of variable with the given index.
	 * 
	 * @param var index of the variable to obtain name of
	 * @return name of {@code var}
	 */
	String getParameterName(int var);

	/**
	 * Returns lower bound of variable with the given index.
	 * 
	 * @param var index of the variable to obtain lower bound of
	 * @return lower bound of {@code var}
	 */
	BigRational getLowerBound(int var);

	/**
	 * Returns upper bound of variable with the given index.
	 * 
	 * @param var index of the variable to obtain upper bound of
	 * @return upper bound of {@code var}
	 */
	BigRational getUpperBound(int var);

	/**
	 * Returns a function representing the value of the given number.
	 * 
	 * @param from number to create function of
	 * @return function representing the number {@code from}
	 */
	default Function fromLong(long from) {
		return fromBigRational(new BigRational(from));
	}

}