/**
 * 
 */
package net.sci.array.generic;

import net.sci.array.Array2D;

/**
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
        return GenericArray.create(dims, iterator().next());
    }

    public abstract T get(int x, int y);
    
    public abstract void set(int x, int y, T value);
    
}
