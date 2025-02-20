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
     * Returns a string representation of the element at the specified row.
     * Default behavior is to convert the result of the <code>get()</code>
     * method into a String instance, but more efficient behaviors may be
     * implemented.
     * 
     * @param row
     *            the row index within this column
     * @return a String representation of the element at the specified row.
     */
    public String getString(int row);
    
    /**
     * Returns the numeric value at the specified row index (optional
     * operation). Whether this operation is valid or not is left to specialized
     * implementations.
     * 
     * @param row
     *            the row index within this column
     * @return the numeric value at specified row index
     */
    public double getValue(int row);

    /**
     * Changes the numerical value at the specified row index (optional
     * operation). Whether this operation is valid or not is left to specialized
     * implementations.
     * 
     * @param row
     *            the row index
     * @param value
     *            the new value at specified row index
     */
    public void setValue(int row, double value);

    /**
     * Returns the set of values within this column as an array of double
     * (optional operation). Whether this operation is valid or not is left to
     * specialized implementations.
     * 
     * @return the set of values within this column as an array of double.
     */
    public double[] getValues();

    /**
     * Returns the number of elements within this column.
     * 
     * @return the number of elements within this column
     */
    public int length();
    
    /**
     * Returns the name associated with this column.
     * 
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
     * Creates a new Column with the same class of content as this column and
     * with the specified size. The content of the new column is arbitrary.
     * 
     * @param name
     *            the name of the new column
     * @param colLength
     *            the length of the new column
     * @return a new Column with the same class of elements.
     */
    public Column newInstance(String name, int colLength);
    
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
