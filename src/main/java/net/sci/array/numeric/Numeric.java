/**
 * 
 */
package net.sci.array.numeric;

/**
 * Definitions of Numeric type, parent type for Scalar and Vector.
 * 
 * @param <N>
 *            The type of numeric
 * @see Scalar
 * @see Vector
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
     * Returns the result of the addition of this numeric with the other
     * numeric.
     * 
     * @param other
     *            the numeric to add.
     * @return the result of addition.
     */
    public N plus(N other);

    /**
     * Returns the result of the subtraction of another numeric from this
     * numeric.
     * 
     * @param other
     *            the numeric to subtract.
     * @return the result of subtraction.
     */
    public N minus(N other);
    
    /**
     * Returns the opposite of this value, i.e. the value symmetric to this
     * value with respect to zero.
     * 
     * Note that the result may be truncated according to the range of values
     * allowed by the data type. For example, the negative of an unsigned type
     * will result in value zero.
     * 
     * @return the opposite of this value.
     */
    public N opposite();

    /**
     * Returns the result of the multiplication of this numeric by a floating
     * point value.
     * 
     * @param k
     *            the numeric value to multiply by
     * @return the result of multiplication.
     */
    public N times(double k);

    /**
     * Returns the result of the division of this numeric by a floating point
     * value.
     * 
     * @param k
     *            the numeric value to divide by
     * @return the result of division.
     */
    public N dividedBy(double k);
}
