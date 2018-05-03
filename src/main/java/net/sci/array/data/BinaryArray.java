/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Cursor;
import net.sci.array.CursorIterator;
import net.sci.array.data.scalar2d.BinaryArray2D;
import net.sci.array.data.scalar2d.BufferedBinaryArray2D;
import net.sci.array.data.scalar3d.BinaryArray3D;
import net.sci.array.data.scalar3d.BufferedBinaryArray3D;
import net.sci.array.data.scalarnd.BinaryArrayND;
import net.sci.array.data.scalarnd.BufferedBinaryArrayND;
import net.sci.array.type.Binary;

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
        public IntArray<Binary> create(int[] dims)
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
     * Converts a scalar array to a binary array, by thresholding all values
     * above zero.
     * 
     * @param array
     *            a scalar array
     * @return the binary array corresponding to values greater than zero.
     */
	public static BinaryArray convert(ScalarArray<?> array)
	{
	    BinaryArray result = BinaryArray.create(array.getSize());
	    ScalarArray.Iterator<?> iter1 = array.iterator();
	    BinaryArray.Iterator iter2 = result.iterator();
	    while (iter1.hasNext() && iter2.hasNext())
	    {
	        iter2.setNextBoolean(iter1.nextValue() > 0);
	    }
	    return result;
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

	public boolean getBoolean(int[] pos);
	
	public void setBoolean(int[] pos, boolean state);
	
	/**
     * Returns the complement of this array. Replaces each 0 by 1, and each 1 by
     * 0.
     * 
     * @return the complement of this array.
     */
	public default BinaryArray complement()
	{
	    BinaryArray result = BinaryArray.create(this.getSize());
        BinaryArray.Iterator iter1 = this.iterator();
        BinaryArray.Iterator iter2 = result.iterator();
        while (iter1.hasNext())
        {
            iter2.setNextBoolean(!iter1.nextBoolean());
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
		setBoolean(pos, value != 0);
	}

	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public default BinaryArray newInstance(int... dims)
	{
		return BinaryArray.create(dims);
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
		BinaryArray result = BinaryArray.create(this.getSize());

		// initialize iterators
		BinaryArray.Iterator iter1 = this.iterator();
		BinaryArray.Iterator iter2 = result.iterator();
		
		// copy values into output array
		while(iter1.hasNext())
		{
			iter2.forward();
			iter2.set(iter1.next());
		}
		
		// return output
		return result;
	}

	@Override
	public default Class<Binary> getDataType()
	{
		return Binary.class;
	}

	public Iterator iterator();
	
	/**
	 * Sets the value at the specified position, by clamping the value between 0
	 * and 255.
	 */
	public default void setValue(int[] pos, double value)
	{
		setInt(pos, (int) Math.min(Math.max(value, 0), 255));
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
	}
	
    /**
     * Wraps a scalar array into a BinaryArray with same dimension.
     * 
     * Conversion between scalar and binary:
     * <ul>
     * <li>scalar value > 0 -> binary value TRUE</li>
     * <li>scalar value <= 0 -> binary value FALSE</li>
     * </ul>
     * 
     * @see BinaryArray.wrap(ScalarArray)
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
        // Implementation of the BinaryArray interface

        @Override
        public BinaryArray complement()
        {
            BinaryArray result = BinaryArray.create(array.getSize());
            ScalarArray.Iterator<?> iter1 = array.iterator();
            BinaryArray.Iterator iter2 = result.iterator();
            while (iter1.hasNext() && iter2.hasNext())
            {
                iter2.setNextBoolean(!(iter1.nextValue() > 0));
            }
            return result;
        }


        // =============================================================
        // Specialization of the Array interface
        
        @Override
        public boolean getBoolean(int[] pos)
        {
            return get(pos).getBoolean();
        }

        @Override
        public void setBoolean(int[] pos, boolean value)
        {
            set(pos, new Binary(value));
        }

        
        // =============================================================
        // Specialization of the Array interface

        @Override
        public int dimensionality()
        {
            return array.dimensionality();
        }

        @Override
        public int[] getSize()
        {
            return array.getSize();
        }

        @Override
        public int getSize(int dim)
        {
            return array.getSize(dim);
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

    	public CursorIterator<? extends Cursor> cursorIterator()
    	{
    		return array.cursorIterator();
    	}

        @Override
        public Iterator iterator()
        {
            return new Iterator(array.iterator());
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
