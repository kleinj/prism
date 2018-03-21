//==============================================================================
//	
//	Copyright (c) 2013-
//	Authors:
//	* Ernst Moritz Hahn <emhahn@cs.ox.ac.uk> (University of Oxford)
//	
//------------------------------------------------------------------------------
//	
//	This file is part of PRISM.
//	
//	PRISM is free software; you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation; either version 2 of the License, or
//	(at your option) any later version.
//	
//	PRISM is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with PRISM; if not, write to the Free Software Foundation,
//	Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//	
//==============================================================================

package param;

import java.util.HashMap;

/**
 * Generates new functions, stores valid ranges of parameters, etc.
 * 
 * @see Function
 * @author Ernst Moritz Hahn <emhahn@cs.ox.ac.uk> (University of Oxford)
 */
abstract class AbstractFunctionFactory implements FunctionFactory {
	/** the parameters */
	protected Parameters parameters;

	/** maps variable name to index in {@code parameterNames},
	 * {@code lowerBounds} and {@code upperBounds}
	 */
	protected HashMap<String, Integer> varnameToInt;
	
	/**
	 * Creates a new function factory.
	 * {@code parameterNames}, {@code lowerBounds}, {@code upperBounds} all
	 * must have the same length. Each index into these arrays represents
	 * respectively the name, lower and upper bound of a given variable.
	 * After this function factory has been created, the number of variables,
	 * their names and bounds are fixed and cannot be changed anymore.
	 * 
	 * @param parameterNames names of parameters
	 * @param lowerBounds lower bounds of parameters
	 * @param upperBounds upper bounds of parameters
	 */
	AbstractFunctionFactory(Parameters parameters) {
		this.parameters = parameters;
		this.varnameToInt = new HashMap<String, Integer>();
		for (int var = 0; var < parameters.size(); var++) {
			varnameToInt.put(parameters.getParameterName(var), var);
		}
	}

	@Override
	public abstract Function getOne();

	@Override
	public abstract Function getZero();

	@Override
	public abstract Function getNaN();

	@Override
	public abstract Function getInf();

	@Override
	public abstract Function getMInf();

	@Override
	public abstract Function fromBigRational(BigRational bigRat);

	@Override
	public abstract Function getVar(int var);

	@Override
	public int getVarIndex(String varName)
	{
		return varnameToInt.get(varName);
	}

	@Override
	public String getParameterName(int var) {
		return parameters.getParameterName(var);
	}

	@Override
	public BigRational getLowerBound(int var) {
		return parameters.getLowerBound(var);
	}

	@Override
	public BigRational getUpperBound(int var) {
		return parameters.getUpperBound(var);
	}

	/* (non-Javadoc)
	 * @see param.FunctionFactory#getNumVariables()
	 */
	@Override
	public int getNumVariables() {
		return parameters.size();
	}

	/* (non-Javadoc)
	 * @see param.FunctionFactory#getFunctionTypeName()
	 */
	@Override
	public abstract String getFunctionTypeName();

}
