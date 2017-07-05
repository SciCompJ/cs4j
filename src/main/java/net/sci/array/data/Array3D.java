/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Array;

/**
 * Base implementation for three-dimensional array.
 * 
 * @author dlegland
 *
 */
public abstract class Array3D<T> implements Array<T>
{
	// =============================================================
	// class members

	protected int size0;
	protected int size1;
	protected int size2;

	
	// =============================================================
	// Constructors

	/**
	 * Initialize the protected size variables. 
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @param size2
	 *            the size of the array along the third dimension
	 */
	protected Array3D(int size0, int size1, int size2)
	{
		this.size0 = size0;
		this.size1 = size1;
		this.size2 = size2;
	}

	// =============================================================
	// New methods

	public abstract T get(int x, int y, int z);

	public abstract void set(int x, int y, int z, T value);

	public abstract double getValue(int x, int y, int z);
	
	public abstract void setValue(int x, int y, int z, double value);
	
	
	// =============================================================
	// Specialization of the Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.Array#dimensionality()
	 */
	@Override
	public int dimensionality()
	{
		return 3;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#getSize()
	 */
	@Override
	public int[] getSize()
	{
		return new int[]{this.size0, this.size1, this.size2};
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#getSize(int)
	 */
	@Override
	public int getSize(int dim)
	{
		switch(dim)
		{
		case 0: return this.size0;
		case 1: return this.size1;
		case 2: return this.size2;
		default:
			throw new IllegalArgumentException("Dimension argument must be comprised between 0 and 2");
		}
	}

	public T get(int[] pos)
	{
		return get(pos[0], pos[1], pos[2]);
	}

	public void set(int[] pos, T value)
	{
		set(pos[0], pos[1], pos[2], value);
	}
	
	public double getValue(int[] pos)
	{
		return getValue(pos[0], pos[1], pos[2]);
	}

	public void setValue(int[] pos, double value)
	{
		setValue(pos[0], pos[1], pos[2], value);
	}
}
