/**
 * 
 */
package net.sci.array.scalar;

import java.util.function.Function;

import net.sci.array.Array3D;

/**
 * @author dlegland
 *
 */
public abstract class ScalarArray3D<T extends Scalar> extends Array3D<T> implements ScalarArray<T>
{
    // =============================================================
    // Static methods

    public final static <T extends Scalar> ScalarArray3D<T> wrap(ScalarArray<T> array)
    {
        if (array instanceof ScalarArray3D)
        {
            return (ScalarArray3D<T>) array;
        }
        return new Wrapper<T>(array);
    }
    
    /**
     * Same as wrap method, but use different name to avoid runtime class cast
     * exceptions.
     * 
     * @param T
     *            the type of data within the array
     * @param array
     *            an instance of ScalarArray with two dimensions
     * @return an instance of ScalarArray2D
     */
   public final static <T extends Scalar> ScalarArray3D<T> wrapScalar3d(ScalarArray<T> array)
    {
        if (array instanceof ScalarArray3D)
        {
            return (ScalarArray3D<T>) array;
        }
        return new Wrapper<T>(array);
    }

    // =============================================================
    // Constructor


	protected ScalarArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

    // =============================================================
    // New methods

    public void populateValues(Function<Double[],Double> fun)
    {
        Double[] input = new Double[3];
        for (int[] pos : this.positions())
        {
            input[0] = (double) pos[0];
            input[1] = (double) pos[1];
            input[2] = (double) pos[2];
            this.setValue(pos[0], pos[1], pos[2], fun.apply(input));
        }
    }

    /**
     * Initializes the content of the array by using the specified function of
     * three variables.
     * 
     * Example:
     * <pre>{@code
     * ScalarArray3D<?> array = UInt8Array3D.create(5, 4, 3);
     * array.populateValues((x,y,z) -> x + y * 10 + z * 100);
     * }</pre>
     * 
     * @param fun
     *            a function of three variables that returns a double. The three
     *            input variables correspond to the x, y, and z coordinates.
     */
    public void populateValues(TriFunction<Double,Double,Double,Double> fun)
    {
        for (int[] pos : this.positions())
        {
            this.setValue(pos[0], pos[1], pos[2], fun.apply((double) pos[0], (double) pos[1], (double) pos[2]));
        }
    }
    
    
    // =============================================================
    // New getter / setter
    
    /**
     * Changes the value of an element in the array at the position given by
     * three integer indices.
     * 
     * @param x
     *            index over the first array dimension
     * @param y
     *            index over the second array dimension
     * @param z
     *            index over the third array dimension
     * @param value
     *            the new value at the specified index
     */
    public abstract void setValue(int x, int y, int z, double value);

    
    // =============================================================
    // Specialization of the Array3D interface

    /**
     * Returns a view over the specified slice.
     * 
     * @param sliceIndex
     *            the index of the slice
     * @return a view on the specific slice, as a 2D array
     */
    public abstract ScalarArray2D<T> slice(int sliceIndex);

    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public abstract Iterable<? extends ScalarArray2D<T>> slices();

    /**
     * Creates an iterator over the slices
     * 
     * @return an iterator over 2D slices
     */
    public abstract java.util.Iterator<? extends ScalarArray2D<T>> sliceIterator();

    
    // =============================================================
    // Specialization of the ScalarArray interface

    
	// =============================================================
    // Specialization of the Array interface

	@Override
	public abstract ScalarArray3D<T> duplicate();

	
    // =============================================================
    // Inner Wrapper class

    private static class Wrapper<T extends Scalar> extends ScalarArray3D<T>
    {
        private ScalarArray<T> array;
        
        protected Wrapper(ScalarArray<T> array)
        {
            super(0, 0, 0);
            if (array.dimensionality() < 3)
            {
                throw new IllegalArgumentException("Requires an array with at least three dimensions");
            }
            this.array = array;
            this.size0 = array.size(0);
            this.size1 = array.size(1);
            this.size2 = array.size(2);
        }

        // =============================================================
        // Management of slices

        public ScalarArray2D<T> slice(int sliceIndex)
        {
            return new SliceView(sliceIndex);
        }

        /**
         * Iterates over the slices
         * 
         * @return an iterator over 2D slices
         */
        public Iterable<? extends ScalarArray2D<T>> slices()
        {
            return new Iterable<ScalarArray2D<T>>()
            {
                @Override
                public java.util.Iterator<ScalarArray2D<T>> iterator()
                {
                    return new SliceIterator();
                }
            };
        }

        public java.util.Iterator<? extends ScalarArray2D<T>> sliceIterator()
        {
            return new SliceIterator();
        }

        // =============================================================
        // Implements the ScalarArray3D interface

        @Override
        public void setValue(int x, int y, int z, double value)
        {
            Wrapper.this.setValue(new int[] {x, y, z}, value);            
        }


        // =============================================================
        // Implements the ScalarArray interface

        /** 
         * return value from specified position 
         * */
        @Override
        public double getValue(int... pos)
        {
            // return value from specified position
            return this.array.getValue(pos);
        }

        /**
         * set double value at specified position
         */
        @Override
        public void setValue(int[] pos, double value)
        {
            this.array.setValue(pos, value);
        }

        
        // =============================================================
        // Implements the Array3D interface

        /** 
         * return value from specified position 
         * */
        @Override
        public T get(int... pos)
        {
            return this.array.get(pos);
        }

        /**
         * set value at specified position
         */
        @Override
        public void set(int[] pos, T value)
        {
            this.array.set(pos, value);
        }

