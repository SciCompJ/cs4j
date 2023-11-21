/**
 * 
 */
package net.sci.array.scalar;

import net.sci.array.Array3D;

/**
 * Specialization of Array for 3D arrays of scalar values.
 * 
 * @param <S>
 *            the type of Scalar.
 * @author dlegland
 *
 */
public abstract class ScalarArray3D<S extends Scalar<S>> extends Array3D<S> implements ScalarArray<S>
{
    // =============================================================
    // Static methods

    public final static <S extends Scalar<S>> ScalarArray3D<S> wrap(ScalarArray<S> array)
    {
        if (array instanceof ScalarArray3D)
        {
            return (ScalarArray3D<S>) array;
        }
        return new Wrapper<S>(array);
    }
    
    /**
     * Same as wrap method, but use different name to avoid runtime class cast
     * exceptions.
     * 
     * @param T
     *            the type of data within the array
     * @param array
     *            an instance of ScalarArray with three dimensions
     * @return an instance of ScalarArray2D
     */
   public final static <S extends Scalar<S>> ScalarArray3D<S> wrapScalar3d(ScalarArray<S> array)
    {
        if (array instanceof ScalarArray3D)
        {
            return (ScalarArray3D<S>) array;
        }
        return new Wrapper<S>(array);
    }

    // =============================================================
    // Constructor


	protected ScalarArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

    // =============================================================
    // New methods

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
    public void fillValues(TriFunction<Integer,Integer,Integer,Double> fun)
    {
        for (int[] pos : this.positions())
        {
            this.setValue(pos[0], pos[1], pos[2], fun.apply(pos[0], pos[1], pos[2]));
        }
    }
    
    
    // =============================================================
    // New getter / setter
    
    /**
     * Returns the value of an element in the array at the position given by the
     * integer indices.
     * 
     * @param x
     *            index over the first array dimension
     * @param y
     *            index over the second array dimension
     * @param z
     *            index over the third array dimension
     * @return the double value at the specified position
     */
    public abstract double getValue(int x, int y, int z);

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
    public abstract ScalarArray2D<S> slice(int sliceIndex);

    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public abstract Iterable<? extends ScalarArray2D<S>> slices();

    /**
     * Creates an iterator over the slices
     * 
     * @return an iterator over 2D slices
     */
    public abstract java.util.Iterator<? extends ScalarArray2D<S>> sliceIterator();

    
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
	public abstract ScalarArray3D<S> duplicate();

	
    // =============================================================
    // Inner implementation of iterator on double values
    
    private class DoubleIterator implements java.util.Iterator<Double>
    {
        int x = -1;
        int y = 0;
        int z = 0;
        
        @Override
        public boolean hasNext()
        {
            return x < ScalarArray3D.this.size0 - 1 || y < ScalarArray3D.this.size1 - 1 || z < ScalarArray3D.this.size2 - 1;
        }

        @Override
        public Double next()
        {
            x++;
            if (x >= ScalarArray3D.this.size0)
            {
                x = 0;
                y++;
                if (y >= ScalarArray3D.this.size1)
                {
                    y = 0;
                    z++;
                }
            }

            return getValue(x, y, z);
        }
    }
    
    
    // =============================================================
    // Inner Wrapper class

    private static class Wrapper<S extends Scalar<S>> extends ScalarArray3D<S>
    {
        private ScalarArray<S> array;
        
        protected Wrapper(ScalarArray<S> array)
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

        public ScalarArray2D<S> slice(int sliceIndex)
        {
            return new SliceView(sliceIndex);
        }

        /**
         * Iterates over the slices
         * 
         * @return an iterator over 2D slices
         */
        public Iterable<? extends ScalarArray2D<S>> slices()
        {
            return new Iterable<ScalarArray2D<S>>()
            {
                @Override
                public java.util.Iterator<ScalarArray2D<S>> iterator()
                {
                    return new SliceIterator();
                }
            };
        }

        public java.util.Iterator<? extends ScalarArray2D<S>> sliceIterator()
        {
            return new SliceIterator();
        }

        // =============================================================
        // Implements the ScalarArray3D interface

        @Override
        public double getValue(int x, int y, int z)
        {
            return array.getValue(new int[] {x, y, z});
        }

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
        public double getValue(int[] pos)
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
        public S get(int[] pos)
        {
            return this.array.get(pos);
        }

        /**
         * set value at specified position
         */
        @Override
        public void set(int[] pos, S value)
        {
            this.array.set(pos, value);
        }

