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
public abstract class IntArray3D<I extends Int<I>> extends ScalarArray3D<I> implements IntArray<I>
{
    // =============================================================
    // Static method
    
    /**
     * Encapsulates the instance of Int array into a new IntArray3D, by creating
     * a Wrapper if necessary. If the original array is already an instance of
     * IntArray3D, it is returned.
     *
     * @param <I>
     *            the type of the input array
     * @param array
     *            the original array
     * @return a Int view of the original array
     */
    public static final <I extends Int<I>> IntArray3D<I> wrap(IntArray<I> array)
    {
        if (array instanceof IntArray3D)
        {
            return (IntArray3D<I>) array;
        }
        return new Wrapper<I>(array);
    }
    
    
    // =============================================================
    // Constructor

    protected IntArray3D(int size0, int size1, int size2)
    {
        super(size0, size1, size2);
    }

    
    // =============================================================
    // Methods specific to IntArray3D

    /**
     * Initializes the content of the array by using the specified function of
     * three variables.
     * 
     * Example:
     * <pre>
     * {@code
     * UInt8Array3D array = UInt8Array3D.create(5, 4, 3);
     * array.fillInts((x, y, z) -> x + y * 10 + z * 100);
     * }
     * </pre>
     * 
     * @param fun
     *            a function of two variables that returns an integer. The three
     *            input variables correspond to the x, y and z coordinates.
     */
    public void fillInts(TriFunction<Integer,Integer,Integer,Integer> fun)
    {
        for (int[] pos : this.positions())
        {
            this.setInt(pos, fun.apply(pos[0], pos[1], pos[2]));
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
        for (int z = 0; z < this.size2; z++)
        {
            stream.println(String.format(Locale.ENGLISH, "slice %d/%d:", z, size2));
            for (int y = 0; y < this.size1; y++)
            {
                for (int x = 0; x < this.size0; x++)
                {
                    stream.print(String.format(Locale.ENGLISH, " %s", getInt(x, y, z)));
                }
                stream.println();
            }
        }
    }


    // =============================================================
    // New methods
    
    public abstract int getInt(int x, int y, int z);

    public abstract void setInt(int x, int y, int z, int value);

    
    // =============================================================
    // Specialization of Array3D

    /**
     * Returns a view over the specified slice.
     * 
     * @param sliceIndex
     *            the index of the slice
     * @return a view on the specific slice, as a 2D array
     */
    public abstract IntArray2D<I> slice(int sliceIndex);
    
    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public abstract Iterable<? extends IntArray2D<I>> slices();

    /**
     * Creates an iterator over the slices
     * 
     * @return an iterator over 2D slices
     */
    public abstract java.util.Iterator<? extends IntArray2D<I>> sliceIterator();
    

    // =============================================================
    // Specialization of IntArray 

    @Override
    public int getInt(int[] pos)
    {
        return getInt(pos[0], pos[1], pos[2]);
    }

    @Override
    public void setInt(int[] pos, int intValue)
    {
        setInt(pos[0], pos[1], pos[2], intValue);
    }

    // =============================================================
    // Specialization of ScalarArray2D 

    @Override
    public double getValue(int x, int y, int z)
    {
        return getInt(x, y, z);
    }
    
    @Override
    public void setValue(int x, int y, int z, double value)
    {
        setInt(x, y, z, (int) value);
    }


    // =============================================================
    // Specialization of Array interface

    @Override
    public abstract IntArray3D<I> duplicate();

    
    // =============================================================
    // Override Object methods

    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(String.format(Locale.ENGLISH, "(%d x %d x %d) Int array;", this.size0, this.size1, this.size2));
        for (int z = 0; z < this.size2; z++)
        {
            buffer.append(String.format(Locale.ENGLISH, "\nslice %d/%d (pos[2]=%d):", z+1, this.size2, z));
            for (int y = 0; y < this.size1; y++)
            {
                buffer.append("\n");
                for (int x = 0; x < this.size0; x++)
                {
                    buffer.append(String.format(Locale.ENGLISH, " %3d", getInt(x, y, z)));
                }
            }
        }
        return buffer.toString();
    }


    // =============================================================
    // Inner wrapper class

    /**
     * Wraps an integer array into a IntArray3D, with two dimensions.
     */
    private static class Wrapper<I extends Int<I>> extends IntArray3D<I>
    {
        IntArray<I> array;

        public Wrapper(IntArray<I> array)
        {
            super(0, 0, 0);
            if (array.dimensionality() != 3)
            {
                throw new IllegalArgumentException("Requires an array of dimensionality equal to 3.");
            }
            this.size0 = array.size(0);
            this.size1 = array.size(1);
            this.size2 = array.size(2);
            this.array = array;
        }


        @Override
        public int getInt(int x, int y, int z)
        {
            return this.array.getInt(new int[] {x, y, z});
        }

        @Override
        public void setInt(int x, int y, int z, int value)
        {
            this.array.setInt(new int[] {x, y, z}, value);
        }


        @Override
        public I get(int x, int y, int z)
        {
            return array.get(new int[] {x, y, z});
        }


        @Override
        public void set(int x, int y, int z, I value)
        {
            this.array.set(new int[] {x, y, z}, value);
        }
        
