/**
 * 
 */
package net.sci.table;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Default implementation for the Table interface
 * 
 * Data are stored as an double array indexed by column first. 
 * Methods access data by row indexing first.
 * 
 * @author David Legland
 *
 */
public class DataTable implements Table
{
    // =============================================================
    // Class variables

	/**
	 * Inner data array, first index corresponds to columns.
	 */
	double[][] data;

	int nCols;
	int nRows;

	String[] colNames = null;
	String[] rowNames = null;


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
	public DataTable(int nRows, int nCols)
	{
		this.data = new double[nCols][nRows];
		// this.colNames = new String[nCols];
		// this.rowNames = new String[nRows];

		this.nCols = nCols;
		this.nRows = nRows;
	}

	public DataTable(double[][] data)
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

		// this.colNames = new String[this.nCols];
		// this.rowNames = new String[this.nRows];

	}

	public DataTable(double[][] data, String[] colNames, String[] rowNames)
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

		if (colNames.length != this.nCols)
			throw new IllegalArgumentException(
					"Number of column names should match number of data columns");
		this.colNames = colNames;

		if (rowNames.length != this.nRows)
			throw new IllegalArgumentException(
					"Number of row names should match number of data rows");
		this.rowNames = rowNames;

	}

	
    // =============================================================
    // General methods

	/**
	 * Returns the number of columns (measurements, variables) in the data
	 * table.
	 */
	public int getColumnNumber()
	{
		return this.nCols;
	}

	/**
	 * Returns the number of rows (individuals, observations) in the data table.
	 */
	public int getRowNumber()
	{
		return this.nRows;
	}

	public String[] getColumnNames()
	{
		return this.colNames;
	}

	public void setColumnNames(String[] names)
	{
		if (names.length != this.nCols)
			throw new IllegalArgumentException(
					"String array must have same length as the number of columns.");
		this.colNames = names;
	}

	public int getColumnIndex(String name)
	{
		if (name == null || this.colNames == null)
			return -1;
		for (int c = 0; c < this.nCols; c++)
		{
			if (name.equals(this.colNames[c]))
				return c;
		}
		return -1;
	}

	public String[] getRowNames()
	{
		return this.rowNames;
	}

	public void setRowNames(String[] names)
	{
		if (names.length != this.nRows)
			throw new IllegalArgumentException(
					"String array must have same length as the number of rows.");
		this.rowNames = names;
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
        int col = this.getColumnIndex(colName);
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
        int col = this.getColumnIndex(colName);
        this.data[col][row] = value;
    }


	/**
	 * Opens a new JFrame and shows this table inside
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
		JTable table = new JTable(dats, this.colNames);
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

	/**
	 * Small demonstration of the usage of the DataTable class.
	 */
	public final static void main(String[] args)
	{
		DataTable tbl = new DataTable(15, 5);
        tbl.setColumnNames(new String[] { "Length", "Area", "Diam.",
                "Number", "Density" });
		tbl.print();
		
		JFrame frame = tbl.show();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
