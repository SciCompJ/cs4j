/**
 * 
 */
package net.sci.table;


/**
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
}
