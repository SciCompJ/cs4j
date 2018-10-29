/**
 * 
 */
package net.sci.image;

import java.util.Locale;

/**
 * Numeric image axis, for spatial, time, wave-length... axes.
 * 
 * @author dlegland
 *
 */
public class NumericalAxis implements ImageAxis
{
    // =============================================================
    // Class variables
   
    /**
     * The name for this axis.
     */
    String name;

    Type type;
    
    double spacing;
    
    double origin;
    
    /**
     * The unit name associated to this axis.
     */
    String unitName;


    // =============================================================
    // Constructors
    
    /**
     * Creates a new numerical axis
     * 
     * @param name the name of the numerical axis
     */
    public NumericalAxis(String name)
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
    public NumericalAxis(String name, double spacing, double origin)
    {
        this(name, Type.UNKNOWN, spacing, origin, "");
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
    public NumericalAxis(String name, ImageAxis.Type type, double spacing, double origin, String unitName)
    {
        this.name = name;
        this.type = type;
        this.spacing = spacing;
        this.origin = origin;
        this.unitName = unitName;
    }

    // =============================================================
    // General methods

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
    public int valueToIndex(double value)
    {
        return (int) Math.round((value - this.origin) / this.spacing);
    }
    
    
    // =============================================================
    // Getters / setters

    public Type getType()
    {
        return this.type;
    }
    
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
    // Getters / setters
    
    @Override
    public String toString()
    {
        String format = "NumericalAxis(name=%s, type=%s, spacing=%.4g, origin=%.4g, unit=%s)";
        return String.format(Locale.ENGLISH, format, this.name, this.type, this.spacing, this.origin, this.unitName);
    }
}
