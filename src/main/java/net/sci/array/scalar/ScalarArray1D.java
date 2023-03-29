/**
 * 
 */
package net.sci.array.scalar;

import java.io.PrintStream;
import java.util.Locale;

import net.sci.array.Array1D;

/**
 * Specialization of Array for 1D arrays of scalar values.
 * 
 * @author dlegland
 *
 */
public abstract class ScalarArray1D<T extends Scalar> extends Array1D<T> implements ScalarArray<T>
{
	// =============================================================
	// Static methods

    /**
     * Same as wrap method, but use different name to avoid runtime class cast
     * exceptions.
     * 
     * @param array
     *            an instance of ScalarArray with two dimensions
     * @return an instance of ScalarArray2D
     */
    public final static <T extends Scalar> ScalarArray1D<T> wrapScalar1d(ScalarArray<T> array)
    {
        if (array instanceof ScalarArray1D)
        {
            return (ScalarArray1D<T>) array;
        }
        return new Wrapper<T>(array);
    }

	// =============================================================
	// Constructor

    /**
     * Main constructor used to setup the size.
     * 
     * @param size0
     *            the size of the array
     */
	protected ScalarArray1D(int size0)
	{
		super(size0);
	}

	
	// =============================================================
	// Methods specific to ScalarArray1D
	
//    /**
//     * Initializes the content of the array by using the specified function of
//     * two variables.
//     * 
//     * Example:
//     * <pre>
//     * {@code
//     * ScalarArray2D<?> array = UInt8Array2D.create(5, 4);
//     * array.fillValues((x, y) -> x + y * 10);
//     * }
//     * </pre>
//     * 
//     * @param fun
//     *            a function of two variables that returns a double. The two
//     *            input variables correspond to the x and y coordinates.
//     */
//    public void fillValues(BiFunction<Integer,Integer,Double> fun)
//    {
//        for (int[] pos : this.positions())
//        {
//            this.setValue(pos, fun.apply(pos[0], pos[1]));
//        }
//    }

	/**
	 * Prints the content of this array on the specified stream.
	 * 
	 * @param stream
	 *            the stream to print on.
	 */
	public void print(PrintStream stream)
	{
		for (int x = 0; x < this.size0; x++)
		{
			System.out.print(String.format(Locale.ENGLISH, " %g", getValue(x)));
		}
		System.out.println();
	}
	

    // =============================================================
    // New getter / setter
    
    /**
     * Returns the value of an element in the array at the position given by the
     * integer index.
     * 
     * @param x
     *            index over the first array dimension
     * @return the double value at the specified position
     */
    public abstract double getValue(int x);

    /**
     * Changes the value of an element in the array at the position given by
     * an integer index.
     * 
     * @param x
     *            index over array dimension
     * @param value
     *            the new value at the specified index
     */
    public abstract void setValue(int x, double value);
	    

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
	public ScalarArray1D<T> duplicate()
	{
	    ScalarArray1D<T> res = ScalarArray1D.wrapScalar1d(this.factory().create(this.size()));
	    for (int x = 0; x < this.size0; x++)
	    {
	        res.setValue(x, this.getValue(x));
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
        buffer.append(String.format(Locale.ENGLISH, "(%d)-length Scalar array with values:\n", this.size0));
        for (int x = 0; x < this.size0; x++)
        {
            buffer.append(String.format(Locale.ENGLISH, " %g", getValue(x)));
        }
        return buffer.toString();
    }
    
    
    // =============================================================
    // Inner implementation of iterator on double values
	
	private class DoubleIterator implements java.util.Iterator<Double>
    {
        int x = -1;
        
        @Override
        public boolean hasNext()
        {
            return x < ScalarArray1D.this.size0 - 1;
        }

        @Override
        public Double next()
        {
            x++;
            return getValue(x);
        }
    }
	
	
	// =============================================================
	// Inner Wrapper class

	private static class Wrapper<T extends Scalar> extends ScalarArray1D<T>
	{
		private ScalarArray<T> array;
		
		protected Wrapper(ScalarArray<T> array)
		{
			super(0);
			this.array = array;
			this.size0 = array.size(0);
		}

        @Override
        public double getValue(int x)
        {
            return array.getValue(new int[] {x});
        }

        @Override
        public void setValue(int x, double value)
        {
            array.setValue(new int[] {x}, value);
        }

        @Override
        public T get(int x)
        {
            // get value at specified position
            return this.array.get(new int[] {x});
        }

        @Override
        public void set(int x, T value)
        {
            // set value at specified position
            this.array.set(new int[] {x}, value);
        }

        @Override
        public double getValue(int[] pos)
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
		public T get(int[] pos)
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
