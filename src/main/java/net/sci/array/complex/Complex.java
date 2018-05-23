/**
 * 
 */
package net.sci.array.complex;

/**
 * Generic interface for complex values, that can be implemented in different ways.
 * 
 * @author dlegland
 *
 */
public interface Complex
{
    public Complex times(Complex complex);
    public Complex plus(Complex complex);
    public Complex minus(Complex complex);
    
    public Complex times(double value);
    public Complex plus(double value);
    public Complex minus(double value);
    
    public Complex conjugate();

    public double modulus();
    
    public double argument();
    
    /**
     * Returns the real part of this complex.
     * 
     * @return the real part of this complex.
     */
    public double real();

    /**
     * Returns the imaginary part of this complex.
     * 
     * @return the imaginary part of this complex.
     */
    public double imag();
}
