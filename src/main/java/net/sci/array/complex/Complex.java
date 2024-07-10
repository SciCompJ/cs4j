/**
 * 
 */
package net.sci.array.complex;

import net.sci.array.numeric.Numeric;

/**
 * Generic interface for complex values, that can be implemented in different ways.
 * 
 * @author dlegland
 *
 */
public interface Complex<C extends Complex<C>> extends Numeric<C>
{
    // =============================================================
    // Naw methods specific to Complex
    
    public C times(C complex);
    
    public C plus(double value);
    public C minus(double value);
    
    public C conjugate();

    
    // =============================================================
    // Accessors
    
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
    
    public double modulus();
    
    public double argument();
    
    
    // =============================================================
    // Specialize definitions from Numeric
    
    public C plus(C complex);
    public C minus(C complex);
    public C times(double value);
    public C divideBy(double value);
    
}
