/**
 * 
 */
package net.sci.array.scalar;

import net.sci.array.Array;


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
	    if (Array.countElements(size0, size1, size2) < Integer.MAX_VALUE)
	        return new BufferedBinaryArray3D(size0, size1, size2);
	    else 
	        return new SlicedBinaryArray3D(size0, size1, size2);
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
	// New methods


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
	    	result.setBoolean(!getBoolean(pos), pos);
	    }
        return result;
    }
	
	
	
	// =============================================================
	// Specialization of IntArrayND interface

	
	// =============================================================
	// Specialization of Array3D interface

	@Override
	public abstract BinaryArray3D duplicate();

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
	public void set(Binary value, int... pos)
	{
		setBoolean(value.getBoolean(), pos);
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
	public void setValue(double value, int... pos)
	{
		setBoolean(value > 0, pos);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public BinaryArray newInstance(int... dims)
	{
		return BinaryArray.create(dims);
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
        public boolean getBoolean(int... pos)
        {
            return this.array.getBoolean(pos);
        }

        @Override
        public void setBoolean(boolean state, int... pos)
        {
            this.array.setBoolean(state, pos);
        }

        @Override
        public IntArray.Factory<Binary> getFactory()
        {
            return this.array.getFactory();
        }

        
        @Override
        public BinaryArray3D duplicate()
        {
            BinaryArray3D result = BinaryArray3D.create(array.size(0), array.size(1), array.size(2));
    	    for (int[] pos : array.positions())
    	    {
    	    	result.setBoolean(array.getBoolean(pos), pos);
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
            return new Iterator3D();
        }
        
        private class Iterator3D implements BinaryArray.Iterator
        {
            int x = -1;
            int y = 0;
            int z = 0;
            
            public Iterator3D() 
            {
            }
            
            @Override
            public boolean hasNext()
            {
                return this.x < size0 - 1 || this.y < size1 - 1 || this.z < size2 - 1;
            }

            @Override
            public Binary next()
            {
                forward();
                return Wrapper.this.get(x, y, z);
            }

            @Override
            public void forward()
            {
                this.x++;
                if (this.x == size0)
                {
                    this.x = 0;
                    this.y++;
                    if (this.y == size1)
                    {
                        this.y = 0;
                        this.z++;
                    }
                }
            }

            @Override
            public Binary get()
            {
                return Wrapper.this.get(x, y, z);
            }

            @Override
            public void set(Binary value)
            {
                Wrapper.this.set(value, x, y, z);
            }

            @Override
            public boolean getBoolean()
            {
                return Wrapper.this.getBoolean(x, y, z);
            }

            @Override
            public void setBoolean(boolean b)
            {
                Wrapper.this.setBoolean(b, x, y, z);
            }
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
        public boolean getBoolean(int... pos)
        {
            return BinaryArray3D.this.getBoolean(pos[0], pos[1], this.sliceIndex);
        }

        @Override
        public void setBoolean(boolean bool, int... pos)
        {
            BinaryArray3D.this.setBoolean(bool, pos[0], pos[1], this.sliceIndex);            
        }

        @Override
        public net.sci.array.scalar.BinaryArray.Iterator iterator()
        {
            return new Iterator();
        }

        class Iterator implements BinaryArray.Iterator
        {
            int indX = -1;
            int indY = 0;
            
            public Iterator() 
            {
            }
            
            @Override
            public Binary next()
            {
                forward();
                return get();
            }

            @Override
            public void forward()
            {
                indX++;
                if (indX >= size0)
                {
                    indX = 0;
                    indY++;
                }
            }

            @Override
            public boolean hasNext()
            {
                return indX < size0 - 1 || indY < size1 - 1;
            }

            @Override
            public boolean getBoolean()
            {
                return BinaryArray3D.this.getBoolean(indX, indY, sliceIndex);
            }

            @Override
            public void setBoolean(boolean b)
            {
                BinaryArray3D.this.setBoolean(b, indX, indY, sliceIndex);
            }
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
            return new SliceView(sliceIndex++);
        }
    }

}
