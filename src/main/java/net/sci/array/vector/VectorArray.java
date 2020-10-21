/**
 * 
 */
package net.sci.array.vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.UnaryOperator;

import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.NumericArray;
import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;

/**
 * Arrays containing vector of floating-point values.
 *
 * @author dlegland
 *
 */
public interface VectorArray<V extends Vector<?>> extends NumericArray<V>
{
	// =============================================================
	// Static methods
	
	/**
	 * Computes the L2-norm of each element of the given vector array.
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
		Float32Array result = Float32Array.create(array.size());
		
        // iterate over both arrays in parallel
        double[] values = new double[array.channelNumber()];
        for(int[] pos : array.positions())
        {
            result.setValue(pos, Vector.norm(array.getValues(pos, values)));
        }

		return result;
	}
	
    /**
     * Computes the max-norm (or infinity norm) of each element of the given
     * vector array. This corresponds to computing the maximum of the absolute
     * values of elements of each vector.
     * 
     * Current implementation returns the result in a new instance of
     * Float32Array.
     * 
     * @param array
     *            a vector array
     * @return a scalar array with the same size at the input array
     */
    public static ScalarArray<?> maxNorm(VectorArray<? extends Vector<?>> array)
    {
        // allocate memory for result
        Float32Array result = Float32Array.create(array.size());
        
        // iterate over both arrays in parallel
        double[] values = new double[array.channelNumber()];
        for(int[] pos : array.positions())
        {
            array.getValues(pos, values);
            result.setValue(pos, Vector.maxNorm(values));
        }

        return result;
    }

    
    /**
     * Splits the vector array into a collection of scalar arrays, corresponding
     * to each component.
     * 
     * @param array
     *            the vector array to split.
     * @return collection of scalar arrays, corresponding to each component.
     */
	public static Collection<ScalarArray<?>> splitChannels(VectorArray<? extends Vector<?>> array)
	{
	    int nc = array.channelNumber();
	    ArrayList<ScalarArray<?>> channels = new ArrayList<ScalarArray<?>>(nc);
	    
	    for (ScalarArray<?> channel : array.channels())
	    {
	        channels.add(channel.duplicate());
	    }
	    return channels;
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
        Float32Array result = Float32Array.create(size());
        
        // iterate over both arrays in parallel
        double[] values = new double[channelNumber()];
        for (int[] pos : this.positions())
        {
            result.setValue(pos, Vector.norm(getValues(pos, values)));
        }
        
        return result;
    }


    // =============================================================
    // Management of channels
    
    /**
     * Returns the number of elements used to represent each array element.
     * 
     * @return the number of elements used to represent each array element.
     */
	public int channelNumber();

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
     * Copies the values of the specified scalar array at the specified channel
     * index.
     * 
     * @param c
     *            the channel index, 0-indexed
     * @param channel
     *            the scalar array containing channel data
     */
    public default void setChannel(int c, ScalarArray<?> channel)
    {
    	// check dims
    	if (!Arrays.isSameDimensionality(this, channel))
    	{
    		throw new IllegalArgumentException("Vector array and channel array must have same dimensonality");
    	}
    	if (!Arrays.isSameSize(this, channel))
    	{
    		throw new IllegalArgumentException("Vector array and channel array must have same size");
    	}
    	
    	// iterate over positions
    	for (int[] pos : positions())
    	{
    		this.setValue(pos, c, channel.getValue(pos));
    	}
    }


    // =============================================================
    // Management of values as double arrays
    
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
    // Implementation of comparison with scalar

    public default VectorArray<V> min(double v)
    {
        // create result array
        VectorArray<V> res = newInstance(size());
        
        // prepare iteration
        int nc = channelNumber();
        double[] values = new double[nc];
        
        // iterate over positions
        for (int[] pos : positions())
        {
            this.getValues(pos, values);
            for (int c = 0; c < nc; c++)
            {
                values[c] = Math.min(values[c], v);
            }
            res.setValues(pos, values);
        }
        
        // return result
        return res;
    }
    
    public default VectorArray<V> max(double v)
    {
        // create result array
        VectorArray<V> res = newInstance(size());
        
        // prepare iteration
        int nc = channelNumber();
        double[] values = new double[nc];
        
        // iterate over positions
        for (int[] pos : positions())
        {
            this.getValues(pos, values);
            for (int c = 0; c < nc; c++)
            {
                values[c] = Math.max(values[c], v);
            }
            res.setValues(pos, values);
        }
        
        // return result
        return res;
    }
    

    // =============================================================
    // Implementation of NumericArray interface
    
