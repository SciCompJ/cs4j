/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public abstract class Float32Array2D extends ScalarArray2D<Float32> implements Float32Array
{
	// =============================================================
	// Static methods

	public static final Float32Array2D create(int size0, int size1)
	{
		return new BufferedFloat32Array2D(size0, size1);
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
	protected Float32Array2D(int size0, int size1)
	{
		super(size0, size1);
	}

    // =============================================================
    // New methods
    

    // =============================================================
    // Specialization of Float32Array
    

	// =============================================================
	// Specialization of Array2D 
	
	// =============================================================
	// Specialization of Array 

	@Override
	public Float32Array newInstance(int... dims)
	{
		return Float32Array.create(dims);
	}

	@Override
    public Float32Array2D duplicate()
    {
        Float32Array2D res = Float32Array2D.create(size0, size1);
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
