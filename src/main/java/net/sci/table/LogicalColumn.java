/**
 * 
 */
package net.sci.table;

import net.sci.table.impl.DefaultLogicalColumn;

/**
 * A column containing only logical values, either true or false.
 */
public interface LogicalColumn extends NumericColumn
{
    public static LogicalColumn create(String name, boolean[] values)
    {
        return new DefaultLogicalColumn(name, values);
    }
    
    /**
     * Returns the logical state at the specified row index.
     * 
     * @param row
     *            the row index (0-based)
     * @return the logical state at the specified row index.
     */
    public boolean getState(int row);
    
    /**
     * Changes the logical state at the specified row index (optional operation).
     * 
     * @param row
     *            the row index (0-based)
     * @param b
     *            the new logical state
     */
    public void setState(int row, boolean b);
    
    @Override
    public default Object get(int row)
    {
        return Boolean.valueOf(this.getState(row));
    }
    
    @Override
    public default String getString(int row)
    {
        return Boolean.toString(this.getState(row));
    }
    
    @Override
    public default double getValue(int row)
    {
        return this.getState(row) ? 1.0 : 0.0;
    }
    
    @Override
    public default double[] getValues()
    {
        double[] values = new double[this.length()];
        for (int i = 0; i < length(); i++)
        {
            values[i] = this.getState(i) ? 1.0 : 0.0;
        }
        return values;
    }
    
    @Override
    public default String contentSummary()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("logical");

        return sb.toString();
    }   
}
