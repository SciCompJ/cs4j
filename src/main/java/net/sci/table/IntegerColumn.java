/**
 * 
 */
package net.sci.table;

import java.util.Locale;

import net.sci.table.impl.DefaultIntegerColumn;

/**
 * A column containing integer values, positive or negative.
 */
public interface IntegerColumn extends NumericColumn, Iterable<Integer>
{
    /**
     * Creates a new column of integer values from a name and a list of (itneger) values.
     * 
     * Example:
     * {@snippet lang = "java" :
     * int[] values = new int[] { 1, 4, 2, 1, 8, 0, 2, 3 };
     * IntegerColumn column = IntegerColumn.create("Values", values);
     * int colLength = column.length(); // returns 8
     * }
     * 
     * @param name
     *            the name of the column
     * @param values
     *            the array of values within the column. The size of this array
     *            determines the size of the column.
     * @return a new numeric column.
     */
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
    
    public default int[] getIntValues()
    {
        int[] values = new int[this.length()];
        for (int i = 0; i < length(); i++)
        {
            values[i] = this.getInt(i);
        }
        return values;
    }
    
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

        int minVal = Integer.MAX_VALUE;
        int maxVal = Integer.MIN_VALUE;
        for (int i = 0; i < length(); i++)
        {
            int v = getInt(i);
            minVal = Math.min(minVal, v);
            maxVal = Math.max(maxVal, v);
        }
        sb.append(String.format(Locale.ENGLISH, " [%d ; %d]", minVal, maxVal));

        return sb.toString();
    }   
}
