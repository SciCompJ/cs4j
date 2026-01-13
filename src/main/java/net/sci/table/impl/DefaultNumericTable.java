/**
 * 
 */
package net.sci.table.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.swing.JFrame;

import net.sci.axis.Axis;
import net.sci.axis.CategoricalAxis;
import net.sci.table.Column;
import net.sci.table.NumericColumn;
import net.sci.table.NumericTable;
import net.sci.table.Table;

/**
 * Default implementation for the Tables that can contains only numeric value.
 * 
 * @see DefaultTable for alternative implementation that also allows for categorical columns
 * 
 * Data are stored as an double array indexed by column first. 
 * Methods access data by row indexing first.
 * 
 * @author David Legland
 *
 */
public class DefaultNumericTable extends TableStub implements NumericTable
{
    // =============================================================
    // Class variables

    /**
     * The list of columns that constitute this table.
     */
    ArrayList<NumericColumn> columns;
    
    /**
     * The description of the column axis. Use a specific class to interpret
     * column names as axis element names.
     */
    ColumnAxisAdapter columnAxis;
    

    // =============================================================
    // Constructors

    public DefaultNumericTable(NumericColumn... cols)
    {
        this.columns = new ArrayList<NumericColumn>(cols.length);
        for (NumericColumn col : cols)
        {
            this.columns.add(col);
        }
        
        this.columnAxis = new ColumnAxisAdapter(this);
    }

    /**
     * Creates a new data table with the given number of rows and columns.
     * 
     * @param nRows
     *            the number of rows
     * @param nCols
     *            the number of columns
     */
    public DefaultNumericTable(int nRows, int nCols)
    {
        this.columns = new ArrayList<NumericColumn>(nCols);
        for (int i = 0; i < nCols; i++)
        {
            this.columns.add(NumericColumn.create("", new double[nRows]));
        }
        
        this.columnAxis = new ColumnAxisAdapter(this);
    }

    public DefaultNumericTable(double[][] data, String[] colNames, String[] rowNames)
    {
        this.columns = new ArrayList<NumericColumn>(data.length);
        for (int i = 0; i < data.length; i++)
        {
            this.columns.add(NumericColumn.create(colNames[i], data[i]));
        }
        
        this.columnAxis = new ColumnAxisAdapter(this);
        this.rowAxis = new CategoricalAxis("", rowNames);
    }

    public DefaultNumericTable(double[][] data)
    {
        this.columns = new ArrayList<NumericColumn>(data.length);
        for (double[] colValues : data)
        {
            this.columns.add(NumericColumn.create("", colValues));
        }
        
        this.columnAxis = new ColumnAxisAdapter(this);
    }


    // =============================================================
    // Data management
    
    public void setColumnValues(int colIndex, double[] values)
    {
        int nRows = rowCount();
        if (values.length != nRows)
        {
            throw new IllegalArgumentException("Values array must have same length as row number in table: " + nRows);
        }
        
        for (int r = 0; r < nRows; r++)
        {
            this.setValue(r, colIndex, values[r]);
        }
    }
    

    // =============================================================
    // Management of columns

    @Override
    public int columnCount()
    {
    	return this.columns.size();
    }

    @Override
    public Table.Columns<NumericColumn> columns()
    {
        return new Columns();
    }

    @Override
    public NumericColumn column(int c)
    {
        return this.columns.get(c);
    }

    @Override
    public void setColumn(int c, Column column)
    {
        if (column.length() != rowCount())
        {
            throw new IllegalArgumentException("Column length must match table size:" + column.length() + "!=" + rowCount());
        }
        
        int nCols = columnCount();
        if (c == nCols)
        {
            addColumn(column);
            return;
        }
        
        if (c < 0 || c > nCols)
        {
            throw new IllegalArgumentException("Illegal column index: " + c);
        }
        
        if (column instanceof NumericColumn numCol)
        {
            this.columns.set(c, numCol);
        }
        else
        {
            throw new IllegalArgumentException("Can only add numeric columns to a NumericTable");
        }
    }

    @Override
    public void addColumn(Column column)
    {
        if (column instanceof NumericColumn numCol)
        {
            this.columns.add(numCol);
        }
        else
        {
            throw new IllegalArgumentException("Can only add numeric columns to a NumericTable");
        }
    }

    /**
     * Adds a new numeric column.
     * 
     * @param colName
     *            the name of the new column
     * @param values
     *            the values of the new column
     */
    public void addColumn(String colName, double[] values)
    {
        if (values.length != this.rowCount())
        {
            throw new IllegalArgumentException("Requires an array with " + this.rowCount() + " values");
        }
        
        this.columns.add(NumericColumn.create(colName, values));
    }
    
    @Override
    public Axis getColumnAxis()
    {
        return this.columnAxis;
    }

    @Override
    public void setColumnAxis(Axis axis)
    {
        // changes the name of each column based on the item values in axis
        if (axis instanceof CategoricalAxis catAxis)
        {
            int nCols = columnCount();
            if (catAxis.length() != nCols)
            {
                throw new RuntimeException("Input axis must have as many elements as column count");
            }
            for (int c = 0; c < nCols; c++)
            {
                this.columns.get(c).setName(catAxis.itemName(c));
            }
        }
        else
        {
            throw new RuntimeException("Can not manage Axis with class: " + axis.getClass().getName());
        }
    }

