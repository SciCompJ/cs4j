/**
 * 
 */
package net.sci.array.binary;

import java.util.function.BiFunction;

import net.sci.array.scalar.IntArray2D;

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
     * Creates a new empty 2D binary array. Uses the default factory, using a
     * wrapper to BinaryArray2D if necessary.
     * 
     * @param size0
     *            the size of the array along the first dimension
     * @param size1
     *            the size of the array along the second dimension
     * @return a new BinaryArray2D with the requested size.
     */
	public static final BinaryArray2D create(int size0, int size1)
	{
		return wrap(BinaryArray.create(size0, size1));
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
    // Methods specific to BooleanArray2D

    /**
     * Initializes the content of the binary array by using the specified
     * function of two variables.
     * 
     * Example:
     * 
     * <pre>
     * {@code
     *     BinaryArray2D array = BinaryArray2D.create(5, 4);
     *     array.fillBooleans((x, y) -> (x + y * 10) > 20);
     * }
     * </pre>
     * 
     * @param fun
     *            a function of two variables that returns a boolean. The two
     *            input variables correspond to the x and y coordinates.
     */
    public void fillBooleans(BiFunction<Integer,Integer,Boolean> fun)
    {
        for (int[] pos : this.positions())
        {
            this.setBoolean(pos, fun.apply(pos[0], pos[1]));
        }
    }
    

	// =============================================================
	// New methods

    public abstract boolean getBoolean(int x, int y);
    
    public abstract void setBoolean(int x, int y, boolean b);
    
	
	// =============================================================
	// Specialization of the BinaryArray interface
	
    /* (non-Javadoc)
     * @see net.sci.array.binary.BinaryArray#complement()
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
     * @see net.sci.array.binary.BinaryArray#setBoolean(int[], boolean)
     */
    @Override
    public boolean getBoolean(int[] pos)
    {
        return getBoolean(pos[0], pos[1]);
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.binary.BinaryArray#setBoolean(int[], boolean)
     */
    @Override
    public void setBoolean(int[] pos, boolean state)
    {
        setBoolean(pos[0], pos[1], state);
    }
    

    // =============================================================
	// Specialization of IntArray2D interface

    @Override
    public int getInt(int x, int y)
    {
        return getBoolean(x, y) ? 1 : 0;
    }
    
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
	public Binary get(int[] pos)
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
	public double getValue(int[] pos)
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
    public double getValue(int x, int y)
    {
        return getBoolean(x, y) ? 1.0 : 0.0;
    }
    
    @Override
    public void setValue(int x, int y, double value)
    {
        setBoolean(x, y, value > 0);
    }
    
    
    // =============================================================
    // Implementation of the Array2D interface
    
    @Override
    public Binary get(int x, int y)
    {
        return new Binary(getBoolean(x, y));
    }
    
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
        public boolean getBoolean(int x, int y)
        {
            return this.array.getBoolean(new int[] {x, y});
        }

        @Override
        public void setBoolean(int x, int y, boolean state)
        {
            this.array.setBoolean(new int[] {x, y}, state);
        }

        @Override
        public boolean getBoolean(int[] pos)
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
        public Class<Binary> elementClass()
        {
            return array.elementClass();
        }

        @Override
        public BinaryArray.Iterator iterator()
        {
            return array.iterator();
        }
    }    
}