        @Override
        public IntArray3D<I> duplicate()
        {
            IntArray<I> dup = this.array.duplicate();
            if (dup instanceof IntArray3D)
            {
                return (IntArray3D<I>) dup;
            }
            return new Wrapper<I>(this.array.duplicate());
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
        public I createElement(double value)
        {
            return array.createElement(value);
        }
        
        @Override
        public IntArray2D<I> slice(int sliceIndex)
        {
            return new SliceView(sliceIndex);
        }

        @Override
        public Iterable<? extends IntArray2D<I>> slices()
        {
            return new Iterable<IntArray2D<I>>()
            {
                @Override
                public java.util.Iterator<IntArray2D<I>> iterator()
                {
                    return new SliceIterator();
                }
            };
        }

        @Override
        public java.util.Iterator<? extends IntArray2D<I>> sliceIterator()
        {
            return new SliceIterator();
        }
        
        
        private class SliceView extends IntArray2D<I>
        {
            int sliceIndex;
            
            protected SliceView(int slice)
            {
                super(Wrapper.this.size0, Wrapper.this.size1);
                if (slice < 0 || slice >= Wrapper.this.size2)
                {
                    throw new IllegalArgumentException(String.format(
                            "Slice index %d must be comprised between 0 and %d", slice, Wrapper.this.size2));
                }
                this.sliceIndex = slice;
            }


            @Override
            public int getInt(int x, int y)
            {
                return array.getInt(new int[] {x, y, this.sliceIndex});
            }

            @Override
            public void setInt(int x, int y, int value)
            {
                array.setInt(new int[] {x, y, this.sliceIndex}, value);
            }

            @Override
            public void setValue(int x, int y, double value)
            {
                array.setValue(new int[] {x, y, this.sliceIndex}, value);
            }

            @Override
            public I get(int x, int y)
            {
                return array.get(new int[] {x, y, this.sliceIndex});
            }
            
            @Override
            public void set(int x, int y, I value)
            {
                array.set(new int[] {x, y, this.sliceIndex}, value);
            }
            
            @Override
            public int getInt(int[] pos)
            {
                return array.getInt(new int[] {pos[0], pos[1], this.sliceIndex});
            }

            @Override
            public void setInt(int[] pos, int value)
            {
                Wrapper.this.setInt(new int[] {pos[0], pos[1], this.sliceIndex}, value);
            }

            @Override
            public double getValue(int[] pos)
            {
                return array.getValue(new int[] {pos[0], pos[1], this.sliceIndex});
            }

            @Override
            public void setValue(int[] pos, double value)
            {
                array.setValue(pos, value);
            }

            @Override
            public I createElement(double value)
            {
                return array.createElement(value);
            }
            
            @Override
            public IntArray<I> newInstance(int... dims)
            {
                return array.newInstance(dims);
            }

            @Override
            public net.sci.array.scalar.IntArray.Factory<I> factory()
            {
                return array.factory();
            }

            @Override
            public Class<I> elementClass()
            {
                return array.elementClass();
            }

            @Override
            public IntArray2D<I> duplicate()
            {
                int[] dims = size();
                IntArray2D<I> res = IntArray2D.wrap(array.newInstance(dims));
                for (int[] pos : res.positions())
                {
                    res.setInt(pos, array.getInt(pos));
                }
                return res;
            }

            @Override
            public I get(int[] pos)
            {
                return array.get(new int[] {pos[0], pos[1], this.sliceIndex});
            }

            @Override
            public void set(int[] pos, I value)
            {
                array.set(new int[] {pos[0], pos[1], this.sliceIndex}, value);
            }

            @Override
            public net.sci.array.scalar.IntArray.Iterator<I> iterator()
            {
                return new Iterator();
            }

            class Iterator implements IntArray.Iterator<I>
            {
                int indX = -1;
                int indY = 0;
                
                public Iterator() 
                {
                }
                
                @Override
                public I next()
                {
                    forward();
                    return get();
                }
            
                @Override
                public void forward()
                {
                    indX++;
                    if (indX >= size0)
                    {
                        indX = 0;
                        indY++;
                    }
                }
            
                @Override
                public boolean hasNext()
                {
                    return indX < size0 - 1 || indY < size1 - 1;
                }
            
                @Override
                public int getInt()
                {
                    return Wrapper.this.getInt(indX, indY, sliceIndex);
                }
            
                @Override
                public void setInt(int val)
                {
                    Wrapper.this.setInt(new int[] {indX, indY, sliceIndex}, val);
                }
            
                @Override
                public I get()
                {
                    return array.get(new int[] {indX, indY, sliceIndex});
                }
            
                @Override
                public void set(I value)
                {
                    array.set(new int[] {indX, indY, sliceIndex}, value);
                }
            }
        }
        
        
        private class SliceIterator implements java.util.Iterator<IntArray2D<I>> 
        {
            int sliceIndex = 0;

            @Override
            public boolean hasNext()
            {
                return sliceIndex < Wrapper.this.size2;
            }

            @Override
            public IntArray2D<I> next()
            {
                return new SliceView(sliceIndex++);
            }
        }
    }
}
