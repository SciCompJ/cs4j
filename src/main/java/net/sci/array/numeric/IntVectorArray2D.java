/**
 * 
 */
package net.sci.array.numeric;

/**
 * Base implementation of the <code>IntVectorArray</code> interface for 2D arrays.
 * 
 * @param <V>
 *            the type of the vector contained within this array
 * @param <I>
 *            the type of the elements contained by the vector, that must be a subclass of Int
 *
 * @author dlegland
 *
 */
public abstract class IntVectorArray2D<V extends IntVector<V, I>, I extends Int<I>> extends VectorArray2D<V, I>
        implements IntVectorArray<V, I>
{
    // =============================================================
    // Constructors
    
    protected IntVectorArray2D(int size0, int size1)
    {
        super(size0, size1);
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
     * @return the array of integer values for the specified position
     */
    public abstract int[] getSamples(int x, int y);
    
    /**
     * Returns the values at a given location in the specified pre-allocated
     * array.
     * 
     * @param x
     *            the x-position of the vector
     * @param y
     *            the y-position of the vector
     * @param values
     *            the pre-allocated array for storing values
     * @return a reference to the pre-allocated array
     */
    public abstract int[] getSamples(int x, int y, int[] values);
    
    /**
     * Sets the values at a given location.
     * 
     * @param x
     *            the x-position of the vector
     * @param y
     *            the y-position of the vector
     * @param values
     *            the array of integer values to put into the array
     */
    public abstract void setSamples(int x, int y, int[] values);
    
    /**
     * Returns the integer value for the specified position and the specified
     * component.
     * 
     * @param x
     *            the x-position of the vector
     * @param y
     *            the y-position of the vector
     * @param c
     *            the component to investigate
     * @return the value of the given component at the given position
     */
    public abstract int getSample(int x, int y, int c);
    
    /**
     * Sets a value at a given location and channel.
     * 
     * @param x
     *            the x-position of the vector
     * @param y
     *            the y-position of the vector
     * @param c
     *            the channel of the element to set
     * @param intValue
     *            the integer value to put into the array
     */
    public abstract void setSample(int x, int y, int c, int intValue);
    
    
    // =============================================================
    // Specialization of IntVectorArray interface
    
    public int[] getSamples(int[] pos)
    {
        return getSamples(pos[0], pos[1]);
    }
    
    public int[] getSamples(int[] pos, int[] intValues)
    {
        return getSamples(pos[0], pos[1], intValues);
    }
    
    public void setSamples(int[] pos, int[] intValues)
    {
        setSamples(pos[0], pos[1], intValues);
    }
    
    public int getSample(int[] pos, int channel)
    {
        return getSample(pos[0], pos[1], channel);
    }
    
    public void setSample(int[] pos, int channel, int sample)
    {
        setSample(pos[0], pos[1], channel, sample);
    }
    
    
    // =============================================================
    // Specialization of VectorArray2D interface
    
    public double getValue(int x, int y, int c)
    {
        return getSample(x, y, c);
    }
    
    public void setValue(int x, int y, int c, double value)
    {
        setSample(x, y, c, (int) value);
    }
    
    
    // =============================================================
    // Specialization of Array interface
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sci.array.data.VectorArray#duplicate()
     */
    @Override
    public abstract IntVectorArray2D<V,I> duplicate();
    
    public interface Iterator<V extends IntVector<V,I>, I extends Int<I>> extends IntVectorArray.Iterator<V,I>
    {
    }
}
