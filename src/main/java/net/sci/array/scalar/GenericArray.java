/**
 * 
 */
package net.sci.array.scalar;

import net.sci.array.Array;
import net.sci.array.generic.BufferedGenericArray2D;
import net.sci.array.generic.BufferedGenericArray3D;
import net.sci.array.generic.BufferedGenericArrayND;

/**
 * Interface for array implementation based on a generic data type.
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
        switch (dims.length)
        {
        case 2:
            return new BufferedGenericArray2D<T>(dims[0], dims[1], value);
        case 3:
            return new BufferedGenericArray3D<T>(dims[0], dims[1], dims[2], value);
        default:
            return new BufferedGenericArrayND<T>(dims, value);
        }
    }


    @Override
    public default GenericArray<T> newInstance(int... dims)
    {
        return GenericArray.create(dims, iterator().next());
    }
    
    @Override
    public default Array.Factory<T> getFactory()
    {
        return new Array.Factory<T>()
        {
            public GenericArray<T> create(int[] dims, T value)
            {
                return GenericArray.create(dims, value);
            }
        };
    }    
}
