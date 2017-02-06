/**
 * 
 */
package net.sci.array.data.vector;

import net.sci.array.data.Array2D;
import net.sci.array.data.Float32Array;
import net.sci.array.data.VectorArray;
import net.sci.array.data.scalar2d.Float32Array2D;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.type.Vector;

/**
 * @author dlegland
 *
 */
public abstract class VectorArray2D<V extends Vector<?>> extends Array2D<V> implements VectorArray<V>
{
    // =============================================================
    // Static methods

    /**
     * Computes the norm of each element of the given vector array and returns
     * an instance of ScalarArray2D.
     * 
     * Current implementation returns the result in a new instance of
     * Float32Array.
     * 
     * @param array
     *            a vector array
     * @return a scalar array with the same size at the input array
     * @deprecated use instance method instead
     */
    @Deprecated
    public static ScalarArray2D<?> norm(VectorArray2D<? extends Vector<?>> array)
    {
        // allocate memory for result
        Float32Array2D result = Float32Array2D.create(array.getSize(0), array.getSize(1));
        
        // create array iterators
        VectorArray.Iterator<? extends Vector<?>> iter1 = array.iterator();
        Float32Array.Iterator iter2 = result.iterator();
        
        // iterate over both arrays in parallel
        while (iter1.hasNext() && iter2.hasNext())
        {
            // get current vector
            double[] values = iter1.next().getValues();
            
            // compute norm of current vector
            double norm = 0;
            for (double d : values)
            {
                norm += d * d;
            }
            norm = Math.sqrt(norm);
            
            // allocate result
            iter2.forward();
            iter2.setValue(norm);
        }
        
        return result;
    }
    
    
	// =============================================================
	// Constructors

	protected VectorArray2D(int size0, int size1)
	{
		super(size0, size1);
	}
	
	
    // =============================================================
    // New methods

   /**
     * Computes the norm of each element of the given vector array.
     * 
     * Current implementation returns the result in a new instance of
     * Float32Array.
     * 
     * @param array
     *            a vector array
     * @return a scalar array with the same size at the input array
     */
    public ScalarArray2D<?> norm()
    {
        // allocate memory for result
        Float32Array2D result = Float32Array2D.create(getSize(0), getSize(1));
        
        // create array iterators
        VectorArray.Iterator<? extends Vector<?>> iter1 = iterator();
        Float32Array.Iterator iter2 = result.iterator();
        
        // iterate over both arrays in parallel
        double[] values = new double[getVectorLength()]; 
        while (iter1.hasNext() && iter2.hasNext())
        {
            // get current vector
            iter1.forward();
            iter1.getValues(values);
            
            // compute norm of current vector
            double norm = 0;
            for (double d : values)
            {
                norm += d * d;
            }
            norm = Math.sqrt(norm);
            
            // allocate result
            iter2.forward();
            iter2.setValue(norm);
        }
        
        return result;
    }
    
    /**
     * Returns a new ScalarArray corresponding to the specified channel.
     * 
     * @param channelIndex
     *            the index of the channel, between 0 and nChannels-1
     * @return a new scalar array.
     */
    public ScalarArray2D<?> channel(int channelIndex)
    {
        // allocate memory for result
        Float32Array2D result = Float32Array2D.create(getSize(0), getSize(1));
        
        // create array iterators
        VectorArray.Iterator<? extends Vector<?>> iter1 = iterator();
        Float32Array.Iterator iter2 = result.iterator();
        
        // iterate over both arrays in parallel
        while (iter1.hasNext() && iter2.hasNext())
        {
            // iterate
            iter1.forward();
            iter2.forward();
            
            iter2.setValue(iter1.getValue(channelIndex));
        }
        
        return result;
    }
    

    // =============================================================
	// New abstract methods

    public abstract double[] getValues(int x, int y);
    
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
    public abstract double[] getValues(int x, int y, double[] values);
    
	public abstract void setValues(int x, int y, double[] values);
	
	/**
	 * Returns the scalar value for the specified position and the specified
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
	public abstract double getValue(int x, int y, int c);
	
	public abstract void setValue(int x, int y, int c, double value);


	// =============================================================
	// Specialization of VectorArray interface
	
	public double[] getValues(int[] pos)
	{
		return getValues(pos[0], pos[1]);
	}
	
	public void setValues(int[] pos, double[] values)
	{
		setValues(pos[0], pos[1], values);
	}
	

	// =============================================================
	// Specialization of Array interface

	/**
	 * Returns the norm of the vector at the given position.
	 * 
	 * @see net.sci.array.data.Array2D#getValue(int, int)
	 */
	@Override
	public double getValue(int x, int y)
	{
		double[] values = getValues(x, y);
		double sum = 0;
		for (double v : values)
		{
			sum += v * v;
		}
		return Math.sqrt(sum);
	}

	/**
	 * Changes the value of the vector at given position, by setting the first
	 * component and clearing the others.
	 * 
	 * @see net.sci.array.data.Array2D#setValue(int, int, double)
	 */
	@Override
	public void setValue(int x, int y, double value)
	{
		setValue(x, y, 0, value);
		for (int c = 1; c < this.getVectorLength(); c++)
		{
			setValue(x, y, c, 0);
		}
	}


	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#duplicate()
	 */
	@Override
	public abstract VectorArray2D<V> duplicate();

}
