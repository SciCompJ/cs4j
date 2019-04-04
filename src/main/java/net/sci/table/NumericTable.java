/**
 * 
 */
package net.sci.table;

import java.util.ArrayList;

/**
 * A table that contains only numeric columns.
 * 
 * @author dlegland
 *
 */
public interface NumericTable extends Table
{
    // =============================================================
    // Static methods

    /**
     * Creates a new numeric data table with the given number of rows and columns.
     * 
     * @param nRows
     *            the number of rows
     * @param nColumns
     *            the number of columns
     * @return a new Table instance            
     */
    public static NumericTable create(int nRows, int nColumns)
    {
        return new DefaultNumericTable(nRows, nColumns);
    }
    
    public static NumericTable keepNumericColumns(Table table)
    {
        // identifies index of numeric columns
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int c = 0; c < table.columnNumber(); c++)
        {
            if (table.column(c) instanceof NumericColumn)
            {
                indices.add(c);
            }
        }
        
        // create the result table
        int nRows = table.rowNumber();
        NumericTable res = NumericTable.create(nRows, indices.size());
        
        int c2 = 0;
        for (int c : indices)
        {
            res.setColumn(c2, table.column(c));
            c2++;
        }
        
        // copy row names
        res.setRowNames(table.getRowNames());
        
        return res;
    }
    
    // =============================================================
    // Management of data
    
    public void setColumnValues(int colIndex, double[] values);
    
    // =============================================================
    // Management of columns
    
    /**
     * Returns a view on the collection of (numeric) columns contained in this table.
     * 
     * @return a view on the collection of (numeric) columns contained in this table
     */
    public Table.Columns<NumericColumn> columns();
    
    /**
     * Returns a view to the specified numeric column.
     * 
     * @param c
     *            the column index, 0-based
     * @return a view or a reference to the column
     */
    public NumericColumn column(int c);
    
    //TODO: (optional) setColumn(int index, [Numeric]Column col) ?
    
    public void addRow(String name, double[] values);
}
