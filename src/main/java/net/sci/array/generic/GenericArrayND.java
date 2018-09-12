/**
 * 
 */
package net.sci.array.generic;

import net.sci.array.ArrayND;

/**
 * @author dlegland
 *
 */
public abstract class GenericArrayND<T> extends ArrayND<T> implements GenericArray<T>
{
    // =============================================================
    // Static factory
    
    public static final <T> GenericArrayND<T> create(int[] sizes, T initValue)
    {
        return new BufferedGenericArrayND<T>(sizes, initValue);
    }
    
    // =============================================================
    // Constructor

    protected GenericArrayND(int[] sizes)
    {
        super(sizes);
    }

    // =============================================================
    // Implementation of ArrayND interface

    /* (non-Javadoc)
     * @see net.sci.array.data.ArrayND#getValue(int[])
     */
    @Override
    public double getValue(int[] pos)
    {
        throw new RuntimeException("Unimplemented operation");
    }

    /* (non-Javadoc)
     * @see net.sci.array.data.ArrayND#setValue(int[], double)
     */
    @Override
    public void setValue(int[] pos, double value)
    {
        throw new RuntimeException("Unimplemented operation");
    }

}
