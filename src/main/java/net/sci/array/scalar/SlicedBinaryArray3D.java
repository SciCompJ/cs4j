/**
 * 
 */
package net.sci.array.scalar;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A 3D array of Binary sliced in several planar slices. 
 * Slicing direction is the last one (usually z-slicing).
 * 
 * @author dlegland
 *
 */
public class SlicedBinaryArray3D extends BinaryArray3D
{
	// =============================================================
	// Class fields

	ArrayList<BinaryArray> slices;

	
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
	public SlicedBinaryArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.slices = new ArrayList<>(size2);
		for (int z = 0; z < size2; z++)
		{
			this.slices.add(BinaryArray2D.create(size0, size1));
		}
	}

	/**
	 * Creates a new instance by specifying the list of slices.
	 * 
	 * @param slices the list of slices composing the new 3D array.
	 */
	public SlicedBinaryArray3D(Collection<? extends BinaryArray> slices)
	{
		super(0,0,0);
		if (slices.size() == 0)
		{
			return;
		}
		
		// check slices dimensionality
		for (BinaryArray slice : slices)
		{
			if (slice.dimensionality() < 2)
			{
				throw new IllegalArgumentException("Slices must have two dimensions");
			}
		}
		
		// check slices have same dimensions
		BinaryArray slice0 = slices.iterator().next();
		int size0 = slice0.getSize(0);
		int size1 = slice0.getSize(1);
		for (BinaryArray slice : slices)
		{
			if (slice.getSize(0) != size0 || slice.getSize(1) != size1)
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
		for (BinaryArray slice : slices)
		{
			this.slices.add(slice);
		}
	}


	// =============================================================
	// Specialization of the BinaryArray3D interface

	/* (non-Javadoc)
	 * @see net.sci.array.scalar.BinaryArray3D#getBoolean(int, int, int)
	 */
	@Override
	public boolean getBoolean(int x, int y, int z)
	{
		return this.slices.get(z).getBoolean(new int[]{x, y});
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.scalar.BinaryArray3D#setBoolean(int, int, int, boolean)
	 */
	@Override
	public void setBoolean(int x, int y, int z, boolean b)
	{
		this.slices.get(z).setBoolean(new int[]{x, y}, b);
	}

	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public BinaryArray3D duplicate()
	{
		ArrayList<BinaryArray> newSlices = new ArrayList<BinaryArray>(this.size2);
		for (BinaryArray slice : this.slices)
		{
			newSlices.add(slice.duplicate());
		}
		return new SlicedBinaryArray3D(newSlices);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public BinaryArray.Iterator iterator()
	{
		return new BinaryIterator();
	}
	
	private class BinaryIterator implements BinaryArray.Iterator
	{
		int sliceIndex = 0;
		BinaryArray.Iterator sliceIterator;
				
		public BinaryIterator() 
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
		public Binary next()
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
		public Binary get()
		{
			return sliceIterator.get();
		}

		@Override
		public boolean getBoolean()
		{
			return sliceIterator.getBoolean();
		}

		@Override
		public void setBoolean(boolean b)
		{
			sliceIterator.setBoolean(b);
		}
	}
}
