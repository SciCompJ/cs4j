/**
 * 
 */
package net.sci.array.binary;

import java.util.function.Function;

import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.binary.process.BinaryMask;
import net.sci.array.binary.process.ConvertToBinary;
import net.sci.array.impl.ArrayWrapperStub;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.ScalarArray;

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

    public static final Factory defaultFactory = new RunLengthBinaryArrayFactory();
    
    
    // =============================================================
    // Static methods
    
    public static BinaryArray create(int... dims)
    {
        return defaultFactory.create(dims);
    }
    
    public static BinaryArray create(int[] dims, boolean[] buffer)
    {
        return switch (dims.length)
        {
            case 2 -> new BufferedBinaryArray2D(dims[0], dims[1], buffer);
            case 3 -> new BufferedBinaryArray3D(dims[0], dims[1], dims[2], buffer);
            default -> new BufferedBinaryArrayND(dims, buffer);
        };
    }
    
    /**
     * Returns an array with same size and type as the input array, but
     * containing non zero values only for the elements of this binary array
     * that are set to <code>true</code>.
     * 
     * The value of the "zero" element (returned for <code>false</code> elements
     * of the mask) is left to array implementations.
     * 
     * Example:
     * 
     * <pre>{@code
     * UInt8Array2D array = UInt8Array2D.create(8, 6);
     * array.fillInts((x, y) -> y * 10 + x);
     * BinaryArray2D binaryArray = BinaryArray2D.create(8, 6);
     * binaryArray.fillBooleans((x, y) -> x > 3 && y > 2);
     * UInt8Array2D masked = UInt8Array2D.wrap2d(UInt8Array.wrap(binaryArray.mask(array)));
     * }</pre>
     * 
     * @param <T>
     *            the type of data contained within array
     * @param array
     *            the array to mask
     * @return an array containing either the same element of the array, or a
     *         zero value, depending on the state of the corresponding element
     *         in this binary mask
     */
	public default <T> Array<T> mask(Array<T> array)
	{
	    return BinaryMask.createView(array, this);
	}

	/**
     * Converts an array to a binary array, by thresholding all values
     * strictly greater than zero.
     *
     * @see net.sci.array.binary.process.ConvertToBinary
     * 
     * @param array
     *            an array
     * @return the binary array corresponding to values greater than zero.
     */
	public static BinaryArray convert(Array<?> array)
	{
	    return new ConvertToBinary().process(array);
	}

    /**
     * Encapsulates the specified array into a new BinaryArray, by creating a
     * Wrapper if necessary. If the original array is already an instance of
     * BinaryArray, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Binary view of the original array
     */
    @SuppressWarnings("unchecked")
    public static BinaryArray wrap(Array<?> array)
    {
        if (array instanceof BinaryArray)
        {
            return (BinaryArray) array;
        }
        if (Binary.class.isAssignableFrom(array.elementClass()))
        {
            return new Wrapper((Array<Binary>) array);
        }
        
        if (array instanceof ScalarArray)
        {
            return wrapScalar((ScalarArray<?>) array);
        }
        
        throw new IllegalArgumentException("Can not wrap an array with class " + array.getClass() + " and type " + array.elementClass());
    }
    
    public static BinaryArray wrapScalar(ScalarArray<?> array)
    {
        if (array instanceof BinaryArray)
        {
            return (BinaryArray) array;
        }
        return new ScalarArrayWrapper(array);
    }
    

    // =============================================================
	// New methods

    /**
     * Iterates over elements of the array that correspond to a true value in
     * this binary array, used as a selection mask.
     * 
     * @param <T>
     *            the type of the elements in the array
     * @param array
     *            the array containing the elements to select
     * @return the selected elements
     */
    public default <T> Iterable<T> selectElements(Array<T> array)
    {
        // check array dimensions
        if (!Arrays.isSameSize(this, array))
        {
            throw new IllegalArgumentException("Mask array must have same size as input array");
        }
        
        // create new iterable
        return new Iterable<T>()
        {
            public java.util.Iterator<T> iterator()
            {
                return new java.util.Iterator<T>()
                {
                    PositionIterator iter = trueElementPositionIterator();
                    
                    @Override
                    public boolean hasNext()
                    {
                        return iter.hasNext();
                    }

                    @Override
                    public T next()
                    {
                        int[] pos = iter.next();
                        return array.get(pos);
                    }
                };
            }
        };
    }
    
    /**
     * Iterates over numerical elements of the specified scalar array that
     * correspond to a true value in this binary array, used as a selection
     * mask.
     * 
     * @param array
     *            the array containing the values to select
     * @return the selected numerical values
     */
    public default Iterable<Double> selectValues(ScalarArray<?> array)
    {
        // check array dimensions
        if (!Arrays.isSameSize(this, array))
        {
            throw new IllegalArgumentException("Mask array must have same size as input array");
        }
        
        // create new iterable
        return new Iterable<Double>()
        {
            public java.util.Iterator<Double> iterator()
            {
                return new java.util.Iterator<Double>()
                {
                    PositionIterator iter = trueElementPositionIterator();
                    
                    @Override
                    public boolean hasNext()
                    {
                        return iter.hasNext();
                    }

                    @Override
                    public Double next()
                    {
                        int[] pos = iter.next();
                        return array.getValue(pos);
                    }
                };
            }
        };
    }
    
    /**
     * Iterates over integer values within the specified int array by retaining
     * only values that correspond to a true value within this binary array,
     * used as a mask;
     * 
     * @param array
     *            the array containing the integer values to select
     * @return the selected integer values
     */
    public default Iterable<Integer> selectInts(IntArray<?> array)
    {
        // check array dimensions
        if (!Arrays.isSameSize(this, array))
        {
            throw new IllegalArgumentException("Mask array must have same size as input array");
        }
        
        // create new iterable
        return new Iterable<Integer>()
        {
            public java.util.Iterator<Integer> iterator()
            {
                return new java.util.Iterator<Integer>()
                {
                    PositionIterator iter = trueElementPositionIterator();
                    
                    @Override
                    public boolean hasNext()
                    {
                        return iter.hasNext();
                    }

                    @Override
                    public Integer next()
                    {
                        int[] pos = iter.next();
                        return array.getInt(pos);
                    }
                };
            }
        };
    }
    
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
    
    public boolean getBoolean(int[] pos);
    
    public void setBoolean(int[] pos, boolean state);
    
    public default long trueElementCount()
    {
        long count = 0;
        for (int[] pos : positions())
        {
            if (getBoolean(pos))
            {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Iterates of the positions that correspond to a <code>true</code> element.
     * 
     * @return an Iterable over the positions of only <code>true</code> elements
     *         within the array.
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
    public default int getInt(int[] pos)
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
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sci.array.Array2D#getValue(int, int)
     */
    @Override
    public default double getValue(int[] pos)
    {
        return getBoolean(pos) ? 1 : 0;
    }
    
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
    @Override
    public default void setValue(int[] pos, double value)
    {
        setBoolean(pos, value > 0);
    }
    
    @Override
    public default Binary typeMin()
    {
        return Binary.FALSE;
    }

    @Override
    public default Binary typeMax()
    {
        return Binary.TRUE;
    }

    @Override
    public default Binary createElement(double value)
    {
        return new Binary(value > 0);
    }
    
    
    // =============================================================
    // Specialization of the Array interface
    
    @Override
    public default BinaryArray newInstance(int... dims)
    {
        return BinaryArray.create(dims);
    }
    
    /**
     * Override default behavior of Array interface to return the value
     * Binary.FALSE.
     * 
     * @return a default Binary value.
     */
    @Override
    public default Binary sampleElement()
    {
        return Binary.FALSE;
    }
    
    @Override
    public default Binary get(int[] pos)
    {
        return new Binary(getBoolean(pos));
    }

    @Override
    public default void set(int[] pos, Binary value)
    {
        setBoolean(pos, value.getBoolean());
    }
    
    @Override
    public default BinaryArray duplicate()
    {
        // create output array
        BinaryArray result = BinaryArray.create(this.size());
        
        // copy values into output array
        for (int[] pos : positions())
        {
            result.setBoolean(pos, this.getBoolean(pos));
        }
        
        // return output
        return result;
    }
    
    public default BinaryArray reshapeView(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new ReshapeView(this, newDims, coordsMapping);
    }

    @Override
    public default Factory factory()
    {
        return defaultFactory;
    }

	@Override
	public default Class<Binary> elementClass()
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
	
	/**
	 * Iterator over the elements of a binary array.
	 * 
	 * Provides nextBoolean() method to avoid class casts.
	 */
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
     * Wraps explicitly an array containing <code>Binary</code> elements into an
     * instance of <code>BinaryArray</code>.
     * 
     * Usage:
     * <pre>
     * {@code
     * Array<Binary> array = ...
     * BinaryArray newArray = new BinaryArray.Wrapper(array);
     * newArray.getBoolean(...);  
     * }
     * </pre>
     */
    static class Wrapper extends ArrayWrapperStub<Binary> implements BinaryArray
    {
        Array<Binary> array;
        
        public Wrapper(Array<Binary> array)
        {
            super(array);
            this.array = array;
        }
        
        @Override
        public boolean getBoolean(int[] pos)
        {
            return array.get(pos).getBoolean();
        }

        @Override
        public void setBoolean(int[] pos, boolean value)
        {
            array.set(pos, new Binary(value));
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
     * @see BinaryArray#wrap(net.sci.array.numeric.ScalarArray)
     */
    static class ScalarArrayWrapper extends ArrayWrapperStub<Binary> implements BinaryArray
    {
        /** The parent array */
        ScalarArray<?> array;
        
        public ScalarArrayWrapper(ScalarArray<?> array)
        {
            super(array);
            this.array = array;
        }
        

        // =============================================================
        // Specialization of the Array interface
        
        @Override
        public boolean getBoolean(int[] pos)
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
        public Binary get(int[] pos)
        {
            return new Binary(array.getValue(pos) > 0);
        }

        @Override
        public void set(int[] pos, Binary value)
        {
            array.setValue(pos, value.getValue());
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
    
    /**
     * Utility class for creating a reshape view on an array using arbitrary
     * coordinate mapping.
     *
     * @see BinaryArray#reshapeView(int[], Function)
     */
    static class ReshapeView implements BinaryArray
    {
        BinaryArray array;
        
        int[] newDims;
        
        Function<int[], int[]> coordsMapping;

        /**
         * Creates a reshape view on the specified array that keeps the type of
         * the original array.
         * 
         * @param array
         *            the array to create a view on.
         * @param newDims
         *            the dimensions of the view.
         * @param coordsMapping
         *            the mapping from coordinate in view to the coordinates in
         *            the original array.
         */
        public ReshapeView(BinaryArray array, int[] newDims, Function<int[], int[]> coordsMapping)
        {
            this.array = array;
            this.newDims = newDims;
            this.coordsMapping = coordsMapping;
        }

        /* (non-Javadoc)
         * @see net.sci.array.binary.BinaryArray#getBoolean(int[])
         */
        @Override
        public boolean getBoolean(int[] pos)
        {
            return array.getBoolean(coordsMapping.apply(pos));
        }

        /* (non-Javadoc)
         * @see net.sci.array.binary.BinaryArray#setBoolean(int[], boolean)
         */
        @Override
        public void setBoolean(int[] pos, boolean bool)
        {
            array.setBoolean(coordsMapping.apply(pos), bool);
        }

        /* (non-Javadoc)
         * @see net.sci.array.Array#dimensionality()
         */
        @Override
        public int dimensionality()
        {
            return newDims.length;
        }

        /* (non-Javadoc)
         * @see net.sci.array.Array#getSize()
         */
        @Override
        public int[] size()
        {
            return newDims;
        }

        /* (non-Javadoc)
         * @see net.sci.array.Array#getSize(int)
         */
        @Override
        public int size(int dim)
        {
            return newDims[dim];
        }
    }
    
    // =============================================================
    // Specialization of the Factory interface

    /**
     * Specialization of the ArrayFactory for generating instances of BinaryArray.
     */
    public interface Factory extends IntArray.Factory<Binary>
    {
        /**
         * Creates a new BinaryArray of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new BinaryArray initialized with zeros
         */
        public BinaryArray create(int... dims);

        /**
         * Creates a new BinaryArray with the specified dimensions, filled with
         * the specified initial value.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param value
         *            an instance of the initial integer value
         * @return a new instance of IntArray
         */
        public BinaryArray create(int[] dims, Binary value);
    }
}
