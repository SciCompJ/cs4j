/**
 * 
 */
package net.sci.table;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.sci.axis.Axis;
import net.sci.axis.CategoricalAxis;
import net.sci.table.impl.DefaultNumericTable;
import net.sci.table.impl.RowNumberTable;

/**
 * A specialization of <code>Table</code> that contains only numeric columns.
 * 
 * As instances of this class contain only numerical values, it is possible to
 * use them for classical machine learning or dimensionality reduction
 * algorithms.
 * 
 * @author dlegland
 *
 */
public interface NumericTable extends Table
{
    // =============================================================
    // Static methods

    /**
     * Creates a new numeric data table with the given number of rows and columns.
     * 
     * @param nRows
     *            the number of rows
     * @param nColumns
     *            the number of columns
     * @return a new Table instance            
     */
    public static NumericTable create(int nRows, int nColumns)
    {
        return new DefaultNumericTable(nRows, nColumns);
    }

    /**
     * Creates a new numeric data table from an Axis instance describing rows,
     * and a number of columns.
     * 
     * @param rowAxis
     *            the axis describing the rows.
     * @param nColumns
     *            the number of columns
     * @return a new Table instance
     */
    public static NumericTable create(Axis rowAxis, int nColumns)
    {
        if (!(rowAxis instanceof CategoricalAxis))
        {
            throw new RuntimeException("Row axis must be an instance of CategoricalAxis");
        }
        int nRows = ((CategoricalAxis) rowAxis).length();
        DefaultNumericTable table = new DefaultNumericTable(nRows, nColumns);
        table.setRowAxis(rowAxis);
        return table;
    }

    /**
     * Creates a new numeric table from an arbitrary table, by keeping only the
     * numeric columns.
     * 
     * @param table
     *            the input table
     * @return a new (numeric) table containing only the numeric columns of the
     *         original table.
     */
    public static NumericTable keepNumericColumns(Table table)
    {
        // identifies index of numeric columns
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int c = 0; c < table.columnCount(); c++)
        {
            if (table.column(c) instanceof NumericColumn)
            {
                indices.add(c);
            }
        }
        
        // create the result table
        int nRows = table.rowCount();
        NumericTable res = NumericTable.create(nRows, indices.size());
        
        int c2 = 0;
        for (int c : indices)
        {
            res.setColumnValues(c2, table.column(c).getValues());
            c2++;
        }
        
        // copy row names
        res.setRowNames(table.getRowNames());
        
        return res;
    }
    

    // =============================================================
    // Global management of table
    
    /**
     * Opens a new JFrame and displays the content of this table.
     * 
     * @return the reference to the graphical widget that was created (usually a
     *         JFrame).
     */
    public default JFrame show()
    {
        // Need to cast to object array...
        Object[][] dats = new Object[rowCount()][columnCount()];
        for (int r = 0; r < rowCount(); r++)
        {
            for (int c = 0; c < columnCount(); c++)
            {
                dats[r][c] = this.getValue(r, c);
            }
        }

        // Create JTable instance
        JTable table = new JTable(dats, getColumnNames());
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
    // Management of data
    
    /**
     * Changes the values of the specified column.
     * 
     * @param colIndex
     *            the index of the column within the table.
     * @param values
     *            the new values of the column.
     */
    public default void setColumnValues(int colIndex, double[] values)
    {
        column(colIndex).setValues(values);
    }
    
    
    // =============================================================
    // Management of columns
    
    /**
     * Returns a view on the collection of (numeric) columns contained in this table.
     * 
     * @return a view on the collection of (numeric) columns contained in this table
     */
    public Table.Columns<NumericColumn> columns();
    
    /**
     * Returns a view to the specified numeric column.
     * 
     * @param c
     *            the column index, 0-based
     * @return a view or a reference to the column
     */
    public NumericColumn column(int c);
    
    /**
     * Adds a row to this numeric table (optional operation).
     * 
     * @param name
     *            the name of the row to add
     * @param values
     *            the values corresponding to this row
     */
    public void addRow(String name, double[] values);
}
