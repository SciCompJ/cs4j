/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public abstract class Int16Array2D extends IntArray2D<Int16> implements Int16Array
{
	// =============================================================
	// Static methods

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @return a new instance of Int16Array2D
	 */
	public static final Int16Array2D create(int size0, int size1)
	{
		return new BufferedInt16Array2D(size0, size1);
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
	protected Int16Array2D(int size0, int size1)
	{
		super(size0, size1);
	}

	// =============================================================
	// New methods
	
    public abstract void setShort(int x, int y, short s);

	
	// =============================================================
	// Specialization of the Int16Array interface

    public void setShort(int [] pos, short s)
    {
        setShort(pos[0], pos[1], s);
    }
    
    
    // =============================================================
    // Specialization of the IntArray2D interface

    @Override
    public void setInt(int x, int y, int value)
    {
        setShort(x, y, (short) Int16.clamp(value));
    }

    
	// =============================================================
	// Specialization of the ScalarArray2D interface

    @Override
    public void setValue(int x, int y, double value)
    {
        setShort(x, y, (short) Int16.clamp(value));
    }

    
	// =============================================================
	// Specialization of Array2D interface

    @Override
    public void set(int x, int y, Int16 value)
    {
        setShort(x, y, value.value);
    }


	// =============================================================
	// Specialization of Array interface
	
    @Override
    public Int16Array2D duplicate()
    {
        // create output array
        Int16Array2D res = Int16Array2D.create(this.size0, this.size1);

        for (int y = 0; y < size1; y++)
        {
            for (int x = 0; x < size0; x++)
            {
                res.setShort(x, y, getShort(x, y));
            }
        }
        
        return res;
    }
}
