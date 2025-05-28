/**
 * 
 */
package net.sci.axis;

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

    public double indexToValue(double index);
    
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
    
    
    // =============================================================
    // Methods overriding Object
    
    @Override
    public NumericalAxis duplicate();
}
