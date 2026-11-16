/**
 * 
 */
package net.sci.table.io;

import java.io.File;
import java.io.IOException;

import net.sci.table.Table;

/**
 * General interface for classes that read data tables from a file. 
 * @author dlegland
 *
 */
public interface TableReader
{
    /**
     * Reads a data table from a file
     * 
     * @param file
     *            the file to read data from
     * @return the data table read from the file
     * @throws IOException
     *             if a problem occurred
     */
	public Table readTable(File file) throws IOException;
}
