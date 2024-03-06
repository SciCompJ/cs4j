/**
 * 
 */
package net.sci.array.scalar;

import net.sci.array.numeric.Numeric;

/**
 * Top-level definition of scalar value. Can represent any double or integer
 * value.
 * 
 * @param <S>
 *            the type of Scalar.
 *
 * @author dlegland
 */
public abstract class Scalar<S extends Scalar<S>> implements Numeric<S>
{
    /**
     * Returns the smallest value (closest or equal to negative infinity) that
     * can be represented with this type.
     * 
     * @return the smallest value that can be represented with this type
     */
    public abstract S typeMin();
    
    /**
     * Returns the largest value (closest or equal to positive infinity) that
     * can be represented with this type.
     * 
     * @return the largest value that can be represented with this type
     */
    public abstract S typeMax();
    
    /**
     * Creates a new Scalar from the specified double value.
     * 
     * @param v
     *            the value
     * @return the Scalar corresponding to the input value.
     */
    public abstract S fromValue(double v);
    
    // methods that depend on the current value
    
    /**
     * Returns the value of this scalar element as a double.
     * 
     * @return the value as a double
     */
    public abstract double getValue();
}
