/**
 * 
 */
package net.sci.axis;

import java.util.Locale;

/**
 * An implementation of NumericalAxis where the values change regularly from an
 * origin, by incrementing with a constant value.
 * 
 * @author dlegland
 *
 */
public class LinearNumericalAxis implements NumericalAxis
{
    // =============================================================
    // Class variables
   
    /**
     * The name for this axis.
     */
    protected String name;

    /**
     * The spacing between two elements along this axis
     */
    protected double spacing;
    
    /**
     * The position of the first element (with index 0) on this axis.
     */
    protected double origin;
    
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
    public LinearNumericalAxis(String name)
    {
        this(name, 1.0, 0.0);
    }

    /**
     * Creates a new numerical axis
     * 
     * @param name
     *            the name of the numerical axis
     * @param spacing
     *            the spacing between each tick of the axis
     * @param origin
     *            the origin of the axis (position of the first tick)
     */
    public LinearNumericalAxis(String name, double spacing, double origin)
    {
        this.name = name;
        this.spacing = spacing;
        this.origin = origin;
    }

    /**
     * Constructor with initial value for all fields.
     * 
     * @param name
     *            the name of the axis
     * @param type
     *            the type of axis
     * @param spacing
     *            the spacing between two elements of the array
     * @param origin
     *            the position of the first element of the array
     * @param unitName
     *            the name of the unit
     */
    public LinearNumericalAxis(String name, double spacing, double origin, String unitName)
    {
        this.name = name;
        this.spacing = spacing;
        this.origin = origin;
        this.unitName = unitName;
    }
    

    // =============================================================
    // General methods

    @Override
    public double indexToValue(int index)
    {
        return index * this.spacing + this.origin;
    }
    
    /**
     * Returns the closest index that corresponds to the specified value.
     * 
     * Uses nearest rounding, do not clamp to 0
     * 
     * @param value
     *            a numerical value on the axis
     * @return the closest index that corresponds to this value
     */
    @Override
    public int valueToIndex(double value)
    {
        return (int) Math.round((value - this.origin) / this.spacing);
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
    
    public double getSpacing()
    {
        return spacing;
    }

    public void setSpacing(double spacing)
    {
        this.spacing = spacing;
    }
    
    public double getOrigin()
    {
        return origin;
    }

    public void setOrigin(double origin)
    {
        this.origin = origin;
    }
    
    
    // =============================================================
    // Methods overriding Object
    
    @Override
    public LinearNumericalAxis duplicate()
    {
        return new LinearNumericalAxis(this.name, this.spacing, this.origin, this.unitName);
    }
    
    @Override
    public String toString()
    {
        String format = "NumericalAxis(name=\"%s\", spacing=%.4g, origin=%.4g, unit=\"%s\")";
        return String.format(Locale.ENGLISH, format, this.name, this.spacing, this.origin, this.unitName);
    }
}
