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
        return new DataTable(nRows, nColumns);
    }
    
    public static NumericTable keepNumericColumns(Table table)
    {
        // identifies index of numeric columns
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int c = 0; c < table.getColumnNumber(); c++)
        {
            if (table.column(c) instanceof NumericColumn)
            {
                indices.add(c);
            }
        }
        
        // create the result table
        int nRows = table.getRowNumber();
        NumericTable res = NumericTable.create(nRows, indices.size());
        
        int c2 = 0;
        for (int c : indices)
        {
            NumericColumn col = (NumericColumn) table.column(c);
            res.setColumnValues(c2, col.getValues());
            res.column(c2).setName(col.getName());
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
     * Returns an Iterable over the (numeric) columns contained in this table.
     * 
     * @return an Iterable over the (numeric) columns contained in this table
     */
    public Iterable<? extends NumericColumn> columns();
    
    /**
     * Returns a view to the specified numeric column.
     * 
     * @param c
     *            the column index, 0-based
     * @return a view or a reference to the column
     */
    public NumericColumn column(int c);
    
    //TODO: (optional) setColumn(int index, [Numeric]Column col) ?
}
