/**
 * 
 */
package net.sci.array.impl;

import net.sci.array.Array2D;

/**
 * Default implementation for a 2D array containing elements with an arbitrary
 * type.
 * 
 * @param <T> the type of elements stored within the array.
 * 
 * @author dlegland
 *
 */
public abstract class GenericArray2D<T> extends Array2D<T> implements GenericArray<T>
{
    // =============================================================
    // Static factory
    
    public static final <T> GenericArray2D<T> create(int size0, int size1, T initValue)
    {
        return new BufferedGenericArray2D<T>(size0, size1, initValue);
    }
    
    // =============================================================
    // Constructor

    protected GenericArray2D(int size0, int size1)
    {
        super(size0, size1);
    }

    @Override
    public GenericArray<T> newInstance(int... dims)
    {
        return GenericArray.create(dims, this.sampleElement());
    }
    
}