    public String[] getColumnNames()
    {
        return columnAxis.itemNames();
    }

    public void setColumnNames(String[] names)
    {
        int nCols = this.columnCount();
        if (names != null && names.length != nCols)
        {
            throw new IllegalArgumentException(
                    "String array must have same length as the number of columns.");
        }
        for (int c = 0; c < nCols; c++)
        {
            this.columns.get(c).setName(names[c]);
        }
    }

    public String getColumnName(int colIndex)
    {
        return columnAxis.itemName(colIndex);
    }

    @Override
    public void setColumnName(int colIndex, String name)
    {
        this.columns.get(colIndex).setName(name);
    }


    // =============================================================
    // Management of rows
    
    /**
     * Returns the number of rows (individuals, observations) in the data table.
     */
    public int rowCount()
    {
        if (columns.size() == 0) return 0;
        return columns.get(0).length();
    }

    /**
     * Adds a new numeric row.
     * 
     * @param name
     *            the name of the new row
     * @param values
     *            the values of the new row
     */
    public void addRow(String name, double[] values)
    {
        int nCols = this.columnCount();
        int nRows = this.rowCount();
        if (values.length != nCols)
        {
            throw new IllegalArgumentException("Requires an array with " + nCols+ " values");
        }
        
        // iterate over columns to create new columns with same type and with
        // one more item
        for (int c = 0; c < nCols; c++)
        {
            NumericColumn col = columns.get(c);
            
            // fill new column
            NumericColumn newCol = col.newInstance(col.getName(), nRows + 1);
            for (int r = 0; r < nRows; r++)
            {
                newCol.setValue(r, col.getValue(r));
            }
            newCol.setValue(nRows, values[c]);
            
            this.columns.set(c, newCol);
        }
        
        // copy row names
        String[] rowNames = new String[nRows+1];
        String axisName = "";
        if (this.rowAxis != null)
        {
            System.arraycopy(this.rowAxis.itemNames(), 0, rowNames, 0, nRows);
            axisName = this.rowAxis.getName(); 
        }
        rowNames[nRows] = name;
        this.rowAxis = new CategoricalAxis(axisName, rowNames);
    }
    
    
    // =============================================================
    // Getters and setters for values

    @Override
    public Object get(int row, int col)
    {
        return getValue(row, col);
    }

    /**
     * Returns the value at the specified position in the table.
     * 
     * @param row
     *            the row index, 0-indexed
     * @param col
     *            the column index, 0-indexed
     * @return the value at the specified position
     */
    public double getValue(int row, int col)
    {
        return this.columns.get(col).getValue(row);
    }

    /**
     * Returns the value at the specified position in the table.
     * 
     * @param row
     *            the row index, 0-indexed
     * @param colName
     *            the name of the column
     * @return the value at the specified position
     */
    public double getValue(int row, String colName)
    {
        int col = this.findColumnIndex(colName);
        return this.columns.get(col).getValue(row);
    }

    /**
     * Returns an entire column of the data table.
     */
    public double[] getColumnValues(int colIndex)
    {
        return this.columns.get(colIndex).getValues();
    }
    
    /**
     * Returns an entire row of the data table.
     */
    public double[] getRowValues(int rowIndex)
    {
        int nCols = this.columnCount();
        double[] values = new double[nCols];
        for (int c = 0; c < nCols; c++)
        {
            values[c] = this.columns.get(c).getValue(rowIndex);
        }
        return values;
    }
    
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
    public void setValue(int row, int col, double value)
    {
        this.columns.get(col).setValue(row, value);
    }

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
    public void setValue(int row, String colName, double value)
    {
        int col = this.findColumnIndex(colName);
        this.columns.get(col).setValue(row, value);
    }


	/**
     * Small demonstration of the usage of the DefaultNumericTable class.
     * 
     * @param args
     *            optional arguments, not used.
     */
    public final static void main(String[] args)
    {
        DefaultNumericTable tbl = new DefaultNumericTable(15, 5);
        tbl.setColumnNames(new String[] { "Length", "Area", "Diam.", "Number", "Density" });
        tbl.print();

        JFrame frame = tbl.show();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private class Columns implements Table.Columns<NumericColumn>
    {
        @Override
        public int size()
        {
            return columnCount();
        }

        @Override
        public NumericColumn get(int index)
        {
            return columns.get(index);
        }
        
        @Override
        public Stream<Column> stream()
        {
            Iterator<NumericColumn> iter = iterator();
            return Stream.generate(() -> null)
                    .takeWhile(x -> iter.hasNext())
                    .map(n -> iter.next());
        }

        @Override
        public Iterator<NumericColumn> iterator()
        {
            return new ColumnIterator();
        }
    }
    
    public class ColumnIterator implements Iterator<NumericColumn>
    {
        int index = 0;

        @Override
        public boolean hasNext()
        {
            return index < columnCount();
        }

        @Override
        public NumericColumn next()
        {
            return column(index++);
        }    
    }
}
