/**
 * 
 */
package net.sci.array.vector;


/**
 * @author dlegland
 *
 */
public abstract class IntVectorArray2D<V extends IntVector<?>> extends VectorArray2D<V> implements IntVectorArray<V>
{
//    // =============================================================
//    // Static methods
//
//    public final static <T extends Vector<?>> IntVectorArray2D<T> wrap(VectorArray<T> array)
//    {
//        if (array instanceof IntVectorArray2D)
//        {
//            return (IntVectorArray2D<T>) array;
//        }
//        return new Wrapper<T>(array);
//    }

//    /**
//     * Creates a new instance of VectorArray from a scalar array with three dimensions.
//     * 
//     * @param array
//     *            an instance of scalar array
//     * @return a new instance of vector array, with the one dimension less than
//     *         original array
//     */
//    public static IntVectorArray2D<?> fromStack(ScalarArray3D<?> array)
//    {
//        // size and dimension of input array
//        int sizeX = array.getSize(0);
//        int sizeY = array.getSize(1);
//        int sizeZ = array.getSize(2);
//    
//        // create output array
//        IntVectorArray2D<? extends Vector<?>> result = Float64VectorArray2D.create(sizeX, sizeY, sizeZ);
//        int[] pos = new int[3];
//        for (int c = 0; c < sizeZ; c++)
//        {
//            pos[2] = c;
//            for (int y = 0; y < sizeY; y++)
//            {
//                pos[1] = y;
//                for (int x = 0; x < sizeX; x++)
//                {
//                    pos[0] = x;
//                    result.setValue(x, y, c, array.getValue(pos));
//                }
//            }
//        }
//        
//        return result;
//    }
    
//    /**
//     * Converts a vector array to a higher-dimensional array, by considering the
//     * channels as a new dimension.
//     * 
//     * Current implementation returns the result in a new instance of
//     * Float32Array.
//     *
//     * @param array
//     *            a vector array with two dimensions
//     * @return a scalar array with three dimensions
//     */
//    public static ScalarArray3D<?> convertToStack(IntVectorArray2D<?> array)
//    {
//        // size and dimension of input array
//        int sizeX = array.getSize(0);
//        int sizeY = array.getSize(1);
//        int nChannels = array.getVectorLength();
//        
//        // create output array
//        Float32Array3D result = Float32Array3D.create(sizeX, sizeY, nChannels);
//        int[] pos = new int[2];
//        for (int c = 0; c < nChannels; c++)
//        {
//            for (int y = 0; y < sizeY; y++)
//            {
//                pos[1] = y;
//                for (int x = 0; x < sizeX; x++)
//                {
//                    pos[0] = x;
//                    result.setValue(x, y, c, array.get(pos).getValue(c));
//                }
//            }
//        }
//        
//        return result;
//    }
    
    
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
    
	public abstract void setSamples(int x, int y, int [] values);
	
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

    /* (non-Javadoc)
     * @see net.sci.array.data.VectorArray#duplicate()
     */
    @Override
    public abstract IntVectorArray2D<V> duplicate();
//    {
//        IntVectorArray<V> tmp = this.newInstance(this.size0, this.size1);
//        if (!(tmp instanceof IntVectorArray2D))
//        {
//            throw new RuntimeException("Can not create VectorArray2D instance from " + this.getClass().getName() + " class.");
//        }
//        
//        IntVectorArray2D<V> result = (IntVectorArray2D <V>) tmp;
//        
//        VectorArray.Iterator<V> iter1 = this.iterator();
//        VectorArray.Iterator<V> iter2 = result.iterator();
//        while (iter1.hasNext())
//        {
//            iter2.setNext(iter1.next());
//        }
//
//        return result;
//    }
    
    public interface Iterator<V extends IntVector<?>> extends IntVectorArray.Iterator<V>
    {
    }



//    // =============================================================
//    // Inner Wrapper class
//
//    private static class Wrapper<T extends Vector<?>> extends IntVectorArray2D<T>
//    {
//        private VectorArray<T> array;
//        
//        protected Wrapper(VectorArray<T> array)
//        {
//            super(0, 0);
//            if (array.dimensionality() < 2)
//            {
//                throw new IllegalArgumentException("Requires an array with at least two dimensions");
//            }
//            this.array = array;
//            this.size0 = array.getSize(0);
//            this.size1 = array.getSize(1);
//        }
//
//        @Override
//        public VectorArray<T> newInstance(int... dims)
//        {
//            return this.array.newInstance(dims);
//        }
//
//        @Override
//        public Array.Factory<T> getFactory()
//        {
//            return this.array.getFactory();
//        }
//
//        @Override
//        public T get(int x, int y)
//        {
//            // return value from specified position
//            return this.array.get(new int[]{x, y});
//        }
//
//        @Override
//        public void set(int x, int y, T value)
//        {
//            // set value at specified position
//            this.array.set(new int[]{x, y}, value);
//        }
//
//        @Override
//        public Class<T> getDataType()
//        {
//            return array.getDataType();
//        }
//
//        @Override
//        public VectorArray.Iterator<T> iterator()
//        {
//            return array.iterator();
//        }
//
//        @Override
//        public int getVectorLength()
//        {
//            return array.getVectorLength();
//        }
//
//        @Override
//        public double[] getValues(int x, int y)
//        {
//            return array.getValues(new int[] {x, y});
//        }
//
//        @Override
//        public double[] getValues(int x, int y, double[] values)
//        {
//            return getValues(new int[] {x, y}, values);
//        }
//
//        @Override
//        public void setValues(int x, int y, double[] values)
//        {
//            setValues(new int[] {x, y}, values);
//        }
//
//        @Override
//        public double getValue(int x, int y, int c)
//        {
//            return getValues(new int[] {x, y})[c];
//        }
//
//        @Override
//        public void setValue(int x, int y, int c, double value)
//        {
//            int[] pos = new int[] {x, y};
//            double[] values = array.getValues(pos);
//            values[c] = value;
//            array.setValues(pos, values);
//        }
//    }
}
