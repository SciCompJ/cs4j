/**
 * 
 */
package net.sci.array.complex;

/**
 * A complex value coded with two double precision floating point values.
 * 
 * @author dlegland
 *
 */
public class Complex64 implements Complex
{
    // =============================================================
    // Constants
    
    public static final Complex64 I = new Complex64(0, 1);
    
    
    // =============================================================
    // Class fields
    
    /** The real part of this complex */
    double real;
    
    /** The imaginary part of this complex */
    double imag;
    
    
    // =============================================================
    // Constructors
    
    /**
     * Empty constructor, corresponding to zero value. 
     */
    public Complex64()
    {
        this.real = 0;
        this.imag = 0;
    }
    
    /**
     * Constructor specifying the real and imaginary parts.
     * 
     * @param real
     *            the real part of the new complex
     * @param imag
     *            the imaginary part of the new complex
     */
    public Complex64(double real, double imag)
    {
        this.real = real;
        this.imag = imag;
    }
    
    /**
     * Copy constructor from another complex.
     * 
     * @param complex
     *            the complex value to copy
     */
    public Complex64(Complex complex)
    {
        this.real = complex.real();
        this.imag = complex.imag();
    }
    
    // =============================================================
    // Computation methods
    
    @Override
    public Complex times(Complex complex)
    {
        double r2 = complex.real();
        double i2 = complex.imag();
        
        double r = this.real * r2 - this.imag * i2;
        double i = this.real * i2 + this.imag * r2;
        
        return new Complex64(r, i);
    }

    public Complex plus(Complex that)
    {
        double r2 = this.real + that.real();
        double i2 = this.imag + that.imag();
        return new Complex64(r2, i2);
    }

    @Override
    public Complex minus(Complex that)
    {
        double r2 = this.real - that.real();
        double i2 = this.imag - that.imag();
        return new Complex64(r2, i2);
    }

    @Override
    public Complex times(double k)
    {
        return new Complex64(this.real * k, this.imag * k);
    }

    @Override
    public Complex plus(double value)
    {
        return new Complex64(this.real + value, this.imag);
    }

    @Override
    public Complex minus(double value)
    {
        return new Complex64(this.real - value, this.imag);
    }

    @Override
    public Complex conjugate()
    {
        return new Complex64(real, -imag);
    }

    
    // =============================================================
    // Accessors
    
    public double modulus()
    {
        return Math.hypot(real, imag);
    }

    public double argument()
    {
        return Math.atan2(imag, real);
    }

    @Override
    public double real()
    {
        return this.real;
    }

    @Override
    public double imag()
    {
        return this.imag;
    }
}
