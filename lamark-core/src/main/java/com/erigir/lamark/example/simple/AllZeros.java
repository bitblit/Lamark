package com.erigir.lamark.example.simple;

import com.erigir.lamark.EFitnessType;

/**
 * A simple fitness function to find a string with all ones in it.
 *
 * NOTE: This is here to simply demonstrate the use of a 
 * minimum-best fitness function.  It just takes the AllOnes 
 * class and inverts the function.
 * 
 * @author cweiss
 * @since 04/2006
 */

public class AllZeros extends AllOnes
{
	/**
	 * @see com.erigir.lamark.example.simple.AllOnes#fitnessType()
	 */
	public EFitnessType fitnessType() {
		return EFitnessType.MINIMUM_BEST;
	}
}
