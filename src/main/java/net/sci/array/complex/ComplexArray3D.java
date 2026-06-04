/**
 * 
 */
package net.sci.array.complex;

import net.sci.array.Array3D;

/**
 * @author dlegland
 *
 */
public abstract class ComplexArray3D<C extends Complex<C>> extends Array3D<C> implements ComplexArray<C>
{
    // =============================================================
    // Constructor
    
    protected ComplexArray3D(int size0, int size1, int size2)
    {
        super(size0, size1, size2);
    }


    // =============================================================
    // Specialization of the Array interface
    
    @Override
    public abstract ComplexArray3D<C> duplicate();    
}
