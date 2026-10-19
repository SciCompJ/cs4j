/**
 * 
 */
package net.sci.array.impl;

import net.sci.array.Array;

/**
 * Interface for array implementation based on a generic data type.
 * 
 * @param <T> the type of elements stored within the array.
 * 
 * @author dlegland
 *
 */
public interface GenericArray<T> extends Array<T>
{
    // =============================================================
    // Static factories

    /**
     * Creates a new array with the specified size, using the sample element to
     * initialize array.
     * 
     * @param <T>
     *            the type of elements within array
     * @param dims
     *            the size of the array to create
     * @param initValue
     *            the value used to initialize the array
     * @return the new array
     */
    public static <T> GenericArray<T> create(int[] dims, T initValue)
    {
        return new DenseGenericArrayFactory<T>().create(dims, initValue);
    }

    public static <T> Array.Factory<T> defaultFactory()
    {
        return new DenseGenericArrayFactory<T>();
    }
    

    // =============================================================
    // Specialization of Array interface

    @Override
    public default GenericArray<T> newInstance(int... dims)
    {
        return GenericArray.create(dims, sampleElement());
    }
    
    @Override
    public default GenericArray.Factory<T> factory()
    {
        return new DenseGenericArrayFactory<T>();
    }
}
