//==============================================================================
//	
//	Copyright (c) 2020-
//	Authors:
//	* Joachim Klein <joachim.klein@automata.tools>
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


package odd;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import jdd.JDD;
import jdd.JDDNode;
import jdd.JDDVars;
import prism.PrismException;
import prism.PrismUtils;


/**
 * Helper to sample from a set of states.
 *
 * Constructs an ODD for the state-set and
 * then samples via the integer indizes.
 *
 * Needs to be cleared after use.
 */
public class ODDSampler
{
	private final JDDNode dd;
	private final JDDVars vars;
	
	private final ODDNode odd;
	private final Random random;

	
	/**
	 * Constructor.
	 *
	 * Throws exception if the state set contains more than INT_MAX states.
	 * Uses a fresh java.util.Random generator.
	 *
	 * @param dd the zero-one MTBDD of the state set
	 * @param vars the variables underlying the state set
	 *
	 * <br>[ copies dd and vars, call clear() to deref ]
]	 */
	public ODDSampler(JDDNode dd, JDDVars vars) throws PrismException
	{
		this(dd, vars, new Random());
	}


	/**
	 * Constructor.
	 *
	 * Throws exception if the state set contains more than INT_MAX states.
	 *
	 * @param dd the zero-one MTBDD of the state set
	 * @param vars the variables underlying the state set
	 * @param random the random number generator to use
	 *
	 * <br>[ copies dd and vars, call clear() to deref ]
]	 */
	public ODDSampler(JDDNode dd, JDDVars vars, Random random) throws PrismException
	{
		if (jdd.SanityJDD.enabled) {
			jdd.SanityJDD.checkIsZeroOneMTBDD(dd);
			jdd.SanityJDD.checkIsDDOverVars(dd, vars);
		}

		this.dd = dd.copy();
		this.vars = vars.copy();
		this.random = random;

		odd = ODDUtils.BuildODD(dd, vars);
		ODDUtils.checkInt(odd, "Can't build sampler ");
	}

	/** Clear the resources held by this sampler. */
	public void clear()
	{
		if (odd != null)
			ODDUtils.ClearODD(odd);
		JDD.Deref(dd);
		vars.derefAll();
	}


	/** Get the number of states contained in the state set. */
	public int getNumStates() {
		// odd.getNumStates returns long, but is guaranteed to fit in int
		// due to the checkInt call in the constructor
		return (int)odd.getNumStates();
	}


	/**
	 * Sample a single state from the state set (uniformly).
	 *
	 * @return zero-one MTBDD of the state set containing the single state (cube)
	 */
	public JDDNode sampleState()
	{
		int n = getNumStates();
		int index = random.nextInt(n);  // get uniform state index from 0..n-1

		// convert index to DD
		return ODDUtils.SingleIndexToDD(index, odd, vars);
	}


	/**
	 * Sample a subset of k states (via rejection sampling).
	 *
	 * If k &lt;= n, returns the full set of states.
	 *
	 * @param k How many states should be sampled?
	 * @return zero-one MTBDD of the sampled set of states
	 */
	public JDDNode sampleStatesViaRejection(int k)
	{
		if (k <= 0)
			throw new IllegalArgumentException();

		if (k == 0)
			return JDD.ZERO.copy();

		if (k >= getNumStates()) {
			return dd.copy();
		}

		JDDNode states = JDD.Constant(0);
		int i = 0;
		while (i < k) {
			JDDNode state = sampleState();

			JDDNode oldStates = states;
			states = JDD.Or(states, state);
			if (states.equals(oldStates)) {
				// state was already contained,
				// reject and continue
				continue;
			} else {
				// a new state was found
				i++;
			}
		}

		return states;
	}

