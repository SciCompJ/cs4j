/**
 * 
 */
package net.sci.table;

/**
 * Defines the interface for storing measurements.
 *   
 * @author dlegland
 *
 */
public interface Table
{
    // =============================================================
    // Static methods

    /**
     * Creates a new data table with the given number of rows and columns.
     * 
     * @param nRows
     *            the number of rows
     * @param nCols
     *            the number of columns
     */
    public static Table create(int nRows, int nColumns)
    {
        return new DataTable(nRows, nColumns);
    }
    
    
    // =============================================================
    // Getters and setters for inner values 
    
    /**
     * Returns the number of columns (measurements, variables) in the data
     * table.
     */
    public int getColumnNumber();

    /**
     * Returns the number of rows (individuals, observations) in the data table.
     */
    public int getRowNumber();

    
    // =============================================================
    // Getters and setters for table meta data
    
    public String[] getColumnNames();

    public void setColumnNames(String[] names);

    public int getColumnIndex(String name);

    public String[] getRowNames();

    public void setRowNames(String[] names);

    
    // =============================================================
    // Getters and setters for inner values 

    /**
     * Returns the value at the specified position in the table.
     * 
     * @param row
     *            the row index, 0-indexed
     * @param col
     *            the column index, 0-indexed
     * @return the value at the specified position
     */
    public double getValue(int row, int col);

    /**
     * Returns the value at the specified position in the table.
     * 
     * @param row
     *            the row index, 0-indexed
     * @param colName
     *            the name of the column
     * @return the value at the specified position
     */
    public double getValue(int row, String colName);

    /**
     * Changes the value at the specified position
     * 
     * @param col
     *            the column index, 0-indexed
     * @param row
     *            the row index, 0-indexed
     * @param value
     *            the new value
     */
    public void setValue(int row, int col, double value);

    /**
     * Changes the value at the specified position
     * 
     * @param row
     *            the row index, 0-indexed
     * @param colName
     *            the name of the column to modify
     * @param value
     *            the new value
     */
    public void setValue(int row, String colName, double value);

    /**
     * Returns an entire column of the data table.
     * 
     * @param colIndex
     *            the column index, 0-indexed
     * @return the set of values of the specified column
     */
    public double[] getColumnValues(int colIndex);    

    
    // =============================================================
    // Some specific implementations

   /**
     * Display the content of the data table to standard output.
     */
    public default void print()
    {
        int nRows = getRowNumber();
        int nCols = getColumnNumber();
        String[] colNames = getColumnNames();
        String[] rowNames = getRowNames();

        // First display column headers
        if (colNames != null)
        {
            for (int c = 0; c < nCols; c++)
            {
                if (rowNames != null)
                    System.out.print("\t");
                System.out.print(colNames[c] + "\t");
            }
            System.out.println();
        }

        // Then display content of each row
        for (int r = 0; r < nRows; r++)
        {
            // row header
            if (rowNames != null)
                System.out.print(rowNames[r] + "\t");

            // row data
            for (int c = 0; c < nCols; c++)
            {
                System.out.print(this.getValue(r, c) + "\t");
            }
            System.out.println();
        }
    }
}