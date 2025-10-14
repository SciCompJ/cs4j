/**
 * 
 */
package net.sci.array.impl;

import java.util.Collection;
import java.util.List;

import net.sci.array.Array;

/**
 * A skeleton implementation for arrays that wrap another array, keeping size
 * and dimensionality of the inner array.
 * 
 * The type of the two arrays may differ.
 * 
 * @param <T> the type of elements stored within the array.
 */
public abstract class ArrayWrapperStub<T> implements Array.View<T>
{
    /**
     * The array this wrapper is based on.
     */
    protected Array<?> array;
    
    protected ArrayWrapperStub(Array<?> array)
    {
        this.array = array;
    }

    @Override
    public Collection<Array<?>> parentArrays()
    {
        return List.of(array);
    }
    
    /**
     * Override the default implementation to return an iterator of positions
     * within the wrapped array.
     */
    @Override
    public Iterable<int[]> positions()
    {
        return array.positions();
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
