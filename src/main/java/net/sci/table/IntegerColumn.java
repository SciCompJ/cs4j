/**
 * 
 */
package net.sci.table;

import net.sci.table.impl.DefaultIntegerColumn;

/**
 * A column containing integer values, positive or negative.
 */
public interface IntegerColumn extends NumericColumn
{
    public static IntegerColumn create(String name, int[] values)
    {
        return new DefaultIntegerColumn(name, values);
    }
    
    /**
     * Returns the integer value at the specified row index.
     * 
     * @param row
     *            the row index (0-based)
     * @return the integer value at the specified row index.
     */
    public int getInt(int row);
    
    /**
     * Changes the integer value at the specified row index (optional
     * operation).
     * 
     * @param row
     *            the row index (0-based)
     * @param value
     *            the new integer value
     */
    public void setInt(int row, int value);
    
    @Override
    public default Object get(int row)
    {
        return Integer.valueOf(this.getInt(row));
    }
    
    @Override
    public default String getString(int row)
    {
        return Integer.toString(this.getInt(row));
    }
    
    @Override
    public default double getValue(int row)
    {
        return this.getInt(row);
    }
    
    @Override
    public default double[] getValues()
    {
        double[] values = new double[this.length()];
        for (int i = 0; i < length(); i++)
        {
            values[i] = this.getInt(i);
        }
        return values;
    }
    
    @Override
    public default String contentSummary()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("integer");

        return sb.toString();
    }   
}
