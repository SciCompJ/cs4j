/**
 * 
 */
package net.sci.table.transform;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import net.sci.axis.CategoricalAxis;
import net.sci.table.Column;
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
    double[] scalings;
    
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
        int nr = table.rowCount();
        int nc = table.columnCount();

        // assumes nr >> nc
        if (nr < nc)
        {
            throw new IllegalArgumentException("Requires table with more rows than columns");
        }
        // Check all columns are numeric
        for (Column column : table.columns())
        {
            if (!(column instanceof NumericColumn))
            {
                throw new IllegalArgumentException("Requires table with numeric columns only");
            }
        }
        
        // get table name
        String name = table.getName();
        
        // First step is to remove mean and scale the data. Keep the mean and
        // the scaling factors within the "meanValues" and "scalings" fields.
        Table cTable = recenterAndScale(table);
        
        // Compute covariance matrix = cData' * cData;
        Matrix covMat = covarianceMatrix(cTable);

        // Diagonalisation of the covariance matrix.
        SingularValueDecomposition svd = new SingularValueDecomposition(covMat);
        
        // Extract eigen values
        this.eigenValues = eigenValuesMatrixToTable(svd.getS());
        this.eigenValues.setName(createNewName("Eigen Values", name));
        
        // convert matrix U into Loadings table
        this.loadings = matrixToTable(svd.getU());
        for (int c = 0; c < nc; c++)
        {
            this.loadings.setRowName(c, table.getColumnName(c));
        }

        // setup column names
        for (int c = 0; c < nc; c++)
        {
            this.loadings.setColumnName(c, "CP" + (c+1));
        }
        this.loadings.setName(createNewName("Loadings", name));

        // Also compute scores
        this.scores = transform(table);
        this.scores.setName(createNewName("Scores", name));

        return this;
    }
    
    private Table recenterAndScale(Table table)
    {
        int nr = table.rowCount();
        int nc = table.columnCount();

        // compute mean values
        this.meanValues = new double[nc];
        for (int c = 0; c < nc; c++)
        {
            this.meanValues[c] = computeMean((NumericColumn) table.column(c));
        }
        
        // create result
        Table res = Table.create(nr, nc);
        
        // compute variances 
        this.scalings = new double[nc];
        if (this.scaled)
        {
            for (int c = 0; c < nc; c++)
            {
                double var = Math.sqrt(computeVariance((NumericColumn) table.column(c), meanValues[c]));
                // avoid degenerate cases
                this.scalings[c] = Math.max(var, 1e-10);
            }
        }
        else
        {
            for (int c = 0; c < nc; c++)
            {
                this.scalings[c] = 1.0;
            }
        }
        
        // removes the mean and divides by variance (that can be 1)
        for (int c = 0; c < nc; c++)
        {
            for (int r = 0; r < nr; r++)
            {
                res.setValue(r, c, (table.getValue(r, c) - this.meanValues[c]) / this.scalings[c]);
            }
        }
        
        return res;
    }

    private static final double computeMean(NumericColumn column)
    {
        double sum = 0;
        for (double v : column.values())
        {
            sum += v;
        }
        return sum / column.length();
    }
    
    private static final double computeVariance(NumericColumn column, double columnMean)
    {
        double sum = 0;
        for (double v : column.values())
        {
            v -= columnMean;
            sum += v * v;
        }

        // normalize by column length
        return sum / (column.length() - 1);
    }
    
    private static final Matrix covarianceMatrix(Table cTable)
    {
        int nr = cTable.rowCount();
        int nc = cTable.columnCount();
        
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
    private static final Table matrixToTable(Matrix matrix)
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
    private static final Table eigenValuesMatrixToTable(Matrix S)
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
            String name = "CP" + (row+1);
            tab.setRowName(row, name);
        }

        return tab;
    }
    
    private static final String createNewName(String baseName, String parentTableName)
    {
        return parentTableName == null ? baseName : baseName + " of " + parentTableName;
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
        int nr = table.rowCount();
        int nc = table.columnCount();

        // check table dimension
        if (nc != this.loadings.rowCount())
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
                    double valCR = (table.getValue(r, k) - meanValues[k]) / scalings[k];
                    value += valCR * this.loadings.getValue(k, c); 
                }
                res.setValue(r, c, value);
            }
        }

        // setup meta data
        res.setColumnNames(this.loadings.getColumnNames());
        res.setRowAxis(table.getRowAxis());

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
    
    public Table normalisationData()
    {
        int nc = loadings.rowCount();
        Table res = Table.create(2, nc);
        for (int c = 0; c < nc; c++)
        {
            res.setValue(0, c, meanValues[c]);
            res.setValue(1, c, scalings[c]);
        }
        
        res.setRowAxis(new CategoricalAxis("",  new String[] {"mean", "variance"}));
        res.setColumnNames(loadings.getRowNames());
        return res;
    }
}
