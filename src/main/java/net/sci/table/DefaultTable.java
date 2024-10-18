/**
 * 
 */
package net.sci.table;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
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
        this.nCols = nCols;
        this.nRows = nRows;
    }

    public DefaultTable(double[][] data, String[] colNames, String[] rowNames)
    {
        this(data);

        if (colNames.length != this.nCols) throw new IllegalArgumentException(
                "Number of column names should match number of data columns");
        this.colNames = colNames;

        if (rowNames.length != this.nRows) throw new IllegalArgumentException(
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
    public void setColumn(int c, Column col)
    {
        // copy column values
        if (col instanceof NumericColumn)
        {
            // use fast method
            ((NumericColumn) col).copyValues(this.data[c], 0);
        }
        else
        {
            for (int r = 0; r < nRows; r++)
            {
                this.data[c][r] = col.getValue(r);
            }
        }

        // copy levels
        if (col instanceof CategoricalColumn)
        {
            this.levels.set(c, ((CategoricalColumn) col).getLevels());
        }

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
	    double[][] newData = new double[nCols+1][nRows];
	    
        // copy columns
	    System.arraycopy(this.data, 0, newData, 0, nCols);
	    
	    // copy new values
        System.arraycopy(values, 0, newData[nCols], 0, nRows);
        this.data = newData;
        
        // add empty level array
        this.levels.add(null);
        
        // copy column names
        String[] newColNames = new String[nCols+1];
        if (this.colNames != null)
        {
            System.arraycopy(this.colNames, 0, newColNames, 0, nCols);
        }
        newColNames[nCols] = name;
        this.colNames = newColNames;
        
        this.nCols++;
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
        
        // update level array
        this.levels.remove(colIndex);
        
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
		if (names.length != this.nCols)
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

    public String[] getRowNames()
	{
		return this.rowNames;
	}

    public void setRowNames(String[] names)
    {
        if (names != null)
        {
            if (names.length != this.nRows)
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
            this.rowNames = new String[nRows];
        }
        this.rowNames[rowIndex] = name;
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

	@Override
    public String toString()
    {
        // retrieve general info
        int nRows = rowCount();
        int nCols = columnCount();
        String[] colNames = getColumnNames();
        String[] rowNames = getRowNames();
        
        // compute column sizes
        int rowNamesSize = 0;
        if (this.rowNames != null)
        {
            for (String name : rowNames)
            {
                rowNamesSize = Math.max(rowNamesSize, name.length());  
            }
        }
        int[] colSizes = computeColSizes();
        
        
        StringBuilder sb = new StringBuilder();

        // First display column headers
        if (colNames != null)
        {
            if (rowNames != null)
            {
                sb.append(spaces(rowNamesSize + 1));
            }
            for (int c = 0; c < nCols; c++)
            {
                String pattern = "%" + colSizes[c] + "s ";
                sb.append(String.format(pattern, colNames[c]));
            }
            sb.append("\n");
        }

        // Then display content of each row
        for (int r = 0; r < nRows; r++)
        {
            // row header
            if (rowNames != null)
            {
                String pattern = "%-" + rowNamesSize + "s ";
                sb.append(String.format(pattern, rowNames[r]));
            }
            
            // row data
            for (int c = 0; c < nCols; c++)
            {
                String pattern = "%" + colSizes[c] + "s ";
                sb.append(String.format(pattern, "" + this.get(r, c)));
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
	
	private int[] computeColSizes()
	{
	    int SIZE_MAX = 16;
	    
	    int[] colSizes = new int[nCols];
	    for (int c = 0; c <nCols; c++)
	    {
	        // default size
	        int size = 10;
	        
	        // use specific processing for factor columns with levels
	        if (!isNumericColumn(c))
	        {
	            String[] colLevels = this.levels.get(c);
	            if (colLevels != null)
	            {
	                for (String s : colLevels)
	                {
	                    size = Math.max(size, s.length());
	                }
	            }
	        }
	        
	        // include size of column name
	        if (colNames != null)
	        {
	            size = Math.max(size, colNames[c].length());
	        }
	        
	        // avoid too long sizes
	        colSizes[c] = Math.min(size, SIZE_MAX);
	    }
	    return colSizes;
	}
    
	private static final String spaces(int nSpaces)
	{
	    StringBuilder sb = new StringBuilder(nSpaces);
	    for (int i = 0; i < nSpaces; i++)
	    {
	        sb.append(" ");
	    }
	    return sb.toString();
	}

    /**
	 * Small demonstration of the usage of the DefaultNumericTable class.
	 * 
	 * @param args optional arguments, not used
	 */
	public final static void main(String[] args)
	{
		DefaultTable tbl = new DefaultTable(15, 5);
        tbl.setColumnNames(new String[] { "Length", "Area", "Diam.",
                "Number", "Density" });
		
        tbl.printInfo(System.out);
        
        System.out.println(tbl);
//      tbl.print();
        
//		JFrame frame = tbl.show();
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
	            throw new IllegalArgumentException("column index must have levels been initialized");
	        }
        }

        @Override
        public String get(int row)
        {
            int index = (int) data[colIndex][row];
            return this.colLevels[index];
        }
        
        @Override
        public String getName(int row)
        {
            int index = (int) data[colIndex][row];
            return this.colLevels[index];
        }

        @Override
        public String[] getLevels()
        {
            return DefaultTable.this.getLevels(colIndex);
        }
        
        @Override
        public Column duplicate()
        {
            int[] indices = new int[nRows];
            for (int r = 0; r < nRows; r++)
            {
                indices[r] = (int) data[colIndex][r];
            }
            String[] levels = Arrays.copyOf(colLevels, colLevels.length);
            return CategoricalColumn.create(name, indices, levels);
        }
	}

	class NumericColumnView extends ColumnView implements NumericColumn
    {
        public NumericColumnView(int index)
        {
            super(index);
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
        public void copyValues(double[] values, int index)
        {
            System.arraycopy(data[colIndex], 0, values, index, nRows);
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
	
	private class Columns implements Table.Columns<Column>
	{
        @Override
        public int size()
        {
            return nCols;
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
}
