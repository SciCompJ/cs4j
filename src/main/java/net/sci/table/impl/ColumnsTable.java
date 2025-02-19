/**
 * 
 */
package net.sci.table.impl;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.sci.axis.Axis;
import net.sci.axis.CategoricalAxis;
import net.sci.table.Column;
import net.sci.table.NumericColumn;
import net.sci.table.Table;

/**
 * Implements the Table interface based on a collection of columns, allowing
 * both numerical and categorical columns.
 * 
 */
public class ColumnsTable extends TableStub
{
    // =============================================================
    // Class variables
    
    ArrayList<Column> columns;
    
    ColumnAxisAdapter columnAxis;
    

    // =============================================================
    // Constructors

    /**
     * Creates a new table from a list of columns. All columns must have same
     * length.
     * 
     * @param columns
     *            the columns that will constitute the table
     */
    public ColumnsTable(Column... columns)
    {
        this.columns = new ArrayList<Column>(columns.length);
        for (Column col : columns)
        {
            this.columns.add(col);
        }
        
        this.columnAxis = new ColumnAxisAdapter(this);
    }

    /**
     * Creates a new table from a collection of columns. All columns must have
     * same length.
     * 
     * @param columns
     *            the columns that will constitute the table
     */
    public ColumnsTable(Collection<Column> columns)
    {
        this.columns = new ArrayList<Column>(columns.size());
        this.columns.addAll(columns);
        this.columnAxis = new ColumnAxisAdapter(this);
    }


    // =============================================================
    // Implementation of the Table interface

    /**
     * Opens a new JFrame and shows this table inside
     * 
     * @param the instance of the widget used for display 
     */
    public JFrame show()
    {
        int nRows = rowCount();
        int nCols = columnCount();
        
        // Need to cast to object array...
        Object[][] dats = new Object[nRows][nCols];
        String[] colNames = new String[nCols];
        for (int c = 0; c < nCols; c++)
        {
            Column col = columns.get(c);
            colNames[c] = col.getName();
            for (int r = 0; r < nRows; r++)
            {
                dats[r][c] = col.get(r);
            }
        }

        // Create JTable instance
        JTable table = new JTable(dats, colNames);
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
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
                rowTable.getTableHeader());

        // panel.add(table.getTableHeader(), BorderLayout.NORTH);
        panel.add(table.getTableHeader(), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        frame.pack();

        // show !
        frame.setVisible(true);
        return frame;
    }

    // =============================================================
    // Implementation of the Table interface

    @Override
    public int[] size()
    {
        return new int[] {rowCount(), columns.size()};
    }

    
    // =============================================================
    // Implementation of the Table interface

    @Override
    public int columnCount()
    {
        return columns.size();
    }

    @Override
    public Columns columns()
    {
        return new Columns();
    }

    @Override
    public Column column(int c)
    {
        return this.columns.get(c);
    }

    @Override
    public void addColumn(String name, double[] values)
    {
        Column col = NumericColumn.create(name, values);
        this.columns.add(col);
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
            for (int c = 0; c < columnCount(); c++)
            {
                columns.get(c).setName(catAxis.itemName(c));
            }
        }
        else
        {
            throw new RuntimeException("Can not manage Axis with class: " + axis.getClass().getName());
        }
    }


    @Override
    public String[] getColumnNames()
    {
        return this.columns.stream()
                .map(col -> col.getName())
                .toList().toArray(new String[]{});
    }

    @Override
    public void setColumnNames(String[] names)
    {
        for (int c = 0; c < this.columnCount(); c++)
        {
            this.columns.get(c).setName(names[c]);
        }
    }

    @Override
    public String getColumnName(int colIndex)
    {
        return this.columns.get(colIndex).getName();
    }

    @Override
    public void setColumnName(int colIndex, String name)
    {
        this.columns.get(colIndex).setName(name);
    }

    @Override
    public double[] getColumnValues(int colIndex)
    {
        return columns.get(colIndex).getValues();
    }

    @Override
    public int rowCount()
    {
        if (columns.size() == 0) return 0;
        return columns.get(0).length();
    }


    // =============================================================
    // Getters and setters for values

    @Override
    public Object get(int row, int col)
    {
        return columns.get(col).get(row);
    }

    @Override
    public double getValue(int row, int col)
    {
        return columns.get(col).getValue(row);
    }

    @Override
    public double getValue(int row, String colName)
    {
        return columns.get(findColumnIndex(colName)).getValue(row);
    }

    @Override
    public void setValue(int row, int col, double value)
    {
        Column column = columns.get(col);
        if (column instanceof NumericColumn)
        {
            ((NumericColumn) column).setValue(row, value);
        }
        else
        {
            throw new RuntimeException("Requires columns to be an instance of NumericColumn");
        }
    }

    @Override
    public void setValue(int row, String colName, double value)
    {
        setValue(row, findColumnIndex(colName), value);
    }

    @Override
    public double[] getRowValues(int rowIndex)
    {
        double[] values = new double[columns.size()];
        for (int c = 0; c < columns.size(); c++)
        {
            values[c] = columns.get(c).getValue(rowIndex);
        }
        return values;
    }
    
    private class Columns implements Table.Columns<Column>
    {
        @Override
        public int size()
        {
            return columnCount();
        }

        @Override
        public Column get(int index)
        {
            return columns.get(index);
        }
 
        @Override
        public Stream<Column> stream()
        {
            return columns.stream();
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
