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
	String delim = "\t";
	
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
		this.delim = delim;
	}

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
		
		int nc = table.columnNumber();
		int nr = table.rowNumber();
		
		String[] colNames = table.getColumnNames();
		String[] rowNames = table.getRowNames();
		
		if (colNames != null)
		{
			writer.print("name");
			for (int c = 0; c < nc; c++)
			{
				writer.print(this.delim + colNames[c]);
			}
			writer.println("");
		}
		
		
		for (int r = 0; r < nr; r++)
		{
			if (rowNames != null)
			{
				writer.print(rowNames[r] + delim);
			}
			
			writer.print(createToken(table, r, 0));
			for (int c = 1; c < nc; c++)
			{
				writer.print(delim + createToken(table, r, c));
			}

			writer.println("");
		}
		
		writer.close();
	}
	
	private static final String createToken(Table table, int row, int col)
	{
	    Object obj = table.get(row, col);
	    if (obj instanceof String)
	    {
	        return (String) obj;
	    }
	    
	    return String.format(Locale.ENGLISH, "%7.2f", table.getValue(row, col));
	}

}
