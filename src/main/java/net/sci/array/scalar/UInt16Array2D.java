/**
 * 
 */
package net.sci.array.scalar;


/**
 * @author dlegland
 *
 */
public abstract class UInt16Array2D extends IntArray2D<UInt16> implements UInt16Array
{
	// =============================================================
	// Static methods

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @return a new instance of UInt16Array2D
	 */
	public static final UInt16Array2D create(int size0, int size1)
	{
		return new BufferedUInt16Array2D(size0, size1);
	}
	
	
	// =============================================================
	// Constructor

	/**
	 * Initialize the protected size variables. 
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 */
	protected UInt16Array2D(int size0, int size1)
	{
		super(size0, size1);
	}

	
	// =============================================================
	// New methods

	
	// =============================================================
	// Specialization of the UInt16Array interface


	// =============================================================
	// Specialization of IntArray2D interface

	public int getInt(int x, int y)
	{
		return getShort(x, y) & 0x00FFFF; 
	}

	public void setInt(int x, int y, int value)
	{
		setShort((short) Math.min(Math.max(value, 0), UInt16.MAX_VALUE), x, y);
	}

	// =============================================================
	// Specialization of Array2D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public UInt16 get(int x, int y)
	{
		return new UInt16(getShort(x, y));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, UInt16 value)
	{
		setShort(value.getShort(), x, y);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#getValue(int, int)
	 */
	@Override
	public double getValue(int x, int y)
	{
		return getShort(x, y) & 0x00FFFF;
	}

	/**
	 * Sets the value at the specified position, by clamping the value between 0
	 * and 2^16-1.
	 * 
	 * @see net.sci.array.Array2D#setValue(int, int, double)
	 */
	@Override
	public void setValue(int x, int y, double value)
	{
		setShort((short) Math.min(Math.max(value, 0), UInt16.MAX_VALUE), x, y);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public UInt16Array newInstance(int... dims)
	{
		return UInt16Array.create(dims);
	}

	@Override
	public UInt16Array2D duplicate()
	{
        // create output array
        UInt16Array2D res = UInt16Array2D.create(this.size0, this.size1);

        for (int y = 0; y < size1; y++)
        {
            for (int x = 0; x < size0; x++)
            {
                res.setShort(getShort(x, y), x, y);
            }
        }
        
        return res;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#get(int[])
	 */
	@Override
	public UInt16 get(int[] pos)
	{
		return new UInt16(getShort(pos));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#set(int[], java.lang.Object)
	 */
	@Override
	public void set(int[] pos, UInt16 value)
	{
		setShort(value.getShort(), pos[0], pos[1]);
	}
}
