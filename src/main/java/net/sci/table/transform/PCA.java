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
        int nr = table.rowNumber();
        int nc = table.columnNumber();

        Table cTable = recenterAndScale(table);
        
        // assumes nr >> nc
        Matrix covMat = new Matrix(nc, nc);
        
        //      V = cData' * cData;
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

            // covariance with other variables not yet computed
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
        
        
//        % Diagonalisation of the covariance matrix.
//        % * eigenVectors: basis transform matrix
//        % * vl: eigen values diagonal matrix
//        % * coord: not used
//        [eigenVectors, vl, coord] = svd(V);
        // Extract singular values
        SingularValueDecomposition svd = new SingularValueDecomposition(covMat);
        Matrix eigenVectors = svd.getU();
        Matrix vl = svd.getS();
        Matrix coord = svd.getU();


        // TODO: complete
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
//                for (int r = 0; r < nr; r++)
//                {
//                    double v = table.getValue(r, c) - this.meanValues[c];
//                    sum += v * v;
//                }
                
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
    
    private void computePCA()
    {
        
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
        // TODO: complete
        return null;
    }
}
