/**
 * 
 */
package net.sci.array.scalar;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A 3D array of Int32 sliced in several planar slices. 
 * Slicing direction is the last one (usually z-slicing).
 * 
 * @author dlegland
 *
 */
public class SlicedInt32Array3D extends Int32Array3D
{
	// =============================================================
	// Class fields

	ArrayList<Int32Array> slices;

	
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
	 * Creates a new instance by specifying the list of slices.
	 * 
	 * @param slices the list of slices composing the new 3D array.
	 */
	public SlicedInt32Array3D(Collection<? extends Int32Array> slices)
	{
		super(0,0,0);
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
			this.slices.add(slice);
		}
	}


	// =============================================================
	// Specialization of the UInt8Array3D interface

	@Override
    public void setInt(int x, int y, int z, int value)
    {
	    this.slices.get(z).setInt(new int[] {x, y}, value);
    }

    /* (non-Javadoc)
	 * @see net.sci.array.scalar.Int32Array3D#getInt(int, int, int)
	 */
	@Override
	public int getInt(int... pos)
	{
		return this.slices.get(pos[2]).getInt(pos[0], pos[1]);
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.scalar.Int32Array3D#setInt(int, int, int, short)
	 */
	@Override
	public void setInt(int[] pos, int intValue)
	{
		this.slices.get(pos[2]).setInt(new int[] {pos[0], pos[1]}, intValue);
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
