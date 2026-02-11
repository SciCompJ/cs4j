/**
 * 
 */
package net.sci.table.process;

import java.util.Arrays;

import net.sci.table.Column;
import net.sci.table.NumericColumn;
import net.sci.table.NumericTable;
import net.sci.table.Table;

/**
 * A collection of summary statistics for numeric columns.
 */
public class SummaryStatistics
{
    /**
     * Computes the minimum value within a numeric column.
     * 
     * @param col
     *            a numeric column
     * @return the minimum value within column
     */
    public static final double min(NumericColumn col)
    {
        double minVal = Double.POSITIVE_INFINITY;
        for (double value : col.values())
        {
            minVal = Math.min(minVal, value);
        }
        return minVal;
    }

    /**
     * Computes the maximum value within a numeric column.
     * 
     * @param col
     *            a numeric column
     * @return the maximum value within column
     */
    public static final double max(NumericColumn col)
    {
        double maxVal = Double.NEGATIVE_INFINITY;
        for (double value : col.values())
        {
            maxVal = Math.max(maxVal, value);
        }
        return maxVal;
    }

    /**
     * Computes the mean value within a numeric column.
     * 
     * @param col
     *            a numeric column
     * @return the mean value within column
     */
    public static final double mean(NumericColumn col)
    {
        return sum(col) / col.length();
    }

    /**
     * Computes the median value within a numeric column.
     * 
     * @param col
     *            a numeric column
     * @return the median value within column
     */
    public static final double median(NumericColumn col)
    {
        int n = col.length();
        double[] values = new double[n];
        System.arraycopy(col.getValues(), 0, values, 0, n);
        Arrays.parallelSort(values);
        if (n % 2 == 1)
        {
            return values[(n - 1) / 2];
        }
        else
        {
            int med = n / 2;
            return (values[med] + values[med + 1]) * 0.5;
        }
    }

    /**
     * Computes the standard deviation of values within a numeric column.
     * 
     * @see #var(NumericColumn)
     * 
     * @param col
     *            a numeric column
     * @return the standard deviation of values within column
     */
    public static final double std(NumericColumn col)
    {
        return Math.sqrt(var(col));
    }

    /**
     * Computes the sum of values within a numeric column.
     * 
     * @param col
     *            a numeric column
     * @return the sum of values within column
     */
    public static final double sum(NumericColumn col)
    {
        double sum = 0.0;
        for (double value : col.values())
        {
            sum += value;
        }
        return sum;
    }

    /**
     * Computes the variance of the values within a numeric column.
     * Normalizes the result by (N-1), where N is the number of values.
     * 
     * @see #std(NumericColumn)
     * @see <a href=
     *      "https://en.wikipedia.org/wiki/Variance#Population_variance_and_sample_variance">
     *      https://en.wikipedia.org/wiki/Variance#Population_variance_and_sample_variance</a>
     * 
     * @param col
     *            a numeric column
     * @return the variance of values within column
     */
    public static final double var(NumericColumn col)
    {
        double mean = mean(col);
        double sumSq = 0.0;
        for (double value : col.values())
        {
            double vc = value - mean;
            sumSq += vc * vc;
        }
        return sumSq / (col.length() - 1);
    }

    public Table process(Table table)
    {
        // check all columns are numeric
        for (Column col : table.columns())
        {
            if (!(col instanceof NumericColumn))
            {
                throw new IllegalArgumentException("Input table must contain numeric columns only");
            }
        }

        // create result table
        int nCols = table.columnCount();
        Table res = NumericTable.create(5, nCols);
        res.setColumnNames(table.getColumnNames());
        res.setRowNames(new String[] { "Mean", "Median", "Std.", "Min.", "Max." });

        // process each column
        for (int iCol = 0; iCol < nCols; iCol++)
        {
            NumericColumn col = (NumericColumn) table.column(iCol);

            res.setValue(0, iCol, mean(col));
            res.setValue(1, iCol, median(col));
            res.setValue(2, iCol, std(col));
            res.setValue(3, iCol, min(col));
            res.setValue(4, iCol, max(col));
        }

        return res;
    }

}
