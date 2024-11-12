/**
 * 
 */
package net.sci.table.process;

import net.sci.table.NumericColumn;
import net.sci.table.NumericTable;
import net.sci.table.Table;
import net.sci.table.TableOperator;

/**
 * Compute the covariance matrix of a numeric table, and returns the result in a
 * new symmetric numeric table.
 * 
 * @author dlegland
 *
 */
public class CovarianceMatrix implements TableOperator
{
    /**
     * 
     */
    public CovarianceMatrix()
    {
    }

    @Override
    public NumericTable process(Table table)
    {
        if (table instanceof NumericTable)
        {
            return processNumeric((NumericTable) table);
        }

        throw new IllegalArgumentException("Requires a numeric table as input");
    }

    public NumericTable processNumeric(NumericTable table)
    {
        int nc = table.columnCount();
        int nr = table.rowCount();

        // compute the average value within each column
        double[] means = new double[nc];
        for (int c= 0; c < nc; c++)
        {
            double cumsum = 0;
            for (double v : table.column(c).getValues())
            {
                cumsum += v;
            }
            means[c] = cumsum / nr;
        }
        
        NumericTable covMat = NumericTable.create(nc, nc);
        
        for (int i = 0; i < nc; i++)
        {
            NumericColumn colI = table.column(i);

            // compute variance of column i
            double var = 0;
            for (int k = 0; k < nr; k++)
            {
                double v1 = colI.getValue(k) - means[i];
                var += v1 * v1;
            }
            covMat.setValue(i, i, var / (nr - 1));

            for (int j = i + 1; j < nc; j++)
            {
                NumericColumn colJ = table.column(j);
                
                // compute covariance of columns i and j
                double cov = 0;
                for (int k = 0; k < nr; k++)
                {
                    double v1 = colI.getValue(k) - means[i];
                    double v2 = colJ.getValue(k) - means[j];
                    cov += v1 * v2;
                }
                cov /= (nr - 1);
                covMat.setValue(i, j, cov);
                covMat.setValue(j, i, cov);
            }
        }
        
        // populate set up meta-data
        covMat.setColumnNames(table.getColumnNames());
        covMat.setRowNames(table.getColumnNames());
        
        return covMat;
    }  
}
