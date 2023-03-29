/**
 * 
 */
package net.sci.array.scalar;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A 3D array of Float64 sliced in several planar slices. 
 * Slicing direction is the last one (usually z-slicing).
 * 
 * This implementation usually allows to represent larger arrays than
 * BufferedFloat64Array3D.
 * 
 * @see BufferedFloat64Array3D
 * 
 * @author dlegland
 *
 */
public class SlicedFloat64Array3D extends Float64Array3D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the SlicedFloat64Array3D
     * class. May return the input array if it is already an instance of
     * SlicedFloat64Array3D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of SlicedFloat64Array3D containing the same values
     *         as the input array.
     */
    public static final SlicedFloat64Array3D convert(Float64Array3D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof SlicedFloat64Array3D)
        {
            return (SlicedFloat64Array3D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        SlicedFloat64Array3D res = new SlicedFloat64Array3D(sizeX, sizeY, sizeZ);
        
        // copy values
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.setValue(x, y, z, array.getValue(x, y, z));
                }
            }
        }
        // return converted array
        return res;
    }
    
    
    // =============================================================
    // Class fields

    /**
     * The inner array of 2D Float64 arrays.
     */
	ArrayList<Float64Array2D> slices;

	
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
	public SlicedFloat64Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.slices = new ArrayList<>(size2);
		for (int z = 0; z < size2; z++)
		{
			this.slices.add(Float64Array2D.create(size0, size1));
		}
	}

	/**
	 * Creates a new instance by specifying the list of slices.
	 * 
	 * @param slices the list of slices composing the new 3D array.
	 */
	public SlicedFloat64Array3D(Collection<? extends Float64Array> slices)
	{
		super(0,0,0);
		if (slices.size() == 0)
		{
			return;
		}
		
		// check slices dimensionality
		for (Float64Array slice : slices)
		{
			if (slice.dimensionality() < 2)
			{
				throw new IllegalArgumentException("Slices must have two dimensions");
			}
		}
		
		// check slices have same dimensions
		Float64Array slice0 = slices.iterator().next();
		int size0 = slice0.size(0);
		int size1 = slice0.size(1);
		for (Float64Array slice : slices)
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
		for (Float64Array slice : slices)
		{
			this.slices.add(Float64Array2D.wrap(slice));
		}
	}

	
	// =============================================================
	// Specialization of the ScalarArray3D interface

    @Override
    public double getValue(int x, int y, int z)
    {
        return this.slices.get(z).getValue(x, y);
    }

    @Override
    public void setValue(int x, int y, int z, double value)
    {
        this.slices.get(z).setValue(x, y, value);
    }

    /* (non-Javadoc)
	 * @see net.sci.array.scalar.Float64Array3D#getDouble(int, int, int)
	 */
	@Override
	public double getValue(int[] pos)
	{
	    return this.slices.get(pos[2]).getValue(pos[0], pos[1]);
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
	public Float64Array3D duplicate()
	{
		ArrayList<Float64Array> newSlices = new ArrayList<Float64Array>(this.size2);
		for (Float64Array slice : this.slices)
		{
			newSlices.add(slice.duplicate());
		}
		return new SlicedFloat64Array3D(newSlices);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public Float64Array.Iterator iterator()
	{
		return new Float64Iterator();
	}
	
	private class Float64Iterator implements Float64Array.Iterator
	{
		int sliceIndex = 0;
		Float64Array.Iterator sliceIterator;
				
		public Float64Iterator() 
		{
			if (slices.size() > 0)
			{
				this.sliceIterator = slices.get(0).iterator();
			}
		}
		
		@Override
		public boolean hasNext()
		{
			return this.sliceIndex < size2 - 1 || sliceIterator.hasNext();
		}

		@Override
		public Float64 next()
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

		@Override
		public Float64 get()
		{
			return sliceIterator.get();
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
	}
}
