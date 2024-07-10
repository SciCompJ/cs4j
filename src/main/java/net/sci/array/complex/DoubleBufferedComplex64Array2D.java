/**
 * 
 */
package net.sci.array.complex;

/**
 * Store complex values as pairs of double real+imag component pairs.
 * 
 * @author dlegland
 *
 */
public class DoubleBufferedComplex64Array2D extends Complex64Array2D
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
     */
    public DoubleBufferedComplex64Array2D(int size0, int size1)
    {
        super(size0, size1);
    }

    public DoubleBufferedComplex64Array2D(int size0, int size1, double[] buffer)
    {
        super(size0, size1);
        if (buffer.length < size0 * size1 * 2)
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
    public Complex64 get(int x, int y)
    {
        int offset = 2 * (y * size0 + x);
        return new Complex64(buffer[offset], buffer[offset+1]);
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
     */
    @Override
    public void set(int x, int y, Complex64 value)
    {
        int offset = 2 * (y * size0 + x);
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
        int offset = 2 * (pos[1] * size0 + pos[0]);
        this.buffer[offset++] = real;
        this.buffer[offset++] = imag;
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.data.ComplexArray#getValues(int[])
     */
    @Override
    public double[] getValues(int[] pos)
    {
        int offset = 2 * (pos[1] * size0 + pos[0]);
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
    public Complex64Array2D duplicate()
    {
        double[] newBuffer = new double[this.buffer.length];
        return new DoubleBufferedComplex64Array2D(size0, size1, newBuffer);
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
            return this.index < (size0 * size1 * 2 - 1);
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
}
