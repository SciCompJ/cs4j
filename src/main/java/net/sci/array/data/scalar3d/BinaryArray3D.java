/**
 * 
 */
package net.sci.array.data.scalar3d;

import net.sci.array.ArrayFactory;
import net.sci.array.data.BinaryArray;
import net.sci.array.type.Binary;

/**
 * A three-dimensional array containing boolean values.
 * 
 * @author dlegland
 *
 */
public abstract class BinaryArray3D extends IntArray3D<Binary> implements BinaryArray
{
	// =============================================================
	// Static methods

	public static final BinaryArray3D create(int size0, int size1, int size2)
	{
		return new BufferedBinaryArray3D(size0, size1, size2);
	}
	
    public final static BinaryArray3D wrap(BinaryArray array)
    {
        if (array instanceof BinaryArray3D)
        {
            return (BinaryArray3D) array;
        }
        return new Wrapper(array);
    }

    
	// =============================================================
	// Constructor

	protected BinaryArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

	
	// =============================================================
	// New methods

	/**
	 * Returns the logical state at a given position.
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @param z
	 *            the z-coordinate of the position
	 * @return the boolean value at the given position
	 */
	public abstract boolean getBoolean(int x, int y, int z);

	/**
	 * Sets the logical state at a given position.
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 * @param z
	 *            the z-coordinate of the position
	 * @param state
	 *            the new state at the given position
	 */
	public abstract void setBoolean(int x, int y, int z, boolean state);
	
	
	// =============================================================
	// Specialization of the BooleanArray interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.BooleanArray#getState(int[])
	 */
	@Override
	public boolean getBoolean(int[] pos)
	{
		return getBoolean(pos[0], pos[1], pos[2]);
	}
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.BooleanArray#setState(int[], java.lang.Boolean)
	 */
	@Override
	public void setBoolean(int[] pos, boolean state)
	{
		setBoolean(pos[0], pos[1], pos[2], state);
	}

	
	// =============================================================
	// Specialization of IntArrayND interface

	public int getInt(int x, int y, int z)
	{
		return getBoolean(x, y, z) ? 1 : 0; 
	}

	public void setInt(int x, int y, int z, int value)
	{
		setBoolean(x, y, z, value != 0);
	}

	
	// =============================================================
	// Specialization of Array3D interface

	@Override
	public abstract BinaryArray3D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#get(int, int, int)
	 */
	@Override
	public Binary get(int x, int y, int z)
	{
		return new Binary(getBoolean(x, y, z));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
	 */
	public void set(int x, int y, int z, Binary value)
	{
		setBoolean(x, y, z, value.getBoolean());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#getValue(int, int, int)
	 */
	@Override
	public double getValue(int x, int y, int z)
	{
		return getBoolean(x, y, z) ? 1 : 0;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#setValue(int, int, int, double)
	 */
	@Override
	public void setValue(int x, int y, int z, double value)
	{
		setBoolean(x, y, z, value != 0);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public BinaryArray newInstance(int... dims)
	{
		return BinaryArray.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#get(int[])
	 */
	@Override
	public Binary get(int[] pos)
	{
		return new Binary(getBoolean(pos[0], pos[1], pos[2]));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#set(int[], java.lang.Object)
	 */
	public void set(int[] pos, Binary value)
	{
		setBoolean(pos[0], pos[1], pos[2], value.getBoolean());
	}
	
    // =============================================================
    // Inner Wrapper class

    private static class Wrapper extends BinaryArray3D
    {
        private BinaryArray array;
        
        protected Wrapper(BinaryArray array)
        {
            super(0, 0, 0);
            if (array.dimensionality() < 3)
            {
                throw new IllegalArgumentException("Requires an array with at least three dimensions");
            }
            this.array = array;
            this.size0 = array.getSize(0);
            this.size1 = array.getSize(1);
            this.size2 = array.getSize(2);
        }

        @Override
        public boolean getBoolean(int x, int y, int z)
        {
            return this.array.getBoolean(new int[] {x, y, z});
        }

        @Override
        public void setBoolean(int x, int y, int z, boolean state)
        {
            this.array.setBoolean(new int[] {x, y, z}, state);
        }

        @Override
        public ArrayFactory<Binary> getFactory()
        {
            return this.array.getFactory();
        }

        
        @Override
        public BinaryArray3D duplicate()
        {
            BinaryArray tmp = this.array.newInstance(this.size0, this.size1, this.size2);
            if (!(tmp instanceof BinaryArray3D))
            {
                // ensure result is instance of BinaryArray3D
                tmp = new Wrapper(tmp);
            }
            
            BinaryArray3D result = (BinaryArray3D) tmp;
            
            BinaryArray.Iterator iter1 = this.array.iterator();
            BinaryArray.Iterator iter2 = result.iterator();
            
            // Fill new array with input array
            while(iter1.hasNext() && iter2.hasNext())
            {
                iter2.setNextBoolean(iter1.nextBoolean());
            }

            return result;
        }
        
        @Override
        public Class<Binary> getDataType()
        {
            return array.getDataType();
        }

        @Override
        public BinaryArray.Iterator iterator()
        {
            return new Iterator3D();
        }
        
        private class Iterator3D implements BinaryArray.Iterator
        {
            int x = -1;
            int y = 0;
            int z = 0;
            
            public Iterator3D() 
            {
            }
            
            @Override
            public boolean hasNext()
            {
                return this.x < size0 - 1 || this.y < size1 - 1 || this.z < size2 - 1;
            }

            @Override
            public Binary next()
            {
                forward();
                return Wrapper.this.get(x, y, z);
            }

            @Override
            public void forward()
            {
                this.x++;
                if (this.x == size0)
                {
                    this.x = 0;
                    this.y++;
                    if (this.y == size1)
                    {
                        this.y = 0;
                        this.z++;
                    }
                }
            }

            @Override
            public Binary get()
            {
                return Wrapper.this.get(x, y, z);
            }

            @Override
            public void set(Binary value)
            {
                Wrapper.this.set(x, y, z, value);
            }

            @Override
            public boolean getBoolean()
            {
                return Wrapper.this.getBoolean(x, y, z);
            }

            @Override
            public void setBoolean(boolean b)
            {
                Wrapper.this.setBoolean(x, y, z, b);
            }
        }

    }
}
