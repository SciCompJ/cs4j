/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.scalar.Int;

/**
 * A vector containing only integer values.
 *
 * @see net.sci.array.color.RGB8
 * @see net.sci.array.color.RGB16
 * 
 * @param <V>
 *            the type of the vector
 * @param <I>
 *            the type of the elements contained by this vector, that must be a subclass of Int
 *
 * @author dlegland
 */
public abstract class IntVector<V extends Vector<V, I>, I extends Int<I>> extends Vector<V, I>
{
    /**
     * Returns the sample values into an integer array.
     * 
     * @return the reference to the sample array
     */
    public abstract int[] getSamples();
    
    /**
     * Returns the sample values into the pre-allocated array of integers.
     * 
     * @param intArray
     *            a preallocated array with as many elements as the vector length
     * @return the reference to the RGB array
     */
    public abstract int[] getSamples(int[] intArray);
    
    public abstract int getSample(int channel);

}
