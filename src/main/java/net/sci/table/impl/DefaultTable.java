/**
 * 
 */
package net.sci.table.impl;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.sci.axis.Axis;
import net.sci.axis.CategoricalAxis;
import net.sci.table.CategoricalColumn;
import net.sci.table.Column;
import net.sci.table.NumericColumn;
import net.sci.table.Table;

/**
 * Implements the Table interface allowing both numerical and categorical columns.
 * 
 * Data are stored as an double array indexed by column first. 
 * Methods access data by row indexing first.
 * Categorical variables are stored in an array of levels for each column.
 * 
 * 
 * @author David Legland
 *
 */
public class DefaultTable extends TableStub
{
    // =============================================================
    // Class variables

    /**
     * Inner data array, first index corresponds to columns.
     */
    double[][] data;

    /**
     * Number of columns
     */
    int nCols;

    /**
     * Number of Rows
     */
    int nRows;

    /**
     * The axis describing columns
     */
    CategoricalAxis columnAxis;

    /**
     * The list of levels for each column, or null if a column is numeric.
     */
    ArrayList<String[]> levels;
    
    /**
     * The array of unit names associated to columns. Must have length equal to
     * the number of columns.
     */
    String[] unitNames = null;
    

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
    public DefaultTable(int nRows, int nCols)
    {
        this(new double[nCols][nRows]);
        this.nCols = nCols;
        this.nRows = nRows;
    }

    public DefaultTable(double[][] data, String[] colNames, String[] rowNames)
    {
        this(data);

        if (colNames.length != this.nCols)
        {
            throw new IllegalArgumentException("Number of column names should match number of data columns");
        }
        this.columnAxis = new CategoricalAxis("", colNames);
        
        if (rowNames.length != this.nRows)
        {
            throw new IllegalArgumentException("Number of row names should match number of data rows");
        }
        this.rowAxis = new CategoricalAxis("", rowNames);
    }

    /**
     * Initialize the data, the number of columns and rows.
     * 
     * @param data
     *            the initial data of the table
     */
    public DefaultTable(double[][] data)
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

        // initialize levels
        this.levels = new ArrayList<>(this.nCols);
        for (int c = 0; c < nCols; c++)
        {
            this.levels.add(null);
        }
        
