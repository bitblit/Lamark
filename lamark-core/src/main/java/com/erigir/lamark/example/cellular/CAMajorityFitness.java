/*
 * Created on Feb 22, 2005
 */
package com.erigir.lamark.example.cellular;

import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.EConfigResult;
import com.erigir.lamark.EFitnessType;
import com.erigir.lamark.IConfigurable;
import com.erigir.lamark.IFitnessFunction;
import com.erigir.lamark.IValidatable;
import com.erigir.lamark.Individual;

/**
 * A fitness function that takes proposed solutions to a Cellular Automata and selects for
 * the ones that come closes to implementing the majority-rules behaviour.
 * 
 * @author cweiss
 * @since 03/2007
 */
public class CAMajorityFitness extends AbstractLamarkComponent implements IFitnessFunction<String>,IConfigurable, IValidatable
{
    /** An internal static random instnace **/
    private static Random INTERNAL_RANDOM = new Random();

    /** Best score found to date?? **/
	public static int highScore = 0;

    /** Number of tables used to test the CA rule **/
	public static final int NUMBER_OF_TABLES = 25;

    /** Number of rows in the table **/
	public static final int NUMBER_OF_CA_ROWS = 5; // this times the next
													// should be

    /** Number of columns in the table **/
	public static final int NUMBER_OF_CA_COLS = 5; // = number of tables

    /** Default widht of one of the tables **/
	public static final int DEFAULT_TABLE_WIDTH = 50;

    /** Defautl height of one of the tables **/
	public static final int DEFAULT_TABLE_HEIGHT = 50;

    /** Default distribution of random data in a table **/
	public static final String DEFAULT_DISTRIBUTION = "FLAT";

    /** Default radius of a rule in a table **/
	public static final int DEFAULT_RADIUS = 3;

    /** Default interval of images in a table **/
	public static final int DEFAULT_IMAGE_INTERVAL = 25;

    /** Width of a given table **/
	private Integer tableWidth;

    /** Height of a given table **/
	private Integer tableHeight;

    /** Radius of a given rule set **/
	private Integer radius;

    /** Length of the string holding a rule set **/
	public int stringLength;

    /** Interval of images in a rule set **/
	public int imageInterval;

    /** A set of pre-created first rows for testing rules **/
	private static String[] firstRows;

    /** 
     * Calculated preferred width of a table
     * @return int containign the value
     */
	public int preferredWidth()
	{
		return (NUMBER_OF_CA_COLS * tableWidth) + NUMBER_OF_CA_COLS - 1;
	}

    /** 
     * Calculated preferred height of a table
     * @return int containign the value
     */
	public int preferredHeight()
	{
		return (NUMBER_OF_CA_COLS * tableWidth) + NUMBER_OF_CA_COLS - 1;
	}

    
	/**
	 * @see com.erigir.lamark.IFitnessFunction#fitnessType()
	 */
	public EFitnessType fitnessType() {
		return EFitnessType.MAXIMUM_BEST;
	}

    
	/**
	 * @see com.erigir.lamark.IFitnessFunction#fitnessValue(com.erigir.lamark.Individual)
	 */
	public double fitnessValue(Individual div) {
		CellularAutomata[] automata = new CellularAutomata[NUMBER_OF_TABLES];

		// For each table, see if it came out right
		int points = 0;
		for (int i = 0; i < NUMBER_OF_TABLES; i++)
		{
			automata[i] = new CellularAutomata((String) div.getGenome(),
				firstRows[i], tableHeight, radius);
			boolean majorityOne = automata[i].tableStartsMajorityTrue();
			if (majorityOne)
			{
				if (automata[i].tableEndsAllTrue())
				{
					points++;
				}
			}
			else
				if (automata[i].tableEndsAllFalse())
				{
					points++;
				}
		}

		// Save the ca's in case we need to draw this guy
		div.setAttribute("CADATA",automata);
		div.setAttribute("NUMBER_OF_CA_COLS",NUMBER_OF_CA_COLS);
		div.setAttribute("NUMBER_OF_CA_ROWS",NUMBER_OF_CA_ROWS);
		div.setAttribute("TABLE_WIDTH",tableWidth);
		div.setAttribute("TABLE_HEIGHT",tableHeight);
		
		return points;
	}

    /**
     * @see com.erigir.lamark.IValidatable#validate(java.util.List)
     */
    public void validate(List < String > errors)
    {
        if (tableWidth==null)
        {
            errors.add("You must set tableWidth");
        }
        if (tableHeight==null)
        {
            errors.add("You must set tableHeight");
        }
        if (radius==null)
        {
            errors.add("You must set radius");
        }
    }

