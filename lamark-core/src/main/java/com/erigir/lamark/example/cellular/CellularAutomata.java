/*
 * Created on Feb 22, 2005
 */
package com.erigir.lamark.example.cellular;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * This class represents a cellular automata, created given a target size, a rule set, and an initial row.
 * 
 * @author cweiss
 * @since 03/2005
 */
public class CellularAutomata
{
    /** Handle to a private random isntace **/
	public final Random random = new Random();

    /** Holds the CA table **/
	private boolean[][] table;

    /** Holds the length of a ruleset string **/
	private int stringLength = -1;

    /** Holds the widht of the target table **/
	private int width = -1;

    /** Holds the height of the target table **/
	private int height = -1;

    /** Holds the radius of the rule set **/
	private int radius = -1;

    /** Holds the distribution stype **/
	private int distribution = -1;

    /** Holds the percentage of true used for a flat distribution **/
	private int flatDistributionPercentage = -1;

    /** Holds the ruleset for this CA **/
	private String ruleSet = null;

    /** Constant for undefined distribution (random)**/
	public static final int UNDEFINED = -1;

    /** Constant for flat distribution**/
	public static final int FLAT_DISTRIBUTION = 1;

    /** Constant for normal distribution**/
	public static final int NORMAL_DISTRIBUTION = 2;

	/**
	 * Standard constructor for creating and running a cellular automata of a
	 * given width and height where the first row is created using one of the
	 * standard distributions
	 * 
	 * @param ruleSet String contianing the rules to use for generation
	 * @param width int containing the width of the output table
	 * @param height int containing the height of the output table
	 * @param radius int containing the radius of a given rule
	 * @param distribution int containing the type of distribution to use
	 * @param flatDistPercent int containing the percent to use in a flat distribution
	 */
	public CellularAutomata(String ruleSet, int width, int height, int radius,
		int distribution, int flatDistPercent)
	{
		stringLength = (int) Math.pow(2, ((2 * radius) + 1));
		if (ruleSet.length() != stringLength)
		{
			throw new RuntimeException("Error : Radius is " + radius
				+ " (Width " + stringLength
				+ ") but supplied rule set length is " + ruleSet.length());
		}
		this.width = width;
		this.height = height;
		this.radius = radius;
		this.table = new boolean[width][height];
		this.distribution = distribution;
		this.flatDistributionPercentage = flatDistPercent;
		this.ruleSet = ruleSet;

		initializeTable();
		fillTable();
	}

	/**
	 * Constructor for creating a "known" CA, usually used to test the CA runner
	 * 
	 * @param ruleSet String containing the ruleSet to initialize with
	 * @param firstRow String containing the first row of the new CA
     * @param height int containing the height of the new CA
     * @param radius int containing the radius of the ruleset
	 */
	public CellularAutomata(String ruleSet, String firstRow, int height,
		int radius)
	{
		stringLength = (int) Math.pow(2, ((2 * radius) + 1));
		if (ruleSet.length() != stringLength)
		{
			throw new RuntimeException("Error : Radius is " + radius
				+ " (Width " + stringLength
				+ ") but supplied rule set length is " + ruleSet.length());
		}
		this.width = firstRow.length();
		this.height = height;
		this.radius = radius;
		this.table = new boolean[width][height];
		this.distribution = UNDEFINED;
		this.ruleSet = ruleSet;

		initializeTable(firstRow);
		fillTable();
	}

    /**
     * Get the boolean value at a location in a table
     * @param x int index of horz value
     * @param y int index of vert value
     * @return boolean true if its white/true
     */
	public boolean getValue(int x, int y)
	{
		return table[x][y];
	}

	/**
     * Accessor method
	 * @return Returns the height.
	 */
	public int getHeight()
	{
		return height;
	}

	/**
     * Accessor method
	 * @return Returns the radius.
	 */
	public int getRadius()
	{
		return radius;
	}

	/**
     * Accessor method
	 * @return Returns the ruleSet.
	 */
	public String getRuleSet()
	{
		return ruleSet;
	}

    /**
     * Accessor method
	 * @return Returns the stringLength.
	 */
	public int getStringLength()
	{
		return stringLength;
	}

    /**
     * Accessor method
	 * @return Returns the width.
	 */
	public int getWidth()
	{
		return width;
	}

    
	/**
     * Calcs the sum of powers of 2 up to width
	 * @param width int containing upper bound
	 * @return int containing the calc'd value
	 */
	public static int andNumber(int width)
	{
		int total = 0;
		int factor = 1;
		for (int i = 0; i < width; i++)
		{
			total += factor;
			factor *= 2;
		}
		return total;
	}

    
	/**
	 * Fills the internal table using the provided rules
	 */
	private void fillTable()
	{

		// Fill the tables
		int andMask = andNumber((2 * radius) + 1);
		int total;
		for (int r = 0; r < height - 1; r++)
		{
			total = rowInitValue(r); // Init for start of row
			for (int c = 0; c < width; c++)
			{
				total = ((total << 1) & andMask);
				if (table[calcIndex(c + radius)][r])
				{
					total++;
				}
				table[c][r + 1] = charToBool(ruleSet.charAt(total));
			}
		}

	}

