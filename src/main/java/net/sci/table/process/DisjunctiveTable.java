/**
 * 
 */
package net.sci.table.process;

import net.sci.table.CategoricalColumn;
import net.sci.table.Column;
import net.sci.table.LogicalColumn;
import net.sci.table.Table;

/**
 * Computes the complete disjunctive table from the levels of a categorical column.
 */
public class DisjunctiveTable
{
    public static final Table process(Table table, String colName)
    {
        int colIndex = table.findColumnIndex(colName);
        Column column = table.column(colIndex);
        if (!(column instanceof CategoricalColumn))
        {
            throw new RuntimeException("Requires the column: " + colName + " to be a categorical column");
        }
        
        LogicalColumn[] columns = process((CategoricalColumn) column);
        return Table.create(table.getRowNames(), columns);
    }
    
    public static final LogicalColumn[] process(CategoricalColumn column)
    {
        String[] levelNames = column.levelNames();
        int nLevels = levelNames.length;
        LogicalColumn[] res = new LogicalColumn[nLevels];
        
        // initialize logical columns
        int nRows = column.length();
        for (int level = 0; level < nLevels; level++)
        {
            res[level] = LogicalColumn.create(column.getName() + "=" + levelNames[level], new boolean[nRows]);
        }
        
        // process each element of the column
        for (int r = 0; r < nRows; r++)
        {
            int index = findIndex(column.getString(r), levelNames);
            res[index].setState(r, true);
        }
        
        return res;
    }
    
    private static final int findIndex(String string, String[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            if (string.equals(array[i])) return i;
        }
        throw new RuntimeException("Could not find string: " + string + " within the set of levels");
    }
}
