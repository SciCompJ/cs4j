/**
 * 
 */
package net.sci.array.scalar;

/**
 * A two-dimensional array containing boolean values.
 * 
 * @author dlegland
 *
 */
public abstract class BinaryArray2D extends IntArray2D<Binary> implements BinaryArray
{
	// =============================================================
	// Static methods

	/**
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 * @return a new instance of BooleanArray2D
	 */
	public static final BinaryArray2D create(int size0, int size1)
	{
		return new BufferedBinaryArray2D(size0, size1);
	}

    public final static BinaryArray2D wrap(BinaryArray array)
    {
        if (array instanceof BinaryArray2D)
        {
            return (BinaryArray2D) array;
        }
        return new Wrapper(array);
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
	protected BinaryArray2D(int size0, int size1)
	{
		super(size0, size1);
	}

	
	// =============================================================
	// New methods

	public abstract void setBoolean(int x, int y, boolean b);
	
	
	// =============================================================
	// Specialization of the BinaryArray interface
	
    /* (non-Javadoc)
     * @see net.sci.array.scalar.BinaryArray#complement()
     */
    @Override
    public BinaryArray2D complement()
    {
        BinaryArray2D result = BinaryArray2D.create(size(0), size(1));
	    for (int[] pos : positions())
	    {
	    	result.setBoolean(pos, !getBoolean(pos));
	    }
        return result;
    }
  
    /* (non-Javadoc)
     * @see net.sci.array.scalar.BinaryArray#setBoolean(int[], boolean)
     */
    @Override
    public void setBoolean(int[] pos, boolean state)
    {
        setBoolean(pos[0], pos[1], state);
    }
    

    // =============================================================
	// Specialization of IntArray2D interface

    @Override
    public void setInt(int x, int y, int value)
    {
        setBoolean(x, y, value > 0);
    }
    

	// =============================================================
	// Specialization of Array2D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public Binary get(int... pos)
	{
		return new Binary(getBoolean(pos));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int[] pos, Binary value)
	{
		setBoolean(pos, value.getBoolean());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#getValue(int, int)
	 */
	@Override
	public double getValue(int... pos)
	{
		return getBoolean(pos) ? 1 : 0;
	}

	/**
	 * Sets the logical state at the specified position.
	 * 
	 * @see net.sci.array.Array2D#setValue(int, int, double)
	 */
	@Override
	public void setValue(int[] pos, double value)
	{
		setBoolean(pos, value > 0);
	}
	
	
    // =============================================================
    // Implementation of the ScalarArray2D interface
    
    @Override
    public void setValue(int x, int y, double value)
    {
        setBoolean(x, y, value > 0);
    }
    
    
    // =============================================================
    // Implementation of the Array2D interface
    
    @Override
    public void set(int x, int y, Binary value)
    {
        setBoolean(x, y, value.state);
    }

    
    // =============================================================
	// Specialization of Array interface
	
	@Override
	public BinaryArray newInstance(int... dims)
	{
		return BinaryArray.create(dims);
	}

    @Override
    public BinaryArray2D duplicate()
    {
        BinaryArray2D res = BinaryArray2D.create(size0, size1);
        for (int y = 0; y < size1; y++)
        {
            for (int x = 0; x < size0; x++)
            {
                res.setBoolean(x, y, getBoolean(x, y));
            }
        }
        return res;
    }

    
    // =============================================================
    // Inner Wrapper class

    private static class Wrapper extends BinaryArray2D
    {
        private BinaryArray array;
        
        protected Wrapper(BinaryArray array)
        {
            super(0, 0);
            if (array.dimensionality() < 2)
            {
                throw new IllegalArgumentException("Requires an array with at least two dimensions");
            }
            this.array = array;
            this.size0 = array.size(0);
            this.size1 = array.size(1);
        }

        @Override
        public void setBoolean(int x, int y, boolean state)
        {
            this.array.setBoolean(new int[] {x, y}, state);
        }

        @Override
        public boolean getBoolean(int... pos)
        {
            return this.array.getBoolean(pos);
        }

        @Override
        public void setBoolean(int[] pos, boolean state)
        {
            this.array.setBoolean(pos, state);
        }

        @Override
        public IntArray.Factory<Binary> getFactory()
        {
            return this.array.getFactory();
        }

        
        @Override
        public BinaryArray2D duplicate()
        {
            BinaryArray2D result = BinaryArray2D.create(this.size0, this.size1);
            
            int nd = this.array.dimensionality();
            int[] pos = new int[nd];

            // Fill new array with input array
            for (int y = 0; y < this.size1; y++)
            {
                pos[1] = y;
                for (int x = 0; x < this.size0; x++)
                {
                    pos[0] = x;
                    result.setBoolean(x, y, this.array.getBoolean(pos));
                }
            }

            return result;
        }
        
        @Override
        public Class<Binary> dataType()
        {
            return array.dataType();
        }

        @Override
        public BinaryArray.Iterator iterator()
        {
            return array.iterator();
        }
    }    
}

