/**
 * 
 */
package net.sci.array.complex;

import net.sci.array.Array2D;
import net.sci.array.complex.ComplexArray;

/**
 * @author dlegland
 *
 */
public abstract class ComplexArray2D<C extends Complex> extends Array2D<C> implements ComplexArray<C>
{
    // =============================================================
    // Constructor
    
    protected ComplexArray2D(int size0, int size1)
    {
        super(size0, size1);
    }


    // =============================================================
    // Implementation of the Array2D superclass

    /* (non-Javadoc)
     * @see net.sci.array.data.Array2D#getValue(int, int)
     */
    @Override
    public double getValue(int x, int y)
    {
        return get(x, y).modulus();
    }
    

    // =============================================================
    // Specialization of the Array interface

    
    @Override
    public abstract ComplexArray2D<C> duplicate();
    
}
