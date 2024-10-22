/**
 * 
 */
package net.sci.table;

/**
 * A column within a table, containing values and identified by a name.
 * Column values may be of any type: numerical values (double), String, date...
 * 
 * A column do not carry any information about row names.
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
     * Changes the name associated with this column (optional operation).
     * 
     * @param newName
     *            the new name of the column
     */
    public void setName(String newName);
    
    /**
     * Returns the value at the i-th row.
     * 
     * @param row
     *            the row index within this column
     * @return the value at specified row index
     */
    public Object get(int row);
    
    /**
     * Returns the numeric value at the i-th row.
     * 
     * @param row
     *            the row index within this column
     * @return the numeric value at specified row index
     */
    public double getValue(int row);
    
    /**
     * Returns the set of values within this column as an array of double.
     * 
     * @return the set of values within this column as an array of double.
     */
    public double[] getValues();
    
    /**
     * Creates a new writable version of this column.
     * 
     * @return a deep copy of this column.
     */
    public Column duplicate();
    
    /**
     * Returns a short summary of the content of the table.
     * 
     * @return a short summary of the content of the table.
     */
    public String contentSummary();
}
