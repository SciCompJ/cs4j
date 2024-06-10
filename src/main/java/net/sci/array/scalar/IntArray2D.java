/**
 * 
 */
package net.sci.array.scalar;

import java.io.PrintStream;
import java.util.Locale;
import java.util.function.BiFunction;

/**
 * Specialization of Array for 2D arrays of integer values.
 * 
 * @author dlegland
 *
 */
public abstract class IntArray2D<I extends Int<I>> extends ScalarArray2D<I> implements IntArray<I>
{
    // =============================================================
    // Static method
    
    /**
     * Encapsulates the instance of Int array into a new IntArray2D, by creating
     * a Wrapper if necessary. If the original array is already an instance of
     * IntArray2D, it is returned.
     *
     * @param <I>
     *            the type of the input array
     * @param array
     *            the original array
     * @return a Int view of the original array
     */
    public final static <I extends Int<I>> IntArray2D<I> wrap(IntArray<I> array)
    {
        if (array instanceof IntArray2D)
        {
            return (IntArray2D<I>) array;
        }
        return new Wrapper<I>(array);
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
	public void printContent(PrintStream stream)
	{
		for (int y = 0; y < this.size1; y++)
		{
			for (int x = 0; x < this.size0; x++)
			{
			    stream.print(String.format(Locale.ENGLISH, " %3d", getInt(x, y)));
			}
			stream.println();
		}
	}
	
    /**
     * Initializes the content of the array by using the specified function of
     * two variables.
     * 
     * Example:
     * <pre>
     * {@code
     * ScalarArray2D<?> array = UInt8Array2D.create(5, 4);
     * array.fillInts((x, y) -> x + y * 10);
     * }
     * </pre>
     * 
     * @param fun
     *            a function of two variables that returns an integer. The two
     *            input variables correspond to the x and y coordinates.
     */
    public void fillInts(BiFunction<Integer,Integer,Integer> fun)
    {
        for (int[] pos : this.positions())
        {
            this.setInt(pos, fun.apply(pos[0], pos[1]));
        }
    }
    

    // =============================================================
    // New methods
    
    public abstract int getInt(int x, int y);

    public abstract void setInt(int x, int y, int value);
    

    // =============================================================
    // Specialization of IntArray 

    @Override
    public int getInt(int[] pos)
    {
        return getInt(pos[0], pos[1]);
    }

    @Override
    public void setInt(int[] pos, int intValue)
    {
        setInt(pos[0], pos[1], intValue);
    }

    
    // =============================================================
    // Specialization of ScalarArray2D 

    /**
     * Prints the content of this array on the specified stream, using a custom
     * number format.
     * 
     * @param stream
     *            the stream to print on.
     * @param numberFormat
     *            a string indicating the number format for floating point
     *            value, e.g "%f" or "%g".
     */
    public void printContent(PrintStream stream, String numberFormat)
    {
        String format = " " + numberFormat;
        for (int y = 0; y < this.size1; y++)
        {
            for (int x = 0; x < this.size0; x++)
            {
                stream.print(String.format(Locale.ENGLISH, format, getValue(x, y)));
            }
            stream.println();
        }
    }
    
    @Override
    public double getValue(int x, int y)
    {
        return getInt(x, y);
    }
    
    @Override
    public void setValue(int x, int y, double value)
    {
        setInt(x, y, (int) value);
    }

    
	// =============================================================
	// Specialization of Array interface

    @Override
    public IntArray2D<I> duplicate()
    {
        IntArray2D<I> res = IntArray2D.wrap(this.factory().create(this.size()));
        for (int y = 0; y < this.size1; y++)
        {
            for (int x = 0; x < this.size0; x++)
            {
                res.setInt(x, y, this.getInt(x, y));
            }
        }
        return res;
    }
	
    
	// =============================================================
	// Override Object methods

	@Override
    public String toString()
    {
	    return String.format(Locale.ENGLISH, "(%d x %d) Int array.", this.size0, this.size1);
    }


	// =============================================================
    // Inner wrapper class

	/**
     * Wraps an integer array into a IntArray2D, with two dimensions.
     */
    private static class Wrapper<I extends Int<I>> extends IntArray2D<I>
    {
        IntArray<I> array;

        public Wrapper(IntArray<I> array)
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
        public int getInt(int x, int y)
        {
            return this.array.getInt(new int[] {x, y});
        }

        @Override
        public void setInt(int x, int y, int value)
        {
            this.array.setInt(new int[] {x, y}, value);
        }

        @Override
        public I createElement(double value)
        {
            return array.createElement(value);
        }
        
        @Override
        public I get(int x, int y)
        {
            return array.get(new int[] {x, y});
        }

        @Override
        public void set(int x, int y, I value)
        {
            array.set(new int[] {x, y}, value);
        }

        /**
         * Simply returns an iterator on the original array.
         */
        @Override
        public net.sci.array.scalar.IntArray.Iterator<I> iterator()
        {
            return this.array.iterator();
        }

        @Override
        public IntArray<I> newInstance(int... dims)
        {
            return array.newInstance(dims);
        }

        @Override
        public Class<I> elementClass()
        {
            return array.elementClass();
        }

        @Override
        public IntArray.Factory<I> factory()
        {
            return array.factory();
        }

        @Override
        public int getInt(int[] pos)
        {
            return array.getInt(pos);
        }

        @Override
        public void setInt(int[] pos, int value)
        {
            array.setInt(pos, value);
        }

        @Override
        public I get(int[] pos)
        {
            return array.get(pos);
        }

        @Override
        public void set(int[] pos, I value)
        {
            array.set(pos, value);
        }
    }
}
