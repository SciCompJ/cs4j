/**
 * 
 */
package net.sci.array.binary;

import java.util.function.Function;

import net.sci.array.Array;
import net.sci.array.process.type.ConvertToBinary;
import net.sci.array.scalar.IntArray;
import net.sci.array.scalar.ScalarArray;

/**
 * A multidimensional array containing boolean values.
 * 
 * @author dlegland
 *
 */
public interface BinaryArray extends IntArray<Binary>
{
    // =============================================================
    // Static variables

    public static final IntArray.Factory<Binary> factory = new IntArray.Factory<Binary>()
    {
        @Override
        public IntArray<Binary> create(int... dims)
        {
            return BinaryArray.create(dims);
        }

        @Override
        public BinaryArray create(int[] dims, Binary value)
        {
            BinaryArray array = BinaryArray.create(dims);
            array.fill(value);
            return array;
        }
    };
    
    
	// =============================================================
	// Static methods

	public static BinaryArray create(int... dims)
	{
		switch (dims.length)
		{
		case 2:
			return BinaryArray2D.create(dims[0], dims[1]);
		case 3:
			return BinaryArray3D.create(dims[0], dims[1], dims[2]);
		default:
			return BinaryArrayND.create(dims);
		}
	}
	
	public static BinaryArray create(int[] dims, boolean[] buffer)
	{
		switch (dims.length)
		{
		case 2:
			return new BufferedBinaryArray2D(dims[0], dims[1], buffer);
		case 3:
			return new BufferedBinaryArray3D(dims[0], dims[1], dims[2], buffer);
		default:
            return new BufferedBinaryArrayND(dims, buffer);
		}
	}

	/**
     * Converts an array to a binary array, by thresholding all values
     * strictly greater than zero.
     *
     * @see net.sci.array.process.type.ConvertToBinary
     * 
     * @param array
     *            an array
     * @return the binary array corresponding to values greater than zero.
     */
	public static BinaryArray convert(Array<?> array)
	{
	    return new ConvertToBinary().process(array);
	}

    public static BinaryArray wrap(ScalarArray<?> array)
    {
        if (array instanceof BinaryArray)
        {
            return (BinaryArray) array;
        }
        return new Wrapper(array);
    }
    

    // =============================================================
	// New methods

    /**
     * Fills this binary array using a function of the elements coordinates.
     * 
     * Example
     * <pre>{@code
     * BinaryArray array = BinaryArray.create(50, 50, 50);
     * array.fillBooleans(pos -> new Boolean(pos[0] < 25 || pos[1] > 30)); 
     * }
     * </pre>
     * 
     * @param fun
     *            the function used to populate the array.
     */
    public default void fillBooleans(Function<int[], Boolean> fun)
    {
        for (int[] pos : this.positions())
        {
            this.setBoolean(pos, fun.apply(pos));
        }
    }
    
    /**
     * Fills this binary array with the specified boolean value.
     * 
     * @param b
     *            the value to fill the array with.
     */
    public default void fill(boolean b)
    {
        for (int[] pos : this.positions())
        {
            this.setBoolean(pos, b);
        }
    }
    

    
	public boolean getBoolean(int... pos);
	
	public void setBoolean(int[] pos, boolean state);

	/**
	 * @return an Iterable over the positions of only true elements within the
	 *         array.
	 */
	public default Iterable<int[]> trueElementPositions()
	{
		return new Iterable<int[]>()
		{
			@Override
			public java.util.Iterator<int[]> iterator()
			{
				return new TrueElementsPositionIterator(BinaryArray.this);
			}
		};
	}

	public default PositionIterator trueElementPositionIterator()
	{
		return new TrueElementsPositionIterator(this);
	}
	
	/**
     * Returns the complement of this array. Replaces each 0 by 1, and each 1 by
     * 0.
     * 
     * @return the complement of this array.
     */
	public default BinaryArray complement()
	{
	    BinaryArray result = BinaryArray.create(this.size());
	    for (int[] pos : positions())
	    {
	    	result.setBoolean(pos, !getBoolean(pos));
	    }
        return result;
	}
	
	
	// =============================================================
	// Specialization of the IntArray interface

	@Override
	public default int getInt(int... pos)
	{
		return getBoolean(pos) ? 1 : 0; 
	}

