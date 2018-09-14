/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.Float32Array2D;
import net.sci.array.scalar.Float32Array3D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;

/**
 * @author dlegland
 *
 */
public abstract class VectorArray2D<V extends Vector<?>> extends Array2D<V> implements VectorArray<V>
{
    // =============================================================
    // Static methods

    public final static <T extends Vector<?>> VectorArray2D<T> wrap(VectorArray<T> array)
    {
        if (array instanceof VectorArray2D)
        {
            return (VectorArray2D<T>) array;
        }
        return new Wrapper<T>(array);
    }

    /**
     * Creates a new instance of VectorArray from a scalar array with three dimensions.
     * 
     * @param array
     *            an instance of scalar array
     * @return a new instance of vector array, with the one dimension less than
     *         original array
     */
    public static VectorArray2D<?> fromStack(ScalarArray3D<?> array)
    {
        // size and dimension of input array
        int sizeX = array.getSize(0);
        int sizeY = array.getSize(1);
        int sizeZ = array.getSize(2);
    
        // create output array
        VectorArray2D<? extends Vector<?>> result = Float64VectorArray2D.create(sizeX, sizeY, sizeZ);
        int[] pos = new int[3];
        for (int c = 0; c < sizeZ; c++)
        {
            pos[2] = c;
            for (int y = 0; y < sizeY; y++)
            {
                pos[1] = y;
                for (int x = 0; x < sizeX; x++)
                {
                    pos[0] = x;
                    result.setValue(x, y, c, array.getValue(pos));
                }
            }
        }
        
        return result;
    }
    
    /**
     * Converts a vector array to a higher-dimensional array, by considering the
     * channels as a new dimension.
     * 
     * Current implementation returns the result in a new instance of
     * Float32Array.
     *
     * @param array
     *            a vector array with two dimensions
     * @return a scalar array with three dimensions
     */
    public static ScalarArray3D<?> convertToStack(VectorArray2D<?> array)
    {
        // size and dimension of input array
        int sizeX = array.getSize(0);
        int sizeY = array.getSize(1);
        int nChannels = array.getVectorLength();
        
        // create output array
        Float32Array3D result = Float32Array3D.create(sizeX, sizeY, nChannels);
        int[] pos = new int[2];
        for (int c = 0; c < nChannels; c++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                pos[1] = y;
                for (int x = 0; x < sizeX; x++)
                {
                    pos[0] = x;
                    result.setValue(x, y, c, array.get(pos).getValue(c));
                }
            }
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
     * Returns a view to the specified channel.
     * 
     * @param channelIndex
     *            the index of the channel, between 0 and nChannels-1
     * @return a new scalar array.
     */
    // TODO: merge with channelView
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
	
	@Override
    public double[] getValues(int[] pos, double[] values)
    {
        return getValues(pos[0], pos[1], values);
    }

    public void setValues(int[] pos, double[] values)
	{
		setValues(pos[0], pos[1], values);
	}
	

	// =============================================================
	// Specialization of Array interface

//	/**
//	 * Returns the norm of the vector at the given position.
//	 * 
//	 * @see net.sci.array.Array2D#getValue(int, int)
//	 */
//	@Override
//	public double getValue(int x, int y)
//	{
//		double[] values = getValues(x, y);
//		double sum = 0;
//		for (double v : values)
//		{
//			sum += v * v;
//		}
//		return Math.sqrt(sum);
//	}
//
//	/**
//	 * Changes the value of the vector at given position, by setting the first
//	 * component and clearing the others.
//	 * 
//	 * @see net.sci.array.Array2D#setValue(int, int, double)
//	 */
//	@Override
//	public void setValue(int x, int y, double value)
//	{
//		setValue(x, y, 0, value);
//		for (int c = 1; c < this.getVectorLength(); c++)
//		{
//			setValue(x, y, c, 0);
//		}
//	}

    /* (non-Javadoc)
     * @see net.sci.array.data.VectorArray#duplicate()
     */
    @Override
    public VectorArray2D<V> duplicate()
    {
        VectorArray<V> tmp = this.newInstance(this.size0, this.size1);
        if (!(tmp instanceof VectorArray2D))
        {
            throw new RuntimeException("Can not create VectorArray2D instance from " + this.getClass().getName() + " class.");
        }
        
        VectorArray2D<V> result = (VectorArray2D <V>) tmp;
        
        VectorArray.Iterator<V> iter1 = this.iterator();
        VectorArray.Iterator<V> iter2 = result.iterator();
        while (iter1.hasNext())
        {
            iter2.setNext(iter1.next());
        }

        return result;
    }
    


    // =============================================================
    // Inner Wrapper class

    private static class Wrapper<T extends Vector<?>> extends VectorArray2D<T>
    {
        private VectorArray<T> array;
        
        protected Wrapper(VectorArray<T> array)
        {
            super(0, 0);
            if (array.dimensionality() < 2)
            {
                throw new IllegalArgumentException("Requires an array with at least two dimensions");
            }
            this.array = array;
            this.size0 = array.getSize(0);
            this.size1 = array.getSize(1);
        }

        @Override
        public VectorArray<T> newInstance(int... dims)
        {
            return this.array.newInstance(dims);
        }

        @Override
        public Array.Factory<T> getFactory()
        {
            return this.array.getFactory();
        }

        @Override
        public T get(int x, int y)
        {
            // return value from specified position
            return this.array.get(new int[]{x, y});
        }

        @Override
        public void set(int x, int y, T value)
        {
            // set value at specified position
            this.array.set(new int[]{x, y}, value);
        }

        @Override
        public Class<T> getDataType()
        {
            return array.getDataType();
        }

        @Override
        public VectorArray.Iterator<T> iterator()
        {
            return array.iterator();
        }

        @Override
        public int getVectorLength()
        {
            return array.getVectorLength();
        }

        @Override
        public double[] getValues(int x, int y)
        {
            return array.getValues(new int[] {x, y});
        }

        @Override
        public double[] getValues(int x, int y, double[] values)
        {
            return getValues(new int[] {x, y}, values);
        }

        @Override
        public void setValues(int x, int y, double[] values)
        {
            setValues(new int[] {x, y}, values);
        }

        @Override
        public double getValue(int x, int y, int c)
        {
            return getValues(new int[] {x, y})[c];
        }

        @Override
        public void setValue(int x, int y, int c, double value)
        {
            int[] pos = new int[] {x, y};
            double[] values = array.getValues(pos);
            values[c] = value;
            array.setValues(pos, values);
        }
    }
}
