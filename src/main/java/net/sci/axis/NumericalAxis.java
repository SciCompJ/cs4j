/**
 * 
 */
package net.sci.axis;

import java.util.stream.IntStream;

/**
 * Numerical axis, for spatial, time, wave-length... axes. Numerical axes are
 * not necessarily "bounded", and may provide values for any index.
 * 
 * @see CategoricalAxis
 * @see LinearNumericalAxis
 * 
 * @author dlegland
 */
public interface NumericalAxis extends Axis
{
    // =============================================================
    // Static factories
    
    /**
     * Creates a new numerical axis from a finite number of numerical values.
     * Uses an empty name for the name of the unit associated to this axis.
     * 
     * @param name
     *            the name of the axis
     * @param values
     *            the array of numerical values within this axis
     * @returns a new instance of NumericalAxis
     */
    public static NumericalAxis create(String name, double[] values)
    {
        return create(name, values, "");
    }
    
    /**
     * Creates a new numerical axis from a finite number of numerical values,
     * and specifying the name of the unit associated to the axis.
     * 
     * @param name
     *            the name of the axis
     * @param values
     *            the array of numerical values within this axis
     * @param unitName
     *            the name of the unit associated to the axis
     * @returns a new instance of NumericalAxis
     */
    public static NumericalAxis create(String name, double[] values, String unitName)
    {
        return new GenericNumericalAxis(name, values, unitName);
    }
    
    /**
     * Creates a new numerical axis, specifying the spacing between two items,
     * the origin. Uses an empty name for the name of the unit associated to
     * this axis.
     * 
     * @param name
     *            the name of the axis
     * @param spacing
     *            the spacing between two elements of the array
     * @param origin
     *            the position of the first element of the array
     * @returns a new instance of NumericalAxis
     */
    public static NumericalAxis create(String name, double spacing, double origin)
    {
        return create(name, spacing, origin, "");
    }
    
    /**
     * Creates a new numerical axis, specifying the spacing between two items,
     * the origin, and the unit name associated to this axis.
     * 
     * @param name
     *            the name of the axis
     * @param spacing
     *            the spacing between two elements of the array
     * @param origin
     *            the position of the first element of the array
     * @param unitName
     *            the name of the unit associated to the axis
     * @returns a new instance of NumericalAxis
     */
    public static NumericalAxis create(String name, double spacing, double origin, String unitName)
    {
        return new LinearNumericalAxis(name, spacing, origin, unitName);
    }
    

    // =============================================================
    // General methods

    /**
     * Returns the numerical value corresponding to the specified index.
     * 
     * @param index
     *            the index within the axis
     * @return the numerical value corresponding to the index
     */
    public double getValue(int index);
    
    /**
     * Returns the closest index that corresponds to the specified value.
     * 
     * Uses nearest rounding, do not clamp to 0
     * 
     * @param value
     *            a numerical value on the axis
     * @return the closest index that corresponds to this value
     */
    public int valueIndex(double value);
    
    
    // =============================================================
    // Getters / setters

    /**
     * Returns the name of the unit associated to this axis.
     * 
     * @return the name of the unit associated to this axis
     */
    public String getUnitName();
    
    /**
     * Changes the name of the unit associated to this axis.
     * 
     * @param unitName
     *            the new name of the unit associated to this axis
     */
    public void setUnitName(String unitName);
    
    @Override
    public default NumericalAxis selectElements(int[] indices)
    {
        double[] newValues = IntStream.of(indices)
                .mapToDouble(index -> getValue(index))
                .toArray();
        return new GenericNumericalAxis(getName(), newValues, getUnitName());
    }
    
    // =============================================================
    // Methods overriding Axis
    
    @Override
    public default String itemName(int index)
    {
        return Double.toString(getValue(index));
    }
    

    // =============================================================
    // Methods overriding Object
    
    @Override
    public NumericalAxis duplicate();
}
