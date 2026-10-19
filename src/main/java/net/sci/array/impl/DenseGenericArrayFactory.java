/**
 * 
 */
package net.sci.array.impl;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;

/**
 * A factory for arrays of any type <T> that generates dense representation of
 * arrays. The required memory is proportional to the number of elements within
 * the array.
 * 
 * @param <T> the type of elements stored within the array.
 */
public class DenseGenericArrayFactory<T> extends AlgoStub implements Array.Factory<T>
{
    /**
     * Default empty constructor.
     */
    public DenseGenericArrayFactory()
    {
    }
    
    @Override
    public GenericArray<T> create(int[] dims, T value)
    {
        return switch (dims.length)
        {
            case 1 -> new BufferedGenericArray1D<T>(dims[0], value);
            case 2 -> new BufferedGenericArray2D<T>(dims[0], dims[1], value);
            case 3 -> new BufferedGenericArray3D<T>(dims[0], dims[1], dims[2], value);
            default -> new BufferedGenericArrayND<T>(dims, value);
        };
    }
}
