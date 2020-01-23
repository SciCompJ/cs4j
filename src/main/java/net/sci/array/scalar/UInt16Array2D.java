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


	// =============================================================
	// Specialization of Array2D interface


	
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

}
