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
	public void writeTable(Table table, File file) throws IOException;
}