        @Override
        public S get(int x, int y, int z)
        {
            // get value at specified position
            return this.array.get(new int[] {x, y, z});
        }

        @Override
        public void set(int x, int y, int z, S value)
        {
            Wrapper.this.set(new int[] {x, y, z}, value);            
        }


        // =============================================================
        // Implements the Array interface

        @Override
        public ScalarArray3D<S> duplicate()
        {
            ScalarArray<S> tmp = this.array.newInstance(this.size0, this.size1, this.size2);
            if (!(tmp instanceof ScalarArray3D))
            {
                // ensure result is instance of ScalarArray3D
                tmp = new Wrapper<S>(tmp);
            }
            
            ScalarArray3D<S> result = (ScalarArray3D <S>) tmp;
            
            ScalarArray.Iterator<S> iter1 = this.array.iterator();
            ScalarArray.Iterator<S> iter2 = result.iterator();
            
            // Fill new array with input array
            while(iter1.hasNext() && iter2.hasNext())
            {
                iter2.setNextValue(iter1.nextValue());
            }
            
            return result;
        }
        
        @Override
        public Class<S> dataType()
        {
            return array.dataType();
        }

        @Override
        public ScalarArray<S> newInstance(int... dims)
        {
            return this.array.newInstance(dims);
        }

        @Override
        public ScalarArray.Factory<S> factory()
        {
            return this.array.factory();
        }

        @Override
        public ScalarArray.Iterator<S> iterator()
        {
            return new Iterator3D();
        }
        
        private class Iterator3D implements ScalarArray.Iterator<S>
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
            public S next()
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
            public S get()
            {
                return Wrapper.this.get(x, y, z);
            }

            @Override
            public void set(S value)
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
        
        private class SliceView extends ScalarArray2D<S>
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
            public double getValue(int x, int y)
            {
                return Wrapper.this.getValue(x, y, this.sliceIndex);            
            }

            @Override
            public void setValue(int x, int y, double value)
            {
                Wrapper.this.setValue(x, y, this.sliceIndex, value);            
            }

            @Override
            public S get(int x, int y)
            {
                return Wrapper.this.get(x, y, this.sliceIndex);            
            }

            @Override
            public void set(int x, int y, S value)
            {
                Wrapper.this.set(x, y, this.sliceIndex, value);            
            }

            @Override
            public double getValue(int[] pos)
            {
                return Wrapper.this.getValue(pos[0], pos[1], this.sliceIndex);
            }

            @Override
            public void setValue(int[] pos, double value)
            {
                Wrapper.this.setValue(pos[0], pos[1], this.sliceIndex, value);            
            }


            @Override
            public S get(int[] pos)
            {
                return Wrapper.this.get(pos[0], pos[1], this.sliceIndex);
            }

            @Override
            public void set(int[] pos, S value)
            {
                Wrapper.this.set(pos[0], pos[1], this.sliceIndex, value);            
            }

            @Override
            public ScalarArray<S> newInstance(int... dims)
            {
                return Wrapper.this.array.newInstance(dims);
            }

            @Override
            public Class<S> dataType()
            {
                return Wrapper.this.array.dataType();
            }

            @Override
            public ScalarArray2D<S> duplicate()
            {
                // create a new array, and ensure type is 2D
                ScalarArray2D<S> result = ScalarArray2D.wrap(Wrapper.this.array.newInstance(this.size0, this.size1));
                
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
            public ScalarArray.Factory<S> factory()
            {
                return Wrapper.this.factory();
            }

            @Override
            public net.sci.array.scalar.ScalarArray.Iterator<S> iterator()
            {
                return new Iterator();
            }

            class Iterator implements ScalarArray.Iterator<S>
            {
                int indX = -1;
                int indY = 0;
                
                public Iterator() 
                {
                }
                
                @Override
                public S next()
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
                public S get()
                {
                    return Wrapper.this.get(indX, indY, sliceIndex);
                }

                @Override
                public void set(S value)
                {
                    Wrapper.this.set(indX, indY, sliceIndex, value);
                }
            }
        }
        
        private class SliceIterator implements java.util.Iterator<ScalarArray2D<S>> 
        {
            int sliceIndex = -1;

            @Override
            public boolean hasNext()
            {
                return sliceIndex < array.size(2) - 1;
            }

            @Override
            public ScalarArray2D<S> next()
            {
                sliceIndex++;
                return new SliceView(sliceIndex);
            }
        }
    }

}
