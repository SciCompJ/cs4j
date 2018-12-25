/**
 * 
 */
package net.sci.array.scalar;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A 3D array of Float32 sliced in several planar slices. 
 * Slicing direction is the last one (usually z-slicing).
 * 
 * @author dlegland
 *
 */
public class SlicedFloat32Array3D extends Float32Array3D
{
	// =============================================================
	// Class fields

	ArrayList<Float32Array> slices;

	
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
	 * Creates a new instance by specifying the list of slices.
	 * 
	 * @param slices the list of slices composing the new 3D array.
	 */
	public SlicedFloat32Array3D(Collection<? extends Float32Array> slices)
	{
		super(0,0,0);
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
		int size0 = slice0.getSize(0);
		int size1 = slice0.getSize(1);
		for (Float32Array slice : slices)
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
		for (Float32Array slice : slices)
		{
			this.slices.add(slice);
		}
	}


	// =============================================================
	// Specialization of the Float32Array3D interface

	/* (non-Javadoc)
	 * @see net.sci.array.scalar.Float32Array3D#getFloat(int, int, int)
	 */
	@Override
	public float getFloat(int x, int y, int z)
	{
		return this.slices.get(z).getFloat(new int[]{x, y});
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.scalar.Float32Array3D#setFloat(int, int, int, float)
	 */
	@Override
	public void setFloat(int x, int y, int z, float floatValue)
	{
		this.slices.get(z).setFloat(new int[]{x, y}, floatValue);
	}

	
	// =============================================================
	// Specialization of the ScalarArray3D interface

	/* (non-Javadoc)
	 * @see net.sci.array.scalar.Float32Array3D#getDouble(int, int, int)
	 */
	@Override
	public double getValue(int x, int y, int z)
	{
		return this.slices.get(z).getValue(new int[]{x, y});
	}
		
	/* (non-Javadoc)
	 * @see net.sci.array.scalar.ScalarArray3D#setValue(int, int, int, double)
	 */
	@Override
	public void setValue(int x, int y, int z, double value)
	{
		this.slices.get(z).setValue(new int[]{x, y}, value);
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

		@Override
		public Float32 get()
		{
			return sliceIterator.get();
		}

//		@Override
//		public float getFloat()
//		{
//			return sliceIterator.getFloat();
//		}
//
//		@Override
//		public void setFloat(float floatValue)
//		{
//			sliceIterator.setFloat(floatValue);
//		}

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
