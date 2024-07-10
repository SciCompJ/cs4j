/**
 * 
 */
package net.sci.array.complex;

/**
 * @author dlegland
 *
 */
public interface Complex64Array extends ComplexArray<Complex64>
{
    // =============================================================
    // Static variables

    public static final ComplexArray.Factory<Complex64> factory = new ComplexArray.Factory<Complex64>()
    {
        @Override
        public ComplexArray<Complex64> create(int[] dims)
        {
            return Complex64Array.create(dims);
        }

        @Override
        public Complex64Array create(int[] dims, Complex64 value)
        {
            Complex64Array array = Complex64Array.create(dims);
            array.fill(value);
            return array;
        }
    };

    
    // =============================================================
    // Static methods

    public static Complex64Array create(int... dims)
    {
        
        switch (dims.length)
        {
        case 2:
            return Complex64Array2D.create(dims[0], dims[1]);
//        case 3:
//            return Float64Array3D.create(dims[0], dims[1], dims[2]);
            // TODO: complete for other dimensions
        default:
            throw new RuntimeException("Not yet implemented");
        }
    }
   
    public static Complex64Array wrap(ComplexArray<?> array)
    {
        if (array instanceof Complex64Array)
        {
            return (Complex64Array) array;
        }
        return new Wrapper(array);
    }
    

    // =============================================================
    // Specialization of Array interface

    @Override
    public default Complex64Array newInstance(int... dims)
    {
        return Complex64Array.create(dims);
    }

    /**
     * Override default behavior of Array interface to return the value
     * <code>Complex64.ZERO</code>.
     * 
     * @return a default Complex64 value.
     */
    @Override
    public default Complex64 sampleElement()
    {
        return Complex64.ZERO;
    }
    
    @Override
    public default ComplexArray.Factory<Complex64> factory()
    {
        return factory;
    }

    @Override
    public default Complex64Array duplicate()
    {
        // create output array
        Complex64Array result = Complex64Array.create(this.size());

        // iterate over positions
        for (int[] pos : this.positions())
        {
            result.set(pos, this.get(pos));
        }
        
        // return output
        return result;
    }

    @Override
    public default Class<Complex64> elementClass()
    {
        return Complex64.class;
    }

    public Iterator iterator();
    
    
    // =============================================================
    // Inner interface

    public interface Iterator extends ComplexArray.Iterator<Complex64>
    {
        // specialize
        @Override
        public Complex64 get();
    }

    class Wrapper implements Complex64Array
    {
        ComplexArray<? extends Complex<?>> array;
        
        public Wrapper(ComplexArray<?> array)
        {
            this.array = array;
        }

        
        // =============================================================
        // Implementation of the ComplexArray interface

        @Override
        public void setValues(int[] pos, double real, double imag)
        {
            array.setValues(pos, real, imag); 
        }

        @Override
        public double[] getValues(int[] pos)
        {
            return array.getValues(pos);
        }


        // =============================================================
        // Implementation of the Array interface

        @Override
        public int dimensionality()
        {
            return array.dimensionality();
        }

        @Override
        public int[] size()
        {
            return array.size();
        }

        @Override
        public int size(int dim)
        {
            return array.size(dim);
        }

        @Override
        public Complex64 get(int[] pos)
        {
            return Complex64.convert(array.get(pos));
        }

        @Override
        public void set(int[] pos, Complex64 value)
        {
            array.setValues(pos, value.real(), value.imag());
        }

        @Override
        public Iterator iterator()
        {
            return new Iterator(array.iterator());
        }
        
        class Iterator implements Complex64Array.Iterator
        {
            ComplexArray.Iterator<?> iter;
            
            public Iterator(ComplexArray.Iterator<?> iter)
            {
                this.iter = iter;
            }

            @Override
            public void forward()
            {
                this.iter.forward();
            }

            @Override
            public Complex64 next()
            {
                return Complex64.convert(iter.next());
            }

            @Override
            public boolean hasNext()
            {
                return iter.hasNext();
            }

            @Override
            public Complex64 get()
            {
                return Complex64.convert(iter.get());
            }

            @Override
            public void set(Complex64 complex)
            {
                iter.setValue(complex.real(), complex.imag());
            }

            @Override
            public void setValue(double real, double imag)
            {
                iter.setValue(real, imag);
            }
        }
    }
}
