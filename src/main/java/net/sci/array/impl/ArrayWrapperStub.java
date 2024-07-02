/**
 * 
 */
package net.sci.array.impl;

import net.sci.array.Array;

/**
 * A skeleton implementation for arrays that wrap another array, keeping size
 * and dimensionality of the inner array.
 * 
 * The type of the two arrays may differ.
 */
public abstract class ArrayWrapperStub<T> implements Array<T>
{
    protected Array<?> array;
    
    protected ArrayWrapperStub(Array<?> array)
    {
        this.array = array;
    }

    /**
     * Override the default implementation to return position iterator of
     * wrapped array.
     */
    @Override
    public PositionIterator positionIterator()
    {
        return array.positionIterator();
    }

    @Override
    public int[] size()
    {
        return array.size();
    }

    @Override
    public int size(int dim)
    {
        return array.size(dim);
    }
    
    @Override
    public int dimensionality()
    {
        return array.dimensionality();
    }
}
