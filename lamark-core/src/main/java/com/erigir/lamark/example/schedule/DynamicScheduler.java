/*
 * Created on Apr 19, 2005
 */
package com.erigir.lamark.example.schedule;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a dynamic scheduling/batching algorithm as
 * described in by Bein.  After instantiating the object and calling
 * the setup routine with the appropriate weights, times, and 
 * setup time, the class performs the calculations necessary to 
 * return the optimal time for this permutation, and also the
 * "split-points", that is, where the batches start and stop
 * 
 * @author cweiss
 * @since 04/2005
 */
public class DynamicScheduler {

    /** Logger instance **/
	private static Logger LOG = Logger.getLogger(DynamicScheduler.class.getName());
	/** The cached table of weights from point to point **/
	private int[] weightTable =null;
	/** The cached table of times from point to point **/
	private int[] timeTable =null;
	/** the cached table of cost from node to node **/
	private int[][] costTable = null;
	/** the cached table of best cost routes from node to node **/
	private int[] bestRoute = null;
	
	
	/** The input weights **/
	private int[] weights = null;
	/** The input times **/
	private int[] times = null;
	/** The input setup time **/
	private int setupTime = -1;
	/** The cache of where the batches should split **/
	private int[] splitPoints = null;
	
	/** Set to true when the cache tables are built **/
	private boolean initialized = false;
	
	/** 
	 * A static method to simplfy use if the user doesnt care about the split points 
	 * @param times int[] of the times of the processes
	 * @param weights int[] of the weights of the processes
	 * @param setupTime int containing the constant setup time
	 * @return int containing the optimal processing time for this configuration
	 */
	public static int calculateBestTime(int[] times,int[] weights,int setupTime)
	{
		DynamicScheduler ds = new DynamicScheduler();
		ds.setup(times,weights,setupTime);
		return ds.optimalTime();
	}
	
	/**
	 * Returns the best time for this configuration
	 * @return int containing the time
	 */
	public int optimalTime()
	{
		if (!initialized)
		{
			throw new IllegalStateException();
		}
		return bestRoute[bestRoute.length-1];
	}
	
	/**
	 * Returns where the schedule should be broken for optimality
	 * @return int[] containing the optimal batch split points
	 */
	public int[] splitPoints()
	{
		if (!initialized)
		{
			throw new IllegalStateException();
		}
		int[] rval = new int[splitPoints.length];
		System.arraycopy(splitPoints,0,rval,0,splitPoints.length);
		return rval;
	}
	
	/**
	 * Given a set of process times and weights,
	 * calculates the optimal distribution of
	 * batches and then calculates the total time
	 * @param ptimes int[] of the times of the processes
	 * @param pweights int[] of the weights of the processes
	 * @param psetupTime int containing the constant setup time
	 */
	public void setup(int[] ptimes,int[] pweights,int psetupTime)
	{
		LOG.finer("Called setup schedule time, times="+intArrayAsString(ptimes)+" weights="+intArrayAsString(pweights)+" setup="+psetupTime);
		if (ptimes==null || pweights==null || psetupTime<0 || ptimes.length!=pweights.length)
		{
			throw new IllegalArgumentException();
		}
		
		weights = new int[pweights.length+1];
		System.arraycopy(pweights,0,weights,1,pweights.length);
		times = new int[ptimes.length+1];
		System.arraycopy(ptimes,0,times,1,ptimes.length);
		setupTime = psetupTime;
		createWeightTable();
		createTimeTable();
		createCostTable();
		createBestRoute();
		if (LOG.isLoggable(Level.FINER))
		{
			printCostTable();
		}
		initialized=true;
	}
	
	/**
	 * Given the cost table, creates the best batching for this
	 * configuration
	 */
	private void createBestRoute()
	{
		bestRoute = new int[timeTable.length];
		ArrayList<Integer> splits = new ArrayList<Integer>();
		splits.add(new Integer(0));
		
		int best = Integer.MAX_VALUE;
		int splitJ = -1;
		for (int i=1;i<timeTable.length;i++)
		{
			best = Integer.MAX_VALUE;
			for (int j=0;j<i;j++)
			{
				if ((costTable[j][i]+bestRoute[j])<best)
				{
					best = costTable[j][i]+bestRoute[j];
					splitJ = j;
				}
			}
			// Back out any previous wrong best splits
			while (((Integer)splits.get(splits.size()-1)).intValue()>splitJ)
			{
				splits.remove(splits.size()-1);
			}
			// Add new split point, if new
			if (splitJ!=((Integer)splits.get(splits.size()-1)).intValue())
			{
				splits.add(new Integer(splitJ));
			}
			bestRoute[i]=best;
		}
		LOG.finer("Would split at : "+splits);
		
		// Now convert the splits holder into an int array
		splitPoints = new int[splits.size()];
		for (int i=0;i<splitPoints.length;i++)
		{
			splitPoints[i]=((Integer)splits.get(i)).intValue();
		}
		
	}
	
	/**
	 * Given the times, creates a cached table of the
	 * sums needed
	 */
	private void createTimeTable()
	{
		timeTable = new int[times.length];
		for(int i=0;i<times.length;i++)
		{
			timeTable[i]=times[i];
			if (i>0)
			{
				timeTable[i]+=timeTable[i-1];
			}
		}
	}
	/**
	 * Given the weights, creates a cached table of the
	 * sums needed
	 */
	private void createWeightTable()
	{
		weightTable = new int[weights.length];
		for(int i=0;i<weights.length;i++)
		{
			weightTable[i]=weights[i];
			if (i>0)
			{
				weightTable[i]+=weightTable[i-1];
			}
		}
	}
	
	/**
	 * Given the cached time table, calcs the time
	 * from process at index from to index to
	 * @param from int containing index of the start process
	 * @param to int containingthe index of the end process
	 * @return int containing the total time between
	 */
	private int time(int from,int to)
	{
		if (timeTable==null)
		{
			throw new IllegalStateException();
		}
		return timeTable[to]-timeTable[from];
	}
	/**
	 * Given the cached weight table, calcs the weight
	 * from process at index from to index to
	 * @param from int containing index of the start process
	 * @param to int containingthe index of the end process
	 * @return int containing the total weight between
	 */
	private int weight(int from,int to)
	{
		if (weightTable==null)
		{
			throw new IllegalStateException();
		}
		return weightTable[to]-weightTable[from];
	}
	
	/**
	 * Converts an int array to a string for printing purposes
	 * @param value int[] to convert
	 * @return String containing the converted value
	 */
	private static String intArrayAsString(int[] value)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i=0;i<value.length;i++)
		{
			sb.append(value[i]);
			if (i<value.length-1)
			{
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Given the time and weight tables, creates a cached cost table
	 */
	private void createCostTable()
	{
		costTable = new int[times.length][times.length];
		
		for (int i=0;i<times.length;i++)
		{
			for (int j=i+1;j<times.length;j++)
			{
				costTable[i][j]=(setupTime+time(i,j))*weight(i,timeTable.length-1);
			}
		}
	}
	
	/**
	 * Prints the cost table to standard out for debuging
	 */
	private void printCostTable()
	{
		for (int i=0;i<times.length;i++)
		{
			for (int j=i+1;j<times.length;j++)
			{
				System.out.println("C "+i+"-"+j+" = "+costTable[i][j]);
			}
		}
	}
	
}
