/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.scalar.Int;

/**
 * An array containing vectors of integers, use as basis for RGB8 and RGB16
 * color arrays.
 * 
 * @param <V>
 *            the type of the vector contained within this array
 * @param <I>
 *            the type of the elements contained by the vector, that must be a subclass of Int
 *
 * @author dlegland
 *
 */
public interface IntVectorArray<V extends IntVector<V, I>, I extends Int<I>> extends VectorArray<V,I>
{
    // =============================================================
    // New abstract methods

    public int[] getSamples(int[] pos);

    public int[] getSamples(int[] pos, int[] intValues);

    public void setSamples(int[] pos, int[] intValues);

    public int getSample(int[] pos, int channel);

    public void setSample(int[] pos, int channel, int intValues);

    
    // =============================================================
    // Specialization of VectorArray interface


    // =============================================================
    // Specialization of Array interface

    public IntVectorArray<V,I> duplicate();
    
    public interface Iterator<V extends IntVector<V,I>, I extends Int<I>> extends VectorArray.Iterator<V,I>
    {
        public default int getSample(int c)
        {
            return get().getSample(c);
        }

        @Override
        public default double getValue(int c)
        {
            return get().getValue(c);
        }
    }
}
