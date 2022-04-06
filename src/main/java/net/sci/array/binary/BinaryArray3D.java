/**
 * 
 */
package net.sci.array.binary;

import net.sci.array.scalar.IntArray;
import net.sci.array.scalar.IntArray3D;
import net.sci.array.scalar.TriFunction;


/**
 * A three-dimensional array containing boolean values.
 * 
 * @author dlegland
 *
 */
public abstract class BinaryArray3D extends IntArray3D<Binary> implements BinaryArray
{
	// =============================================================
	// Static methods

	public static final BinaryArray3D create(int size0, int size1, int size2)
	{
	    return new RunLengthBinaryArray3D(size0, size1, size2);
	}
	
    public final static BinaryArray3D wrap(BinaryArray array)
    {
        if (array instanceof BinaryArray3D)
        {
            return (BinaryArray3D) array;
        }
        return new Wrapper(array);
    }

    
	// =============================================================
	// Constructor

	protected BinaryArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

	
    // =============================================================
    // Methods specific to BooleanArray3D

    /**
     * Initializes the content of the binary array by using the specified
     * function of two variables.
     * 
     * Example:
     * 
     * <pre>
     * {@code
     *     BinaryArray3D array = BinaryArray3D.create(5, 4, 3);
     *     array.fillBooleans((x, y, z) -> (x + y * 10 + z) > 120);
     * }
     * </pre>
     * 
     * @param fun
     *            a function of two variables that returns a boolean. The two
     *            input variables correspond to the x and y coordinates.
     */
    public void fillBooleans(TriFunction<Integer,Integer,Integer,Boolean> fun)
    {
        for (int[] pos : this.positions())
        {
            this.setBoolean(pos, fun.apply(pos[0], pos[1], pos[2]));
        }
    }
    
    
    // =============================================================
    // New methods

    public abstract void setBoolean(int x, int y, int z, boolean b);


    // =============================================================
    // Management of slices

    public BinaryArray2D slice(int sliceIndex)
    {
        return new SliceView(sliceIndex);
    }

    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public Iterable<? extends BinaryArray2D> slices()
    {
        return new Iterable<BinaryArray2D>()
        {
            @Override
            public java.util.Iterator<BinaryArray2D> iterator()
            {
                return new SliceIterator();
            }
        };
    }

    public java.util.Iterator<? extends BinaryArray2D> sliceIterator()
    {
        return new SliceIterator();
    }

	
	// =============================================================
	// Specialization of the BooleanArray interface

    /* (non-Javadoc)
     * @see net.sci.array.data.BooleanArray#complement()
     */
    @Override
    public BinaryArray3D complement()
    {
        BinaryArray3D result = BinaryArray3D.create(this.size0, this.size1, this.size2);
	    for (int[] pos : positions())
	    {
	    	result.setBoolean(pos, !getBoolean(pos));
	    }
        return result;
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.data.scalar2d.BooleanArray3D#setState(int, int, int, boolean)
     */
    @Override
    public void setBoolean(int[] pos, boolean b)
    {
       setBoolean(pos[0], pos[1], pos[2], b);
    }
    

    // =============================================================
    // Specialization of IntArray3D interface

    @Override
    public void setInt(int x, int y, int z, int value)
    {
        setBoolean(x, y, z, value > 0);
    }
    
    
    // =============================================================
    // Implementation of the ScalarArray3D interface
    
    @Override
    public void setValue(int x, int y, int z, double value)
    {
        setBoolean(x, y, z, value > 0);
    }
    
    
    // =============================================================
    // Implementation of the Array3D interface
    
    @Override
    public void set(int x, int y, int z, Binary value)
    {
        setBoolean(x, y, z, value.state);
    }

    
	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public BinaryArray3D duplicate()
    {
	    BinaryArray3D res = BinaryArray3D.create(size0, size1, size2);
        for (int z = 0; z < size2; z++)
        {
            for (int y = 0; y < size1; y++)
            {
                for (int x = 0; x < size0; x++)
                {
                    res.setBoolean(x, y, z, getBoolean(x, y, z));
                }
            }
        }
        return res;
    }

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#get(int, int, int)
	 */
	@Override
	public Binary get(int... pos)
	{
		return new Binary(getBoolean(pos));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
	 */
	public void set(int[] pos, Binary value)
	{
		setBoolean(pos, value.getBoolean());
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#getValue(int, int, int)
	 */
	@Override
	public double getValue(int... pos)
	{
		return getBoolean(pos) ? 1 : 0;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#setValue(int, int, int, double)
	 */
	@Override
	public void setValue(int[] pos, double value)
	{
		setBoolean(pos, value > 0);
	}

	
    // =============================================================
    // Inner Wrapper class

    private static class Wrapper extends BinaryArray3D
    {
        private BinaryArray array;
        
        protected Wrapper(BinaryArray array)
        {
            super(0, 0, 0);
            if (array.dimensionality() < 3)
            {
                throw new IllegalArgumentException("Requires an array with at least three dimensions");
            }
            this.array = array;
            this.size0 = array.size(0);
            this.size1 = array.size(1);
            this.size2 = array.size(2);
        }

        @Override
        public void setBoolean(int x, int y, int z, boolean b)
        {
            this.array.setBoolean(new int[] {x, y, z}, b);
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
    
    private class SliceView extends BinaryArray2D
    {
        int sliceIndex;
        
        protected SliceView(int slice)
        {
            super(BinaryArray3D.this.size0, BinaryArray3D.this.size1);
            if (slice < 0 || slice >= BinaryArray3D.this.size2)
            {
                throw new IllegalArgumentException(String.format(
                        "Slice index %d must be comprised between 0 and %d", slice, BinaryArray3D.this.size2));
            }
            this.sliceIndex = slice;
        }

        @Override
        public void setBoolean(int x, int y, boolean b)
        {
            BinaryArray3D.this.setBoolean(new int[] {x, y, this.sliceIndex}, b);
        }
        
        @Override
        public boolean getBoolean(int... pos)
        {
            return BinaryArray3D.this.getBoolean(pos[0], pos[1], this.sliceIndex);
        }

        @Override
        public void setBoolean(int[] pos, boolean bool)
        {
            BinaryArray3D.this.setBoolean(pos[0], pos[1], this.sliceIndex, bool);            
        }
    }
    
    private class SliceIterator implements java.util.Iterator<BinaryArray2D> 
    {
        int sliceIndex = 0;

        @Override
        public boolean hasNext()
        {
            return sliceIndex < BinaryArray3D.this.size2;
        }

        @Override
        public BinaryArray2D next()
        {
            // default implementation in BinaryArray3D returns an instance of 
            // BinaryArray3D.SliceView 
            return BinaryArray3D.this.slice(sliceIndex++);
        }
    }

}