	@Override
	public default void setInt(int[] pos, int value)
	{
		setBoolean(pos, value > 0);
	}

	
    // =============================================================
    // Specialization of the ScalarArray interface

    /**
     * Sets the value at the specified position, using true if value is greater
     * than zero.
     * 
     * @param pos
     *            the position to modify
     * @param value
     *            the value to set up. The position is set to true if value is
     *            greater than zero, and to false otherwise.
     * 
     */
    public default void setValue(int[] pos, double value)
    {
    	setBoolean(pos, value > 0);
    }

    
    // =============================================================
	// Specialization of the Array interface

	@Override
	public default BinaryArray newInstance(int... dims)
	{
		return BinaryArray.create(dims);
	}

    @Override
    public default Binary get(int... pos)
    {
        return new Binary(getBoolean(pos));
    }

    @Override
    public default void set(int[] pos, Binary value)
    {
        setBoolean(pos, value.getBoolean());
    }


	@Override
	public default IntArray.Factory<Binary> getFactory()
	{
		return factory;
	}

	@Override
	public default BinaryArray duplicate()
	{
		// create output array
		BinaryArray result = BinaryArray.create(this.size());
		
		// copy values into output array
		for(int[] pos : positions())
		{
			result.setBoolean(pos, this.getBoolean(pos));
		}
		
		// return output
		return result;
	}

	@Override
	public default Class<Binary> dataType()
	{
		return Binary.class;
	}

    public default Iterator iterator()
    {
        return new Iterator()
        {
            PositionIterator iter = positionIterator();

            @Override
            public boolean hasNext()
            {
                return iter.hasNext();
            }

            @Override
            public void forward()
            {
                iter.forward();
            }

            @Override
            public Binary next()
            {
                iter.forward();
                return BinaryArray.this.get(iter.get());
            }

            @Override
            public void setValue(double value)
            {
                BinaryArray.this.setBoolean(iter.get(), value > 0);
            }

            @Override
            public boolean getBoolean()
            {
                return BinaryArray.this.getBoolean(iter.get());
            }

            @Override
            public void setBoolean(boolean b)
            {
                BinaryArray.this.setBoolean(iter.get(), b);
            }
        };
    }

	
	// =============================================================
	// Inner interface

	public class TrueElementsPositionIterator implements PositionIterator
	{
	    /** The reference binary array */
		BinaryArray array;
		
		/** Iterator on the binary array */
		PositionIterator iter;
		
		/** the position on the current true element */
		int[] currentPos = null;

		public TrueElementsPositionIterator(BinaryArray array)
		{
			this.array = array;
			iter = array.positionIterator();
			findNextPos();
		}
		
		@Override
		public boolean hasNext()
		{
			return currentPos != null;
		}

		@Override
		public void forward()
		{
			findNextPos();			
		}

		private void findNextPos()
		{
			currentPos = null;
			while (iter.hasNext())
			{
				iter.forward();
				if (array.getBoolean(iter.get()))
				{
					currentPos = iter.get();
					break;
				}
			}
		}

		@Override
		public int[] next()
		{
			int[] pos = currentPos;
			forward();
			return pos;
		}

		@Override
		public int[] get()
		{
			return currentPos;
		}

        @Override
        public int[] get(int[] pos)
        {
            System.arraycopy(this.currentPos, 0, pos, 0, this.currentPos.length);
            return pos;
        }

        @Override
		public int get(int dim)
		{
			return currentPos[dim];
		}
	}

	// =============================================================
	// Inner interface

	public interface Iterator extends IntArray.Iterator<Binary>
	{
		/**
         * Moves this iterator to the next element and updates the value with
         * the specified boolean (optional operation).
         * 
         * @param b
         *            the new boolean value for the next position
         */
        public default void setNextBoolean(boolean b)
        {
            forward();
            setBoolean(b);
        }
        
        /**
         * Iterates and returns the next boolean.
         * 
         * @return the next int value.
         */
        public default boolean nextBoolean()
        {
            forward();
            return getBoolean();
        }
        
        /**
         * @return the current state pointed by this iterator
         */
        public boolean getBoolean();
        
        /**
         * Changes the state of the current position pointed by this iterator.
         * 
         * @param b
         *            the new state for the current position
         */
        public void setBoolean(boolean b);
        
