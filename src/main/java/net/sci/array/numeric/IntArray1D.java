/**
 * 
 */
package net.sci.array.numeric;

import java.io.PrintStream;
import java.util.Locale;

/**
 * Specialization of Array for 2D arrays of integer values.
 * 
 * @author dlegland
 *
 */
public abstract class IntArray1D<I extends Int<I>> extends ScalarArray1D<I> implements IntArray<I>
{
    // =============================================================
    // Static method
    
    /**
     * Encapsulates the instance of Int array into a new IntArray1D, by creating
     * a Wrapper if necessary. If the original array is already an instance of
     * IntArray1D, it is returned.
     *
     * @param <I>
     *            the type of the input array
     * @param array
     *            the original array
     * @return a Int view of the original array
     */
    public final static <I extends Int<I>> IntArray1D<I> wrap(IntArray<I> array)
    {
        if (array instanceof IntArray1D)
        {
            return (IntArray1D<I>) array;
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
    protected IntArray1D(int size0)
    {
        super(size0);
    }

    
    // =============================================================
    // Methods specific to IntArray1D

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
            stream.print(String.format(Locale.ENGLISH, " %3d", getInt(x)));
        }
        stream.println();
    }
        

    // =============================================================
    // New methods
    
    public abstract int getInt(int x);

    public abstract void setInt(int x, int value);
    

    // =============================================================
    // Specialization of IntArray 

    @Override
    public int getInt(int[] pos)
    {
        return getInt(pos[0]);
    }
    
    @Override
    public void setInt(int[] pos, int intValue)
    {
        setInt(pos[0], intValue);
    }

    
    // =============================================================
    // Specialization of ScalarArray1D 

    @Override
    public double getValue(int x)
    {
        return getInt(x);
    }
    
    @Override
    public void setValue(int x, double value)
    {
        setInt(x, (int) value);
    }

    
    // =============================================================
    // Specialization of Array interface

    @Override
    public IntArray1D<I> duplicate()
    {
        IntArray1D<I> res = IntArray1D.wrap(this.factory().create(this.size()));
        for (int x = 0; x < this.size0; x++)
        {
            res.setInt(x, this.getInt(x));
        }
        return res;
    }
    
    
    // =============================================================
    // Override Object methods

    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(String.format(Locale.ENGLISH, "(%d)-length Int array with values:\n", this.size0));
        for (int x = 0; x < this.size0; x++)
        {
            buffer.append(String.format(Locale.ENGLISH, " %3d", getInt(x)));
        }
        return buffer.toString();
    }


    // =============================================================
    // Inner wrapper class

    /**
     * Wraps an integer array into a IntArray1D, with two dimensions.
     */
    private static class Wrapper<I extends Int<I>> extends IntArray1D<I>
    {
        IntArray<I> array;

        public Wrapper(IntArray<I> array)
        {
            super(0);
            this.size0 = array.size(0);
            this.array = array;
        }

        @Override
        public int getInt(int x)
        {
            return this.array.getInt(new int[] {x});
        }

        @Override
        public void setInt(int x, int value)
        {
            this.array.setInt(new int[] {x}, value);
        }

        @Override
        public I get(int x)
        {
            return array.get(new int[] {x});
        }

        @Override
        public void set(int x, I value)
        {
            array.set(new int[] {x}, value);
        }

        /**
         * Simply returns an iterator on the original array.
         */
        @Override
        public net.sci.array.numeric.IntArray.Iterator<I> iterator()
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
        public I createElement(double value)
        {
            return array.createElement(value);
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
