/**
 * 
 */
package net.sci.spectra;

import java.util.Iterator;
import java.util.stream.Stream;

import net.sci.axis.Axis;
import net.sci.axis.CategoricalAxis;
import net.sci.axis.LinearNumericalAxis;
import net.sci.axis.NumericalAxis;
import net.sci.table.Column;
import net.sci.table.FloatColumn;
import net.sci.table.NumericColumn;
import net.sci.table.NumericTable;
import net.sci.table.Table;
import net.sci.table.impl.TableStub;

/**
 * Represents a collection of spectra or curves with same length as a data table
 * where rows correspond to spectra, and columns correspond to frequency,
 * wavelength, time...
 * 
 * Implementation is based on that of Table. Column names can not be updated as
 * they are based on a numerical axis.
 */
public class Spectra extends TableStub implements NumericTable
{
    // =============================================================
    // Static factories
    
    /**
     * Converts a (numerical) data table into a {@code Spectra} instance. All
     * columns of the data table must be numeric.
     * 
     * @param table
     *            the table to convert
     * @return an instance of Spectra with the same values as in the table.
     * @throws RuntimeException
     *              if one the columns is not numeric.
     */
    public static final Spectra convert(Table table)
    {
        if (table.columns().stream().anyMatch(col -> !(col instanceof NumericColumn)))
        {
            throw new RuntimeException("All Table columns must be numeric");
        }
        
        // retrieve data size
        int nr = table.rowCount();
        int nc = table.columnCount();
        
        // pick values
        Spectra spectra = new Spectra(nr, nc);
        for (int r = 0; r < nr; r++)
        {
            for (int c = 0; c < nc; c++)
            {
                spectra.setValue(r, c, table.getValue(r, c));
            }
        }
        
        spectra.setRowAxis(table.getRowAxis().duplicate());
        Axis colAxis = table.getColumnAxis();
        if (colAxis != null)
        {
            double[] colValues = new double[nc];
            for (int c = 0; c <nc; c++)
            {
                colValues[c] = Double.parseDouble(colAxis.itemName(c));
            }
            NumericalAxis colAxis2 = NumericalAxis.create(colAxis.getName(), colValues);
            spectra.setColumnAxis(colAxis2);
        }
        
        spectra.setName(table.getName());
        
        return spectra;
    }
    

    // =============================================================
    // Class variables

    /**
     * Inner data array, first index corresponds to spectra, second index
     * correspond to wavelength / wave number...
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
     * The axis describing columns
     */
    NumericalAxis columnAxis = null;
    
    
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
    public Spectra(int nRows, int nCols)
    {
        this(new double[nRows][nCols]);
    }

    /**
     * Creates a new Spectra from the 2D array of values. The first indexing of
     * the array corresponds to the rows, the second indexing to the columns.
     * 
     * @param data
     *            the Nr-by-Nc array containing the spectra data.
     */
    public Spectra(double[][] data)
    {
        this.data = data;

        this.nRows = data.length;
        if (this.nRows > 0)
        {
            this.nCols = data[0].length;
        } 
        else
        {
            this.nCols = 0;
        }
        
        this.columnAxis = new LinearNumericalAxis("", 0, 1);
    }


    // =============================================================
    // Data management
    