	/**
	 * Sample a subset of k states (via reservoir sampling, using Li's algorithm L).
	 *
	 * If k &lt;= n, returns the full set of states.
	 *
	 * @param k How many states should be sampled?
	 * @return zero-one MTBDD of the sampled set of states
	 */
	public JDDNode sampleStatesViaReservoirSampling(int k)
	{
		if (k < 0)
			throw new IllegalArgumentException();

		if (k == 0)
			return JDD.ZERO.copy();

		final int n = getNumStates();

		if (k >= n) {
			return dd.copy();
		}

		// This is an implementation of the reservoir sampling algorithm L
		// from Li, Kim-Hung: "Reservoir-Sampling Algorithms of Time Complexity O(n(1+log(N/n)))"
		// See https://en.wikipedia.org/wiki/Reservoir_sampling and
		// https://github.com/gstamatelat/random-sampling/blob/master/src/main/java/gr/james/sampling/LiLSampling.java

		int reservoir[] = new int[k];

		for (int i=0; i < k; i++) {
			reservoir[i] = i;
		}

		// obtain uniformly sampled double from 0..1
		double r = random.nextDouble();
		// double W = Math.exp(Math.log(r)/k);
		double W = Math.pow(r, 1.0/k);

		int i = k - 1;
		while (true) {
			double r1 = random.nextDouble();
			double r2 = random.nextDouble();

			double skip = Math.floor(Math.log(r1)/Math.log(1.0-W));
			if (skip < 0 || skip >= Integer.MAX_VALUE) {
				// (1) if W is very small, log(1.0-W) can become +0,
				// and skip becomes negative infinity
				// (2) if skip is larger than MAX_INT, we can abort as well
				break;
			}

			i += (int)skip + 1;

			if (i < 0 || i >= n) {
				// negative: int overflow
				// larger than n -> done
				// done
				break;
			}

			// replace a random item of the reservoir with item i
			int j = random.nextInt(k);
			reservoir[j] = i;

			// W *= Math.exp(Math.log(r2)/k);
			W *= Math.pow(r2, 1.0/k);
		}

		// Now that we have determined which indizes to return,
		// construct the DD.
		JDDNode states = JDD.Constant(0);
		for (int index : reservoir) {
			JDDNode state = ODDUtils.SingleIndexToDD(index, odd, vars);

			states = JDD.Or(states, state);
		}

		return states;
	}


	// ------ testing -----------------------------------------------

