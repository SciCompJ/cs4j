/**
 * 
 */
package net.sci.table;

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
    

}
