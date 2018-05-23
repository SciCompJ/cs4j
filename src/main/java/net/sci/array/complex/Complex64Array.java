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
//        case 2:
//            return Float64Array2D.create(dims[0], dims[1]);
//        case 3:
//            return Float64Array3D.create(dims[0], dims[1], dims[2]);
        default:
            throw new RuntimeException("Not yet implemented");
//            return Float64ArrayND.create(dims);
        }
        
    }
   
//    public static Complex64Array convert(Array<?> array)
//    {
//        Complex64Array result = Complex64Array.create(array.getSize());
//        Array.Iterator<?> iter1 = array.iterator();
//        Complex64Array.Iterator iter2 = result.iterator();
//        while (iter1.hasNext() && iter2.hasNext())
//        {
//            iter2.setNextValue(iter1.nextValue());
//        }
//        return result;
//    }
    
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

    @Override
    public default ComplexArray.Factory<Complex64> getFactory()
    {
        return factory;
    }

    @Override
    public default Complex64Array duplicate()
    {
        // create output array
        Complex64Array result = Complex64Array.create(this.getSize());

        // iterate over positions
        for (int[] pos : this.positions())
        {
            result.set(pos, this.get(pos));
        }
        
        // return output
        return result;
    }

    @Override
    public default Class<Complex64> getDataType()
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
        ComplexArray<? extends Complex> array;
        
        public Wrapper(ComplexArray<?> array)
        {
            this.array = array;
        }

        
        // =============================================================
        // Implementation of the ComplexArray interface

        @Override
        public void setValue(int[] pos, double real, double imag)
        {
            array.setValue(pos, real, imag); 
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
        public int[] getSize()
        {
            return array.getSize();
        }

        @Override
        public int getSize(int dim)
        {
            return array.getSize(dim);
        }

        @Override
        public double getValue(int[] position)
        {
            return array.getValue(position);
        }


        @Override
        public void setValue(int[] position, double value)
        {
            array.setValue(position, value);
        }


        @Override
        public Complex64 get(int[] pos)
        {
            Complex c = array.get(pos);
            return new Complex64(c.real(), c.imag());
        }

        @Override
        public void set(int[] pos, Complex64 value)
        {
            array.setValue(pos, value.real(), value.imag());
        }

        @Override
        public PositionIterator positionIterator()
        {
            return array.positionIterator();
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
                return new Complex64(iter.next());
            }

            @Override
            public boolean hasNext()
            {
                return iter.hasNext();
            }

            @Override
            public double getValue()
            {
                return iter.getValue();
            }

            @Override
            public void setValue(double value)
            {
                iter.setValue(value);
            }

            @Override
            public Complex64 get()
            {
                return new Complex64(iter.get());
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
