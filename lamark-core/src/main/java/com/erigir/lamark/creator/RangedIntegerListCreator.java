/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.creator;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.EConfigResult;
import com.erigir.lamark.IConfigurable;
import com.erigir.lamark.ICreator;
import com.erigir.lamark.IValidatable;
import com.erigir.lamark.Individual;


/**
 * Creates individuals as lists of integers, with each integer taken from the supplied range.
 * 
 * @author cweiss
 * @since 04/2006
 */
public class RangedIntegerListCreator extends AbstractLamarkComponent implements ICreator<List<Integer>>, IValidatable, IConfigurable
{
    /** Lower bound for the integers to be created **/
	private int lowerBoundInclusive = Integer.MIN_VALUE;
    /** Upper bound for the integers to be created **/
	private int upperBoundInclusive = Integer.MAX_VALUE;
    /** Size of the list to generate (REQUIRED)**/
   private Integer size;
    
   /**
    * Accessor method
    * @return Integer containing the property
    */
    public Integer getSize()
    {
        return size;
    }


    /**
     * Mutator method
     * @param size new value
     */
    public void setSize(Integer size)
    {
        this.size = size;
    }


    /**
     * @see com.erigir.lamark.ICreator#create()
     */
	public Individual create()
    {
        int range = upperBoundInclusive - lowerBoundInclusive;
        if (size==null)
        {
            throw new IllegalStateException("Cannot process, 'size' not set");
        }
        List<Integer> rval = new ArrayList<Integer>(size);
        for (int i = 0; i < size; i++)
        {
            rval.add(new Integer(getLamark().getRandom().nextInt(range)
                + lowerBoundInclusive));
        }
        Individual i = new Individual();
        i.setGenome(rval);
        return i;
    }

       /**
        * Accessor method
        * @return int containing the property
        */
    public int getLowerBoundInclusive()
    {
        return lowerBoundInclusive;
    }

    /**
     * Mutator method
     * @param lowerBoundInclusive new value
     */
   public void setLowerBoundInclusive(int lowerBoundInclusive)
    {
        this.lowerBoundInclusive = lowerBoundInclusive;
    }

    /**
     * Accessor method
     * @return int containing the property
     */
    public int getUpperBoundInclusive()
    {
        return upperBoundInclusive;
    }

    /**
     * Mutator method
     * @param upperBoundInclusive new value
     */
    public void setUpperBoundInclusive(int upperBoundInclusive)
    {
        this.upperBoundInclusive = upperBoundInclusive;
    }


    /**
     * Checks if size was set.
     * @see com.erigir.lamark.IValidatable#validate(List)
     */
    public void validate(List < String > errors)
    {
        if (size==null)
        {
            errors.add("No 'size' set for the creator");
        }
    }


    /**
     * @see com.erigir.lamark.IConfigurable#setProperty(String, String)
     */
    public EConfigResult setProperty(String name, String value)
    {
        if (name.equalsIgnoreCase("size"))
        {
            try
            {
                size = new Integer(value);
                return EConfigResult.OK;
            }
            catch (Exception e)
            {
                return EConfigResult.INVALID_VALUE;
            }
        }
        else 
        {
            return EConfigResult.NO_SUCH_PROPERTY;
        }
    }

    /**
     * @see com.erigir.lamark.IConfigurable#getProperties()
     */
    public Properties getProperties()
    {
        Properties p = new Properties();
        if (size!=null)
        {
            p.setProperty("size", size.toString());
        }
        return p;
    }

}