/**
 * 
 */
package net.sci.table;


/**
 * A column in a Table that contains only numeric values.
 * 
 * @author dlegland
 *
 */
public interface NumericColumn extends Column, Iterable<Double>
{
    /**
     * Returns the numerical value of the specified row.
     * 
     * @param row
     *            the row index
     * @return the value at specified row index
     */
    public double getValue(int row);
    
    /**
     * Copies the values from this column into the specified array.
     * 
     * @param values
     *            to array to put values in
     * @param index
     *            the starting index within the target array
     */
    public void copyValues(double[] values, int index);
}