	public static void main(String[] args) throws PrismException
	{
		boolean reservoirSampling = true;
		if (args.length >= 1) {
			switch (args[0]) {
			case "reservoir":
				reservoirSampling = true;
				break;
			case "rejection":
				reservoirSampling = false;
				break;
			default:
				System.out.println("Unknown sampling method '" + args[0] + ", try 'reservoir' or 'rejection'");
				return;
			}
		}

		System.out.println("Using " + (reservoirSampling ? "reservoir" : "rejection") + " sampling");

		JDD.InitialiseCUDD();
		ODDUtils.setCUDDManager();

		JDDVars vars = new JDDVars();
		vars.addVar(JDD.Var(0));
		vars.addVar(JDD.Var(1));
		vars.addVar(JDD.Var(2));
		vars.addVar(JDD.Var(3));

		@SuppressWarnings("serial")
		class FrequencyMap<K> extends TreeMap<K, Long> {
			private double average;

			public FrequencyMap(Map<K,Long> m) {
				double sum = 0;

				for (Entry<K,Long> e : m.entrySet()) {
					put(e.getKey(), e.getValue());
					sum += e.getValue();
				}
				
				average = sum / m.size();
			}

			public String toString() {
				StringBuffer s = new StringBuffer();

				for (Entry<K, Long> e : this.entrySet()) {
					s.append(e.getKey().toString());
					s.append("\t");
					s.append(e.getValue());
					s.append("\t( diff=");
					double diff = e.getValue() - average;
					double percentageDiff = diff / average * 100.0;
					s.append(PrismUtils.formatDouble2dp(diff));
					s.append(", ");
					s.append(PrismUtils.formatDouble2dp(percentageDiff));
					s.append("% )\n");
				}

				s.append("\nAverage = " + PrismUtils.formatDouble2dp(average));
				return s.toString();
			}
		}

		JDDNode states = JDD.ONE.copy();
		for (int i = 0; i < (1 << vars.n()); i++) {
			if (i % 2 == 1)
				states = JDD.SetVectorElement(states, vars, i, 0);
		}
		ODDNode odd = ODDUtils.BuildODD(JDD.ONE, vars);

		ODDSampler sampler = new ODDSampler(states, vars);

		common.StopWatch timer = new common.StopWatch();
		{
			timer.start();
			System.out.println("\n-- Sample single states ----------");
			Map<Long, Long> counts = new HashMap<>();
			for (long i = 0; i < 10000000; i++) {
				JDDNode dd = sampler.sampleState();
				int j = ODDUtils.GetIndexOfFirstFromDD(dd, odd, vars);
				JDD.Deref(dd);

				counts.merge((long)j, 1L, (a,b) -> a+b);
			}
			timer.stop();

			FrequencyMap<Long> freq = new FrequencyMap<>(counts);
			System.out.println(freq);
			System.out.println("Elapsed: " + timer.elapsedSeconds() + "s");
		}

		{
			class LongPair implements Comparable<LongPair> {

				public long a, b;

				public LongPair(long a, long b) {
					this.a = a;
					this.b = b;
				}

				@Override
				public int hashCode()
				{
					final int prime = 31;
					int result = 1;
					result = prime * result + (int) (a ^ (a >>> 32));
					result = prime * result + (int) (b ^ (b >>> 32));
					return result;
				}

				@Override
				public boolean equals(Object obj)
				{
					if (!(obj instanceof LongPair))
						return false;

					LongPair other = (LongPair) obj;
					return (a == other.a) && (b == other.b);
				}

				@Override
				public int compareTo(LongPair o)
				{
					if (a == o.a) {
						return Long.compare(b, o.b);
					}	
					return Long.compare(a, o.a);
				}

				@Override
				public String toString() {
					return a + "-" + b;
				}

			};

			System.out.println("\n-- Sample pairs of states ----------");
			timer.start();
			Map<LongPair, Long> counts = new HashMap<>();
			for (long i = 0; i < 5600000; i++) {
				JDDNode dd = reservoirSampling
						? sampler.sampleStatesViaReservoirSampling(2)
						: sampler.sampleStatesViaRejection(2);

				int x = ODDUtils.GetIndexOfFirstFromDD(dd, odd, vars);
				dd = JDD.SetVectorElement(dd, vars, x, 0);

				if (dd.equals(JDD.ZERO)) {
					throw new RuntimeException("Oops, sampling less sthan 2 elements");
				}

				int y = ODDUtils.GetIndexOfFirstFromDD(dd, odd, vars);
				dd = JDD.SetVectorElement(dd, vars, y, 0);

				if (!dd.equals(JDD.ZERO)) {
					throw new RuntimeException("Oops, sampling more than 2 elements");
				}
				JDD.Deref(dd);

				counts.merge(new LongPair(x,y), 1L, (a,b) -> a+b);
			}

			timer.stop();
			FrequencyMap<LongPair> freq = new FrequencyMap<>(counts);
			System.out.println(freq);
			System.out.println("Elapsed: " + timer.elapsedSeconds() + "s");
		}

		{
			System.out.println("\n-- Sample randomly sized sets of states, count occurences for each state -------");

			timer.start();
			Random random = new Random();
			Map<Long, Long> counts = new HashMap<>();
			for (long i = 0; i < 1600000; i++) {
				int k = random.nextInt(sampler.getNumStates()) + 1;

				JDDNode dd = reservoirSampling
						? sampler.sampleStatesViaReservoirSampling(k)
						: sampler.sampleStatesViaRejection(k);

				int actualK = 0;
				while (!dd.equals(JDD.ZERO)) {
					int x = ODDUtils.GetIndexOfFirstFromDD(dd, odd, vars);
					counts.merge((long)x, 1L, (a,b) -> a+b);
					dd = JDD.SetVectorElement(dd, vars, x, 0);
					actualK++;
				}
				JDD.Deref(dd);

				if (actualK != k) {
					throw new RuntimeException("Ooops, wanted to sample " + k + ", but sampled " + actualK);
				}
			}
			timer.stop();

			FrequencyMap<Long> freq = new FrequencyMap<>(counts);
			System.out.println(freq);
			System.out.println("Elapsed: " + timer.elapsedSeconds() + "s");
		}

		ODDUtils.ClearODD(odd);
		sampler.clear();
	}

}
