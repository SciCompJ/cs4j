/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public abstract class Float64Array2D extends ScalarArray2D<Float64> implements Float64Array
{
	// =============================================================
	// Static methods

	public static final Float64Array2D create(int size0, int size1)
	{
		return new BufferedFloat64Array2D(size0, size1);
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
	protected Float64Array2D(int size0, int size1)
	{
		super(size0, size1);
	}


	// =============================================================
	// Specialization of Array2D 

	
	// =============================================================
	// Specialization of Array 

	@Override
	public Float64Array newInstance(int... dims)
	{
		return Float64Array.create(dims);
	}

    @Override
    public Float64Array2D duplicate()
    {
        Float64Array2D res = Float64Array2D.create(size0, size1);
        for (int y = 0; y < size1; y++)
        {
            for (int x = 0; x < size0; x++)
            {
                res.setValue(getValue(x, y), x, y);
            }
        }
        return res;
    }

}
