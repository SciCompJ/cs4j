/**
 * 
 */
package net.sci.array.complex;

import net.sci.array.Array;

/**
 * @author dlegland
 *
 */
public interface ComplexArray<C extends Complex<C>> extends Array<C>
{
    // =============================================================
    // New methods
    
    /**
     * Changes the real and imaginary parts at the specified position.
     * 
     * @param pos
     *            the position
     * @param real
     *            the real part of the new complex value
     * @param imag
     *            the imaginary part of the new complex value
     */
    public void setValues(int[] pos, double real, double imag);

    /**
     * Returns the value at the specified position as an array of two double
     * values, containing the real and imaginary parts of the complex number.
     * 
     * @param pos
     *            the query position
     * @return the real and imaginary parts of the complex value at specified
     *         position
     */
    public double[] getValues(int[] pos);
    
    
    // =============================================================
    // Specialization of the Array interface

    @Override
    public ComplexArray<C> newInstance(int... dims);

    @Override
    public ComplexArray<C> duplicate();
    
    @Override
    public ComplexArray.Factory<C> factory();

    public ComplexArray.Iterator<C> iterator();
    
    
    // =============================================================
    // Specialization of the Factory interface

    public interface Factory<C extends Complex<C>> extends Array.Factory<C>
    {
        /**
         * Creates a new complex array of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new complex array initialized with zeros
         */
        public ComplexArray<C> create(int[] dims);

        /**
         * Creates a new complex array with the specified dimensions, filled with
         * the specified initial value.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param value
         *            an instance of the initial value
         * @return a new instance of ComplexArray
         */
        public ComplexArray<C> create(int[] dims, C value);
    }
    
    
    // =============================================================
    // Inner interface

    public interface Iterator<C extends Complex<C>> extends Array.Iterator<C>
    {
        public void setValue(double real, double imag);
    }
}
