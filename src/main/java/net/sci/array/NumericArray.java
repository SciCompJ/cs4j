/**
 * 
 */
package net.sci.array;

/**
 * Specialization of Array interface that supports elementary arithmetic operations (plus, minus...).
 * 
 * Main sub-interfaces are ScalarArray and VectorArray.
 * 
 * @author dlegland
 */
public interface NumericArray<N> extends Array<N>
{
    // =============================================================
    // Default methods for arithmetic on arrays
    

    public NumericArray<N> plus(double v);

    public NumericArray<N> minus(double v);

    public NumericArray<N> times(double k);

    public NumericArray<N> divideBy(double k);

    
    // =============================================================
    // Specialization of the Array interface

    @Override
    public NumericArray<N> newInstance(int... dims);

    @Override
    public NumericArray<N> duplicate();
}
