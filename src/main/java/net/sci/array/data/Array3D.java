/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Array;
import net.sci.array.Cursor;
import net.sci.array.CursorIterator;

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

    @Override
    public abstract Array3D<T> duplicate();

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
	
	public CursorIterator<Cursor3D> cursorIterator()
	{
		return new Cursor3DIterator();
	}


	/**
	 * Iterator over the positions of an array.
	 * 
	 * @author dlegland
	 *
	 */
	public class Cursor3DIterator implements CursorIterator<Cursor3D>
	{
		int posX = -1;
		int posY = 0;
		int posZ = 0;
		
		public Cursor3DIterator()
		{
			
		}
		
		@Override
		public boolean hasNext()
		{
			return posX < size0 - 1 || posY < size1 - 1 || posZ < size2 - 1;
		}

		@Override
		public Cursor3D next()
		{
			forward();
			return new Cursor3D(posX, posY, posZ);
		}

		@Override
		public void forward()
		{
			posX++;
			if (posX == size0)
			{
				posX = 0;
				posY++;
				if (posY == size1)
				{
					posY = 0;
					posZ++;
				}
			}
		}

		@Override
		public int[] getPosition()
		{
			return new int[]{posX, posY, posZ};
		}
	}
	
	public class Cursor3D implements Cursor
	{
		int x;
		int y;
		int z;

		public Cursor3D(int x, int y, int z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public int[] getPosition()
		{
			return new int[]{x, y, z};
		}

		@Override
		public int getPosition(int dim)
		{
			switch(dim)
			{
			case 0: return x;
			case 1: return y;
			case 2: return z;
			default: throw new IllegalArgumentException("Requires dimension beween 0 and 2");
			}
		}
		
	}
}
