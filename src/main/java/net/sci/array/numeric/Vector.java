/**
 * 
 */
package net.sci.array.numeric;

/**
 * A vector of numeric values.
 * 
 * @param <V>
 *            the type of the vector
 * @param <S>
 *            the type of the elements contained by this vector
 *
 * @author dlegland
 */
public interface Vector<V extends Vector<V,S>, S extends Scalar<S>> extends Numeric<V>
{
    // =============================================================
    // Static methods
    
    /**
     * Computes the norm of the vector represented by given values
     * 
     * @param values
     *            the values of the vector
     * @return the norm of the vector
     */
    public static double norm(double[] values)
    {
        // compute norm of current vector
        double norm = 0;
        for (double d : values)
        {
            norm += d * d;
        }
        return Math.sqrt(norm);
    }
    
    /**
     * Computes the max norm, or infinity norm, of the vector represented by
     * given values.
     * 
     * @param values
     *            the values of the vector
     * @return the maximum of the absolute values of the elements in the vector.
     */
    public static double maxNorm(double[] values)
    {
        // compute max-norm of current vector
        double max = 0;
        for (double v : values)
        {
            max = Math.max(max, Math.abs(v));
        }
        return max;
    }
    
    
    // =============================================================
    // Interface methods
    
    /**
     * Returns the number of component of this vector.
     * 
     * @return the number of components of this vector. 
     */
    public int size();

    
    // =============================================================
    // Access to values
    
    /**
     * Returns the set of values that constitutes this vector.
     * 
     * @return the set of values that constitutes this vector.
     */
    public double[] getValues();

    /**
     * Returns the set of values that constitutes this vector.
     * 
     * @param values
     *            an array used to store the result
     * @return the set of values that constitutes this vector.
     */
    public double[] getValues(double[] values);

    /**
     * Returns the value at the specified index as a double.
     * 
     * @param i
     *            the index of element
     * @return the value at the specified index.
     */
    public double getValue(int i);

    /**
     * Returns the value at the specified index.
     * 
     * @param i
     *            the index of element
     * @return the value at the specified index.
     */
    public S get(int i);

}
