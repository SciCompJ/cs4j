/**
 * 
 */
package net.sci.array.numeric;

import net.sci.array.Array;

/**
 * Specialization of Array interface that supports elementary arithmetic
 * operations (plus, minus...).
 * 
 * Main sub-interfaces are ScalarArray and VectorArray.
 * 
 * @author dlegland
 */
public interface NumericArray<N extends Numeric<N>> extends Array<N>
{
    // =============================================================
    // Default methods for arithmetic on arrays
    
    /**
     * Adds a numeric value to each element of this numeric array, and returns
     * the result array.
     * 
     * @param v
     *            the value to add
     * @return a new array with the value added
     */
    public default NumericArray<N> plus(N v)
    {
        NumericArray<N> res = this.newInstance(this.size());
        res.fill(pos -> this.get(pos).plus(v));
        return res;
    }

    /**
     * Subtracts a numeric value from each element of this numeric array, and
     * returns the result array.
     * 
     * @param v
     *            the value to subtract
     * @return a new array with the value subtracted
     */
    public default NumericArray<N> minus(N v)
    {
        NumericArray<N> res = this.newInstance(this.size());
        res.fill(pos -> this.get(pos).minus(v));
        return res;        
    }

    /**
     * Applies a multiplication with a scalar value to each element of this
     * numeric array, and returns the result array.
     * 
     * @param k
     *            the factor to multiply by
     * @return a new array with the elements multiplied
     */
    public default NumericArray<N> times(double k)
    {
        NumericArray<N> res = this.newInstance(this.size());
        res.fill(pos -> this.get(pos).times(k));
        return res;        
    }
    
    /**
     * Applies a division by a scalar value to each element of this numeric
     * array, and returns the result array.
     * 
     * @param k
     *            the factor to divide by
     * @return a new array with the elements divided
     */
    public default NumericArray<N> divideBy(double k)
    {
        NumericArray<N> res = this.newInstance(this.size());
        res.fill(pos -> this.get(pos).divideBy(k));
        return res;        
    }


    
    // =============================================================
    // Specialization of the Array interface

    @Override
    public NumericArray<N> newInstance(int... dims);

    @Override
    public NumericArray<N> duplicate();
}
