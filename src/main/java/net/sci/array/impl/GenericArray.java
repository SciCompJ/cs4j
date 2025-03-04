/**
 * 
 */
package net.sci.array.impl;

import net.sci.algo.AlgoStub;
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
    // Specialization of Array interface

    public static <T> GenericArray<T> create(int[] dims, T value)
    {
        return switch (dims.length)
        {
            case 2 -> new BufferedGenericArray2D<T>(dims[0], dims[1], value);
            case 3 -> new BufferedGenericArray3D<T>(dims[0], dims[1], dims[2], value);
            default -> new BufferedGenericArrayND<T>(dims, value);
        };
    }


    @Override
    public default GenericArray<T> newInstance(int... dims)
    {
        return GenericArray.create(dims, iterator().next());
    }
    
    @Override
    public default Array.Factory<T> factory()
    {
        return new DefaultFactory<T>();
    }
    
    /**
     * The default factory for generation of GenericArray instances containing
     * values with type T.
     * 
     * @param <T> the type of elements stored within the array.
     */
    public static class DefaultFactory<T> extends AlgoStub implements Array.Factory<T>
    {
        @Override
        public GenericArray<T> create(int[] dims, T value)
        {
            return switch (dims.length)
            {
                case 2 -> new BufferedGenericArray2D<T>(dims[0], dims[1], value);
                case 3 -> new BufferedGenericArray3D<T>(dims[0], dims[1], dims[2], value);
                default -> new BufferedGenericArrayND<T>(dims, value);
            };
        }
    };
}
