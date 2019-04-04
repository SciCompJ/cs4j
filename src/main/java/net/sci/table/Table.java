/**
 * 
 */
package net.sci.table;

import java.io.PrintStream;

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
     * @param nColumns
     *            the number of columns
     * @return a new Table instance            
     */
    public static Table create(int nRows, int nColumns)
    {
        return new DefaultTable(nRows, nColumns);
    }
    
    public static Table selectColumns(Table table, int[] columnIndices)
    {
    	int nr = table.rowNumber();
    	int nc = table.columnNumber();
    	int nc2 = columnIndices.length;
    	
    	// TODO: choose type depending on parent table. Use "newInstance"-like strategy?
		Table result = Table.create(nr, nc2);
		String[] colNames = new String[nc2];
		
		for (int c = 0; c < nc2; c++)
		{
			// check validity of column index
			int index = columnIndices[c];
			if (index < 0)
			{
				throw new IllegalArgumentException("Column indices must be positive");
			}
			if (index >= nc)
			{
				throw new IllegalArgumentException("Column index greater than column number: " + nc);
			}
			
			// copy column values
			for (int i = 0; i < nr; i++)
			{
				result.setValue(i, c, table.getValue(i, index));
			}
			colNames[c] = table.getColumnNames()[index];
		}
		
		result.setRowNames(table.getRowNames());
		result.setColumnNames(colNames);

		return result;
    }
    
    
    // =============================================================
    // Getters and setters for inner values 
    
    /**
     * Returns the dimensions of this table: first the number of rows, then the
     * number of columns.
     * 
     * @return an array of integers containing the dimensions of this table
     */
    public int[] getSize();
    
    
    /**
     * Returns a name associated to this table, that can be used to identify
     * table or populate graphical widgets.
     * 
     * @return the name of this table
     */
    public String getName();

    /**
     * Changes the name of this table.
     * 
     * @param name the new name for this table.
     */
    public void setName(String name);


    // =============================================================
    // Management of columns
    
    /**
     * Returns the number of columns (measurements, variables) in the data
     * table.
     * 
     * @return the number of columns in this table
     */
    public int columnNumber();

    /**
     * Returns a view on the collection of columns contained in this table.
     * 
     * @return an Iterable over the columns contained in this table
     */
    public Columns<? extends Column> columns();
    
    /**
     * Returns a view to the specified column.
     * @param c the column index, 0-based
     * @return a view or a reference to the column
     */
    public Column column(int c);
    
    public void setColumn(int c, Column col);

    public void addColumn(String name, double[] values);
    
    public void removeColumn(int c);
    
    public String[] getColumnNames();

    public void setColumnNames(String[] names);

    public String getColumnName(int colIndex);

    public void setColumnName(int colIndex, String name);

    public int findColumnIndex(String name);


    // =============================================================
    // Management of rows
    
    /**
     * Returns the list of values of column of the table.
     * 
     * @param colIndex
     *            the column index, 0-indexed
     * @return the set of values of the specified column
     */
    public double[] getColumnValues(int colIndex);

    /**
     * Returns the number of rows (individuals, observations) in the data table.
     * 
     * @return the number of rows in this table
     */
    public int rowNumber();

//    public void addRow(String name, double[] values);
    
    public String[] getRowNames();

    public void setRowNames(String[] names);

    public String getRowName(int rowIndex);

    public void setRowName(int rowIndex, String newName);

    
    // =============================================================
    // Getters and setters for inner values 

    /**
     * Returns the content at the specified position as an Object.
     * 
     * @param row
     *            the row index (0-indexed)
     * @param col
     *            the column index (0-indexed)
     * @return the content at the specified position.
     */
    public Object get(int row, int col);
    
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
     * Returns an entire row of the data table.
     * 
     * @param rowIndex
     *            the row index, 0-indexed
     * @return the set of values of the specified row
     */
    public double[] getRowValues(int rowIndex);    

    
    // =============================================================
    // Some specific implementations

    /**
     * Displays some info about columns within table.
     * 
     * @param stream
     *            the stream to use.
     */
    public default void printInfo(PrintStream stream)
    {
        // print table name
        stream.println("Table: " + getName());
        
        // determine max length of column names
        int nChars = 0;
        for (String name : getColumnNames())
        {
            nChars = Math.max(nChars, name.length());
        }
        nChars = Math.min(nChars+1, 15);
        
        // create format string
        int nDigits = columnNumber() > 9 ? 2 : 1;
        String format = " [%" + nDigits + "d] %-" + nChars + "s ";
        
        // iterate over columns
        int c = 0;
        for (Column col : columns())
        {
            stream.print(String.format(format, c++, col.getName() + ":"));
            if (col instanceof NumericColumn)
            {
                stream.print("numerical  ");
                double minVal = Double.POSITIVE_INFINITY;
                double maxVal = Double.NEGATIVE_INFINITY;
                for (double v : (NumericColumn) col)
                {
                    minVal = Math.min(minVal, v);
                    maxVal = Math.max(maxVal, v);
                }
                stream.print(String.format("  [ %7.3f ; %7.3f ]", minVal, maxVal));
            }
            else
            {
                stream.print("categorical");
                int nLevels = ((CategoricalColumn) col).getLevels().length;
                stream.print("  with " + nLevels + " levels");
            }
            stream.println();
        }
    }

   /**
     * Display the content of the data table to standard output.
     */
    public default void print()
    {
        int nRows = rowNumber();
        int nCols = columnNumber();
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
                System.out.print(this.get(r, c) + "\t");
            }
            System.out.println();
        }
    }

    /**
     * A container of columns.
     *
     */
    public interface Columns<T extends Column> extends Iterable<T>
    {
        public int size();
        
//        /**
//         * Returns a subset of the columns as specified by the index list.
//         * 
//         * @param indices
//         *            the indices of the columns to return
//         * @return a new list of views on the reduced list of columns
//         */
//        public Columns<T> select(int[] indices);
    }
}
