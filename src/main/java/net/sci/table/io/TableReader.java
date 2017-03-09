/**
 * 
 */
package net.sci.table.io;

import java.io.File;
import java.io.IOException;

import net.sci.table.Table;

/**
 * @author dlegland
 *
 */
public interface TableReader
{
	public Table readTable(File file) throws IOException;
}
