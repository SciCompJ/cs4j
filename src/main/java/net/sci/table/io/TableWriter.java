/**
 * 
 */
package net.sci.table.io;

import java.io.IOException;

import net.sci.table.Table;

/**
 * @author dlegland
 *
 */
public interface TableWriter
{
	public void writeTable(Table table) throws IOException;
}
