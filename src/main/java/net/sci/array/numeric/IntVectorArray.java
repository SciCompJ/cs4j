/**
 * 
 */
package net.sci.array.numeric;

/**
 * A specialization of the {@code VectorArray} interface, where vector elements
 * implement the {@code Int} interface. This interface is used as basis for RGB8
 * and RGB16 color arrays.
 * 
 * @param <V>
 *            the type of the vector contained within this array
 * @param <I>
 *            the type of the elements contained by the vector, that must be a
 *            subclass of Int
 *           
 * @author dlegland
 *           
 */
public interface IntVectorArray<V extends IntVector<V, I>, I extends Int<I>> extends VectorArray<V,I>
{
    // =============================================================
    // New abstract methods

    /**
     * Retrieves the vector values at the specified position, returning the
     * result as an array of int.
     * 
     * @param pos
     *            the position
     * @return an array of integer values
     */
    public int[] getSamples(int[] pos);

    /**
     * Retrieves the vector values at the specified position, using a
     * pre-allocated int array to store the result.
     * 
     * @param pos
     *            the position
     * @param intValues
     *            the pre-allocated int array used to store values
     * @return an array of integer values
     */
    public int[] getSamples(int[] pos, int[] intValues);

    /**
     * Updates the vector values at the specified position, using the specified
     * array of int values.
     * 
     * @param pos
     *            the position
     * @param intValues
     *            the integer array of new values
     */
    public void setSamples(int[] pos, int[] intValues);

    /**
     * Retrieves the sample values at the specified position and channel index,
     * returning the result as an integer value.
     * 
     * @param pos
     *            the position
     * @param channel
     *            the channel index
     * @return the integer value in the specified position and channel index
     */
    public int getSample(int[] pos, int channel);

    /**
     * Updates the sample values at the specified position and channel index.
     * 
     * @param pos
     *            the position
     * @param channel
     *            the channel index
     * @param intValues
     *            the new values at the specified position and channel index
     */
    public void setSample(int[] pos, int channel, int intValues);

    
    // =============================================================
    // Specialization of VectorArray interface


    // =============================================================
    // Specialization of Array interface

    @Override
    public IntVectorArray<V,I> duplicate();
    
    /**
     * A specialization of the {@code Array.Iterator} interface to iterate over
     * the elements of this array.
     * 
     * @param <V>
     *            the type of the vector contained within this array
     * @param <I>
     *            the type of the elements contained by the vector, that must be
     *            a subclass of Int
     */
    public interface Iterator<V extends IntVector<V,I>, I extends Int<I>> extends VectorArray.Iterator<V,I>
    {
        /**
         * Retrieves the sample value for the specified channel.
         * 
         * @param c
         *            the channel index
         * @return the value for the specified channel
         */
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
