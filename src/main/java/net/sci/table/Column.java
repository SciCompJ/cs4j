/**
 * 
 */
package net.sci.table;

/**
 * The values in a Table column.
 * 
 * @author dlegland
 *
 */
public interface Column
{
    /**
     * @return the number of elements within this column
     */
    public int length();
    
    public String getName();
}
