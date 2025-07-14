/**
 * 
 */
package net.sci.axis;

import java.util.Arrays;
import java.util.Locale;

/**
 * An implementation of NumericalAxis that may contain value in any order, that
 * only requires that values exist only once in the axis.
 * 
 * @see LinearNumericalAxis
 * 
 * @author dlegland
 */
public class GenericNumericalAxis implements NumericalAxis
{
    // =============================================================
    // Class variables
   
    /**
     * The name for this axis.
     */
    protected String name;

    /**
     * The array of values composing this axis.
     */
    protected double[] values;
    
    /**
     * The unit name associated to this axis.
     */
    protected String unitName;


    // =============================================================
    // Constructors
    
    /**
     * Creates a new numerical axis
     * 
     * @param name the name of the numerical axis
     */
    public GenericNumericalAxis(String name)
    {
        this(name, new double[] {0, 1});
    }

    /**
     * Creates a new numerical axis
     * 
     * @param name
     *            the name of the numerical axis
     * @param values
     *            the array of numerical values within this axis
     */
    public GenericNumericalAxis(String name, double[] values)
    {
        this.name = name;
        this.values = Arrays.copyOf(values, values.length);
    }

    /**
     * Constructor with initial value for all fields.
     * 
     * @param name
     *            the name of the axis
     * @param values
     *            the array of numerical values within this axis
     * @param unitName
     *            the name of the unit
     */
    public GenericNumericalAxis(String name, double[] values, String unitName)
    {
        this.name = name;
        this.values = Arrays.copyOf(values, values.length);
        this.unitName = unitName;
    }
    

    // =============================================================
    // General methods

    /**
     * @return the physical range occupied by the given number of elements  
     */
    public double[] physicalRange()
    {
        double mini = Double.POSITIVE_INFINITY;
        double maxi = Double.NEGATIVE_INFINITY;
        for (double v : values)
        {
            mini = Math.min(mini, v);
            maxi = Math.max(maxi, v);
        }
        return new double[] {mini, maxi};
    }
    
    @Override
    public double indexToValue(int index)
    {
        return this.values[(int) index];
    }
    
    /**
     * Returns the closest index that corresponds to the specified value.
     * If this axis is empty, returns -1.
     * 
     * @param value
     *            a numerical value on the axis
     * @return the closest index that corresponds to this value
     */
    @Override
    public int valueToIndex(double value)
    {
        int index = -1;
        double minDiff = Double.POSITIVE_INFINITY;
        for (int i = 0 ; i < values.length; i++)
        {
            double diff = Math.abs(value - values[i]); 
            if (diff < minDiff)
            {
                index = i;
                minDiff = diff;
            }
        }
        return index;
    }
    
    
    // =============================================================
    // Getters / setters

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getUnitName()
    {
        return unitName;
    }
    
    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }
    
    
    // =============================================================
    // Methods overriding Object
    
    @Override
    public GenericNumericalAxis duplicate()
    {
        return new GenericNumericalAxis(this.name, this.values, this.unitName);
    }
    
    @Override
    public String toString()
    {
        String format = "NumericalAxis(name=\"%s\", %d values, unit=\"%s\")";
        return String.format(Locale.ENGLISH, format, this.name, this.values.length, this.unitName);
    }
}
