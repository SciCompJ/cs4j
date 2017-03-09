/**
 * 
 */
package net.sci.table.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

import net.sci.table.Table;

/**
 * @author dlegland
 *
 */
public class DelimitedTableReader implements TableReader
{
	/**
	 * 
	 */
	public DelimitedTableReader()
	{
	}

	/* (non-Javadoc)
	 * @see net.sci.table.io.TableReader#readTable()
	 */
	@Override
	public Table readTable(File file) throws IOException
	{
		LineNumberReader reader;
		try 
		{
			reader = new LineNumberReader(new FileReader(file));
		} 
		catch(IOException ex)
		{
			throw new RuntimeException("Could not open file: " + file, ex);
		}
		
		// todo: read only if necessary
		String firstLine = reader.readLine();
		
		String[] tokens = firstLine.split("[ \t]+");
		
		int nCols = tokens.length - 1;
		String[] colNames = new String[nCols];
		for (int i = 0; i < nCols; i++)
		{
			colNames[i] = tokens[i + 1];
		}

		// Allocate array lists for columns
		ArrayList<ArrayList<String>> columns = new ArrayList<ArrayList<String>>(nCols);
		for (int c = 0; c < nCols; c++)
		{
			columns.add(new ArrayList<String>());
		}

		int nLines = 0;

		while (true)
		{
			String line = reader.readLine(); 
			if (line == null)
			{
				break;
			}
			
			nLines++;
//			System.out.println(line);
			
			tokens = line.split("[ \t]+");
			for (int c = 0; c < nCols; c++)
			{
				columns.get(c).add(tokens[c + 1]);
			}			
		}
		
		reader.close();
		
		// convert columns
		Table table = Table.create(nLines, nCols);
		
		for (int c = 0; c < nCols; c++)
		{
			ArrayList<String> column = columns.get(c);
			for (int r = 0; r < nLines; r++)
			{
				String token = column.get(r);
				double value;
				try 
				{
					value = Double.parseDouble(token);
					table.setValue(r, c, value);
				}
				catch(NumberFormatException ex)
				{
					table.setValue(r, c, Double.NaN);
				}
			}
		}

		table.setColumnNames(colNames);
		return table;
	}
}
