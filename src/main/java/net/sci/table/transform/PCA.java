/**
 * 
 */
package net.sci.table.transform;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import net.sci.table.NumericColumn;
import net.sci.table.Table;

/**
 * Transform a table using Principal Component Analysis.
 * 
 * @author dlegland
 */
public class PCA
{
    boolean scaled = true;
    
    double[] meanValues;
    
    Table scores;

    Table loadings;
    
    Table eigenValues;
    
    
    /**
     * Default empty constructor.
     */
    public PCA()
    {
    }

    public PCA fit(Table table)
    {
        // get table size
        int nr = table.rowNumber();
        int nc = table.columnNumber();

        // assumes nr >> nc
        if (nr < nc)
        {
            throw new IllegalArgumentException("Requires table with more rows than columns");
        }
        // Check all columns are numeric
        for (int c = 0; c < nc; c++)
        {
            if (!(table.column(c) instanceof NumericColumn))
            {
                throw new IllegalArgumentException("Requires table with numeric columns only");
            }
        }
        
        // get table name
        String name = table.getName();
        
        // First step is to recenter the data (keep the mean in the meanValues field).
        Table cTable = recenterAndScale(table);
        
        // Compute covariance matrix = cData' * cData;
        Matrix covMat = covarianceMatrix(cTable);

        
        // Diagonalisation of the covariance matrix.
        SingularValueDecomposition svd = new SingularValueDecomposition(covMat);

        
        // Extract eigen values
        this.eigenValues = eigenValuesMatrixToTable(svd.getS());
        if (name == null)
        {
            this.eigenValues.setName("Eigen Values");
        }
        else
        {
            this.eigenValues.setName("Eigen Values of " + name);
        }
        
        // convert matrix U into Loadings table
        this.loadings= matrixToTable(svd.getU());
        for (int c = 0; c < nc; c++)
        {
            this.loadings.setRowName(c, table.getColumnName(c));
        }

        // setup column names
        for (int c = 0; c < nc; c++)
        {
            this.loadings.setColumnName(c, "CP" + c);
        }
        if (name == null)
        {
            this.loadings.setName("Loadings");
        }
        else
        {
            this.loadings.setName("Loadings of " + name);
        }

        // Also compute scores
        this.scores = transform(table);
        if (name == null)
        {
            this.scores.setName("Scores");
        }
        else
        {
            this.scores.setName("Scores of " + name);
        }

        return this;
    }
    
    private Table recenterAndScale(Table table)
    {
        int nr = table.rowNumber();
        int nc = table.columnNumber();

        // compute mean values
        this.meanValues = new double[nc];
        for (int c = 0; c < nc; c++)
        {
            this.meanValues[c] = computeMean((NumericColumn) table.column(c));
        }
        
        // create result
        Table res = Table.create(nr, nc);
        if (this.scaled)
        {
            // compute variance
            double[] vars = new double[nc];
            for (int c = 0; c < nc; c++)
            {
                double sum = 0;
                for (double v : (NumericColumn) table.column(c))
                {
                    v -= this.meanValues[c];
                    sum += v * v;
                }
                
                // avoid degenerate cases
                vars[c] = Math.sqrt(sum / (nr - 1));
                if (vars[c] < 1e-10)
                {
                    vars[c] = 1.0;
                }
            }            

            // remove mean and variance
            for (int c = 0; c < nc; c++)
            {
                for (int r = 0; r < nr; r++)
                {
                    res.setValue(r, c, (table.getValue(r, c) - this.meanValues[c]) / vars[c]);
                }
            }            
        }
        else
        {
            // simply remove the mean
            for (int c = 0; c < nc; c++)
            {
                for (int r = 0; r < nr; r++)
                {
                    res.setValue(r, c, table.getValue(r, c) - this.meanValues[c]);
                }
            }            
        }
        
        return res;
    }

    private double computeMean(NumericColumn column)
    {
        int nr = column.length();
        double sum = 0;
        for (double v : column) 
        {
            sum += v;
        }
        return sum / nr;
    }
    
