/**
 * 
 */
package net.sci.array.generic;

import net.sci.array.Array3D;

/**
 * @author dlegland
 *
 */
public abstract class GenericArray3D<T> extends Array3D<T> implements GenericArray<T>
{
    // =============================================================
    // Static factory
    
//    public static final <T> GenericArray3D<T> create(int size0, int size1, int size2, T initValue)
//    {
//        return new BufferedGenericArray3D<T>(size0, size1, size2, initValue);
//    }
    
    // =============================================================
    // Constructor

    protected GenericArray3D(int size0, int size1, int size2)
    {
        super(size0, size1, size2);
    }

    @Override
    public GenericArray<T> newInstance(int... dims)
    {
        return GenericArray.create(dims, iterator().next());
    }
}