        @Override
        public void set(int x, int y, int z, T value)
        {
            Wrapper.this.set(new int[] {x, y, z}, value);            
        }


        // =============================================================
        // Implements the Array interface

        @Override
        public ScalarArray3D<T> duplicate()
        {
            ScalarArray<T> tmp = this.array.newInstance(this.size0, this.size1, this.size2);
            if (!(tmp instanceof ScalarArray3D))
            {
                // ensure result is instance of ScalarArray3D
                tmp = new Wrapper<T>(tmp);
            }
            
            ScalarArray3D<T> result = (ScalarArray3D <T>) tmp;
            
            ScalarArray.Iterator<T> iter1 = this.array.iterator();
            ScalarArray.Iterator<T> iter2 = result.iterator();
            
            // Fill new array with input array
            while(iter1.hasNext() && iter2.hasNext())
            {
                iter2.setNextValue(iter1.nextValue());
            }
            
            return result;
        }
        
        @Override
        public Class<T> dataType()
        {
            return array.dataType();
        }

        @Override
        public ScalarArray<T> newInstance(int... dims)
        {
            return this.array.newInstance(dims);
        }

        @Override
        public ScalarArray.Factory<T> getFactory()
        {
            return this.array.getFactory();
        }

        @Override
        public ScalarArray.Iterator<T> iterator()
        {
            return new Iterator3D();
        }
        
        private class Iterator3D implements ScalarArray.Iterator<T>
        {
            int x = -1;
            int y = 0;
            int z = 0;
            
            public Iterator3D() 
            {
            }
            
            @Override
            public boolean hasNext()
            {
                return this.x < size0 - 1 || this.y < size1 - 1|| this.z < size2 - 1;
            }

            @Override
            public T next()
            {
                forward();
                return Wrapper.this.get(x, y, z);
            }

            @Override
            public void forward()
            {
                this.x++;
                if (this.x == size0)
                {
                    this.x = 0;
                    this.y++;
                    if (this.y == size1)
                    {
                        this.y = 0;
                        this.z++;
                    }
                }
            }

            @Override
            public T get()
            {
                return Wrapper.this.get(x, y, z);
            }

            @Override
            public void set(T value)
            {
                Wrapper.this.set(x, y, z, value);
            }
            
            @Override
            public double nextValue()
            {
                forward();
                return getValue();
            }

            @Override
            public double getValue()
            {
                return Wrapper.this.getValue(x, y, z);
            }

            @Override
            public void setValue(double value)
            {
                Wrapper.this.setValue(x, y, z, value);             
            }
        }
        
        private class SliceView extends ScalarArray2D<T>
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
            public void setValue(int x, int y, double value)
            {
                Wrapper.this.setValue(x, y, this.sliceIndex, value);            
            }

            @Override
            public void set(int x, int y, T value)
            {
                Wrapper.this.set(x, y, this.sliceIndex, value);            
            }

             @Override
            public double getValue(int... pos)
            {
                return Wrapper.this.getValue(pos[0], pos[1], this.sliceIndex);
            }

            @Override
            public void setValue(int[] pos, double value)
            {
                Wrapper.this.setValue(pos[0], pos[1], this.sliceIndex, value);            
            }


            @Override
            public T get(int... pos)
            {
                return Wrapper.this.get(pos[0], pos[1], this.sliceIndex);
            }

            @Override
            public void set(int[] pos, T value)
            {
                Wrapper.this.set(pos[0], pos[1], this.sliceIndex, value);            
            }

            @Override
            public ScalarArray<T> newInstance(int... dims)
            {
                return Wrapper.this.array.newInstance(dims);
            }

            @Override
            public Class<T> dataType()
            {
                return Wrapper.this.array.dataType();
            }

            @Override
            public ScalarArray2D<T> duplicate()
            {
                // create a new array, and ensure type is 2D
                ScalarArray2D<T> result = ScalarArray2D.wrap(Wrapper.this.array.newInstance(this.size0, this.size1));
                
                // Fill new array with input slice
                for (int y = 0; y < Wrapper.this.size1; y++)
                {
                    for (int x = 0; x < Wrapper.this.size0; x++)
                    {
                        result.setValue(x, y, Wrapper.this.getValue(x, y, sliceIndex));
                    }
                }
                                
                return result;
            }

            @Override
            public ScalarArray.Factory<T> getFactory()
            {
                return Wrapper.this.getFactory();
            }

            @Override
            public net.sci.array.scalar.ScalarArray.Iterator<T> iterator()
            {
                return new Iterator();
            }

            class Iterator implements ScalarArray.Iterator<T>
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
                public double getValue()
                {
                    return Wrapper.this.getValue(indX, indY, sliceIndex);
                }

                @Override
                public void setValue(double value)
                {
                    Wrapper.this.setValue(indX, indY, sliceIndex, value);
                }

                @Override
                public T get()
                {
                    return Wrapper.this.get(indX, indY, sliceIndex);
                }

                @Override
                public void set(T value)
                {
                    Wrapper.this.set(indX, indY, sliceIndex, value);
                }
            }
        }
        
        private class SliceIterator implements java.util.Iterator<ScalarArray2D<T>> 
        {
            int sliceIndex = -1;

            @Override
            public boolean hasNext()
            {
                return sliceIndex < array.size(2) - 1;
            }

            @Override
            public ScalarArray2D<T> next()
            {
                sliceIndex++;
                return new SliceView(sliceIndex);
            }
        }
    }

}
