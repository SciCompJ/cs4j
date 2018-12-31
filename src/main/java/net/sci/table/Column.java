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
    
    /**
     * @return the name associated with this column
     */
    public String getName();
    
    /**
     * Returns the value at the i-th row.
     * 
     * @param row
     *            the row index within this column
     * @return the value at specified row index
     */
    public double getValue(int row);
    
    /**
     * @return the set of values within this column as an array of double.
     */
    public double[] getValues();
}
