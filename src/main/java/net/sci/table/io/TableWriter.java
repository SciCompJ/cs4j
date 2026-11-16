/**
 * 
 */
package net.sci.table.io;

import java.io.File;
import java.io.IOException;

import net.sci.table.Table;

/**
 * Defines the interface for writing data table into a file.
 * 
 * @author dlegland
 *
 */
public interface TableWriter
{
    /**
     * Writes a data table into a text file.
     * 
     * @param table
     *            the table to write
     * @param file
     *            the file to write in
     * @throws IOException
     *             if a problem occurred
     */
	public void writeTable(Table table, File file) throws IOException;
}
