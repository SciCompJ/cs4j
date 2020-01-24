/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public abstract class Int32Array2D extends IntArray2D<Int32> implements Int32Array
{
	// =============================================================
	// Static methods

	public static final Int32Array2D create(int size0, int size1)
	{
		return new BufferedInt32Array2D(size0, size1);
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
	protected Int32Array2D(int size0, int size1)
	{
		super(size0, size1);
	}

	
	// =============================================================
	// Specialization of Array2D interface

	// =============================================================
	// Specialization of Array interface
	
    @Override
    public Int32Array2D duplicate()
    {
        // create output array
        Int32Array2D res = Int32Array2D.create(this.size0, this.size1);

        for (int y = 0; y < size1; y++)
        {
            for (int x = 0; x < size0; x++)
            {
                res.setInt(getInt(x, y), x, y);
            }
        }
        
        return res;
    }

}
