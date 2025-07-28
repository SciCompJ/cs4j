/**
 * 
 */
package net.sci.table;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import net.sci.axis.Axis;
import net.sci.table.impl.ColumnsTable;
import net.sci.table.impl.DefaultNumericTable;
import net.sci.table.impl.DefaultTable;
import net.sci.table.impl.TablePrinter;

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
        if (Arrays.stream(columns).allMatch(col -> (col instanceof FloatColumn)))
        {
            NumericTable table = new DefaultNumericTable(nRows, nCols);
            for (int c = 0; c < nCols; c++)
            {
                table.setColumnValues(c, columns[c].getValues());
                table.setColumnName(c, columns[c].getName());
            }
            return table;
        }
        
        return new ColumnsTable(columns);
    }
    
    /**
     * Creates a new data table from a collection of columns. If all columns are
     * instances of NumericColumn, returns a NumericTable.
     * 
     * @param columns
     *            the columns that will constitute the table
     * @return a new Table instance
     */
    public static Table create(Collection<Column> columns)
    {
        if (columns.size() == 0)
        {
            throw new RuntimeException("Requires at least one column to create a table");
        }
        
        // retrieve table size
        int nCols = columns.size();
        int nRows = columns.iterator().next().length();
        
        // If all columns are numeric, should return a numeric table
        if (columns.stream().allMatch(col -> (col instanceof FloatColumn)))
        {
            NumericTable table = new DefaultNumericTable(nRows, nCols);
            Iterator<Column> iter = columns.iterator();
            for (int c = 0; c < nCols; c++)
            {
                NumericColumn column = (NumericColumn) iter.next();
                table.setColumnValues(c, column.getValues());
                table.setColumnName(c, column.getName());
            }
            return table;
        }
        
        return new ColumnsTable(columns);
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
    
    /**
     * Creates a new data table from a series of columns, and a row axis.
     * 
     * @param rowAxis
     *            an axis object describing the rows of the table
     * @param columns
     *            the columns
     * @return a new Table instance            
     */
    public static Table create(Axis rowAxis, Column... columns)
    {
        // initialize table
        ColumnsTable table = new ColumnsTable(columns);
        
        // setup meta-data
        table.setRowAxis(rowAxis);
        return table;
    }
    
    /**
     * Creates a new table by keeping a selection of the columns. Order of
     * columns in the new table is defined by the <code>columnIndices</code>
     * argument. Columns are not necessarily duplicated.
     * The row axis of the new table is the same as the original table. 
     * 
     * @param table
     *            the table to select columns from.
     * @param columnIndices
     *            the indices of the columns that will be used to populate the
     *            new table
     * @return the new table.
     */
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

        // copy meta-data
        result.setRowAxis(table.getRowAxis());
        result.setName(table.getName());

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
    public int[] size();
    
    
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
    
    /**
     * Returns the Axis instance that describes the columns of this table.
     * 
     * @return the Axis instance that describes the columns of this table.
     */
    public Axis getColumnAxis();
    
    /**
     * Changes the meta-data associated to the columns axis by specifying a new Axis instance.
     * 
     * @param axis the Axis instance that describes the columns of this table.
     */
    public void setColumnAxis(Axis axis);

    /**
     * Adds a new column to the table (optional operation). The new column must
     * have the same number of rows as the existing columns.
     * 
     * @param column
     *            the column to add
     * @throws IllegalArgumentException
     *             if the column has a a different length than existing columns
     */
    public void addColumn(Column column);
    
    /**
     * Adds a new (numeric) column to this table (optional operation). The
     * column is defined by a name and a series of value.
     * 
     * @param colName
     *            the name of the column to add
     * @param values
     *            the values that compose the column. Array length must match
     *            row count of the table.
     * @throws IllegalArgumentException
     *             if array has a a different length than existing columns
     */
    public void addColumn(String colName, double[] values);
    
    /**
     * Returns the names of the columns within this table as a simple String
     * array.
     * 
     * @return the names of the columns within this table as a string array.
     */
    public String[] getColumnNames();

    /**
     * Changes the name of the columns within the table.
     * 
     * @param names
     *            the new names of the columns.
     */
    public void setColumnNames(String[] names);

    /**
     * Returns the name of the column at the specified position.
     * 
     * @param colIndex
     *            the index of the column.
     * @return the name of the column.
     */
    public String getColumnName(int colIndex);

    /**
     * Changes the name of the column at the specified position.
     * 
     * @param colIndex
     *            the index of the column.
     * @param name
     *            the new name of the column.
     */
    public void setColumnName(int colIndex, String name);

    /**
     * Retrieve the index of a column from its name. If no column with the
     * specified name is found, an exception is thrown.
     * 
     * @param name
     *            the name of the column
     * @return the index of the column with the specified name
     */
    public default int findColumnIndex(String name)
    {
        for (int c = 0; c < this.columnCount(); c++)
        {
            if (name.equals(column(c).getName()))
            {
                return c;
            }
        }
        throw new RuntimeException("Table does not contain any column with name: " + name);
    }


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
     * Returns the Axis instance that describes the rows of this table, if it
     * exists.
     * 
     * @return the Axis instance that describes the rows of this table.
     */
    public Axis getRowAxis();
    
    /**
     * Changes the meta-data associated to the row axis by specifying a new Axis instance.
     * 
     * @param axis the Axis instance that describes the rows of this table.
     */
    public void setRowAxis(Axis axis);

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
            stream.println(String.format(format, c++, col.getName() + ":") + col.contentSummary());
        }
    }

    /**
     * Display the content of the data table to standard output.
     */
    public default void print()
    {
        System.out.println(new TablePrinter().print(this));
    }

    /**
     * A container of columns.
     *
     * @param <C> the type of columns within the table
     */
    public interface Columns<C extends Column> extends Iterable<C>
    {
        /**
         * Returns the number of columns within the table.
         * 
         * @return the number of columns.
         */
        public int size();
        
        /**
         * Retrieves the column at the specified index.
         * 
         * @param index
         *            the index of the column.
         * @return the column at the specified index.
         */
        public C get(int index);
        
        public Stream<Column> stream();
    }
}
