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
	
    /**
     * Creates a new Float64Array2D from a two-dimensional array of double. The
     * first index of the double array is the second dimension of the result array,
     * i.e. <code>doubleArray[y][x]</code> is the same value as
     * <code>array.getValue(x,y)</code>.
     * 
     * @param floatArray
     *            the array of floats containing the values.
     * @return a new instance of Float64Array2D initialized with the values of
     *         <code>floatArray</code>
     */
    public static final Float64Array2D fromDoubleArray(double[][] doubleArray)
    {
        int size1 = doubleArray.length;
        int size0 = doubleArray[0].length;
        Float64Array2D res = Float64Array2D.create(size0, size1);
        for (int y = 0; y < size1; y++)
        {
            for (int x = 0; x < size0; x++)
            {
                res.setValue(x, y, doubleArray[y][x]);
            }
        }
        return res;
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

    @Override
    public void set(int x, int y, Float64 value)
    {
        setValue(x, y, value.value);
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.data.Array2D#setValue(int, int, double)
     */
    @Override
    public void setValue(int[] pos, double value)
    {
        setValue(pos[0], pos[1], value);
    }
    

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
                res.setValue(x, y, getValue(x, y));
            }
        }
        return res;
    }

}
