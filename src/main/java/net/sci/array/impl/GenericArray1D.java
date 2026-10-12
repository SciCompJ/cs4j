/**
 * 
 */
package net.sci.array.impl;

import net.sci.array.Array1D;

/**
 * Default implementation for a 1D array containing elements with an arbitrary
 * type.
 * 
 * @param <T> the type of elements stored within the array.
 * 
 * @author dlegland
 *
 */
public abstract class GenericArray1D<T> extends Array1D<T> implements GenericArray<T>
{
    // =============================================================
    // Static factory
    
    /**
     * Creates a new array with the specified size, using the sample element to
     * initialize array.
     * 
     * @param <T>
     *            the type of elements within array
     * @param size0
     *            the size of the array to create in the first direction
     * @param initValue
     *            the value used to initialize the array
     * @return the new array
     */
    public static final <T> GenericArray1D<T> create(int size0, T initValue)
    {
        return new BufferedGenericArray1D<T>(size0, initValue);
    }
    
    // =============================================================
    // Constructor

    protected GenericArray1D(int size0)
    {
        super(size0);
    }

    @Override
    public GenericArray<T> newInstance(int... dims)
    {
        return GenericArray.create(dims, this.sampleElement());
    }
}
