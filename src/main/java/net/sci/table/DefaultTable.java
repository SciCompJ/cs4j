/**
 * 
 */
package net.sci.table;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

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
public class DefaultTable implements Table
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

	String name = "";
	String[] colNames = null;
	String[] rowNames = null;

	/**
	 * The list of levels for each column, or null if a column is numeric.
	 */
	ArrayList<String[]> levels = null;

	
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
	}

	public DefaultTable(double[][] data, String[] colNames, String[] rowNames)
	{
		this(data);

		if (colNames.length != this.nCols)
			throw new IllegalArgumentException(
					"Number of column names should match number of data columns");
		this.colNames = colNames;

		if (rowNames.length != this.nRows)
			throw new IllegalArgumentException(
					"Number of row names should match number of data rows");
		this.rowNames = rowNames;

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
		
		// initialize levels
		this.levels = new ArrayList<>(this.nCols);
		for (int c = 0; c < nCols; c++)
		{
		    this.levels.add(null);
		}
	}

    // =============================================================
    // Global information
	
	/**
     * Displays some info about columns within table.
     * 
     * @param stream
     *            the stream to use.
     */
	public void printInfo(PrintStream stream)
	{
	    stream.println("Table: " + name);
	    for (int c = 0; c < nCols; c++)
	    {
	        stream.print(String.format(" [%d] %s: ", c, colNames[c]));
	        stream.print(isNumericColumn(c) ? "numeric" : "qualitative");
	        stream.println();
	    }
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
    // General methods

    /**
     * Returns the dimensions of this table: first the number of rows, then the
     * number of columns.
     * 
     * @return an array of integers containing the dimensions of this table
     */
    public int[] getSize()
    {
        return new int[]{this.nRows, this.nCols};
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

	@Override
    public Column column(int c)
    {
        return new NumericColumnView(c);
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
     * 
     * @param the instance of the widget used for display 
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
	 * 
	 * @param args optiojnal arguments, not used
	 */
	public final static void main(String[] args)
	{
		DefaultTable tbl = new DefaultTable(15, 5);
        tbl.setColumnNames(new String[] { "Length", "Area", "Diam.",
                "Number", "Density" });
//		tbl.print();
		
        tbl.printInfo(System.out);
        
//		JFrame frame = tbl.show();
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	class CategoricalColumnView implements CategoricalColumn
	{
	    int colIndex;
	    String[] colLevels;
	    
	    public CategoricalColumnView(int index)
	    {
	        this.colIndex = index;
	        this.colLevels = levels.get(colIndex);
	        if (this.colLevels == null)
	        {
	            throw new IllegalArgumentException("column index must have levels been initialized");
	        }
        }

        @Override
        public String getName(int row)
        {
            int index = (int) data[colIndex][row];
            return this.colLevels[index];
        }

        @Override
        public int length()
        {
            return nRows;
        }
	}

	class NumericColumnView implements NumericColumn
    {
        int colIndex;
        
        public NumericColumnView(int index)
        {
            this.colIndex = index;
        }
        
        @Override
        public double getValue(int row)
        {
            return data[colIndex][row];
        }

        @Override
        public int length()
        {
            return nRows;
        }

        @Override
        public Iterator<Double> iterator()
        {
            return new RowIterator();
        }
        
        class RowIterator implements Iterator<Double>
        {
            int index = -1;
            
            public RowIterator()
            {
            }

            @Override
            public boolean hasNext()
            {
                return index < nRows;
            }

            @Override
            public Double next()
            {
                return data[colIndex][++index];
            }
        }

    }
}
