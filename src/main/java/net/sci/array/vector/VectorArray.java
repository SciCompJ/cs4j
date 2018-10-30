/**
 * 
 */
package net.sci.array.vector;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.array.Array;
import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;

/**
 * Arrays containing vector of floating-point values.
 *
 * @author dlegland
 *
 */
public interface VectorArray<V extends Vector<?>> extends Array<V>
{
	// =============================================================
	// Static methods
	
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
	public static ScalarArray<?> norm(VectorArray<? extends Vector<?>> array)
	{
		// allocate memory for result
		Float32Array result = Float32Array.create(array.getSize());
		
        // create array iterators
        PositionIterator posIter = array.positionIterator();
        
        // iterate over both arrays in parallel
        double[] values = new double[array.getVectorLength()];
        while(posIter.hasNext())
        {
            int[] pos = posIter.next();
            result.setValue(pos, norm(array.getValues(pos, values)));
        }

		return result;
	}
	
	public static Collection<ScalarArray<?>> splitChannels(VectorArray<? extends Vector<?>> array)
	{
	    int nc = array.getVectorLength();
	    ArrayList<ScalarArray<?>> channels = new ArrayList<ScalarArray<?>>(nc);
	    
	    for (ScalarArray<?> channel : array.channels())
	    {
	        channels.add(channel.duplicate());
	    }
	    return channels;
	}
	
    /**
     * Computes the norm of the vector represented by given values
     * 
     * @param values
     *            the values of the vector
     * @return the norm of the vector
     */
    public static double norm(double[] values)
    {
        // compute norm of current vector
        double norm = 0;
        for (double d : values)
        {
            norm += d * d;
        }
        return Math.sqrt(norm);
    }
    


	// =============================================================
	// New methods
		
	/**
     * Computes the norm of each element of the given vector array.
     * 
     * Current implementation returns the result in a new instance of
     * Float32Array.
     * 
     * @return a scalar array with the same size at the input array
     */
    public default ScalarArray<?> norm()
    {
        // allocate memory for result
        Float32Array result = Float32Array.create(getSize());
        
        // create array iterators
        PositionIterator posIter = this.positionIterator();
        
        // iterate over both arrays in parallel
        double[] values = new double[getVectorLength()];
        while(posIter.hasNext())
        {
            int[] pos = posIter.next();
            result.setValue(pos, norm(getValues(pos, values)));
        }
        
        return result;
    }

    /**
     * Returns the number of elements used to represent each array element.
     * 
     * @return the number of elements used to represent each array element.
     */
	public int getVectorLength();

    /**
     * Returns a view on the channel specified by the given index.
     * 
     * @param channel
     *            index of the channel to view
     * @return a view on the channel
     */
    public ScalarArray<?> channel(int channel);

    /**
     * Iterates over the channels
     * 
     * @return an iterator over the (scalar) channels
     */
    public Iterable<? extends ScalarArray<?>> channels();

    public java.util.Iterator<? extends ScalarArray<?>> channelIterator();
	
    /**
     * Returns the set of values corresponding to the array element for the
     * given position.
     * 
     * @param pos
     *            list of indices in each dimension
     * @return the set of values corresponding to the array element for the
     *         given position
     */
    public double[] getValues(int[] pos);
    
    /**
	 * Returns the set of values corresponding to the array element for the
	 * given position.
	 * 
	 * @param pos
	 *            the position as a list of indices in each dimension
	 * @param values
	 *            the new set of values corresponding for the given position
	 * @return a reference to the array of values at the given position
	 */
    public double[] getValues(int[] pos, double[] values);
    
	/**
	 * Sets of values corresponding to the array element for the given position.
	 * 
	 * @param pos
	 *            list of indices in each dimension
	 * @param values
	 *            the new set of values to assign to the array
	 */
	public void setValues(int[] pos, double[] values);
	
    /**
     * Returns the value corresponding to a position and a channel index.
     * 
     * @param pos
     *            the n-dimensional position
     * @param channel
     *            index of channel (from 0)
     * @return the scalar value at specified position of specified channel
     */
	public double getValue(int[] pos, int channel);
	
    /**
     * Changes the value corresponding to a position and a channel index.
     * 
     * @param pos
     *            the n-dimensional position
     * @param channel
     *            index of channel (from 0)
     * @param value
     *            the new value
     */
    public void setValue(int[] pos, int channel, double value);

    
    // =============================================================
    // Specialization of Array interface
    
	@Override
	public VectorArray<V> newInstance(int... dims);
	
    @Override
    public default VectorArray<V> duplicate()
    {
        VectorArray<V> result = this.newInstance(this.getSize());
        
        // initialize iterators
        Array.PositionIterator iter1 = this.positionIterator();
        Array.PositionIterator iter2 = result.positionIterator();

        // copy values into output array
        double[] values = new double[getVectorLength()];
        while(iter1.hasNext())
        {
            result.setValues(iter2.next(), this.getValues(iter1.next(), values));
        }

        // return output
        return result;
    }
   
	public VectorArray.Iterator<V> iterator();
	
	// =============================================================
	// Inner interface

	public interface Iterator<V extends Vector<? extends Scalar>> extends Array.Iterator<V>
	{
		/**
		 * Returns the value of the c-th component of the current vector.
		 * 
		 * Index must be comprised between 0 and the number of components of
		 * this vector array.
		 * 
		 * @param c
		 *            the component / channel index
		 * @return the value of the specified component
		 */
		public double getValue(int c);
		
        /**
         * Returns the values at current position of the iterator into a
         * pre-allocated array.
         * 
         * Default implementation uses the <code>getValues(double[])</code> of a new
         * <code>Vector</code> instance. Subclasses may use a more efficient
         * implementation.
         * 
         * @param values
         *            a pre-allocated array with <code>getVectorLength()</code>
         *            elements.
         * @return the reference to the values array.
         */
        public default double[] getValues(double[] values)
        {
            return get().getValues(values);
        }

		/**
		 * Changes the value of the c-th component of the current vector.
		 * 
		 * Index must be comprised between 0 and the number of components of
		 * this vector array.
		 * 
		 * @param c
		 *            the component / channel index
		 * @param value
		 *            the new value of the specified component
		 */
		public void setValue(int c, double value);
	}

}
