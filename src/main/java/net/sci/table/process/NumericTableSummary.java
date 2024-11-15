/**
 * 
 */
package net.sci.table.process;

import net.sci.axis.CategoricalAxis;
import net.sci.table.NumericColumn;
import net.sci.table.NumericTable;
import net.sci.table.Table;
import net.sci.table.TableOperator;

/**
 * Computes summary features for each column of a numeric table.
 */
public class NumericTableSummary implements TableOperator
{

    @Override
    public NumericTable process(Table table)
    {
        if (!(table instanceof NumericTable))
        {
            throw new IllegalArgumentException("Requires an instance of NumericTable as input");
        }
        
        return processNumeric((NumericTable) table);
    }
    
    public NumericTable processNumeric(NumericTable table)
    {
        CategoricalAxis rowAxis = new CategoricalAxis("", new String[] {"min", "mean", "max"});
        NumericColumn[] colums = table.columns().stream()
                .map(col -> (NumericColumn) summary((NumericColumn) col))
                .toArray(NumericColumn[]::new);
        return NumericTable.create(rowAxis, colums);
    }
    
    private NumericColumn summary(NumericColumn col)
    {
        double mean = 0.0;
        double vmin = Double.POSITIVE_INFINITY;
        double vmax = Double.NEGATIVE_INFINITY;
        for (double v : col.values())
        {
            vmin = Math.min(vmin, v);
            vmax = Math.max(vmax, v);
            mean += v;
        }
        mean /= col.length();
        
        return NumericColumn.create(col.getName(), new double[] {vmin, mean, vmax});
    }
    
}
