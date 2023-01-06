/**
 * 
 */
package net.sci.array.scalar;

import java.io.PrintStream;
import java.util.Locale;

/**
 * Specialization of Array for 2D arrays of integer values.
 * 
 * @author dlegland
 *
 */
public abstract class IntArray1D<T extends Int> extends ScalarArray1D<T> implements IntArray<T>
{
    // =============================================================
    // Static method
    
    /**
     * Encapsulates the instance of Int array into a new IntArray1D, by creating
     * a Wrapper if necessary. If the original array is already an instance of
     * IntArray1D, it is returned.
     *
     * @param <T>
     *            the type of the input array
     * @param array
     *            the original array
     * @return a Int view of the original array
     */
    public final static <T extends Int> IntArray1D<T> wrap(IntArray<T> array)
    {
        if (array instanceof IntArray1D)
        {
            return (IntArray1D<T>) array;
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
    
    public abstract void setInt(int x, int value);


    // =============================================================
    // Specialization of IntArray 

    @Override
    public void setInt(int[] pos, int intValue)
    {
        setInt(pos[0], intValue);
    }

    
    @Override
    public void setValue(int x, double value)
    {
        setInt(x, (int) value);
    }

    
    // =============================================================
    // Specialization of Array interface

    @Override
    public IntArray1D<T> duplicate()
    {
        IntArray1D<T> res = IntArray1D.wrap(this.factory().create(this.size()));
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
    private static class Wrapper<T extends Int> extends IntArray1D<T>
    {
        IntArray<T> array;

        public Wrapper(IntArray<T> array)
        {
            super(0);
            this.size0 = array.size(0);
            this.array = array;
        }

        @Override
        public void setInt(int x, int value)
        {
            this.array.setInt(new int[] {x}, value);
        }

        @Override
        public void set(int x, T value)
        {
            array.set(new int[] {x}, value);
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
        public IntArray.Factory<T> factory()
        {
            return array.factory();
        }

        @Override
        public int getInt(int... pos)
        {
            return array.getInt(pos);
        }

        @Override
        public void setInt(int[] pos, int value)
        {
            array.setInt(pos, value);
        }

        @Override
        public T get(int... pos)
        {
            return array.get(pos);
        }

        @Override
        public void set(int[] pos, T value)
        {
            array.set(pos, value);
        }
    }
}
