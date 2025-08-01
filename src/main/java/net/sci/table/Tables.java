/**
 * 
 */
package net.sci.table;

import java.util.ArrayList;

/**
 * A collection of utility methods for management of tables.
 */
public class Tables
{
    /**
     * Concatenates the columns  of the two tables. Both tables must have same
     * number of rows.
     * 
     * @param table1
     *            the first table to concatenate.
     * @param table2
     *            the second table to concatenate.
     * @return the result of column concatenation
     */
    public static final Table concatenateColumns(Table table1, Table table2)
    {
        int nCols = table1.rowCount();
        if (table2.rowCount() != nCols)
        {
            throw new RuntimeException("Both columns must have same number of columns");
        }
        
        // concatenate columns
        ArrayList<Column> columns = new ArrayList<Column>(table1.columnCount() + table2.columnCount());
        for (Column col : table1.columns())
        {
            columns.add(col);
        }
        for (Column col : table2.columns())
        {
            columns.add(col);
        }
        
        // create table
        Table res = Table.create(columns.toArray(new Column[] {}));
        
        // setup meta-data
        res.setName(table1.getName() + "+" + table2.getName());
        res.setRowNames(table1.getRowNames());
        
        return res;
    }
    
    /**
     * Concatenates the rows of the two tables. Both tables must have same
     * number of columns, and compatible types of columns.
     * 
     * @param table1
     *            the first table to concatenate.
     * @param table2
     *            the second table to concatenate.
     * @return the result of row concatenation
     */
    public static final Table concatenateRows(Table table1, Table table2)
    {
        int nCols = table1.columnCount();
        if (table2.columnCount() != nCols)
        {
            throw new RuntimeException("Both columns must have same number of columns");
        }
        
        ArrayList<Column> columns = new ArrayList<Column>(nCols);
        for (int iCol = 0; iCol < nCols; iCol++)
        {
            Column col1 = table1.column(iCol);
            Column col2 = table2.column(iCol);
            if (col1 instanceof CategoricalColumn catCol1)
            {
                if (col2 instanceof CategoricalColumn catCol2)
                {
                    columns.add(CategoricalColumn.concatenate(catCol1, catCol2));
                }
                else
                {
                    throw new RuntimeException(String.format("Columns at index %d have incompatible types", iCol));
                }
            }
            else if (col1 instanceof NumericColumn numCol1)
            {
                if (col2 instanceof NumericColumn numCol2)
                {
                    columns.add(NumericColumn.concatenate(numCol1, numCol2));
                }
                else
                {
                    throw new RuntimeException(String.format("Columns at index %d have incompatible types", iCol));
                }
            }
            else
            {
                throw new RuntimeException("Can not process columns with class: " + col1.getClass().getName());
            }
                
        }
        
        // create the result table
        Table res = Table.create(columns);
        
        // propagate some meta data
        res.setRowNames(table1.getRowNames());

        return res;
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Tables()
    {
    }
}
