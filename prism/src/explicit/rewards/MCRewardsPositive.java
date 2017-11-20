//==============================================================================
//	
//	Copyright (c) 2017-
//	Authors:
//	* Joachim Klein <klein@tcs.inf.tu-dresden.de> (TU Dresden)
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

package explicit.rewards;

import explicit.Model;
import explicit.Product;

/**
 * Wrapper for a given MCRewards structure.
 * This wrapper ensures that there are only non-negative rewards by
 * setting all negative rewards to 0.0.
 * Optionally, the rewards are negated beforehand, i.e., all negative rewards
 * become positive and all previously positive rewards are mapped to 0.0.
 */
public class MCRewardsPositive implements MCRewards
{
	/** Should we negate? */
	private boolean negate = false;
	/** The underlying reward structure */
	private MCRewards rew;

	/** Constructor, don't negate */
	public MCRewardsPositive(MCRewards rew)
	{
		this(rew, false);
	}

	/** Constructor, optionally negate */
	public MCRewardsPositive(MCRewards rew, boolean negate)
	{
		this.rew = rew;
		this.negate = negate;
	}

	@Override
	public boolean hasTransitionRewards()
	{
		return false;
	}

	@Override
	public boolean hasPositiveRewards()
	{
		if (negate) {
			return rew.hasNegativeRewards();
		} else {
			return rew.hasPositiveRewards();
		}
	}

	@Override
	public boolean hasNegativeRewards()
	{
		return false;
	}

	@Override
	public double getStateReward(int s)
	{
		double r = rew.getStateReward(s);
		if (negate) {
			r = -r;
		}
		if (r < 0) {
			r = 0;
		}

		return r;
	}

	@Override
	public MCRewards liftFromModel(Product<? extends Model> product)
	{
		throw new UnsupportedOperationException();
	}

}
