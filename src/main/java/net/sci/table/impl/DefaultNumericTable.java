/**
 * 
 */
package net.sci.table.impl;

import java.util.Iterator;
import java.util.stream.Stream;

import javax.swing.JFrame;

import net.sci.axis.Axis;
import net.sci.axis.CategoricalAxis;
import net.sci.table.Column;
import net.sci.table.FloatColumn;
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
     * Inner data array, first index corresponds to columns.
     */
    double[][] data;

    /**
     * Number of columns in the table
     */
    int nCols;

    /**
     * Number of rows in the table
     */
    int nRows;

    /**
     * The name of the columns.
     */
//    String[] colNames = null;
    
    CategoricalAxis columnAxis = null;
    

    // =============================================================
    // Constructors

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
        this(new double[nCols][nRows]);
    }

    public DefaultNumericTable(double[][] data, String[] colNames, String[] rowNames)
    {
        this(data);

        if (colNames.length != this.nCols) throw new IllegalArgumentException(
                "Number of column names should match number of data columns");
        this.columnAxis = new CategoricalAxis("", colNames);
        
        if (rowNames.length != this.nRows) throw new IllegalArgumentException(
                "Number of row names should match number of data rows");
        this.rowAxis = new CategoricalAxis("", rowNames);
    }

    public DefaultNumericTable(double[][] data)
    {
        this.data = data;

        this.nCols = data.length;
        if (this.nCols > 0)
        {
            this.nRows = data[0].length;
        } 
        else
        {
            this.nRows = 0;
        }
        
        this.columnAxis = new CategoricalAxis("", new String[nCols]);
    }


    // =============================================================
    // Data managment
    
    public void setColumnValues(int colIndex, double[] values)
    {
        if (values.length != this.nRows)
        {
            throw new IllegalArgumentException("Values array must have same length as row number in table: " + this.nRows);
        }
        
        for (int r = 0; r < nRows; r++)
        {
            this.setValue(r, colIndex, values[r]);
        }
    }
    

    // =============================================================
    // Management of columns

    /**
     * Returns the number of columns (measurements, variables) in the data
     * table.
     */
    public int columnCount()
    {
    	return this.nCols;
    }

    public Table.Columns<NumericColumn> columns()
    {
        return new Columns();
    }

    @Override
    public NumericColumn column(int c)
    {
        return new ColumnView(c);
    }

    /**
     * Adds a new numeric column.
     * 
     * @param name
     *            the name of the new column
     * @param values
     *            the values of the new column
     */
    public void addColumn(String name, double[] values)
    {
        if (values.length != nRows)
        {
            throw new IllegalArgumentException("Requires an array with " + nRows + " values");
        }
        
        // create new data array
        double[][] data = new double[nCols+1][nRows];
        
        // duplicate existing columns
        for (int c = 0; c < nCols; c++)
        {
            System.arraycopy(data[c], 0, this.data[c], 0, nRows);
        }
        
        // copy new values
        System.arraycopy(data[nCols], 0, values, 0, nRows);
        this.data = data;
        
        // copy column names
        String[] colNames = new String[nCols+1];
        System.arraycopy(columnAxis.itemNames(), 0, colNames, 0, nCols);
        colNames[nCols] = name;
        
        columnAxis = new CategoricalAxis(columnAxis.getName(), colNames);
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
            if (catAxis.length() != rowCount())
            {
                throw new RuntimeException("Input axis must have as many elements as column count");
            }
            this.columnAxis = catAxis;
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
        if (names != null && names.length != this.nCols)
            throw new IllegalArgumentException(
                    "String array must have same length as the number of columns.");
        columnAxis = new CategoricalAxis(columnAxis.getName(), names);
    }

    public String getColumnName(int colIndex)
    {
        return columnAxis.itemName(colIndex);
    }

    @Override
    public void setColumnName(int colIndex, String name)
    {
        String[] names = columnAxis.itemNames();
        names[colIndex] = name;
        this.columnAxis = new CategoricalAxis(columnAxis.getName(), names);
    }


    // =============================================================
    // Management of rows
    
    /**
     * Returns the number of rows (individuals, observations) in the data table.
     */
    public int rowCount()
    {
        return this.nRows;
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
        if (values.length != nCols)
        {
            throw new IllegalArgumentException("Requires an array with " + nCols+ " values");
        }
        
        // create new data array
        double[][] data = new double[nCols][nRows+1];
        
        // duplicate existing columns
        for (int c = 0; c < nCols; c++)
        {
            System.arraycopy(data[c], 0, this.data[c], 0, nRows);
        }
        
        // copy new values
        for (int c = 0; c < nCols; c++)
        {
            data[c][nRows] = values[c];
        }
        this.data = data;
        
        // copy column names
        String[] rowNames = new String[nRows+1];
        if (this.rowAxis != null)
        {
            System.arraycopy(this.rowAxis.itemNames(), 0, rowNames, 0, nRows);
        }
        rowNames[nRows] = name;
        this.rowAxis = new CategoricalAxis("", rowNames);
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
        return this.data[col][row];
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
        return this.data[col][row];
    }

    /**
     * Returns an entire column of the data table.
     */
    public double[] getColumnValues(int colIndex)
    {
        return this.data[colIndex];
    }
    
    /**
     * Returns an entire row of the data table.
     */
    public double[] getRowValues(int rowIndex)
    {
        double[] values = new double[this.nCols];
        for (int c = 0; c < this.nCols; c++)
        {
            values[c] = this.data[c][rowIndex];
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
        this.data[col][row] = value;
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
        this.data[col][row] = value;
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

    class ColumnView implements FloatColumn
    {
        int colIndex;
        
        public ColumnView(int index)
        {
            this.colIndex = index;
        }
        
        @Override
        public void copyValues(double[] values, int index)
        {
            System.arraycopy(data[colIndex], 0, values, index, nRows);
        }

        @Override
        public Double get(int row)
        {
            return data[colIndex][row];
        }

        @Override
        public double getValue(int row)
        {
            return data[colIndex][row];
        }

        @Override
        public void setValue(int row, double value)
        {
            data[colIndex][row] = value;
        }

        @Override
        public double[] getValues()
        {
            return data[colIndex];
        }
        
        @Override
        public int length()
        {
            return nRows;
        }
        
        public String getName()
        {
            return columnAxis.itemName(colIndex);
        }

        @Override
        public void setName(String newName)
        {
            setColumnName(colIndex, newName);
        }

        @Override
        public Column duplicate()
        {
            double[] values = new double[nRows];
            System.arraycopy(data[colIndex], 0, values, 0, nRows);
            return NumericColumn.create(name, values);
        }

        @Override
        public Iterator<Double> iterator()
        {
            return new ValueIterator();
        }
        
        class ValueIterator implements Iterator<Double>
        {
            int index = 0;
            
            @Override
            public boolean hasNext()
            {
                return index < nRows;
            }

            @Override
            public Double next()
            {
                return data[colIndex][index++];
            }
        }
    }

    private class Columns implements Table.Columns<NumericColumn>
    {
        @Override
        public int size()
        {
            return nCols;
        }

        @Override
        public NumericColumn get(int index)
        {
            return new ColumnView(index);
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
