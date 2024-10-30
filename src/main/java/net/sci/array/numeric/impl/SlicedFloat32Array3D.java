/**
 * 
 */
package net.sci.array.numeric.impl;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.array.numeric.Float32;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Float32Array3D;

/**
 * A 3D array of Float32 sliced in several planar slices. 
 * Slicing direction is the last one (usually z-slicing).
 * 
 * This implementation usually allows to represent larger arrays than
 * BufferedFloat32Array3D.
 * 
 * @see BufferedFloat32Array3D
 * 
 * @author dlegland
 *
 */
public class SlicedFloat32Array3D extends Float32Array3D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the SlicedFloat32Array3D
     * class. May return the input array if it is already an instance of
     * SlicedFloat32Array3D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of SlicedFloat32Array3D containing the same values
     *         as the input array.
     */
    public static final SlicedFloat32Array3D convert(Float32Array3D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof SlicedFloat32Array3D)
        {
            return (SlicedFloat32Array3D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        SlicedFloat32Array3D res = new SlicedFloat32Array3D(sizeX, sizeY, sizeZ);
        
        // copy values
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.setFloat(x, y, z, array.getFloat(x, y, z));
                }
            }
        }
        // return converted array
        return res;
    }
    
    
    // =============================================================
    // Class fields

    /**
     * The inner array of 2D Float32 arrays.
     */
    ArrayList<Float32Array2D> slices;
    

    // =============================================================
    // Constructors

    /**
     * Creates a new instance by specifying the dimensions, and creates slice
     * instances.
     * 
     * @param size0
     *            size of array in first dimension
     * @param size1
     *            size of array in second dimension
     * @param size2
     *            size of array in third dimension, corresponding to slice
     *            number
     */
    public SlicedFloat32Array3D(int size0, int size1, int size2)
    {
        super(size0, size1, size2);
        this.slices = new ArrayList<>(size2);
        for (int z = 0; z < size2; z++)
        {
            this.slices.add(Float32Array2D.create(size0, size1));
        }
    }

    /**
     * Creates a new instance by specifying the dimensions, and creates slice
     * instances using the specified factory.
     * 
     * @param size0
     *            size of array in first dimension
     * @param size1
     *            size of array in second dimension
     * @param size2
     *            size of array in third dimension, corresponding to slice
     *            number
     * @param factory
     *            the factory for initializing the slices
     */
    public SlicedFloat32Array3D(int size0, int size1, int size2, Float32Array.Factory sliceFactory)
    {
        super(size0, size1, size2);
        this.slices = new ArrayList<>(size2);
        for (int z = 0; z < size2; z++)
        {
            this.slices.add(Float32Array2D.wrap(sliceFactory.create(size0, size1)));
        }
    }

    /**
     * Creates a new instance by specifying the list of slices.
     * 
     * @param slices
     *            the list of slices composing the new 3D array.
     */
    public SlicedFloat32Array3D(Collection<? extends Float32Array> slices)
    {
        super(0, 0, 0);
        if (slices.size() == 0)
        {
            return;
        }

        // check slices dimensionality
        for (Float32Array slice : slices)
        {
            if (slice.dimensionality() < 2)
            {
                throw new IllegalArgumentException("Slices must have two dimensions");
            }
        }

        // check slices have same dimensions
        Float32Array slice0 = slices.iterator().next();
        int size0 = slice0.size(0);
        int size1 = slice0.size(1);
        for (Float32Array slice : slices)
        {
            if (slice.size(0) != size0 || slice.size(1) != size1)
            {
                throw new IllegalArgumentException("All slices must have the same size");
            }
        }

        // update size information
        this.size0 = size0;
        this.size1 = size1;
        this.size2 = slices.size();

        // Create and populate the slice array
        this.slices = new ArrayList<>(size2);
        for (Float32Array slice : slices)
        {
            this.slices.add(Float32Array2D.wrap(slice));
        }
    }
    

    // =============================================================
    // Specialization of the ScalarArray3D interface

    @Override
    public float getFloat(int x, int y, int z)
    {
        return this.slices.get(z).getFloat(x, y);
    }

    @Override
    public void setFloat(int x, int y, int z, float value)
    {
        this.slices.get(z).setFloat(x, y, value);
    }

    /* (non-Javadoc)
	 * @see net.sci.array.scalar.Float32Array3D#getDouble(int, int, int)
	 */
	@Override
	public double getValue(int[] pos)
	{
		return this.slices.get(pos[2]).getValue(pos[0], pos[1]);
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.scalar.ScalarArray3D#setValue(int, int, int, double)
	 */
	@Override
	public void setValue(int[] pos, double value)
	{
		this.slices.get(pos[2]).setValue(new int[] {pos[0], pos[1]}, value);
	}

	   
    // =============================================================
    // Specialization of the ScalarArray interface
    
    @Override
    public Iterable<Double> values()
    {
        return new Iterable<Double>()
        {
            @Override
            public java.util.Iterator<Double> iterator()
            {
                return new DoubleIterator();
            }
        };
    }
    
    /**
     * Inner implementation of iterator on double values.
     */
    private class DoubleIterator implements java.util.Iterator<Double>
    {
        int sliceIndex = 0;
        java.util.Iterator<Double> sliceIterator;
    
        public DoubleIterator()
        {
            if (slices.size() > 0)
            {
                this.sliceIterator = slices.get(0).values().iterator();
            }
        }
    
        @Override
        public boolean hasNext()
        {
            return this.sliceIndex < size2 - 1 || sliceIterator.hasNext();
        }
    
        @Override
        public Double next()
        {
            if (!sliceIterator.hasNext())
            {
                sliceIndex++;
                if (sliceIndex == size2)
                {
                    throw new RuntimeException("can not access slice after the the last one");
                }
                sliceIterator = slices.get(sliceIndex).values().iterator();
            }
            return sliceIterator.next();
        }
    }

    
	// =============================================================
	// Specialization of the Array interface

	@Override
	public Float32Array3D duplicate()
	{
		ArrayList<Float32Array> newSlices = new ArrayList<Float32Array>(this.size2);
		for (Float32Array slice : this.slices)
		{
			newSlices.add(slice.duplicate());
		}
		return new SlicedFloat32Array3D(newSlices);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public Float32Array.Iterator iterator()
	{
		return new Float32Iterator();
	}
	
	private class Float32Iterator implements Float32Array.Iterator
	{
		int sliceIndex = 0;
		Float32Array.Iterator sliceIterator;
				
		public Float32Iterator() 
		{
			if (slices.size() > 0)
			{
				this.sliceIterator = slices.get(0).iterator();
			}
		}
		
		@Override
        public float getFloat()
        {
            return sliceIterator.getFloat();
        }

        @Override
        public void setFloat(float value)
        {
            sliceIterator.setFloat(value);
        }

        @Override
        public double getValue()
        {
        	return sliceIterator.getValue();
        }

        @Override
        public void setValue(double value)
        {
        	sliceIterator.setValue(value);
        }

        @Override
        public Float32 get()
        {
        	return sliceIterator.get();
        }

        @Override
		public boolean hasNext()
		{
			return this.sliceIndex < size2 - 1 || sliceIterator.hasNext();
		}

		@Override
		public Float32 next()
		{
			forward();
			return get();
		}

		@Override
		public void forward()
		{
			if (!sliceIterator.hasNext())
			{
				sliceIndex++;
				if (sliceIndex == size2)
				{
					throw new RuntimeException("can not access slice after the the last one");
				}
				sliceIterator = slices.get(sliceIndex).iterator();
			}
			sliceIterator.forward();
		}
	}
}
