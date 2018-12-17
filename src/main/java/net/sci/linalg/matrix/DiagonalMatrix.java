/**
 * 
 */
package net.sci.linalg.matrix;

import net.sci.linalg.Matrix;

/**
 * @author dlegland
 *
 */
public class DiagonalMatrix extends Matrix
{
    // =============================================================
    // Inner variables
    
    double[] diagValues;
    
    
    // =============================================================
    // Constructors

    /**
     * Creates a new diagonal matrix by specifying the values along the
     * diagonal.
     * 
     * @param diagValues
     *            the values along the diagonal
     */
    public DiagonalMatrix(double[] diagValues)
    {
        super(diagValues.length, diagValues.length);
        this.diagValues = diagValues;
    }
    

    // =============================================================
    // getter / setter
    
    /* (non-Javadoc)
     * @see net.sci.linalg.Matrix#get(int, int)
     */
    @Override
    public double get(int i, int j)
    {
        return i == j ? diagValues[i] : 0.0;
    }
    
    /* (non-Javadoc)
     * @see net.sci.linalg.Matrix#set(int, int, double)
     */
    @Override
    public void set(int i, int j, double value)
    {
        if (i == j)
        {
            this.diagValues[i] = value;
        }
        else
        {
            throw new IllegalArgumentException("row and column indices must be the same."); 
        }
    }
    
    
    // =============================================================
    // Overload the Matrix methods
    
    public DiagonalMatrix transpose()
    {
        return this;
    }
    
    public DiagonalMatrix duplicate()
    {
        int n = this.diagValues.length;
        double[] diag2 = new double[n];
        System.arraycopy(this.diagValues, 0, diag2, 0, n);
        return new DiagonalMatrix(diag2);
    }

}
