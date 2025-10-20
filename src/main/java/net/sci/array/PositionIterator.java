/**
 * 
 */
package net.sci.array;

import net.sci.array.impl.DefaultPositionIterator;

/**
 * Iterator over the element positions within a multi-dimensional array. Can be
 * used to design operators based on the neighborhood of each element.
 * 
 */
public interface PositionIterator extends java.util.Iterator<int[]>
{
    /**
     * Creates a default position iterator for the specified array.
     * 
     * @param array
     *            a multi-dimensional array
     * @return an iterator over the positions within the specified array
     */
    public static PositionIterator of(Array<?> array)
    {
        return new DefaultPositionIterator(array.size());
    }
    
    /**
     * Moves this iterator to the next position.
     */
    public void forward();
    
    /**
     * Returns the current position.
     * 
     * @return the current position.
     */
    public int[] get();
    
    /**
     * Returns a specific coordinate from the current position.
     * 
     * @param dim
     *            the dimension, between 0 and dimensionality - 1
     * @return the specified coordinate
     */
    public int get(int dim);
    
    /**
     * Returns the current position in a pre-allocated array.
     * 
     * @param pos
     *            the pre-allocated array for storing current position
     * @return the current position
     */
    public int[] get(int[] pos);
}
