/**
 * 
 */
package net.sci.array.numeric;

/**
 * Base implementation of the <code>IntVectorArray</code> interface for 3D arrays.
 * 
 * @param <V>
 *            the type of the vector contained within this array
 * @param <I>
 *            the type of the elements contained by the vector, that must be a subclass of Int
 *
 * @author dlegland
 *
 */
public abstract class IntVectorArray3D<V extends IntVector<V,I>, I extends Int<I>> extends VectorArray3D<V,I> implements IntVectorArray<V,I>
{
    // =============================================================
    // Constructors
    
    protected IntVectorArray3D(int size0, int size1, int size2)
    {
        super(size0, size1, size2);
    }
    
    
    // =============================================================
    // New abstract methods
    
    /**
     * Returns the values at a given location.
     * 
     * @param x
     *            the x-position of the vector
     * @param y
     *            the y-position of the vector
     * @param z
     *            the z-position of the vector
     * @return the array of integer values for the specified position
     */
    public abstract int[] getSamples(int x, int y, int z);
    
    /**
     * Returns the values at a given location in the specified pre-allocated
     * array.
     * 
     * @param x
     *            the x-position of the vector
     * @param y
     *            the y-position of the vector
     * @param z
     *            the z-position of the vector
     * @param values
     *            the pre-allocated array for storing values
     * @return a reference to the pre-allocated array
     */
    public abstract int[] getSamples(int x, int y, int z, int[] values);
    
    /**
     * Sets the values at a given location.
     * 
     * @param x
     *            the x-position of the vector
     * @param y
     *            the y-position of the vector
     * @param z
     *            the z-position of the vector
     * @param values
     *            the array of integer values to put into the array
     */
    public abstract void setSamples(int x, int y, int z, int [] values);
    
    /**
     * Returns the integer value for the specified position and the specified
     * component.
     * 
     * @param x
     *            the x-position of the vector
     * @param y
     *            the y-position of the vector
     * @param z
     *            the z-position of the vector
     * @param c
     *            the component to investigate
     * @return the value of the given component at the given position
     */
    public abstract int getSample(int x, int y, int z, int c);
    
    /**
     * Sets a value at a given location and channel.
     * 
     * @param x
     *            the x-position of the vector
     * @param y
     *            the y-position of the vector
     * @param z
     *            the z-position of the vector
     * @param c
     *            the channel of the element to set
     * @param intValue
     *            the integer value to put into the array
     */
    public abstract void setSample(int x, int y, int z, int c, int intValue);
    
    
    // =============================================================
    // Specialization of VectorArray3D interface
    
    /**
     * Returns a view over the specified slice.
     * 
     * @param sliceIndex
     *            the index of the slice
     * @return a view on the specific slice, as a 2D array
     */
    public abstract IntVectorArray2D<V,I> slice(int sliceIndex);

    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public abstract Iterable<? extends IntVectorArray2D<V,I>> slices();

    /**
     * Creates an iterator over the slices
     * 
     * @return an iterator over 2D slices
     */
    public abstract java.util.Iterator<? extends IntVectorArray2D<V,I>> sliceIterator();

    
    public double getValue(int x, int y, int z, int c)
    {
        return getSample(x, y, z, c);
    }
    
    public void setValue(int x, int y, int z, int c, double value)
    {
        setSample(x, y, z, c, (int) value);
    }


    // =============================================================
    // Specialization of IntVectorArray interface

	@Override
    public int[] getSamples(int[] pos)
    {
        return getSamples(pos[0], pos[1], pos[2]);
    }
    
    @Override
    public int[] getSamples(int[] pos, int[] intValues)
    {
        return getSamples(pos[0], pos[1], pos[2], intValues);
    }

    @Override
    public void setSamples(int[] pos, int[] intValues)
    {
        setSamples(pos[0], pos[1], pos[2], intValues);
    }
    
    @Override
    public int getSample(int[] pos, int channel)
    {
        return getSample(pos[0], pos[1], pos[2], channel);
    }
    
    @Override
    public void setSample(int[] pos, int channel, int intValues)
    {
        setSample(pos[0], pos[1], pos[2], channel, intValues);
    }
    
    
    // =============================================================
    // Specialization of Array interface
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sci.array.data.VectorArray#duplicate()
     */
    @Override
    public abstract IntVectorArray3D<V, I> duplicate();
    
    public interface Iterator<V extends IntVector<V,I>, I extends Int<I>> extends IntVectorArray.Iterator<V,I>
    {
    }
}
