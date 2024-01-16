/**
 * 
 */
package net.sci.array.numeric;

/**
 * Definitions of Numeric type, parent types for Scalar and Vector.
 *  
 */
public interface Numeric<N extends Numeric<N>>
{
    /**
     * Returns the numeric instance that corresponds to the unity.
     * 
     * @return the numeric instance that corresponds to one.
     */
    public N one();

    /**
     * Returns the numeric instance that corresponds to zero (does not modify a
     * numeric value when added)
     * 
     * @return the numeric instance that corresponds to zero.
     */
    public N zero();

    /**
     * Adds another numeric to this numeric, and returns the result of addition.
     * 
     * @param other
     *            the numeric to add.
     * @return the result of addition.
     */
    public N plus(N other);

    /**
     * Subtracts another numeric from this numeric, and returns the result of subtraction.
     * 
     * @param other
     *            the numeric to subtract.
     * @return the result of subtraction.
     */
    public N minus(N other);

    /**
     * Multiplies this numeric by a scalar constant, and returns the result of
     * multiplication.
     * 
     * @param k
     *            the scaling factor
     * @return the result of multiplication.
     */
    public N times(double k);

    /**
     * Divides this numeric by a scalar constant, and returns the result of
     * division.
     * 
     * @param k
     *            the scaling factor
     * @return the result of division.
     */
    public N divideBy(double k);
}
