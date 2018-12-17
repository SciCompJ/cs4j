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
        for (int c = 0; c < nc; c++)
        {
            for (int r = 0; r < nr; r++)
            {
                // iterate over current row of first matrix and current column of second matrix
                double acc = 0;
                for (int i = 0; i < n; i++)
                {
                    acc += this.get(r, i) * that.get(i, c);
                }
                res.set(r, c, acc);
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
        for (int r = 0; r < this.nRows; r++)
        {
            for (int c = 0; c < this.nCols; c++)
            {
                res.set(c, r, this.get(r, c));
            }
        }
        
        // return result
        return res;
    }

    
    // =============================================================
    // getter / setter

    public abstract double get(int x, int y);

    public abstract void set(int x, int y, double value);

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
        for (int c = 0; c < this.nCols; c++)
        {
            for (int r = 0; r < this.nRows; r++)
            {
                matrix.set(r, c, this.get(r, c));
            }
        }
        return matrix;
    }

}
