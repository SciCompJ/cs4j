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
	File file;

	/**
	 * 
	 */
	public DelimitedTableWriter(File file)
	{
		this.file = file;
	}

	/* (non-Javadoc)
	 * @see net.sci.table.io.TableWriter#writeTable(net.sci.table.Table)
	 */
	@Override
	public void writeTable(Table table) throws IOException
	{
		PrintWriter writer;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(this.file)));
		} catch(IOException ex) {
			throw new RuntimeException("Could not open file: " + this.file, ex);
		}
		
		int nc = table.getColumnNumber();
		int nr = table.getRowNumber();
		
		String[] colNames = table.getColumnNames();
		String[] rowNames = table.getRowNames();
		
		if (colNames != null)
		{
			writer.print("name");
			for (int c = 0; c < nc; c++)
			{
				writer.print("\t" + colNames[c]);
			}
			writer.println("");
		}
		
		
		for (int r = 0; r < nr; r++)
		{
			if (rowNames != null)
			{
				writer.print(rowNames[r] + "\t");
			}
			
			for (int c = 0; c < nc; c++)
			{
				writer.print(String.format(Locale.ENGLISH, "%7.2f\t", table.getValue(r, c)));
			}

			writer.println("");
		}
		
		writer.close();
	}

}