    private Matrix covarianceMatrix(Table cTable)
    {
        int nr = cTable.rowNumber();
        int nc = cTable.columnNumber();
        
        // Compute covariance matrix = cData' * cData;
        Matrix covMat = new Matrix(nc, nc);
        for (int c1 = 0; c1 < nc; c1++)
        {
            // variance of i-th variable
            double var = 0;
            for (int r = 0; r < nr; r++)
            {
                double v = cTable.getValue(r, c1);
                var += v * v; 
            }
            covMat.set(c1, c1, var / (nr - 1));

            // compute covariance with other variables that are not yet computed
            for (int c2 = c1+1; c2 < nc; c2++)
            {
                double sum = 0;
                for (int r = 0; r < nr; r++)
                {
                    sum += cTable.getValue(r, c1) * cTable.getValue(r, c2); 
                }
                covMat.set(c1, c2, sum / (nr - 1));
                covMat.set(c2, c1, sum / (nr - 1));
            }
        }
        
        return covMat;
    }
    
    /**
     * Converts a JAMA matrix into a Table instance.
     * 
     * @param matrix
     *            the matrix to convert
     * @return the converted table.
     */
    private Table matrixToTable(Matrix matrix)
    {
        int nRows = matrix.getRowDimension();
        int nCols = matrix.getColumnDimension();
        Table table = Table.create(nRows,  nCols);
        
        for (int row = 0; row < nRows; row++)
        {
            for (int col = 0; col < nCols; col++)
            {
                table.setValue(row, col, matrix.get(row, col));
            }
        }

        return table;
    }

    /**
     * Convert the diagonal matrix containing eigen values into an eigen values
     * table with three columns containing eigen values, inertia fractions, and
     * cumulated inertia fractions.
     * 
     * @param S
     *            the diagonal matrix of the eigen values
     * @return the eigen values table
     */
    private Table eigenValuesMatrixToTable(Matrix S)
    {
        int nRows = S.getRowDimension();
        
        Table tab = Table.create(nRows, 3);
        tab.setColumnNames(new String[]{"EigenValues", "Inertia", "Cumulated"});
        
        // compute sum of inertia
        double sum = 0;
        for (int row = 0; row < nRows; row++)
        {
            double val = S.get(row, row);
            tab.setValue(row, 0, val);
            sum += val;
        }

        // populate table
        double buffer = 0;
        for (int row = 0; row < nRows; row++)
        {
            double val = S.get(row, row) / sum;
            tab.setValue(row, 1, val);
            buffer += val;
            tab.setValue(row, 2, buffer);
        }

        // set up names
        for (int row = 0; row < nRows; row++)
        {
            String name = "CP" + row;
            tab.setRowName(row, name);
        }

        return tab;
    }

    /**
     * Applies dimensionality reduction to the input data table.
     * 
     * @param table
     *            the date table whose dimension has to be reduced
     * @return the transformed data table
     */
    public Table transform(Table table)
    {
        // get table size
        int nr = table.rowNumber();
        int nc = table.columnNumber();

        // check table dimension
        if (nc != this.loadings.rowNumber())
        {
            throw new IllegalArgumentException("Input table must have " + nc + " columns");
        }
        
        // Computes scores
        Table res = Table.create(nr, nc);
        for (int r = 0; r < nr; r++)
        {
            for (int c = 0; c < nc; c++)
            {
                double value = 0;
                for (int k = 0; k < nc; k++)
                {
                    value += table.getValue(r, k) * this.loadings.getValue(k, c); 
                }
                res.setValue(r, c, value);
            }
            
            res.setRowName(r, table.getRowName(r));
        }

        // setup column names
        res.setColumnNames(this.loadings.getColumnNames());

        return res;
    }
    
    public Table eigenValues()
    {
        return this.eigenValues;
    }
    
    public Table loadings()
    {
        return this.loadings;
    }
    
    public Table scores()
    {
        return this.scores;
    }
}
