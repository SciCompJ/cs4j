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
    // General methods

    /**
     * Returns the numerical value corresponding to the specified index.
     * 
     * @param index
     *            the index within the axis
     * @return the numerical value corresponding to the index
     */
    public double indexToValue(int index);
    
    /**
     * Returns the closest index that corresponds to the specified value.
     * 
     * Uses nearest rounding, do not clamp to 0
     * 
     * @param value
     *            a numerical value on the axis
     * @return the closest index that corresponds to this value
     */
    public int valueToIndex(double value);
    
    
    // =============================================================
    // Getters / setters

    public String getUnitName();
    
    public void setUnitName(String unitName);
    
    @Override
    public default NumericalAxis selectElements(int[] indices)
    {
        double[] newValues = IntStream.of(indices)
                .mapToDouble(index -> indexToValue(index))
                .toArray();
        return new GenericNumericalAxis(getName(), newValues, getUnitName());
    }

    // =============================================================
    // Methods overriding Object
    
    @Override
    public NumericalAxis duplicate();
}
