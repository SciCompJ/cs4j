/**
 * 
 */
package net.sci.linalg.matrix;

import net.sci.array.scalar.Float64Array2D;
import net.sci.linalg.Matrix;

/**
 * Default matrix implementation, based on an inner array of Float64.
 * 
 * @author dlegland
 *
 */
public class DefaultMatrix extends Matrix 
{
    // =============================================================
    // Inner variables
    
    Float64Array2D data;
    
    
    // =============================================================
    // Constructors

    /**
     * Creates a new matrix with the specified size.
     * 
     * @param nRows
     *            the number of rows
     * @param nCols
     *            the number of columns
     */
    public DefaultMatrix(int nRows, int nCols)
    {
        super(nRows, nCols);
        this.data = Float64Array2D.create(nRows, nCols);
    }

    /**
     * Creates a new matrix that encapsulates the specified array.
     * 
     * @param data
     *            the array containing matrix data
     */
    public DefaultMatrix(Float64Array2D data)
    {
        super(data.getSize(0), data.getSize(1));
        this.data = data;
    }


    // =============================================================
    // New methods


    
    // =============================================================
    // getter / setter

    /* (non-Javadoc)
     * @see net.sci.linalg.Matrix#get(int, int)
     */
    @Override
    public double get(int i, int j)
    {
        return this.data.getValue(i, j);
    }

    /* (non-Javadoc)
     * @see net.sci.linalg.Matrix#set(int, int, double)
     */
    @Override
    public void set(int i, int j, double value)
    {
        this.data.setValue(i, j, value);
    }


    // =============================================================
    // Implementation of the Array interface

    public DefaultMatrix duplicate()
    {
        return new DefaultMatrix(this.data.duplicate());
    }

}
