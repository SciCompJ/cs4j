/**
 * 
 */
package net.sci.array.complex;

import net.sci.array.Array2D;

/**
 * @author dlegland
 *
 */
public abstract class ComplexArray2D<C extends Complex<C>> extends Array2D<C> implements ComplexArray<C>
{
    // =============================================================
    // Constructor
    
    protected ComplexArray2D(int size0, int size1)
    {
        super(size0, size1);
    }


    // =============================================================
    // Specialization of the Array interface
    
    @Override
    public abstract ComplexArray2D<C> duplicate();    
}
