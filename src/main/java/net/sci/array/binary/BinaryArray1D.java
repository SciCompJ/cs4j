/**
 * 
 */
package net.sci.array.binary;

import net.sci.array.scalar.IntArray1D;

/**
 * A one-dimensional array containing boolean values.
 * 
 * @author dlegland
 *
 */
public abstract class BinaryArray1D extends IntArray1D<Binary> implements BinaryArray
{
	// =============================================================
	// Static methods

    /**
     * Creates a new empty 1D binary array. Uses the default factory, using a
     * wrapper to BinaryArray2D if necessary.
     * 
     * @param size0
     *            the size of the array along the first dimension
     * @param size1
     *            the size of the array along the second dimension
     * @return a new BinaryArray2D with the requested size.
     */
	public static final BinaryArray1D create(int size0)
	{
		return wrap(BinaryArray.create(size0));
	}

    public final static BinaryArray1D wrap(BinaryArray array)
    {
        if (array instanceof BinaryArray1D)
        {
            return (BinaryArray1D) array;
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
	 */
	protected BinaryArray1D(int size0)
	{
		super(size0);
	}
	

	// =============================================================
	// New methods

	public abstract void setBoolean(int x, boolean b);
	
	
	// =============================================================
	// Specialization of the BinaryArray interface
	
    /* (non-Javadoc)
     * @see net.sci.array.scalar.BinaryArray#complement()
     */
    @Override
    public BinaryArray1D complement()
    {
        BinaryArray1D result = BinaryArray1D.create(size(0));
	    for (int x = 0; x < size0; x++)
	    {
	    	result.setBoolean(x, !getBoolean(x));
	    }
        return result;
    }
  
    /* (non-Javadoc)
     * @see net.sci.array.scalar.BinaryArray#setBoolean(int[], boolean)
     */
    @Override
    public void setBoolean(int[] pos, boolean state)
    {
        setBoolean(pos[0], state);
    }
    

    // =============================================================
	// Specialization of IntArray1D interface

    @Override
    public void setInt(int x, int value)
    {
        setBoolean(x, value > 0);
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
    public void setValue(int x, double value)
    {
        setBoolean(x, value > 0);
    }
    
    
    // =============================================================
    // Implementation of the Array2D interface
    
    @Override
    public void set(int x, Binary value)
    {
        setBoolean(x, value.state);
    }

    
    // =============================================================
	// Specialization of Array interface
	
	@Override
	public BinaryArray newInstance(int... dims)
	{
		return BinaryArray.create(dims);
	}

    @Override
    public BinaryArray1D duplicate()
    {
        BinaryArray1D res = BinaryArray1D.create(size0);
        for (int x = 0; x < size0; x++)
        {
            res.setBoolean(x, getBoolean(x));
        }
        return res;
    }

    
    // =============================================================
    // Inner Wrapper class

    private static class Wrapper extends BinaryArray1D
    {
        private BinaryArray array;
        
        protected Wrapper(BinaryArray array)
        {
            super(0);
            this.array = array;
            this.size0 = array.size(0);
        }

        @Override
        public void setBoolean(int x, boolean state)
        {
            this.array.setBoolean(new int[] {x}, state);
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
        public BinaryArray.Factory factory()
        {
            return this.array.factory();
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
