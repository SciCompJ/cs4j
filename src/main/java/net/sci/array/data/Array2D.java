/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Array;

/**
 * @author dlegland
 *
 */
public abstract class Array2D<T> implements Array<T>
{
	// =============================================================
	// class members

	protected int size0;
	protected int size1;
	
	// =============================================================
	// Constructors

	/**
	 * 
	 */
	protected Array2D(int size0, int size1)
	{
		this.size0 = size0;
		this.size1 = size1;
	}

	// =============================================================
	// New methods

	public abstract T get(int x, int y);

	public abstract void set(int x, int y, T value);

	public abstract double getValue(int x, int y);
	
	public abstract void setValue(int x, int y, double value);
	
	// =============================================================
	// Specialization of the Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.Array#dimensionality()
	 */
	@Override
	public int dimensionality()
	{
		return 2;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#getSize()
	 */
	@Override
	public int[] getSize()
	{
		return new int[]{this.size0, this.size1};
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
		default:
			throw new IllegalArgumentException("Dimension argument must be 0 or 1");
		}
	}

	@Override
	public abstract Array2D<T> duplicate();

	public T get(int[] pos)
	{
		return get(pos[0], pos[1]);
	}

	public void set(int[] pos, T value)
	{
		set(pos[0], pos[1], value);
	}
	
	public double getValue(int[] pos)
	{
		return getValue(pos[0], pos[1]);
	}

	public void setValue(int[] pos, double value)
	{
		setValue(pos[0], pos[1], value);
	}
}
