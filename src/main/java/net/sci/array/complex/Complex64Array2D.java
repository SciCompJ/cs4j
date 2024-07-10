/**
 * 
 */
package net.sci.array.complex;

/**
 * @author dlegland
 *
 */
public abstract class Complex64Array2D extends ComplexArray2D<Complex64> implements Complex64Array
{
    // =============================================================
    // Static methods
    
    public static final Complex64Array2D create(int size0, int size1)
    {
        return new DoubleBufferedComplex64Array2D(size0, size1);
    }
    
    
    // =============================================================
    // Constructor
    
    protected Complex64Array2D(int size0, int size1)
    {
        super(size0, size1);
    }

    // =============================================================
    // Specialization of the Array interface
    
    @Override
    public abstract Complex64Array2D duplicate();
}
