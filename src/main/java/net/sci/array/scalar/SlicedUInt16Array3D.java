/**
 * 
 */
package net.sci.array.scalar;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A 3D array of UInt16 sliced in several planar slices. 
 * Slicing direction is the last one (usually z-slicing).
 * 
 * @author dlegland
 *
 */
public class SlicedUInt16Array3D extends UInt16Array3D
{
	// =============================================================
	// Class fields

	ArrayList<UInt16Array> slices;

	
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
	public SlicedUInt16Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.slices = new ArrayList<>(size2);
		for (int z = 0; z < size2; z++)
		{
			this.slices.add(UInt16Array2D.create(size0, size1));
		}
	}

	/**
	 * Creates a new instance by specifying the list of slices.
	 * 
	 * @param slices the list of slices composing the new 3D array.
	 */
	public SlicedUInt16Array3D(Collection<? extends UInt16Array> slices)
	{
		super(0,0,0);
		if (slices.size() == 0)
		{
			return;
		}
		
		// check slices dimensionality
		for (UInt16Array slice : slices)
		{
			if (slice.dimensionality() < 2)
			{
				throw new IllegalArgumentException("Slices must have two dimensions");
			}
		}
		
		// check slices have same dimensions
		UInt16Array slice0 = slices.iterator().next();
		int size0 = slice0.size(0);
		int size1 = slice0.size(1);
		for (UInt16Array slice : slices)
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
		for (UInt16Array slice : slices)
		{
			this.slices.add(slice);
		}
	}


	// =============================================================
	// Specialization of the UInt16Array3D interface

	/* (non-Javadoc)
	 * @see net.sci.array.scalar.UInt16Array3D#getShort(int, int, int)
	 */
	@Override
	public short getShort(int... pos)
	{
		return this.slices.get(pos[2]).getShort(new int[]{pos[0], pos[1]});
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.scalar.UInt16Array3D#setShort(int, int, int, short)
	 */
	@Override
	public void setShort(short s, int... pos)
	{
		this.slices.get(pos[2]).setShort(s, new int[]{pos[0], pos[1]});
	}

	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public UInt16Array3D duplicate()
	{
		ArrayList<UInt16Array> newSlices = new ArrayList<UInt16Array>(this.size2);
		for (UInt16Array slice : this.slices)
		{
			newSlices.add(slice.duplicate());
		}
		return new SlicedUInt16Array3D(newSlices);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public UInt16Array.Iterator iterator()
	{
		return new UInt16Iterator();
	}
	
	private class UInt16Iterator implements UInt16Array.Iterator
	{
		int sliceIndex = 0;
		UInt16Array.Iterator sliceIterator;
				
		public UInt16Iterator() 
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
		public UInt16 next()
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
		public UInt16 get()
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
