/**
 * 
 */
package net.sci.array.data.scalar3d;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.array.data.UInt8Array;
import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.array.type.UInt8;

/**
 * A 3D array of UInt8 sliced in several planar slices. 
 * Slicing direction is the last one (usually z-slicing).
 * 
 * @author dlegland
 *
 */
public class SlicedUInt8Array3D extends UInt8Array3D
{
	// =============================================================
	// Class fields

	ArrayList<UInt8Array> slices;

	
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
	public SlicedUInt8Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.slices = new ArrayList<>(size2);
		for (int z = 0; z < size2; z++)
		{
			this.slices.add(UInt8Array2D.create(size0, size1));
		}
	}

	/**
	 * Creates a new instance by specifying the list of slices.
	 * 
	 * @param slices the list of slices composing the new 3D array.
	 */
	public SlicedUInt8Array3D(Collection<? extends UInt8Array> slices)
	{
		super(0,0,0);
		if (slices.size() == 0)
		{
			return;
		}
		
		// check slices dimensionality
		for (UInt8Array slice : slices)
		{
			if (slice.dimensionality() < 2)
			{
				throw new IllegalArgumentException("Slices must have two dimensions");
			}
		}
		
		// check slices have same dimensions
		UInt8Array slice0 = slices.iterator().next();
		int size0 = slice0.getSize(0);
		int size1 = slice0.getSize(1);
		for (UInt8Array slice : slices)
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
		for (UInt8Array slice : slices)
		{
			this.slices.add(slice);
		}
	}


	// =============================================================
	// Specialization of the UInt8Array3D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.UInt8Array3D#getByte(int, int, int)
	 */
	@Override
	public byte getByte(int x, int y, int z)
	{
		return this.slices.get(z).getByte(new int[]{x, y});
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.data.scalar2d.UInt8Array3D#setByte(int, int, int, byte)
	 */
	@Override
	public void setByte(int x, int y, int z, byte b)
	{
		this.slices.get(z).setByte(new int[]{x, y}, b);
	}

	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public UInt8Array3D duplicate()
	{
		ArrayList<UInt8Array> newSlices = new ArrayList<UInt8Array>(this.size2);
		for (UInt8Array slice : this.slices)
		{
			newSlices.add(slice.duplicate());
		}
		return new SlicedUInt8Array3D(newSlices);
	}

	
	// =============================================================
	// Implementation of the Iterator interface

	public UInt8Array.Iterator iterator()
	{
		return new UInt8Iterator();
	}
	
	private class UInt8Iterator implements UInt8Array.Iterator
	{
		int sliceIndex = 0;
		UInt8Array.Iterator sliceIterator;
				
		public UInt8Iterator() 
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
		public UInt8 next()
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
		public UInt8 get()
		{
			return sliceIterator.get();
		}

		@Override
		public byte getByte()
		{
			return sliceIterator.getByte();
		}

		@Override
		public void setByte(byte b)
		{
			sliceIterator.setByte(b);
		}
	}
}
