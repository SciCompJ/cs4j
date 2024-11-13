/**
 * 
 */
package net.sci.table.process;

import net.sci.axis.CategoricalAxis;
import net.sci.table.CategoricalColumn;
import net.sci.table.Column;
import net.sci.table.NumericColumn;
import net.sci.table.Table;

/**
 * Aggregates values of a numeric table based on the levels of a column (with
 * same number of rows).
 */
public class Aggregate
{
    public static final Table aggregate(Table table, CategoricalColumn groups)
    {
        Column[] columns = new Column[table.columnCount()];
        for (int c = 0; c < table.columnCount(); c++)
        {
            Column column = table.column(c);
            if (!(column instanceof NumericColumn))
            {
                throw new RuntimeException("Column #" + c + " (" + column.getName() + ") is not a numeric column");
            }
            columns[c] = aggregate((NumericColumn) column, groups);
        }
        
        Table res = Table.create(columns);
        String[] levels = groups.levelNames();
        String[] rowNames = new String[levels.length];
        for (int i = 0; i < levels.length; i++)
        {
            rowNames[i] = groups.getName() + "=" + levels[i];
        }
        res.setRowAxis(new CategoricalAxis("Levels", rowNames));
        res.setName(table.getName()+"-mean");
        
        return res;
    }

    public static final NumericColumn aggregate(NumericColumn column, CategoricalColumn groups)
    {
        if (column.length() != groups.length())
        {
            throw new RuntimeException("Requires the two columns to have the same length");
        }
        String[] levels = groups.levelNames();
        int nGroups = levels.length;
        
        double[] means = new double[nGroups];
        int[] counts = new int[nGroups];
        
        for (int r = 0; r < column.length(); r++)
        {
            String level = groups.getString(r);
            for (int j = 0; j < nGroups; j++)
            {
                if (levels[j].equals(level))
                {
                    means[j] += column.getValue(r);
                    counts[j]++;
                    break;
                }
            }
        }
        
        // normalize sums by counts to get the mean
        for (int j = 0; j < nGroups; j++)
        {
            means[j] /= counts[j];
        }
        
        // create result column
        return NumericColumn.create(column.getName(), means);
    }
}
