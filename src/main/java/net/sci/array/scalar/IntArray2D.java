/**
 * 
 */
package net.sci.array.scalar;

import java.io.PrintStream;
import java.util.Locale;

/**
 * @author dlegland
 *
 */
public abstract class IntArray2D<T extends Int> extends ScalarArray2D<T> implements IntArray<T>
{
    // =============================================================
    // Static method
    
    /**
     * Encapsulates the instance of Int array into a new IntArray2D, by creating
     * a Wrapper if necessary. If the original array is already an instance of
     * IntArray2D, it is returned.
     *
     * @param <T>
     *            the type of the input array
     * @param array
     *            the original array
     * @return a Int view of the original array
     */
    public final static <T extends Int> IntArray2D<T> wrap(IntArray<T> array)
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

	
    // =============================================================
    // Specialization of IntArray interface


	// =============================================================
	// Specialization of Array interface

	@Override
	public abstract IntArray2D<T> duplicate();
	

	// =============================================================
    // Inner wrapper class

	/**
     * Wraps an integer array into a IntArray2D, with two dimensions.
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
            this.size0 = array.size(0);
            this.size1 = array.size(1);
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
        public net.sci.array.scalar.IntArray.Iterator<T> iterator()
        {
            return this.array.iterator();
        }

        @Override
        public IntArray<T> newInstance(int... dims)
        {
            return array.newInstance(dims);
        }

        @Override
        public Class<T> dataType()
        {
            return array.dataType();
        }

        @Override
        public IntArray.Factory<T> getFactory()
        {
            return array.getFactory();
        }

        @Override
        public int getInt(int... pos)
        {
            return array.getInt(pos);
        }
        
        @Override
        public void setInt(int value, int... pos)
        {
            array.setInt(value, pos);
        }
        
        @Override
        public T get(int... pos)
        {
            return array.get(pos);
        }

        @Override
        public void set(T value, int... pos)
        {
            array.set(value, pos);
        }
    }
}
