/**
 * 
 */
package net.sci.linalg;

import net.sci.linalg.matrix.DefaultMatrix;

/**
 * A matrix that behave like a 2D array with additional properties.
 * 
 * @author dlegland
 *
 */
public abstract class Matrix 
{
    // =============================================================
    // Static constructors
    
    public static final Matrix create(int nRows, int nCols)
    {
        return new DefaultMatrix(nRows, nCols);
    }
    
    // =============================================================
    // Inner variables
    
    int nRows;
    int nCols;
    
    
    // =============================================================
    // Constructors

    /**
     * Constructor that specificies matrix size.
     * 
     * @param nRows
     *            the number of rows
     * @param nCols
     *            the number of columns
     */
    protected Matrix(int nRows, int nCols)
    {
        this.nRows = nRows;
        this.nCols = nCols;
    }


    // =============================================================
    // New methods

    public Matrix times(Matrix that)
    {
        // number of rows of first matrix (and of result)
        int nr = this.nRows;

        // number of columns / number of rows of argument
        int n = this.nCols;
        int n2 = that.nRows;
        if (n2 != n)
        {
            throw new IllegalArgumentException(String.format(
                    "Number of columns of this matrix (%d) does not match number of rows of argument matrix (%d)",
                    n, n2));
        }
        
        // number of columns of second matrix (and of result)
        int nc = that.nCols;
        
        // allocate memory for result
        Matrix res = Matrix.create(nr, nc);
        
        // iterate over positions within result matrix
        for (int j = 0; j < nc; j++)
        {
            for (int i = 0; i < nr; i++)
            {
                // iterate over current row of first matrix and current column of second matrix
                double acc = 0;
                for (int k = 0; k < n; k++)
                {
                    acc += this.get(i, k) * that.get(k, j);
                }
                res.set(i, j, acc);
            }
        }

        // return result
        return res;   
    }

    public Matrix transpose()
    {
        // allocate memory for result
        Matrix res = Matrix.create(this.nCols, this.nRows);
        
        // iterate over positions of input
        for (int i = 0; i < this.nRows; i++)
        {
            for (int j = 0; j < this.nCols; j++)
            {
                res.set(j, i, this.get(i, j));
            }
        }
        
        // return result
        return res;
    }

    
    // =============================================================
    // getter / setter

    public abstract double get(int i, int j);

    public abstract void set(int i, int j, double value);

    public int getSize(int dim)
    {
        switch (dim)
        {
        case 0: return nRows;
        case 1: return nCols;
        default:
            throw new IllegalArgumentException("Size argument must be either 0 or 1, not " + dim);
        }
    }

    // =============================================================
    // Implementation of the Array interface

    public Matrix duplicate()
    {
        Matrix matrix = Matrix.create(this.nRows, this.nCols);
        for (int j = 0; j < this.nCols; j++)
        {
            for (int i = 0; i < this.nRows; i++)
            {
                matrix.set(i, j, this.get(i, j));
            }
        }
        return matrix;
    }

}