    /**
     * @see com.erigir.lamark.IConfigurable#getProperties()
     */
    public Properties getProperties()
    {
        Properties rval = new Properties();
        if (tableWidth!=null)
        {
            rval.setProperty("tableWidth",tableWidth.toString());
        }
        if (tableHeight!=null)
        {
            rval.setProperty("tableHeight",tableHeight.toString());
        }
        if (radius!=null)
        {
            rval.setProperty("radius",radius.toString());
        }
        
        return rval;
    }

    /**
     * @see com.erigir.lamark.IConfigurable#setProperty(java.lang.String, java.lang.String)
     */
    public EConfigResult setProperty(String name, String value)
    {
        if (name.equalsIgnoreCase("tableHeight"))
        {
            try
            {
                this.setTableHeight(new Integer(value));
            }
            catch (Exception e)
            {
                return EConfigResult.INVALID_VALUE;
            }
        }
        else if (name.equalsIgnoreCase("tableWidth"))
        {
            try
            {
                setTableWidth(new Integer(value));
            }
            catch (Exception e)
            {
                return EConfigResult.INVALID_VALUE;
            }
        }
        else if (name.equalsIgnoreCase("radius"))
        {
            try
            {
                setRadius(new Integer(value));
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
        return EConfigResult.OK;
    }

    /**
     * Accessor method
     * @return Integer containing the value
     */
	public Integer getRadius()
    {
        return radius;
    }

    /** 
     * Mutator method.
     * NOTE: also sets string length expected
     * @param radius new value
     */
    public void setRadius(Integer radius)
    {
        this.radius = radius;
        stringLength = (int) Math.pow(2, ((2 * radius) + 1));

    }

    /**
     * Accessor method
     * @return Integer containing the value
     */
    public Integer getTableHeight()
    {
        return tableHeight;
    }

    /**
     * Mutator method
     * @param tableHeight new value
     */
    public void setTableHeight(Integer tableHeight)
    {
        this.tableHeight = tableHeight;
    }

    /**
     * Accessor method
     * @return Integer containing the value
     */
    public Integer getTableWidth()
    {
        return tableWidth;
    }

    /**
     * Mutator method.
     * NOTE: Also creates the initial rows
     * @param tableWidth new value
     */
    public void setTableWidth(Integer tableWidth)
    {
        this.tableWidth = tableWidth;
        firstRows = new String[NUMBER_OF_TABLES];
        for (int i = 0; i < NUMBER_OF_TABLES; i++)
        {
            int pctTrue = (int)(((double)i/(double)NUMBER_OF_TABLES)*100.0);
            firstRows[i] = booleanArrayToString(newFlatDistributionBooleanArray(tableWidth, pctTrue));
        }
    }

	
	/**
     * Fills a boolean array with a normal distribution of values
	 * @param value boolean array to fill
	 */
	public static void fillNormalDistributionBooleanArray(boolean[] value)
	{
		for (int i = 0; i < value.length; i++)
		{
			value[i] = INTERNAL_RANDOM.nextBoolean();
		}
	}

    /**
     * Fills a boolean array with a flat distribution of values
     * @param value boolean array to fill
     * @param percentTrue int containinig approximate percentage of values that should be true
     */
	public static void fillFlatDistributionBooleanArray(boolean[] value,
		int percentTrue)
	{
		if (percentTrue < 0 || percentTrue > 100)
		{
			throw new IllegalArgumentException("Invalid percent true:"
				+ percentTrue);
		}
		for (int i = 0; i < value.length; i++)
		{
			value[i] = (INTERNAL_RANDOM.nextInt(100) < percentTrue);
		}
	}

    /**
     * Creates and fills a boolean array with a normal distribution of values
     * @param length int contianign the size of the new array
     * @return boolean array containing the values
     */
	public static boolean[] newNormalDistributionBooleanArray(int length)
	{
		boolean[] rval = new boolean[length];
		fillNormalDistributionBooleanArray(rval);
		return rval;
	}

    /**
     * Creates and fills a boolean array with a flat distribution of values
     * @param length int contianign the size of the new array
     * @param percentTrue int containinig approximate percentage of values that should be true
     * @return boolean array containing the values
     */
	public static boolean[] newFlatDistributionBooleanArray(int length,
		int percentTrue)
	{
		boolean[] rval = new boolean[length];
		fillFlatDistributionBooleanArray(rval, percentTrue);
		return rval;
	}

    /**
     * Converts a boolean array to a string of 0 and 1
     * @param data boolean array to convert
     * @return String containing the values
     */
	public static String booleanArrayToString(boolean[] data)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; i++)
		{
			if (data[i])
			{
				sb.append("1");
			}
			else
			{
				sb.append("0");
			}
		}
		return sb.toString();
	}

    /**
     * Calculate how many true values there are in a boolean array
     * @param data boolean array to survey
     * @return int containing the number of trues
     */
	public static int trueCount(boolean[] data)
	{
		int rval = 0;
		for (int i = 0; i < data.length; i++)
		{
			if (data[i])
			{
				rval++;
			}
		}
		return rval;
	}



}