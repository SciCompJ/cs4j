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
        return switch (dims.length)
        {
            case 2 -> new BufferedGenericArray2D<T>(dims[0], dims[1], initValue);
            case 3 -> new BufferedGenericArray3D<T>(dims[0], dims[1], dims[2], initValue);
            default -> new BufferedGenericArrayND<T>(dims, initValue);
        };
    }


    // =============================================================
    // Specialization of Array interface

    @Override
    public default GenericArray<T> newInstance(int... dims)
    {
        return GenericArray.create(dims, sampleElement());
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
        private DefaultFactory() {};
        
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