        this.unitNames = new String[nCols];
    }
    

    // =============================================================
    // Management of factor levels

    public boolean isNumericColumn(int col)
    {
        return this.levels.get(col) == null;
    }

    /**
     * Returns the levels of the factor column specified by the column index, or
     * null if the column contains quantitative data.
     * 
     * @param col
     *            index of the column
     * @return levels of the factor column, or null if the column is
     *         quantitative
     */
    public String[] getLevels(int col)
    {
        return this.levels.get(col);
    }
    
    public void setLevels(int col, String[] levels)
    {
        this.levels.set(col, levels);
    }
    
    public void clearLevels(int col)
    {
        this.levels.set(col, null);
    }

    public void addLevel(int col, String newLevel)
    {
        String[] colLevels = this.levels.get(col);
        String[] newLevels = new String[colLevels.length];
        System.arraycopy(colLevels, 0, newLevels, 0, colLevels.length);
        newLevels[colLevels.length] = newLevel;
        this.levels.set(col, newLevels);
    }

    public Object get(int row, int col)
    {
        double value = getValue(row, col);
        if (isNumericColumn(col))
        {
            return value;
        }
        else
        {
            return this.levels.get(col)[(int) value];
        }
    }
    
    
    // =============================================================
    // Management of columns

    public Table.Columns<Column> columns()
    {
        return new Columns();
    }

    /**
     * Returns the number of columns (measurements, variables) in the data
     * table.
     */
    public int columnCount()
    {
        return this.nCols;
    }

    @Override
    public Column column(int c)
    {
        if (isNumericColumn(c))
            return new NumericColumnView(c);
        else
            return new CategoricalColumnView(c);
    }

    @Override
    public void setColumn(int c, Column column)
    {
        if (c == columnCount())
        {
            addColumn(column);
            return;
        }
        
        if (c < 0 || c > columnCount())
        {
            throw new IllegalArgumentException("Illegal column index: " + c);
        }
        if (column.length() != rowCount())
        {
            throw new IllegalArgumentException("Column length must match table size:" + column.length() + "!=" + rowCount());
        }
        
        if (column instanceof NumericColumn numCol)
        {
            for (int i = 0; i < numCol.length(); i++)
            {
                this.data[c][i] = numCol.getValue(i);
            }
            
            // import meta-data
            setColumnName(c, numCol.getName());
            this.unitNames[c] = numCol.getUnitName();
        }
        else if (column instanceof CategoricalColumn catCol)
        {
            for (int i = 0; i < catCol.length(); i++)
            {
                this.data[c][i] = catCol.getLevelIndex(i);
            }
            
            // import meta-data
            this.levels.set(c, catCol.levelNames());
            setColumnName(c, catCol.getName());
        }
        else
        {
            throw new IllegalArgumentException("Can not add columns with type: " + column.getClass().getName());
        }
    }
    
    @Override
    public void addColumn(Column column)
    {
        if (column instanceof NumericColumn numCol)
        {
            addColumn(numCol.getName(), numCol.getValues());
            
            // import meta-data
            this.unitNames[nCols-1] = numCol.getUnitName();
        }
        else if (column instanceof CategoricalColumn catCol)
        {
            addCategoricalColumn(catCol);
        }
        else
        {
            throw new IllegalArgumentException("Can not add columns with type: " + column.getClass().getName());
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
        double[][] newData = new double[nCols + 1][nRows];

        // copy columns
        System.arraycopy(this.data, 0, newData, 0, nCols);

        // copy new values
        System.arraycopy(values, 0, newData[nCols], 0, nRows);
        this.data = newData;

        // add empty level array
        this.levels.add(null);

        // copy column names
        String[] colNames = new String[nCols + 1];
        System.arraycopy(columnAxis.itemNames(), 0, colNames, 0, nCols);
        colNames[nCols] = name;
        columnAxis = new CategoricalAxis(columnAxis.getName(), colNames);

        this.nCols++;
        // 
        updateUnitNameArray();
    }

    private void addCategoricalColumn(CategoricalColumn column)
    {
        if (column.length() != nRows)
        {
            throw new IllegalArgumentException("Requires a column with length " + nRows);
        }

        // create new data array
        double[][] newData = new double[nCols + 1][nRows];

        // copy columns
        System.arraycopy(this.data, 0, newData, 0, nCols);

        // copy new values
        System.arraycopy(column.getValues(), 0, newData[nCols], 0, nRows);
        this.data = newData;

        // copy level array from original column
        String[] levelNames = column.levelNames();
        this.levels.add(Arrays.copyOf(levelNames, levelNames.length));

        // copy column names, and create new column axis
        String[] colNames = new String[nCols + 1];
        System.arraycopy(columnAxis.itemNames(), 0, colNames, 0, nCols);
        colNames[nCols] = column.getName();
        columnAxis = new CategoricalAxis(columnAxis.getName(), colNames);

        this.nCols++;
        updateUnitNameArray();
    }
    
    private void updateUnitNameArray()
    {
        // check  if already up-to-date
        if (this.unitNames.length == nCols) return;
        
        String[] newNames = new String[nCols];
        System.arraycopy(this.unitNames, 0, newNames, 0, this.unitNames.length);
        this.unitNames = newNames;
    }

    @Override
    public Axis getColumnAxis()
    {
        return columnAxis;
    }

    @Override
    public void setColumnAxis(Axis axis)
    {
        // changes the name of each column based on the item values in axis
        if (axis instanceof CategoricalAxis catAxis)
        {
            if (catAxis.length() != columnCount())
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
        if (names.length != this.nCols) throw new IllegalArgumentException(
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
    

    // =============================================================
    // Getters and setters for values

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
     * Opens a new JFrame and shows this table inside
     * 
     * @param the
     *            instance of the widget used for display
     */
    public JFrame show()
    {
        // Need to cast to object array...
        Object[][] dats = new Object[this.nRows][this.nCols];
        for (int r = 0; r < this.nRows; r++)
        {
            for (int c = 0; c < this.nCols; c++)
            {
                dats[r][c] = this.data[c][r];
            }
        }

        // Create JTable instance
        JTable table = new JTable(dats, this.columnAxis.itemNames());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Create the frame containing the table
        JFrame frame = new JFrame("Data Table");
        frame.setPreferredSize(new Dimension(400, 300));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Setup layout
        Container panel = frame.getContentPane();

        JScrollPane scrollPane = new JScrollPane(table);
        JTable rowTable = new RowNumberTable(table);
        scrollPane.setRowHeaderView(rowTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());

        // panel.add(table.getTableHeader(), BorderLayout.NORTH);
        panel.add(table.getTableHeader(), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        frame.pack();

        // show !
        frame.setVisible(true);
        return frame;
    }

    /**
     * Small demonstration of the usage of the DefaultNumericTable class.
     * 
     * @param args
     *            optional arguments, not used
     */
    public final static void main(String[] args)
    {
        DefaultTable tbl = new DefaultTable(20, 3);
        tbl.setColumnNames(new String[] { "t", "sin(t)", "cos(t)"});
        for (int i = 0; i < 20; i++)
        {
            double t = i * Math.PI / 20;
            tbl.setValue(i, 0, t);
            tbl.setValue(i, 1, Math.sin(t));
            tbl.setValue(i, 2, Math.cos(t));
        }

        tbl.printInfo(System.out);

//        System.out.println(tbl);
         tbl.print();

        // JFrame frame = tbl.show();
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    abstract class ColumnView implements Column
    {
        int colIndex;

        public ColumnView(int index)
        {
            this.colIndex = index;
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
        public double getValue(int row)
        {
            return data[colIndex][row];
        }

        @Override
        public double[] getValues()
        {
            return data[colIndex];
        }
    }

    class CategoricalColumnView extends ColumnView implements CategoricalColumn
    {
        String[] colLevels;

        public CategoricalColumnView(int index)
        {
            super(index);
            this.colLevels = levels.get(colIndex);
            if (this.colLevels == null)
            {
                throw new IllegalArgumentException(
                        "column index must have levels been initialized");
            }
        }

        @Override
        public int getLevelIndex(int row)
        {
            return (int) DefaultTable.this.data[colIndex][row];
        }

        @Override
        public void setLevelIndex(int row, int index)
        {
            if (index >= colLevels.length || index < 0)
            {
                throw new IllegalArgumentException("Index must be smaller than level number");
            }
            DefaultTable.this.data[colIndex][row] = index;
        }

        @Override
        public int length()
        {
            return nRows;
        }

        @Override
        public String get(int row)
        {
            int index = (int) data[colIndex][row];
            return this.colLevels[index];
        }
        
        @Override
        public String getString(int row)
        {
            int index = (int) data[colIndex][row];
            return this.colLevels[index];
        }

        @Override
        public String[] levelNames()
        {
            return DefaultTable.this.getLevels(colIndex);
        }
        
        @Override
        public CategoricalColumn duplicate()
        {
            String colName = columnAxis.itemName(colIndex);
            // copy floating point values into int array
            int[] indices = new int[nRows];
            for (int r = 0; r < nRows; r++)
            {
                indices[r] = (int) data[colIndex][r];
            }
            String[] levels = Arrays.copyOf(colLevels, colLevels.length);
            return CategoricalColumn.create(colName, indices, levels);
        }
	}

	class NumericColumnView extends ColumnView implements NumericColumn
    {
        public NumericColumnView(int index)
        {
            super(index);
        }
        
        @Override
        public String getUnitName()
        {
            return unitNames[colIndex];
        }

        @Override
        public void setUnitName(String unitName)
        {
            unitNames[colIndex] = unitName;
        }
        
        @Override
        public Double get(int row)
        {
            return data[colIndex][row];
        }
        
        @Override
        public void setValue(int row, double value)
        {
            data[colIndex][row] = value;
        }

        @Override
        public NumericColumn duplicate()
        {
            String colName = columnAxis.itemName(colIndex);
            double[] values = new double[nRows];
            System.arraycopy(data[colIndex], 0, values, 0, nRows);
            NumericColumn dup = NumericColumn.create(colName, values);
            dup.setUnitName(unitNames[colIndex]);
            return dup;
        }
    }

    private class Columns implements Table.Columns<Column>
    {
        @Override
        public int size()
        {
            return nCols;
        }
        
        @Override
        public Column get(int index)
        {
            if (isNumericColumn(index))
                return new NumericColumnView(index);
            else
                return new CategoricalColumnView(index);
        }

        @Override
        public Stream<Column> stream()
        {
            Iterator<Column> iter = iterator();
            return Stream.generate(() -> null)
                    .takeWhile(x -> iter.hasNext())
                    .map(n -> iter.next());
        }
        
        @Override
        public Iterator<Column> iterator()
        {
            return new ColumnIterator();
        }
    }

    private class ColumnIterator implements Iterator<Column>
    {
        int index = 0;

        @Override
        public boolean hasNext()
        {
            return index < columnCount();
        }

        @Override
        public Column next()
        {
            return column(index++);
        }    
	}
}