    public void setColumnValues(int colIndex, double[] values)
    {
        if (values.length != this.nRows)
        {
            throw new IllegalArgumentException("Values array must have same length as number of spectra: " + this.nRows);
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
     * Adds a new column to this collection of spectra. The column must be
     * numeric and have the same length as the number of spectra within this
     * class.
     * 
     * @param column
     *            the column to add
     */
    @Override
    public void addColumn(Column column)
    {
        if (column instanceof NumericColumn numCol)
        {
            if (numCol.length() != nRows)
            {
                throw new IllegalArgumentException("Requires an array with " + nRows + " values");
            }
            
            // create new data array
            double[][] data = new double[nCols+1][nRows];
            
            // update each row
            for (int r = 0; r < nRows; r++)
            {
                double[] newRow = new double[nCols+1];
                System.arraycopy(data[r], 0, newRow, 0, nCols);
                newRow[nCols] = numCol.getValue(r);
                data[r] = newRow;
            }
            
//            // copy column names
//            String[] colNames = new String[nCols+1];
//            System.arraycopy(columnAxis.itemNames(), 0, colNames, 0, nCols);
//            colNames[nCols] = name;
            
            this.columnAxis = new LinearNumericalAxis("", 0, 1);
            
            // and increment column count
            this.nCols++;
        }
        else
        {
            throw new IllegalArgumentException("The column to add must be numeric");
        }
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
        
        // update each row
        for (int r = 0; r < nRows; r++)
        {
            double[] newRow = new double[nCols+1];
            System.arraycopy(data[r], 0, newRow, 0, nCols);
            newRow[nCols] = values[r];
            data[r] = newRow;
        }
        
//        // copy column names
//        String[] colNames = new String[nCols+1];
//        System.arraycopy(columnAxis.itemNames(), 0, colNames, 0, nCols);
//        colNames[nCols] = name;
        
        this.columnAxis = new LinearNumericalAxis("", 0, 1);
        
        // and increment column count
        this.nCols++;
    }
    
    
    @Override
    public Axis getColumnAxis()
    {
        return this.columnAxis;
    }

    @Override
    public void setColumn(int c, Column column)
    {
        if (column instanceof NumericColumn numCol)
        {
            if (numCol.length() != nRows)
            {
                throw new IllegalArgumentException("Requires an array with " + nRows + " values");
            }
            
            // update each row
            for (int r = 0; r < nRows; r++)
            {
                double[] newRow = new double[nCols+1];
                System.arraycopy(data[r], 0, newRow, 0, nCols);
                newRow[nCols] = numCol.getValue(r);
                data[r][c] = numCol.getValue(r);
            }
            
//            // copy column names
//            String[] colNames = new String[nCols+1];
//            System.arraycopy(columnAxis.itemNames(), 0, colNames, 0, nCols);
//            colNames[nCols] = name;
            
            this.columnAxis = new LinearNumericalAxis("", 0, 1);
            
            // and increment column count
            this.nCols++;
        }
        else
        {
            throw new IllegalArgumentException("Must replace with a numeric column");
        }
    }

    @Override
    public void setColumnAxis(Axis axis)
    {
        // changes the name of each column based on the item values in axis
        if (axis instanceof NumericalAxis numAxis)
        {
            this.columnAxis = numAxis;
        }
        else
        {
            throw new RuntimeException("Can not manage Axis with class: " + axis.getClass().getName());
        }
    }

    public String[] getColumnNames()
    {
        String[] names = new String[nCols];
        for (int c = 0; c < nCols; c++)
        {
            names[c] = getColumnName(c);
        }
        return names;
    }

    public void setColumnNames(String[] names)
    {
        throw new RuntimeException("Unauthorized operation");
    }

    public String getColumnName(int colIndex)
    {
        return Double.toString(columnAxis.getValue(colIndex));
    }

    @Override
    public void setColumnName(int colIndex, String name)
    {
        throw new RuntimeException("Unauthorized operation");
    }


    // =============================================================
    // Management of rows
    
    /**
     * Returns the number of individual spectra within this class.
     * 
     * @return the number of spectra.
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
        double[][] newData = new double[nRows+1][nCols];
        System.arraycopy(data, 0, newData, 0, nRows);
        newData[nRows + 1] = values;
        this.data = newData;
        
        // copy row names
        String[] rowNames = new String[nRows+1];
        if (this.rowAxis != null)
        {
            System.arraycopy(this.rowAxis.itemNames(), 0, rowNames, 0, nRows);
        }
        rowNames[nRows] = name;
        this.rowAxis = new CategoricalAxis(this.rowAxis.getName(), rowNames);
        
        this.nRows++;
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
        return this.data[row][col];
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
        return this.data[row][col];
    }

    /**
     * Returns an entire column of the data table.
     */
    public double[] getColumnValues(int colIndex)
    {
        double[] values = new double[this.nRows];
        for (int r = 0; r < this.nRows; r++)
        {
            values[r] = this.data[r][colIndex];
        }
        return values;
    }
    
    /**
     * Returns an entire row of the data table.
     */
    public double[] getRowValues(int rowIndex)
    {
        return this.data[rowIndex];
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
        this.data[row][col] = value;
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
        this.data[row][col] = value;
    }


    /**
     * A view as a Column on the values corresponding to the same wavelength
     * within all spectra.
     */
    class ColumnView implements FloatColumn
    {
        int colIndex;
        
        public ColumnView(int index)
        {
            this.colIndex = index;
        }
        
        @Override
        public Double get(int row)
        {
            return data[row][colIndex];
        }

        @Override
        public double getValue(int row)
        {
            return data[row][colIndex];
        }

        @Override
        public void setValue(int row, double value)
        {
            data[row][colIndex] = value;
        }
        
        @Override
        public int length()
        {
            return nRows;
        }
        
        public String getName()
        {
            return Double.toString(columnAxis.getValue(colIndex));
        }

        @Override
        public void setName(String newName)
        {
            throw new RuntimeException("Unauthorized operation");
        }

        @Override
        public FloatColumn duplicate()
        {
            double[] values = new double[nRows];
            copyValues(values, 0);
            return FloatColumn.create(getName(), values);
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
                return data[index++][colIndex];
            }
        }

        @Override
        public String getUnitName()
        {
            return columnAxis.getUnitName();
        }

        @Override
        public void setUnitName(String unitName)
        {
            throw new RuntimeException("Can not change the unit name of a column view");
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

//  /**
//   * Small demonstration of the usage of the DefaultNumericTable class.
//   * 
//   * @param args
//   *            optional arguments, not used.
//   */
//  public final static void main(String[] args)
//  {
//      DefaultNumericTable tbl = new DefaultNumericTable(15, 5);
//      tbl.setColumnNames(new String[] { "Length", "Area", "Diam.", "Number", "Density" });
//      tbl.print();
//
//      JFrame frame = tbl.show();
//      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//  }
}
