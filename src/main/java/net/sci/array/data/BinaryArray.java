/**
 * 
 */
package net.sci.array.data;

import net.sci.array.ArrayFactory;
import net.sci.array.data.scalar2d.BinaryArray2D;
import net.sci.array.data.scalar2d.BufferedBinaryArray2D;
import net.sci.array.data.scalar3d.BinaryArray3D;
import net.sci.array.data.scalar3d.BufferedBinaryArray3D;
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
			throw new IllegalArgumentException("Can not create BinaryArray with " + dims.length + " dimensions");
//			return UInt8ArrayND.create(dims);
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
			throw new IllegalArgumentException("Can not create BinaryArray with " + dims.length + " dimensions");
//			return UInt8ArrayND.create(dims);
		}
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
	public BinaryArray complement();
	
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
	public default ArrayFactory<Binary> getFactory()
	{
		return new ArrayFactory<Binary>()
		{
			@Override
			public BinaryArray create(int[] dims, Binary value)
			{
				BinaryArray array = BinaryArray.create(dims);
				array.fill(value);
				return array;
			}
		};
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
}
