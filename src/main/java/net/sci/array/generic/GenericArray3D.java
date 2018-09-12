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
    
    public static final <T> GenericArray3D<T> create(int size0, int size1, int size2, T initValue)
    {
        return new BufferedGenericArray3D<T>(size0, size1, size2, initValue);
    }
    
    // =============================================================
    // Constructor

    protected GenericArray3D(int size0, int size1, int size2)
    {
        super(size0, size1, size2);
    }

    // =============================================================
    // Implementation of Array3D interface

    /* (non-Javadoc)
     * @see net.sci.array.data.Array3D#getValue(int, int, int)
     */
    @Override
    public double getValue(int x, int y, int z)
    {
        throw new RuntimeException("Unimplemented operation");
    }

    /* (non-Javadoc)
     * @see net.sci.array.data.Array3D#setValue(int, int, int, double)
     */
    @Override
    public void setValue(int x, int y, int z, double value)
    {
        throw new RuntimeException("Unimplemented operation");
    }


}
