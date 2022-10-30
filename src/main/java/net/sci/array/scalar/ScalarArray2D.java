/**
 * 
 */
package net.sci.array.scalar;

import java.io.PrintStream;
import java.util.Locale;
import java.util.function.BiFunction;

import net.sci.array.Array2D;

/**
 * Specialization of Array for 2D arrays of scalar values.
 * 
 * @author dlegland
 *
 */
public abstract class ScalarArray2D<T extends Scalar> extends Array2D<T> implements ScalarArray<T>
{
	// =============================================================
	// Static methods

    public final static <T extends Scalar> ScalarArray2D<T> wrap(ScalarArray<T> array)
    {
        if (array instanceof ScalarArray2D)
        {
            return (ScalarArray2D<T>) array;
        }
        return new Wrapper<T>(array);
    }

    /**
     * Same as wrap method, but use different name to avoid runtime class cast
     * exceptions.
     * 
     * @param array
     *            an instance of ScalarArray with two dimensions
     * @return an instance of ScalarArray2D
     */
    public final static <T extends Scalar> ScalarArray2D<T> wrapScalar2d(ScalarArray<T> array)
    {
        if (array instanceof ScalarArray2D)
        {
            return (ScalarArray2D<T>) array;
        }
        return new Wrapper<T>(array);
    }

	// =============================================================
	// Constructor

	protected ScalarArray2D(int size0, int size1)
	{
		super(size0, size1);
	}

	
	// =============================================================
	// Methods specific to ScalarArray2D
	
    /**
     * Initializes the content of the array by using the specified function of
     * two variables.
     * 
     * Example:
     * <pre>
     * {@code
     * ScalarArray2D<?> array = UInt8Array2D.create(5, 4);
     * array.fillValues((x, y) -> x + y * 10);
     * }
     * </pre>
     * 
     * @param fun
     *            a function of two variables that returns a double. The two
     *            input variables correspond to the x and y coordinates.
     */
    public void fillValues(BiFunction<Integer,Integer,Double> fun)
    {
        for (int[] pos : this.positions())
        {
            this.setValue(pos, fun.apply(pos[0], pos[1]));
        }
    }

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
				System.out.print(String.format(Locale.ENGLISH, " %g", getValue(x, y)));
			}
			System.out.println();
		}
	}

    // =============================================================
    // New getter / setter
    
    /**
     * Changes the value of an element in the array at the position given by
     * two integer indices.
     * 
     * @param x
     *            index over the first array dimension
     * @param y
     *            index over the second array dimension
     * @param value
     *            the new value at the specified index
     */
    public abstract void setValue(int x, int y, double value);
	    

    // =============================================================
    // Specialization of the ScalarArray interface
    
    public Iterable<Double> values()
    {
        return new Iterable<Double>()
        {
            @Override
            public java.util.Iterator<Double> iterator()
            {
                return new DoubleIterator();
            }
        };
    }
    
    
	// =============================================================
	// Specialization of the Array interface

	@Override
	public ScalarArray2D<T> duplicate()
	{
	    ScalarArray2D<T> res = ScalarArray2D.wrapScalar2d(this.factory().create(this.size()));
	    for (int y = 0; y < this.size1; y++)
	    {
	        for (int x = 0; x < this.size0; x++)
	        {
	            res.setValue(x, y, this.getValue(x, y));
	        }
	    }
	    return res;
	}
	    

	
    // =============================================================
    // Override Object methods

    /**
     * Overrides the method to display a String representation of this values
     * within the array.
     * 
     * @return a String representation of the inner values of the array.
     */
    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(String.format(Locale.ENGLISH, "(%d x %d) Scalar array with values:", this.size0, this.size1));
        for (int y = 0; y < this.size1; y++)
        {
            buffer.append("\n");
            for (int x = 0; x < this.size0; x++)
            {
                buffer.append(String.format(Locale.ENGLISH, " %g", getValue(x, y)));
            }
        }
        return buffer.toString();
    }
    
    // =============================================================
    // Inner implementation of iterator on double values
	
	private class DoubleIterator implements java.util.Iterator<Double>
    {
        int x = -1;
        int y = 0;
        
        @Override
        public boolean hasNext()
        {
            return x < ScalarArray2D.this.size0 - 1 || y < ScalarArray2D.this.size1 - 1;
        }

        @Override
        public Double next()
        {
            x++;
            if (x >= ScalarArray2D.this.size0)
            {
                x = 0;
                y++;
            }

            return getValue(x, y);
        }
    }
	
	
	// =============================================================
	// Inner Wrapper class

	private static class Wrapper<T extends Scalar> extends ScalarArray2D<T>
	{
		private ScalarArray<T> array;
		
		protected Wrapper(ScalarArray<T> array)
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
        public void setValue(int x, int y, double value)
        {
            array.setValue(new int[] {x, y}, value);
        }

        @Override
        public void set(int x, int y, T value)
        {
            // set value at specified position
            this.array.set(new int[] {x, y}, value);
        }

        @Override
        public double getValue(int... pos)
        {
            return array.getValue(pos);
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            array.setValue(pos, value);
        }

        @Override
		public ScalarArray<T> newInstance(int... dims)
		{
			return this.array.newInstance(dims);
		}

		@Override
		public ScalarArray.Factory<T> factory()
		{
			return this.array.factory();
		}

		@Override
		public T get(int... pos)
		{
			// return value from specified position
			return this.array.get(pos);
		}

        @Override
		public void set(int[] pos, T value)
		{
			// set value at specified position
			this.array.set(pos, value);
		}

		@Override
		public Class<T> dataType()
		{
			return array.dataType();
		}

		@Override
		public ScalarArray.Iterator<T> iterator()
		{
			return array.iterator();
		}
	}
}
