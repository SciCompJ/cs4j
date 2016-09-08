/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Array;

/**
 * Base implementation for arrays of any dimensionality.
 * 
 * @author dlegland
 */
public abstract class ArrayND<T> implements Array<T>
{
	// =============================================================
	// Class fields

	protected int[] sizes;
	
	// =============================================================
	// Constructors

	protected ArrayND(int[] sizes)
	{
		this.sizes = sizes;
	}


	
	// =============================================================
	// Utility methods

	/**
	 * Computes the number of image elements.
	 * @return the number of image elements ("pixels")
	 */
	protected int elementNumber()
	{
		int number = 1;
		for (int d = 0; d < this.sizes.length; d++)
		{
			number *= this.sizes[d];
		}
		return number;
	}
	
	/**
	 * Converts a list of indices along each dimension into a linear index.
	 * 
	 * @param inds
	 *            the list of indices for each dimension
	 * @return a linear index
	 */
	protected int subsToInd(int[] inds)
	{
		int index = 0;
		int offset = 1;
		for (int d = 0; d < this.sizes.length; d++)
		{
			index += inds[d] * offset;
			offset *= this.sizes[d];
		}
		return index;
	}


	// =============================================================
	// Specialization of the Array interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.Array#getDimension()
	 */
	@Override
	public int dimensionality()
	{
		return this.sizes.length;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#getSize()
	 */
	@Override
	public int[] getSize()
	{
		int[] res = new int[this.sizes.length];
		for (int d=0; d < this.sizes.length; d++)
		{
			res[d] = this.sizes[d];
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#getSize(int)
	 */
	@Override
	public int getSize(int dim)
	{
		return this.sizes[dim];
	}

//	/* (non-Javadoc)
//	 * @see net.sci.array.Array#getValueRange()
//	 */
//	@Override
//	public double[] getValueRange()
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see net.sci.array.Array#fill(double)
//	 */
//	@Override
//	public void fill(double value)
//	{
//		// TODO Auto-generated method stub
//
//	}
//
}
