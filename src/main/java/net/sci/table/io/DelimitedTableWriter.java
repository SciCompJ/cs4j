/**
 * 
 */
package net.sci.table.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import net.sci.table.Table;

/**
 * @author dlegland
 *
 */
public class DelimitedTableWriter implements TableWriter
{
    // =============================================================
    // Class members

    /**
     * The delimiter to print between tokens in the file. Can be "\t", " ",
     * ";"... Default is "\t" (tab character).
     */
    String delimiter = "\t";
	
    /**
     * The pattern used to print floating point values. Default is "%7.3f".
     */
	String floatPattern = "%7.3f";
	
	
    // =============================================================
    // Constructors

	/**
	 * Creates a new instance with default delimiter set as tabulation.
	 */
	public DelimitedTableWriter()
	{
	}

	/**
     * Creates a new instance specifying the delimiter.
     * 
     * @param delim
     *            the delimiters
     */
	public DelimitedTableWriter(String delim)
	{
		this.delimiter = delim;
	}


	// =============================================================
    // Getters and setters

	/**
     * @return the delimiter
     */
    public String getDelimiter()
    {
        return delimiter;
    }

    /**
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(String delimiter)
    {
        this.delimiter = delimiter;
    }

    /**
     * @return the floatPattern
     */
    public String getFloatPattern()
    {
        return floatPattern;
    }

    /**
     * @param floatPattern the floatPattern to set
     */
    public void setFloatPattern(String floatPattern)
    {
        this.floatPattern = floatPattern;
    }

    
    // =============================================================
    // Implementation of TableWriter interface

    /* (non-Javadoc)
	 * @see net.sci.table.io.TableWriter#writeTable(net.sci.table.Table)
	 */
	@Override
	public void writeTable(Table table, File file) throws IOException
	{
		PrintWriter writer;
		try 
		{
			writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		} catch(IOException ex) 
		{
			throw new RuntimeException("Could not open file: " + file, ex);
		}
		
		// retrieve table size
		int nc = table.columnCount();
		int nr = table.rowCount();

		// retrieve column and row names
		String[] colNames = table.getColumnNames();
        String[] rowNames = table.getRowNames();
        
        // print header if appropriate
		if (colNames != null)
		{
		    if (rowNames != null)
		    {
	            writer.print("name");
		    }
			for (int c = 0; c < nc; c++)
			{
				writer.print(this.delimiter + colNames[c]);
			}
			writer.println("");
		}
		
		// print the content of each regular row
		for (int r = 0; r < nr; r++)
		{
			if (rowNames != null)
			{
				writer.print(rowNames[r] + delimiter);
			}
			
			writer.print(createToken(table, r, 0));
			for (int c = 1; c < nc; c++)
			{
				writer.print(delimiter + createToken(table, r, c));
			}

			writer.println("");
		}
		
		writer.close();
	}
	
	private String createToken(Table table, int row, int col)
	{
	    Object obj = table.get(row, col);
	    if (obj instanceof String)
	    {
	        return (String) obj;
	    }
	    
	    return String.format(Locale.ENGLISH, floatPattern, table.getValue(row, col));
	}

}
