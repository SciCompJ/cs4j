/**
 * 
 */
package net.sci.array.vector;

/**
 * @author dlegland
 *
 */
public abstract class IntVectorArray3D<V extends IntVector<?>> extends VectorArray3D<V> implements IntVectorArray<V>
{
//    // =============================================================
//    // Static methods
//
//    public final static <T extends Vector<?>> IntVectorArray3D<T> wrap(VectorArray<T> array)
//    {
//        if (array instanceof IntVectorArray3D)
//        {
//            return (IntVectorArray3D<T>) array;
//        }
//        return new Wrapper<T>(array);
//    }

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
    public abstract IntVectorArray2D<V> slice(int sliceIndex);

    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public abstract Iterable<? extends IntVectorArray2D<V>> slices();

    /**
     * Creates an iterator over the slices
     * 
     * @return an iterator over 2D slices
     */
    public abstract java.util.Iterator<? extends IntVectorArray2D<V>> sliceIterator();

    
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

	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#duplicate()
	 */
    @Override
    public abstract IntVectorArray3D<V> duplicate();

    public interface Iterator<V extends IntVector<?>> extends IntVectorArray.Iterator<V>
    {
    }

    
//    // =============================================================
//    // Inner Wrapper class
//
//    private static class Wrapper<T extends Vector<?>> extends IntVectorArray3D<T>
//    {
//        private VectorArray<T> array;
//        
//        protected Wrapper(VectorArray<T> array)
//        {
//            super(0, 0, 0);
//            if (array.dimensionality() < 3)
//            {
//                throw new IllegalArgumentException("Requires an array with at least three dimensions");
//            }
//            this.array = array;
//            this.size0 = array.getSize(0);
//            this.size1 = array.getSize(1);
//            this.size2 = array.getSize(2);
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
//        public T get(int x, int y, int z)
//        {
//            // return value from specified position
//            return this.array.get(new int[]{x, y, z});
//        }
//
//        @Override
//        public void set(int x, int y, int z, T value)
//        {
//            // set value at specified position
//            this.array.set(new int[]{x, y, z}, value);
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
//        public double[] getValues(int x, int y, int z)
//        {
//            return array.getValues(new int[] {x, y, z});
//        }
//
//        @Override
//        public double[] getValues(int x, int y, int z, double[] values)
//        {
//            return getValues(new int[] {x, y, z}, values);
//        }
//
//        @Override
//        public void setValues(int x, int y, int z, double[] values)
//        {
//            setValues(new int[] {x, y, z}, values);
//        }
//
//        @Override
//        public double getValue(int x, int y, int z, int c)
//        {
//            return getValues(new int[] {x, y, z})[c];
//        }
//
//        @Override
//        public void setValue(int x, int y, int z, int c, double value)
//        {
//            int[] pos = new int[] {x, y, z};
//            double[] values = array.getValues(pos);
//            values[c] = value;
//            array.setValues(pos, values);
//        }
//    }
}
