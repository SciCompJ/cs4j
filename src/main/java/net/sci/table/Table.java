/**
 * 
 */
package net.sci.table;

import java.io.PrintStream;
import java.util.Arrays;

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
    
    /**
     * Creates a new data table with the given number of rows the specified
     * column names.
     * 
     * @param nRows
     *            the number of rows
     * @param columnNames
     *            the names of the columns
     * @return a new Table instance
     */
    public static Table create(int nRows, String[] columnNames)
    {
        DefaultTable table = new DefaultTable(nRows, columnNames.length);
        table.setColumnNames(columnNames);
        return table;
    }
    
    /**
     * Creates a new data table from a series of columns. If all columns are
     * instances of NumericColumn, returns a NumericTable.
     * 
     * @param columns
     *            the columns that will constitute the table
     * @return a new Table instance
     */
    public static Table create(Column... columns)
    {
        if (columns.length == 0)
        {
            throw new RuntimeException("Requires at least one column to create a table");
        }
        
        // retrieve table size
        int nCols = columns.length;
        int nRows = columns[0].length();
        
        // If all columns are numeric, should return a numeric table
        if (Arrays.stream(columns).allMatch(col -> (col instanceof NumericColumn)))
        {
            NumericTable table = new DefaultNumericTable(nRows, nCols);
            for (int c = 0; c < nCols; c++)
            {
                table.setColumn(c, columns[c]);
            }
            return table;
        }
        
        Table table = new DefaultTable(nRows, nCols);
        for (int c = 0; c < nCols; c++)
        {
            table.setColumn(c, columns[c]);
        }
        return table;
    }
    
    /**
     * Creates a new data table from a series of columns.
     * 
     * @param rowNames
     *            the names of the rows
     * @param columns
     *            the columns
     * @return a new Table instance            
     */
    public static Table create(String[] rowNames, Column... columns)
    {
        // initialize table
        Table table = create(columns);
        
        // setup meta-data
        table.setRowNames(rowNames);
        return table;
    }
    
    public static Table selectColumns(Table table, int[] columnIndices)
    {
        // get column counts
    	int nc = table.columnCount();
    	int nc2 = columnIndices.length;
    	
    	// create array of columns
    	Column[] cols = new Column[nc2];
    	for (int ic = 0; ic < nc2; ic++)
    	{
            // check validity of column index
            int index = columnIndices[ic];
            if (index < 0)
            {
                throw new IllegalArgumentException("Column indices must be positive");
            }
            if (index >= nc)
            {
                throw new IllegalArgumentException("Column index greater than column number: " + nc);
            }
            
            // keep reference to column
    	    cols[ic] = table.column(index);
    	}
    	
    	// create table from column array
        Table result = Table.create(cols);
        
        // setup row names
        String[] rowNames = table.getRowNames(); 
		if (rowNames != null)
		{
		    result.setRowNames(rowNames);
		}

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
    public int columnCount();

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
    public int rowCount();

//    public void addRow(String name, double[] values);
    
    /**
     * Returns the array of row names of the table. May be null.
     * 
     * @return the array of row names of the table.
     */
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
        int nDigits = columnCount() > 9 ? 2 : 1;
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
        int nRows = rowCount();
        int nCols = columnCount();
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
    public interface Columns<C extends Column> extends Iterable<C>
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
