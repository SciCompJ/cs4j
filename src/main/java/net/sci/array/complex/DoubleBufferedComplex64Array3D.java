/**
 * 
 */
package net.sci.array.complex;

import net.sci.array.Array2D;
import net.sci.util.MathUtils;

/**
 * Store complex values as pairs of double real+imag component pairs.
 * 
 * @author dlegland
 *
 */
public class DoubleBufferedComplex64Array3D extends Complex64Array3D
{
    // =============================================================
    // Class members

    double[] buffer;
    
    
    // =============================================================
    // Constructor
    
    /**
     * Initialize a new array of complex values.
     * 
     * @param size0
     *            array size in the first dimension
     * @param size1
     *            array size in the second dimension
     * @param size2
     *            array size in the third dimension
     */
    public DoubleBufferedComplex64Array3D(int size0, int size1, int size2)
    {
        super(size0, size1, size2);
    }

    public DoubleBufferedComplex64Array3D(int size0, int size1, int size2, double[] buffer)
    {
        super(size0, size1, size2);
        if (buffer.length < MathUtils.prod(size0, size1, size2, 2))
        {
            throw new IllegalArgumentException("Buffer size does not match array dimensions");
        }
        this.buffer = buffer;
    }

    
    // =============================================================
    // Implementation of the Array2D interface

    /* (non-Javadoc)
     * @see net.sci.array.data.Array2D#get(int, int)
     */
    @Override
    public Complex64 get(int x, int y, int z)
    {
        int offset = 2 * ((z * size1 + y) * size0 + x);
        return new Complex64(buffer[offset], buffer[offset+1]);
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
     */
    @Override
    public void set(int x, int y, int z, Complex64 value)
    {
        int offset = 2 * ((z * size1 + y) * size0 + x);
        this.buffer[offset++] = value.real();
        this.buffer[offset++] = value.imag();
    }
    
    
    // =============================================================
    // Methods
    
    /* (non-Javadoc)
     * @see net.sci.array.data.ComplexArray#setValue(int[], double, double)
     */
    @Override
    public void setValues(int[] pos, double real, double imag)
    {
        int offset = 2 * ((pos[2] * size1 + pos[1]) * size0 + pos[0]);
        this.buffer[offset++] = real;
        this.buffer[offset++] = imag;
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.data.ComplexArray#getValues(int[])
     */
    @Override
    public double[] getValues(int[] pos)
    {
        int offset = 2 * ((pos[2] * size1 + pos[1]) * size0 + pos[0]);
        return new double[] { this.buffer[offset], this.buffer[offset + 1] };
    }
    


    // =============================================================
    // Implementation of the Array interface

    /* (non-Javadoc)
     * @see net.sci.array.data.ComplexArray#newInstance(int[])
     */
    @Override
    public Complex64Array newInstance(int... dims)
    {
        return Complex64Array.create(dims);
    }

    /* (non-Javadoc)
     * @see net.sci.array.data.complex.Complex64Array2D#duplicate()
     */
    @Override
    public Complex64Array3D duplicate()
    {
        double[] newBuffer = new double[this.buffer.length];
        return new DoubleBufferedComplex64Array3D(size0, size1, size2, newBuffer);
    }


    /* (non-Javadoc)
     * @see net.sci.array.data.ComplexArray#iterator()
     */
    @Override
    public Complex64Array.Iterator iterator()
    {
        return new Iterator();
    }
    
    private class Iterator implements Complex64Array.Iterator
    {
        int index = -1;
        
        public Iterator() 
        {
        }
        
        @Override
        public boolean hasNext()
        {
            return this.index < (size0 * size1 * size2 * 2 - 1);
        }

        @Override
        public Complex64 next()
        {
            this.index += 2;
            return new Complex64(buffer[index], buffer[index+1]);
        }

        @Override
        public void forward()
        {
            this.index += 2;
        }

        @Override
        public void setValue(double real, double imag)
        {
            buffer[index] = real;
            buffer[index + 1] = imag;
        }

        @Override
        public Complex64 get()
        {
            return new Complex64(buffer[index], buffer[index+1]);
        }

        @Override
        public void set(Complex64 vect)
        {
            buffer[index] = vect.real();
            buffer[index + 1] = vect.imag();
        }
    }

    @Override
    public Array2D<Complex64> slice(int sliceIndex)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<? extends Array2D<Complex64>> slices()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public java.util.Iterator<? extends Array2D<Complex64>> sliceIterator()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
