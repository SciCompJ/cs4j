/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.scalar.Int;

/**
 * An array containing vectors of integers, use as basis for RGB8 and RGB16 color arrays.
 * 
 * @author dlegland
 *
 */
public interface IntVectorArray<V extends IntVector<? extends Int>> extends VectorArray<V>
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

    public IntVectorArray<V> duplicate();
    
    public interface Iterator<V extends IntVector<?>> extends VectorArray.Iterator<V>
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
