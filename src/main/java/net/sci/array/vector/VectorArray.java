/**
 * 
 */
package net.sci.array.vector;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.array.Array;
import net.sci.array.scalar.Float32Array;
//import net.sci.array.scalar.Float32ArrayND;
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
		Iterator<? extends Vector<?>> iter1 = array.iterator();
		Float32Array.Iterator iter2 = result.iterator();
		
		// iterate over both arrays in parallel
		double[] values = new double[array.getVectorLength()]; 
		while (iter1.hasNext() && iter2.hasNext())
		{
			// get current vector
			iter1.next().getValues(values);
			
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
	
	public static Collection<ScalarArray<?>> splitChannels(VectorArray<? extends Vector<?>> array)
	{
	    // TODO: remove static
		int nc = array.getVectorLength();
		ArrayList<ScalarArray<?>> result = new ArrayList<ScalarArray<?>>(nc);
		
		int[] dims = array.getSize();
		
		// allocate memory for each channel array
		for (int c = 0; c < nc; c++)
		{
		    //TODO: add psb to choose output type
			ScalarArray<?> channel = Float32Array.create(dims);
			
			// create iterators
			VectorArray.Iterator<? extends Vector<? extends Scalar>> iter1 = array.iterator();
			ScalarArray.Iterator<? extends Scalar> iter2 = channel.iterator();
			
			// iterate both iterators in parallel
			while (iter1.hasNext() && iter2.hasNext())
			{
				iter1.forward();
				iter2.forward();
				iter2.setValue(iter1.getValue(c));
			}
			
			result.add(channel);
		}
		
		return result;
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
        Iterator<? extends Vector<?>> iter1 = iterator();
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
     * Returns the number of elements used to represent each array element.
     * 
     * @return the number of elements used to represent each array element.
     */
	public int getVectorLength();

//	public java.util.Iterator<ScalarArray<?>> channelIterator();
	
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
	

    // =============================================================
    // Specialization of Array interface
    
//    /**
//     * Returns the maximum values within the components/channels.
//     */
//    @Override
//    public default double getValue(int[] pos)
//    {
//        double maxi = Double.NEGATIVE_INFINITY;
//        for (double v :  getValues(pos))
//        {
//            maxi = Math.max(maxi, v);
//        }
//        return maxi;
//    }
//    
//    /**
//     * Sets all the components/channels at the given position to the specified value.
//     */
//    @Override
//    public default void setValue(int[] pos, double value)
//    {
//        int nc = this.getVectorLength();
//        double[] vals = new double[nc];
//        for (int c = 0; c < nc; c++)
//        {
//            vals[c] = value;
//        }
//        setValues(pos, vals);
//    }

	@Override
	public VectorArray<V> newInstance(int... dims);
	
    @Override
    public default VectorArray<V> duplicate()
    {
        VectorArray<V> result = this.newInstance(this.getSize());
        
        VectorArray.Iterator<V> iter1 = this.iterator();
        VectorArray.Iterator<V> iter2 = result.iterator();
        while (iter1.hasNext())
        {
            iter2.setNext(iter1.next());
        }

        return result;
    }
   
	public VectorArray.Iterator<V> iterator();
	
	// =============================================================
	// Inner interface

	public interface Iterator<V extends Vector<? extends Scalar>> extends Array.Iterator<V>
	{
		public default double nextValue()
		{
			forward();
			return getValue();
		}
		
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
