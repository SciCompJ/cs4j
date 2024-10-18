/**
 * 
 */
package net.sci.table;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Implements the Table interface based on a collection of columns, allowing
 * both numerical and categorical columns.
 * 
 */
public class ColumnsTable implements Table
{
    // =============================================================
    // Class variables
    
    ArrayList<Column> columns;
    
    String name;
    
    String[] rowNames = null;
    

    // =============================================================
    // Constructors

    /**
     * Creates a new table from a list of columns. All columns must have same length.
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

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
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
    public void setColumn(int c, Column col)
    {
        this.columns.set(c, col);
    }

    @Override
    public void addColumn(String name, double[] values)
    {
        Column col = NumericColumn.create(name, values);
        this.columns.add(col);
    }

    @Override
    public void removeColumn(int c)
    {
        this.columns.remove(c);
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
    public int findColumnIndex(String name)
    {
        for (int c = 0; c < this.columnCount(); c++)
        {
            if (name.equals(columns.get(c).getName()))
            {
                return c;
            }
        }
        throw new RuntimeException("Table does not contain any column with name: " + name);
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

    public String[] getRowNames()
    {
        return this.rowNames;
    }

    public void setRowNames(String[] names)
    {
        if (names != null)
        {
            if (names.length != rowCount())
            {
                throw new IllegalArgumentException(
                        "String array must have same length as the number of rows.");
            }
        }
        this.rowNames = names;
    }

    public String getRowName(int rowIndex)
    {
        if (this.rowNames == null)
            return null;
        return this.rowNames[rowIndex];
    }

    @Override
    public void setRowName(int rowIndex, String name)
    {
        if (this.rowNames == null)
        {
            this.rowNames = new String[rowCount()];
        }
        this.rowNames[rowIndex] = name;
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
        public Iterator<Column> iterator()
        {
            return new ColumnIterator();
        }
    }

    public class ColumnIterator implements Iterator<Column>
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
    
    /**
     * Small demonstration of the usage of the DefaultNumericTable class.
     * 
     * @param args optional arguments, not used
     */
    public final static void main(String[] args)
    {
        Column[] columns = new Column[] { 
                NumericColumn.create("Length", new double[15]), 
                NumericColumn.create("Area", new double[15]), 
                NumericColumn.create("Diam.", new double[15]), 
                NumericColumn.create("Number", new double[15]), 
                NumericColumn.create("Density", new double[15]), 
        };
        ColumnsTable table = new ColumnsTable(columns);
        
        table.printInfo(System.out);
        
        System.out.println(table);
//      tbl.print();
        
//      JFrame frame = table.show();
//      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
