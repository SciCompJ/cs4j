/**
 * 
 */
package net.sci.array.scalar;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A 3D array of Int16 sliced in several planar slices. 
 * Slicing direction is the last one (usually z-slicing).
 * 
 * This implementation usually allows to represent larger arrays than
 * BufferedInt16Array3D.
 * 
 * @see BufferedInt16Array3D
 * 
 * @author dlegland
 *
 */
public class SlicedInt16Array3D extends Int16Array3D
{
    // =============================================================
    // Static methods

    /**
     * Converts the input array into an instance of the SlicedInt16Array3D
     * class. May return the input array if it is already an instance of
     * SlicedInt16Array3D.
     * 
     * @param array
     *            the array to convert
     * @return an instance of SlicedInt16Array3D containing the same values
     *         as the input array.
     */
    public static final SlicedInt16Array3D convert(Int16Array3D array)
    {
        // if array is of correct class, simply use class cast
        if (array instanceof SlicedInt16Array3D)
        {
            return (SlicedInt16Array3D) array;
        }
        
        // allocate memory
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        SlicedInt16Array3D res = new SlicedInt16Array3D(sizeX, sizeY, sizeZ);
        
        // copy values
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.setShort(x, y, z, array.getShort(x, y, z));
                }
            }
        }
        // return converted array
        return res;
    }
    
    
	// =============================================================
	// Class fields

    /**
     * The inner array of 2D Int16 arrays.
     */
    ArrayList<Int16Array2D> slices;

	
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
	public SlicedInt16Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.slices = new ArrayList<>(size2);
		for (int z = 0; z < size2; z++)
		{
			this.slices.add(Int16Array2D.create(size0, size1));
		}
	}

	/**
	 * Creates a new instance by specifying the list of slices.
	 * 
	 * @param slices the list of slices composing the new 3D array.
	 */
	public SlicedInt16Array3D(Collection<? extends Int16Array> slices)
	{
		super(0,0,0);
		if (slices.size() == 0)
		{
			return;
		}
		
		// check slices dimensionality
		for (Int16Array slice : slices)
		{
			if (slice.dimensionality() < 2)
			{
				throw new IllegalArgumentException("Slices must have two dimensions");
			}
		}
		
		// check slices have same dimensions
		Int16Array slice0 = slices.iterator().next();
		int size0 = slice0.size(0);
		int size1 = slice0.size(1);
		for (Int16Array slice : slices)
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
		for (Int16Array slice : slices)
		{
			this.slices.add(Int16Array2D.wrap(slice));
		}
	}


	// =============================================================
	// Specialization of the UInt8Array3D interface

    @Override
    public short getShort(int x, int y, int z)
    {
        return this.slices.get(z).getShort(x, y);
    }

    @Override
    public void setShort(int x, int y, int z, short s)
    {
        this.slices.get(z).setShort(x, y, s);
    }

    /* (non-Javadoc)
	 * @see net.sci.array.scalar.Int16Array3D#getShort(int, int, int)
	 */
	@Override
	public short getShort(int[]  pos)
	{
		return this.slices.get(pos[2]).getShort(pos[0], pos[1]);
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.scalar.Int16Array3D#setShort(int, int, int, short)
	 */
	@Override
	public void setShort(int[] pos, short s)
	{
		this.slices.get(pos[2]).setShort(pos[0], pos[1], s);
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
	public Int16Array3D duplicate()
	{
		ArrayList<Int16Array> newSlices = new ArrayList<Int16Array>(this.size2);
		for (Int16Array slice : this.slices)
		{
			newSlices.add(slice.duplicate());
		}
		return new SlicedInt16Array3D(newSlices);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public Int16Array.Iterator iterator()
	{
		return new Int16Iterator();
	}
	
	private class Int16Iterator implements Int16Array.Iterator
	{
		int sliceIndex = 0;
		Int16Array.Iterator sliceIterator;
				
		public Int16Iterator() 
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
		public Int16 next()
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
		public Int16 get()
		{
			return sliceIterator.get();
		}

		@Override
		public short getShort()
		{
			return sliceIterator.getShort();
		}

		@Override
		public void setShort(short s)
		{
			sliceIterator.setShort(s);
		}
	}
}