		@Override
		public default int getInt()
		{
			return getBoolean() ? 1 : 0; 
		}

		@Override
		public default void setInt(int value)
		{
			setBoolean(value > 0);
		}

		@Override
		public default Binary get()
		{
		    return new Binary(getBoolean());
		}

		@Override
		public default void set(Binary value)
		{
		    setBoolean(value.getBoolean());
		}
	}
	
	/**
     * Wraps a scalar array into a BinaryArray with same dimension.
     * 
     * Conversion between scalar and binary:
     * <ul>
     * <li>scalar value &gt; 0: binary value TRUE</li>
     * <li>scalar value &lt; or = 0: binary value FALSE</li>
     * </ul>
     * 
     * @see BinaryArray#wrap(ScalarArray)
     */
    static class Wrapper implements BinaryArray
    {
        /** The parent array */
        ScalarArray<?> array;
        
        public Wrapper(ScalarArray<?> array)
        {
            this.array = array;
        }
        

        // =============================================================
        // Specialization of the Array interface
        
        @Override
        public boolean getBoolean(int... pos)
        {
            return array.getValue(pos) > 0;
        }

        @Override
        public void setBoolean(int[] pos, boolean value)
        {
            array.setValue(pos, value ? 1.0 : 0.0);
        }

        
        // =============================================================
        // Specialization of the Array interface

    	public Iterable<int[]> trueElementPositions()
    	{
    		return new Iterable<int[]>()
    		{
    			@Override
    			public java.util.Iterator<int[]> iterator()
    			{
    				return new ItemPositionIterator();
    			}
    		};
    	}
    	
        @Override
        public int dimensionality()
        {
            return array.dimensionality();
        }

        @Override
        public int[] size()
        {
            return array.size();
        }

        @Override
        public int size(int dim)
        {
            return array.size(dim);
        }

        @Override
        public Binary get(int... pos)
        {
            return new Binary(array.getValue(pos) > 0);
        }

        @Override
        public void set(int[] pos, Binary value)
        {
            array.setValue(pos, value.getValue());
        }

        @Override
        public PositionIterator positionIterator()
        {
            return array.positionIterator();
        }

        @Override
        public Iterator iterator()
        {
            return new Iterator(array.iterator());
        }
        

    	private class ItemPositionIterator implements PositionIterator
    	{
    		PositionIterator iter;
    		int[] nextPos = null;

    		public ItemPositionIterator()
    		{
    			iter = positionIterator();
    			findNextPos();
    		}
    		
    		@Override
    		public boolean hasNext()
    		{
    			return nextPos != null;
    		}

    		@Override
    		public void forward()
    		{
    			findNextPos();			
    		}

    		private void findNextPos()
    		{
    			nextPos = null;
    			while (iter.hasNext())
    			{
    				iter.forward();
    				if (getBoolean(iter.get()))
    				{
    					nextPos = iter.get();
    					break;
    				}
    			}
    		}

    		@Override
    		public int[] next()
    		{
    			forward();
    			return nextPos;
    		}

    		@Override
    		public int[] get()
    		{
    			return nextPos;
    		}

            @Override
            public int[] get(int[] pos)
            {
                System.arraycopy(this.nextPos, 0, pos, 0, this.nextPos.length);
                return pos;
            }

            @Override
    		public int get(int dim)
    		{
    			return nextPos[dim];
    		}
    	}

    	class Iterator implements BinaryArray.Iterator
        {
            ScalarArray.Iterator<?> iter;
            
            public Iterator(ScalarArray.Iterator<?> iter)
            {
                this.iter = iter;
            }

            @Override
            public boolean getBoolean()
            {
                return get().getBoolean();
            }

            @Override
            public void setBoolean(boolean b)
            {
                iter.setValue(new Binary(b).getValue());
            }

            @Override
            public Binary get()
            {
                return new Binary(iter.getValue() > 0);
            }

            @Override
            public void set(Binary value)
            {
                iter.setValue(value.getValue());
            }
            
            @Override
            public void forward()
            {
                this.iter.forward();
            }

            @Override
            public Binary next()
            {
                return new Binary(iter.nextValue() > 0);
            }

            @Override
            public boolean hasNext()
            {
                return iter.hasNext();
            }
        }
    }
}
