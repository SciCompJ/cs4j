/**
 * 
 */
package net.sci.table;

/**
 * Apply an operation to a table.
 * 
 * @author dlegland
 *
 */
public interface TableOperator
{
	/**
	 * Process a given table.
	 * 
	 * @param table
	 *            the table to process
	 * @return the resulting table
	 */
	public Table process(Table table);
}
