/**
 * 
 */
package net.sci.array.numeric.impl;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.array.numeric.Int32;
import net.sci.array.numeric.Int32Array;
import net.sci.array.numeric.Int32Array2D;
import net.sci.array.numeric.Int32Array3D;

/**
 * A 3D array of Int32 sliced in several planar slices. 
 * Slicing direction is the last one (usually z-slicing).
 * 
 * This implementation usually allows to represent larger arrays than
 * BufferedInt32Array3D.
 * 
 * @see BufferedInt32Array3D
 * 
 * @author dlegland
 *
 */
public class SlicedInt32Array3D extends Int32Array3D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the SlicedInt32Array3D
     * class. May return the input array if it is already an instance of
     * SlicedInt32Array3D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of SlicedInt32Array3D containing the same values
     *         as the input array.
     */
    public static final SlicedInt32Array3D convert(Int32Array3D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof SlicedInt32Array3D)
        {
            return (SlicedInt32Array3D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        SlicedInt32Array3D res = new SlicedInt32Array3D(sizeX, sizeY, sizeZ);
        
        // copy values
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.setInt(x, y, z, array.getInt(x, y, z));
                }
            }
        }
        // return converted array
        return res;
    }
    
    
    // =============================================================
    // Class fields

    /**
     * The inner array of 2D Int32 arrays.
     */
    ArrayList<Int32Array2D> slices;
    

    // =============================================================
    // Constructors

    /**
     * Creates a new instance by specifying the dimensions, and creating slice
     * instance for each z-index.
     * 
     * @param size0
     *            size of array in first dimension
     * @param size1
     *            size of array in second dimension
     * @param size2
     *            size of array in third dimension, corresponding to slice
     *            number
     */
    public SlicedInt32Array3D(int size0, int size1, int size2)
    {
        super(size0, size1, size2);
        this.slices = new ArrayList<>(size2);
        for (int z = 0; z < size2; z++)
        {
            this.slices.add(Int32Array2D.create(size0, size1));
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
    public SlicedInt32Array3D(int size0, int size1, int size2, Int32Array.Factory sliceFactory)
    {
        super(size0, size1, size2);
        this.slices = new ArrayList<>(size2);
        for (int z = 0; z < size2; z++)
        {
            this.slices.add(Int32Array2D.wrap(sliceFactory.create(size0, size1)));
        }
    }

    /**
     * Creates a new instance by specifying the list of slices.
     * 
     * @param slices
     *            the list of slices composing the new 3D array.
     */
    public SlicedInt32Array3D(Collection<? extends Int32Array> slices)
    {
        super(0, 0, 0);
        if (slices.size() == 0)
        {
            return;
        }

        // check slices dimensionality
        for (Int32Array slice : slices)
        {
            if (slice.dimensionality() < 2)
            {
                throw new IllegalArgumentException("Slices must have two dimensions");
            }
        }

        // check slices have same dimensions
        Int32Array slice0 = slices.iterator().next();
        int size0 = slice0.size(0);
        int size1 = slice0.size(1);
        for (Int32Array slice : slices)
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
        for (Int32Array slice : slices)
        {
            this.slices.add(Int32Array2D.wrap(slice));
        }
    }
    

    // =============================================================
    // Specialization of the UInt32Array3D interface

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.array.scalar.Int32Array3D#getInt(int, int, int)
     */
    @Override
    public int getInt(int x, int y, int z)
    {
        return this.slices.get(z).getInt(x, y);
    }
        
	@Override
    public void setInt(int x, int y, int z, int value)
    {
	    this.slices.get(z).setInt(x, y, value);
    }

    /* (non-Javadoc)
	 * @see net.sci.array.scalar.Int32Array3D#getInt(int, int, int)
	 */
	@Override
	public int getInt(int[] pos)
	{
		return this.slices.get(pos[2]).getInt(pos[0], pos[1]);
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.scalar.Int32Array3D#setInt(int, int, int, short)
	 */
	@Override
	public void setInt(int[] pos, int intValue)
	{
		this.slices.get(pos[2]).setInt(pos[0], pos[1], intValue);
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
	public Int32Array3D duplicate()
	{
		ArrayList<Int32Array> newSlices = new ArrayList<Int32Array>(this.size2);
		for (Int32Array slice : this.slices)
		{
			newSlices.add(slice.duplicate());
		}
		return new SlicedInt32Array3D(newSlices);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public Int32Array.Iterator iterator()
	{
		return new Int32Iterator();
	}
	
	private class Int32Iterator implements Int32Array.Iterator
	{
		int sliceIndex = 0;
		Int32Array.Iterator sliceIterator;
				
		public Int32Iterator() 
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
		public Int32 next()
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
		public Int32 get()
		{
			return sliceIterator.get();
		}

		@Override
		public int getInt()
		{
			return sliceIterator.getInt();
		}

		@Override
		public void setInt(int intValue)
		{
			sliceIterator.setInt(intValue);
		}
	}
}
