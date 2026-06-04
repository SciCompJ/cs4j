/**
 * 
 */
package net.sci.array.complex;

/**
 * @author dlegland
 *
 */
public abstract class Complex64Array3D extends ComplexArray3D<Complex64> implements Complex64Array
{
    // =============================================================
    // Static methods
    
    public static final Complex64Array3D create(int size0, int size1, int size2)
    {
        return new DoubleBufferedComplex64Array3D(size0, size1, size2);
    }
    
    
    // =============================================================
    // Constructor
    
    protected Complex64Array3D(int size0, int size1, int size2)
    {
        super(size0, size1, size2);
    }

    // =============================================================
    // Specialization of the Array interface
    
    @Override
    public abstract Complex64Array3D duplicate();
}
