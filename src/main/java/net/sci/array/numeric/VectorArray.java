/**
 * 
 */
package net.sci.array.numeric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.UnaryOperator;

import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.numeric.process.VectorArrayL2Norm;
import net.sci.array.numeric.process.VectorArrayMaxNorm;

/**
 * Arrays containing vector of floating-point values.
 *
 * @param <V>
 *            the type of the vector
 * @param <S>
 *            the type of the elements contained by this vector
 *
 * @author dlegland
 */
public interface VectorArray<V extends Vector<V, S>, S extends Scalar<S>> extends NumericArray<V>
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
    public static ScalarArray<?> norm(VectorArray<?, ?> array)
    {
        return new VectorArrayL2Norm().processVector(array);
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
    public static ScalarArray<?> maxNorm(VectorArray<?, ?> array)
    {
        return new VectorArrayMaxNorm().processVector(array);
    }

    
    /**
     * Splits the vector array into a collection of scalar arrays, corresponding
     * to each component.
     * 
     * @param array
     *            the vector array to split.
     * @return collection of scalar arrays, corresponding to each component.
     */
    public static Collection<ScalarArray<?>> splitChannels(VectorArray<?, ?> array)
    {
        int nc = array.channelCount();
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
        double[] values = new double[channelCount()];
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
	public int channelCount();

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
     *            index of channel (starting from 0)
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

    public default VectorArray<V,S> min(double v)
    {
        // create result array
        VectorArray<V,S> res = newInstance(size());
        
        // prepare iteration
        int nc = channelCount();
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
    
    public default VectorArray<V,S> max(double v)
    {
        // create result array
        VectorArray<V,S> res = newInstance(size());
        
        // prepare iteration
        int nc = channelCount();
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
    
    public default VectorArray<V,S> plus(double v)
    {
        VectorArray<V,S> res = newInstance(size());
        
        int nc = channelCount();
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

    public default VectorArray<V,S> minus(double v)
    {
        VectorArray<V,S> res = newInstance(size());
        
        int nc = channelCount();
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

    public default VectorArray<V,S> times(double v)
    {
        VectorArray<V,S> res = newInstance(size());
        
        int nc = channelCount();
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

    public default VectorArray<V,S> divideBy(double v)
    {
        VectorArray<V,S> res = newInstance(size());
        
        int nc = channelCount();
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
    public default VectorArray<V,S> apply(UnaryOperator<Double> fun)
    {
        VectorArray<V,S> res = newInstance(size());
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
    public default VectorArray<V,S> apply(UnaryOperator<Double> fun, VectorArray<V,S> output)
    {
        if (!Arrays.isSameSize(this, output))
        {
            throw new IllegalArgumentException("Output array must have same size as input array");
        }
        
        // prepare iteration
        int nc = channelCount();
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
       
    @Override
    public VectorArray<V, S> newInstance(int... dims);
    
    @Override
    public default VectorArray<V,S> duplicate()
    {
        VectorArray<V,S> result = this.newInstance(this.size());
        
        // copy values into output array
        double[] values = new double[channelCount()];
        for (int[] pos : result.positions())
        {
            result.setValues(pos, this.getValues(pos, values));
        }
        
        // return output
        return result;
    }
    
    @Override
    public Factory<V,S> factory();
    
    @Override
    public Iterator<V, S> iterator();
    
    
    // =============================================================
    // Inner interface
    
    /**
     * Specialization of the Array Factory for generating instances of
     * VectorArray. Provides a new method for creating arrays with specified
     * dimension and number of components.
     */
    public interface Factory<V extends Vector<V, S>, S extends Scalar<S>> extends Array.Factory<V>
    {
        /**
         * Creates a new VectorArray with the specified dimensions and number of
         * components. Note that for some specialization of VectorArray, the
         * number of components may be fixed, and this method may throw an
         * exception.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param nComopnents
         *            the number of components of the vectors
         * @return a new instance of VectorArray
         */
        public VectorArray<V,S> create(int[] dims, int nComponents);
    }

    public interface Iterator<V extends Vector<V, S>, S extends Scalar<S>> extends Array.Iterator<V>
    {
        /**
         * Returns the value of the i-th component of the current vector.
         * 
         * Index must be comprised between 0 and the number of components of
         * this vector array.
         * 
         * @param i
         *            the component / channel index
         * @return the value of the specified component
         */
        public double getValue(int i);
        
        /**
         * Returns the values at current position of the iterator into a
         * pre-allocated array.
         * 
         * Default implementation uses the <code>getValues(double[])</code> of a
         * new <code>Vector</code> instance. Subclasses may use a more efficient
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
