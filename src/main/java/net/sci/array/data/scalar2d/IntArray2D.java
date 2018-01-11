/**
 * 
 */
package net.sci.array.data.scalar2d;

import java.io.PrintStream;
import java.util.Locale;

import net.sci.array.ArrayFactory;
import net.sci.array.data.IntArray;
import net.sci.array.type.Int;

/**
 * @author dlegland
 *
 */
public abstract class IntArray2D<T extends Int> extends ScalarArray2D<T> implements IntArray<T>
{
    // =============================================================
    // Static method
    
    /**
     * Encapsulates the instance of Int array into a new IntArray2D, by
     * creating a Wrapper if necessary. 
     * If the original array is already an instance of IntArray2D, it is returned.  
     * 
     * @param array
     *            the original array
     * @return a Int view of the original array
     */
    public static <T extends Int> IntArray2D<T> wrap(IntArray<T> array)
    {
        if (array instanceof IntArray2D)
        {
            return (IntArray2D<T>) array;
        }
        return new Wrapper<T>(array);
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
	protected IntArray2D(int size0, int size1)
	{
		super(size0, size1);
	}

	
	// =============================================================
	// Methods specific to IntArray2D

	/**
	 * Prints the content of this array on the specified stream.
	 * 
	 * @param stream
	 *            the stream to print on.
	 */
	public void print(PrintStream stream)
	{
		for (int y = 0; y < this.size1; y++)
		{
			for (int x = 0; x < this.size0; x++)
			{
				System.out.print(String.format(Locale.ENGLISH, " %3d", getInt(x, y)));
			}
			System.out.println();
		}
	}

	// =============================================================
	// New methods

	public abstract int getInt(int x, int y);
	public abstract void setInt(int x, int y, int value);
	
	
    // =============================================================
    // Specialization of IntArray interface

    @Override
    public int getInt(int[] pos)
    {
        return getInt(pos[0], pos[1]);
    }

    @Override
    public void setInt(int[] pos, int value)
    {
        setInt(pos[0], pos[1], value);
    }


	// =============================================================
	// Specialization of Array interface

	@Override
	public abstract IntArray2D<T> duplicate();
	

	// =============================================================
    // Inner wrapprt class

	/**
     * Wraps a UInt8 array into a IntArray2D, with two dimensions.
     */
    private static class Wrapper<T extends Int> extends IntArray2D<T>
    {
        IntArray<T> array;

        public Wrapper(IntArray<T> array)
        {
            super(0, 0);
            if (array.dimensionality() != 2)
            {
                throw new IllegalArgumentException("Requires an array of dimensionality equal to 2.");
            }
            this.size0 = array.getSize(0);
            this.size1 = array.getSize(1);
            this.array = array;
        }

        @Override
        public IntArray2D<T> duplicate()
        {
            IntArray<T> dup = this.array.duplicate();
            if (dup instanceof IntArray2D)
            {
                return (IntArray2D<T>) dup;
            }
            return new Wrapper<T>(this.array.duplicate());
        }

        /**
         * Simply returns an iterator on the original array.
         */
        @Override
        public net.sci.array.data.IntArray.Iterator<T> iterator()
        {
            return this.array.iterator();
        }

        @Override
        public IntArray<T> newInstance(int... dims)
        {
            return array.newInstance(dims);
        }

        @Override
        public Class<T> getDataType()
        {
            return array.getDataType();
        }

        @Override
        public ArrayFactory<T> getFactory()
        {
            return array.getFactory();
        }

        @Override
        public int getInt(int x, int y)
        {
            return array.getInt(new int[] { x, y });
        }
        
        @Override
        public void setInt(int x, int y, int value)
        {
            array.setInt(new int[] { x, y }, value);
        }
        
        @Override
        public T get(int x, int y)
        {
            return array.get(new int[] { x, y });
        }

        @Override
        public void set(int x, int y, T value)
        {
            array.set(new int[] { x, y }, value);
        }

        @Override
        public double getValue(int x, int y)
        {
            return array.getValue(new int[] { x, y });
        }

        @Override
        public void setValue(int x, int y, double value)
        {
            array.setValue(new int[] { x, y }, value);
        }
        
        @Override
        public int getInt(int[] pos)
        {
            return array.getInt(pos);
        }

        @Override
        public void setInt(int[] pos, int value)
        {
            setInt(pos, value);
        }
    }
}