    /**
     * Value used to initialize the row
     * @param row int containing the value
     * @return int representing the row
     */
	private int rowInitValue(int row)
	{
		int rval = 0;
		String test = rowAsString(row);
		String val = test.substring(test.length() - (radius))
			+ test.substring(0, radius);
		rval = binToInt(val);

		return rval;
	}

    /**
     * Converts binary string to integer
     * @param value String containing the binary number
     * @return int containing the new number in int form
     */
public static int binToInt(String value)
	{
		int rval = 0;
		int factor = 1;
		for (int i = value.length() - 1; i > -1; i--)
		{
			if (value.charAt(i) == '1')
			{
				rval += factor;
			}
			factor *= 2;
		}

		return rval;
	}

/**
 * Converts integer into binary string
 * @param value int containing value to convert
 * @param width int containing width of desired output string
 * @return String containing the binary number
 */
	public static String intToBin(int value, int width)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(Integer.toBinaryString(value));
		while (sb.length() < width)
		{
			sb.insert(0, '0');
		}
		return sb.toString();
	}


	/**
     * Calcs if a table starts with the majority of its items true 
     * @return true if this exists
	 */
	public boolean tableStartsMajorityTrue()
	{
		int oneCount = 0;
		for (int i = 0; i < width; i++)
		{
			if (table[i][0])
			{
				oneCount++;
			}
		}
		return oneCount > (width / 2);
	}

    /**
     * Calcs if a table ends all true
     * @return true if this occurs
     */
	public boolean tableEndsAllTrue()
	{
		boolean rval = true;

		for (int i = 0; i < width && rval; i++)
		{
			rval = (table[i][height - 1]);
		}

		return rval;
	}

    /**
     * Calcs if a table ends all false
     * @return true if this occurs
     */
	public boolean tableEndsAllFalse()
	{
		boolean rval = true;

		for (int i = 0; i < width && rval; i++)
		{
			rval = (!table[i][height - 1]);
		}

		return rval;
	}

    /**
     * Converts a 1 or 0 character to a boolean
     * @param c Character to test
     * @return true if this occurs
     */
	private boolean charToBool(char c)
	{
		if (c == '1')
		{
			return true;
		}
		return false;
	}

    /**
     * Calculates the current index to use
     * @param idx int containing the pre-calc index
     * @return int containing the post-calc index
     */
	private int calcIndex(int idx)
	{
		int rval = idx;
		if (idx < 0)
		{
			rval = width + idx;
		}
		if (idx > width - 1)
		{
			rval = idx - width;
		}
		return rval;
	}

	/**
	 * Generates the first row of the tables using the specified distribution
	 */
	private void initializeTable()
	{
		boolean[] array = null;
		if (distribution == FLAT_DISTRIBUTION) // FIrst has 1% 1's, 50 has 50%
											   // 1's, etc
		{
			array = CAMajorityFitness.newFlatDistributionBooleanArray(width,
				flatDistributionPercentage);
		}
		else
		// normal
		{
			array = CAMajorityFitness.newNormalDistributionBooleanArray(width);
		}
		for (int i = 0; i < width; i++)
		{
			table[i][0] = array[i];
		}
	}

	/**
	 * Generates the first row of the tables using given values.
     * @param values String containing the values
	 */
	private void initializeTable(String values)
	{
		for (int i = 0; i < values.length(); i++)
		{
			table[i][0] = (values.charAt(i) == '1');
		}
	}

	/**
     * Returns a row as string of 0 and 1
	 * @param tableRowIdx int contianing the tables row index
	 * @return String contaoining the row
	 */
	public String rowAsString(int tableRowIdx)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < width; i++)
		{
			if (table[i][tableRowIdx])
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
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < height; i++)
		{
			sb.append(rowAsString(i));
			sb.append("\n");
		}

		return sb.toString();
	}

    /**
     * Converts the table to a buffered image for display
     * @return BufferedImage containing the table
     */
	public BufferedImage asImage()
	{
		BufferedImage img = new BufferedImage(getWidth(), getHeight(),
			BufferedImage.TYPE_BYTE_BINARY);

		for (int i = 0; i < getWidth(); i++)
		{
			for (int j = 0; j < getHeight(); j++)
			{
				if (getValue(i, j))
				{
					img.setRGB(i, j, Integer.MAX_VALUE);
				}
				else
				{
					img.setRGB(i, j, 0);
				}
			}
		}

		return img;
	}

}