    public default VectorArray<V> plus(double v)
    {
        VectorArray<V> res = newInstance(size());
        
        int nc = channelNumber();
        double[] values = new double[nc];
        
        for (int[] pos : positions())
        {
            this.getValues(pos, values);
            for (int c = 0; c < nc; c++)
            {
                values[c] += v;
            }
            res.setValues(pos, values);
        }
        return res;
    }

    public default VectorArray<V> minus(double v)
    {
        VectorArray<V> res = newInstance(size());
        
        int nc = channelNumber();
        double[] values = new double[nc];
        
        for (int[] pos : positions())
        {
            this.getValues(pos, values);
            for (int c = 0; c < nc; c++)
            {
                values[c] -= v;
            }
            res.setValues(pos, values);
        }
        return res;
    }

    public default VectorArray<V> times(double v)
    {
        VectorArray<V> res = newInstance(size());
        
        int nc = channelNumber();
        double[] values = new double[nc];
        
        for (int[] pos : positions())
        {
            this.getValues(pos, values);
            for (int c = 0; c < nc; c++)
            {
                values[c] *= v;
            }
            res.setValues(pos, values);
        }
        return res;
    }

    public default VectorArray<V> divideBy(double v)
    {
        VectorArray<V> res = newInstance(size());
        
        int nc = channelNumber();
        double[] values = new double[nc];
        
        for (int[] pos : positions())
        {
            this.getValues(pos, values);
            for (int c = 0; c < nc; c++)
            {
                values[c] /= v;
            }
            res.setValues(pos, values);
        }
        return res;
    }

    /**
     * Applies the given function to each element of each channel the array, and
     * return a new Array with the same class.
     * 
     * @param fun the function to apply
     * @return the result array
     */
    public default VectorArray<V> apply(UnaryOperator<Double> fun)
    {
        VectorArray<V> res = newInstance(size());
        apply(fun, res);
        return res;
    }

    /**
     * Applies the given function to each element of each channel the array, and
     * return a reference to the output array.
     * 
     * @param fun
     *            the function to apply
     * @param output
     *            the array to put the result in
     * @return the result array
     */
    public default VectorArray<V> apply(UnaryOperator<Double> fun, VectorArray<V> output)
    {
        if (!Arrays.isSameSize(this, output))
        {
            throw new IllegalArgumentException("Output array must have same size as input array");
        }
        
        // prepare iteration
        int nc = channelNumber();
        double[] values = new double[nc];

        for (int[] pos : positions())
        {
            // get values from source
            this.getValues(pos, values);
            
            // apply function to each value
            for (int c = 0; c < nc; c++)
            {
                values[c] = fun.apply(values[c]);
            }
            
            // put result in target at current position
            output.setValues(pos, values);
        }
        return output;
    }
    

    // =============================================================
    // Specialization of Array interface
    
//    /**
//     * Creates a new vector array with new dimensions and containing the same
//     * elements.
//     * 
//     * This method overrides the default behavior of the Array interface to
//     * simply manipulate double values.
//     * 
//     * </pre>{@code
//     * UInt8Array array = UInt8Array2D.create(6, 4);
//     * array.populateValues((x,y) -> x + 10 * y);
//     * ScalarArray<?> reshaped = array.reshape(4, 3, 2);
//     * double last = reshaped.getValue(new int[]{3, 2, 1}); // equals 35
//     * }
//     * 
//     * @param newDims
//     *            the dimensions of the new array
//     * @return a new array with same type and containing the same values
//     */
//    @Override
//    public default VectorArray<V> reshape(int... newDims)
//    {
//        // check dimension consistency
//        int n2 = 1;
//        for (int dim : newDims)
//        {
//            n2 *= dim;
//        }
//        if (n2 != this.elementNumber())
//        {
//            throw new IllegalArgumentException("The element number should not change after reshape.");
//        }
//        
//        // allocate memory
//        VectorArray<V> res = this.newInstance(newDims);
//        
//        // iterate using a pair of Iterator instances
//        Iterator<V> iter1 = this.iterator();
//        Iterator<V> iter2 = res.iterator();
//        while(iter1.hasNext())
//        {
//            iter1.forward();
//            iter2.forward();
//            iter2.siter1.getValues(values);
//            iter2.setNextValue(iter1.nextValues());
//        }
//        
//        return res;
//    }
   
	@Override
	public VectorArray<V> newInstance(int... dims);
	
    @Override
    public default VectorArray<V> duplicate()
    {
        VectorArray<V> result = this.newInstance(this.size());
        
        // initialize iterators
        Array.PositionIterator iter1 = this.positionIterator();
        Array.PositionIterator iter2 = result.positionIterator();

        // copy values into output array
        double[] values = new double[channelNumber()];
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
