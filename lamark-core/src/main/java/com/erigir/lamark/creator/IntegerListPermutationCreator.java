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
 * Creator that generates lists of integers that have the permuation property (0..size)
 * 
 * @author cweiss
 * @since 04/2006
 */
public class IntegerListPermutationCreator extends AbstractLamarkComponent implements ICreator<List<Integer>>, IValidatable, IConfigurable
{
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
    public Individual<List<Integer>> create()
    {
	  getLamark().logFine("create called");
      if (size==null)
      {
          throw new IllegalStateException("Cannot process, 'size' not set");
      }

        ArrayList<Integer> rval = new ArrayList<Integer>(size);
        ArrayList<Integer> temp = new ArrayList<Integer>(size);

        for (int i = 0; i < size; i++)
        {
            temp.add(new Integer(i));
        }

        int loc = -1;
        for (int i = 0; i < size; i++)
        {
            loc = getLamark().getRandom().nextInt(temp.size());
            rval.add(temp.get(loc));
            temp.remove(loc);
        }
        Individual i = new Individual();
        i.setGenome(rval);
        
        return i;
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