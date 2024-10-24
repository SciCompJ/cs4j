/**
 * 
 */
package net.sci.table;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

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
public class DefaultNumericTable implements NumericTable
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
     * The name of this table
     */
    String name = "";
    
    /**
     * The name of the columns.
     */
	String[] colNames = null;
	
	/**
	 * The name of the rows.
	 */
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
	public DefaultNumericTable(int nRows, int nCols)
	{
		this(new double[nCols][nRows]);
	}

	public DefaultNumericTable(double[][] data, String[] colNames, String[] rowNames)
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
    // General methods
    
    /**
     * Returns the dimensions of this table: first the number of rows, then the
     * number of columns.
     * 
     * @return an array of integers containing the dimensions of this table
     */
    public int[] size()
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

    @Override
    public void setColumn(int c, Column col)
    {
        // check type
        if (!(col instanceof NumericColumn))
        {
            throw new IllegalArgumentException("Requires a numeric column as argument");
        }
        
        // copy column values
        NumericColumn numCol = (NumericColumn) col;
        numCol.copyValues(this.data[c], 0);

        // copy name
        setColumnName(c, col.getName());
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
        if (this.colNames != null)
        {
            System.arraycopy(this.colNames, 0, colNames, 0, nCols);
        }
        colNames[nCols] = name;
        this.colNames = colNames;
    }
    
    
    @Override
    public void removeColumn(int colIndex)
    {
        if (colIndex < 0 || colIndex >= nCols)
        {
            throw new IllegalArgumentException("Illegal column index: " + colIndex);
        }

        // create new data array
        double[][] data = new double[nCols-1][nRows];
        
        // duplicate columns before index
        for (int c = 0; c < colIndex; c++)
        {
            System.arraycopy(data[c], 0, this.data[c], 0, nRows);
        }
        // duplicate columns after index
        for (int c = colIndex+1; c < nCols; c++)
        {
            System.arraycopy(data[c-1], 0, this.data[c], 0, nRows);
        }
        this.data = data;
        
        // copy column names
        if (this.colNames != null)
        {
            String[] colNames = new String[nCols-1];
            System.arraycopy(colNames, 0, this.colNames, 0, colIndex - 1);
            System.arraycopy(colNames, colIndex, this.colNames, colIndex + 1, nCols - 1 - colIndex);
            this.colNames = colNames;
        }
    }

    public String[] getColumnNames()
	{
		return this.colNames;
	}

    public void setColumnNames(String[] names)
    {
        if (names != null && names.length != this.nCols)
            throw new IllegalArgumentException(
                    "String array must have same length as the number of columns.");
        this.colNames = names;
    }

    public String getColumnName(int colIndex)
    {
        if (this.colNames == null)
            return null;
        return this.colNames[colIndex];
    }

    @Override
    public void setColumnName(int colIndex, String name)
    {
        if (this.colNames == null)
        {
            this.colNames = new String[nCols];
        }
        this.colNames[colIndex] = name;
    }

    public int findColumnIndex(String name)
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
        if (this.rowNames != null)
        {
            System.arraycopy(this.rowNames, 0, rowNames, 0, nRows);
        }
        rowNames[nRows] = name;
        this.rowNames = rowNames;
    }
    
	public String[] getRowNames()
	{
		return this.rowNames;
	}

    public void setRowNames(String[] names)
    {
        if (names != null && names.length != this.nRows)
            throw new IllegalArgumentException(
                    "String array must have same length as the number of rows.");
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
            this.rowNames = new String[nRows];
        }
        this.rowNames[rowIndex] = name;
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
     * Small demonstration of the usage of the DefaultNumericTable class.
     * 
     * @param args
     *            optional arguments, not used.
     */
	public final static void main(String[] args)
	{
		DefaultNumericTable tbl = new DefaultNumericTable(15, 5);
        tbl.setColumnNames(new String[] { "Length", "Area", "Diam.",
                "Number", "Density" });
		tbl.print();
		
		JFrame frame = tbl.show();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
    class ColumnView implements NumericColumn
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
            if (colNames == null)
                return null;
            return colNames[colIndex];
        }

        @Override
        public void setName(String newName)
        {
            if (colNames == null)
            {
                colNames = new String[nCols];
            }
            if (colNames.length != nCols)
            {
                String[] newColNames = new String[nCols];
                System.arraycopy(colNames, 0, newColNames, 0, Math.min(nCols, colNames.length));
            }
            
            colNames[colIndex] = newName;
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
            return new RowIterator();
        }
        
        class RowIterator implements Iterator<Double>
        {
            int index = 0;
            
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
