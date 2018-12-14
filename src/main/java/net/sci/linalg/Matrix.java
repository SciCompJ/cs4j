/**
 * 
 */
package net.sci.linalg;

import net.sci.array.scalar.Float64Array2D;

/**
 * A matrix that behave like a 2D array with additional properties.
 * 
 * @author dlegland
 *
 */
public class Matrix 
{
    // =============================================================
    // inner variables
    
    Float64Array2D data;
    int nRows;
    int nCols;
    
    
    // =============================================================
    // Constructors

    /**
     * 
     */
    public Matrix(int nRows, int nCols)
    {
        this.nRows = nRows;
        this.nCols = nCols;
        this.data = Float64Array2D.create(nRows, nCols);
    }

    /**
     * 
     */
    public Matrix(Float64Array2D data)
    {
        this.nRows = data.getSize(0);
        this.nCols = data.getSize(1);
        this.data = data;
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
        Matrix res = new Matrix(nr, nc);
        
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
        Matrix res = new Matrix(this.nCols, this.nRows);
        
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

    public double get(int x, int y)
    {
        return this.data.getValue(x, y);
    }

    public void set(int x, int y, double value)
    {
        this.data.setValue(x, y, value);
    }

    public int getSize(int dim)
    {
        return data.getSize(dim);
    }

    // =============================================================
    // Implementation of the Array interface

    public Matrix duplicate()
    {
        return new Matrix(this.data.duplicate());
    }

}
