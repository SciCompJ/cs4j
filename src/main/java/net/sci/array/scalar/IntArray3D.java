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
public abstract class IntArray3D<T extends Int> extends ScalarArray3D<T> implements IntArray<T>
{
    // =============================================================
    // Static method
    
    /**
     * Encapsulates the instance of Int array into a new IntArray3D, by creating
     * a Wrapper if necessary. If the original array is already an instance of
     * IntArray3D, it is returned.
     *
     * @param <T>
     *            the type of the input array
     * @param array
     *            the original array
     * @return a Int view of the original array
     */
    public static final <T extends Int> IntArray3D<T> wrap(IntArray<T> array)
    {
        if (array instanceof IntArray3D)
        {
            return (IntArray3D<T>) array;
        }
        return new Wrapper<T>(array);
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
    
    public abstract void setInt(int x, int y, int z, int value);


    // =============================================================
    // Specialization of IntArray 

    @Override
    public void setInt(int[] pos, int intValue)
    {
        setInt(pos[0], pos[1], pos[2], intValue);
    }

    @Override
    public void setValue(int x, int y, int z, double value)
    {
        setInt(x, y, z, (int) value);
    }


    // =============================================================
    // Specialization of Array interface

	@Override
	public abstract IntArray3D<T> duplicate();

	
    // =============================================================
    // Inner wrapper class

    /**
     * Wraps an integer array into a IntArray3D, with two dimensions.
     */
    private static class Wrapper<T extends Int> extends IntArray3D<T>
    {
        IntArray<T> array;

        public Wrapper(IntArray<T> array)
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
        public void setInt(int x, int y, int z, int value)
        {
            this.array.setInt(new int[] {x, y, z}, value);
        }


        @Override
        public void set(int x, int y, int z, T value)
        {
            this.array.set(new int[] {x, y, z}, value);
        }
        
        @Override
        public IntArray3D<T> duplicate()
        {
            IntArray<T> dup = this.array.duplicate();
            if (dup instanceof IntArray3D)
            {
                return (IntArray3D<T>) dup;
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
        public IntArray2D<T> slice(int sliceIndex)
        {
            return new SliceView(sliceIndex);
        }

        @Override
        public Iterable<? extends IntArray2D<T>> slices()
        {
            return new Iterable<IntArray2D<T>>()
            {
                @Override
                public java.util.Iterator<IntArray2D<T>> iterator()
                {
                    return new SliceIterator();
                }
            };
        }

        @Override
        public java.util.Iterator<? extends ScalarArray2D<T>> sliceIterator()
        {
            return new SliceIterator();
        }
        
        
        private class SliceView extends IntArray2D<T>
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
            public void set(int x, int y, T value)
            {
                array.set(new int[] {x, y, this.sliceIndex}, value);
            }
            
            @Override
            public int getInt(int... pos)
            {
                return array.getInt(new int[] {pos[0], pos[1], this.sliceIndex});
            }

            @Override
            public void setInt(int[] pos, int value)
            {
                Wrapper.this.setInt(new int[] {pos[0], pos[1], this.sliceIndex}, value);
            }

            @Override
            public double getValue(int... pos)
            {
                return array.getValue(pos[0], pos[1], this.sliceIndex);
            }

            @Override
            public void setValue(int[] pos, double value)
            {
                array.setValue(pos, value);
            }

            @Override
            public IntArray<T> newInstance(int... dims)
            {
                return array.newInstance(dims);
            }

            @Override
            public net.sci.array.scalar.IntArray.Factory<T> getFactory()
            {
                return array.getFactory();
            }

            @Override
            public Class<T> dataType()
            {
                return array.dataType();
            }

            @Override
            public IntArray2D<T> duplicate()
            {
                int[] dims = size();
                IntArray2D<T> res = IntArray2D.wrap(array.newInstance(dims));
                for (int[] pos : res.positions())
                {
                    res.setInt(pos, array.getInt(pos));
                }
                return res;
            }

            @Override
            public T get(int... pos)
            {
                return array.get(new int[] {pos[0], pos[1], this.sliceIndex});
            }

            @Override
            public void set(int[] pos, T value)
            {
                array.set(new int[] {pos[0], pos[1], this.sliceIndex}, value);
            }

            @Override
            public net.sci.array.scalar.IntArray.Iterator<T> iterator()
            {
                return new Iterator();
            }

            class Iterator implements IntArray.Iterator<T>
            {
                int indX = -1;
                int indY = 0;
                
                public Iterator() 
                {
                }
                
                @Override
                public T next()
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
                public T get()
                {
                    return array.get(indX, indY, sliceIndex);
                }
            
                @Override
                public void set(T value)
                {
                    array.set(new int[] {indX, indY, sliceIndex}, value);
                }
            }
        }
        
        
        private class SliceIterator implements java.util.Iterator<IntArray2D<T>> 
        {
            int sliceIndex = 0;

            @Override
            public boolean hasNext()
            {
                return sliceIndex < Wrapper.this.size2;
            }

            @Override
            public IntArray2D<T> next()
            {
                return new SliceView(sliceIndex++);
            }
        }
    }
}
