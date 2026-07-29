/**
 * 
 */
package net.sci.table;

import java.util.function.Function;

import net.sci.table.impl.DefaultLogicalColumn;

/**
 * A column containing only logical values, either true or false.
 */
public interface LogicalColumn extends NumericColumn, Iterable<Boolean>
{
    /**
     * Creates a new logical column from a name and a list of (boolean) values.
     * 
     * Example:
     * {@snippet lang = "java" :
     * boolean[] values = new boolean[] { true, false, true, true, false, false, false, true };
     * LogicalColumn column = LogicalColumn.create("Values", values);
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
    public static LogicalColumn create(String name, boolean[] values)
    {
        return new DefaultLogicalColumn(name, values);
    }
    
    /**
     * Creates a new logical column by converting numeric values in the
     * specified numeric column using the specified function.
     * 
     * @param col
     *            the column containing numeric values
     * @param fun
     *            the conversion function from double to boolean
     * @return the resulting logical column
     */
    public static LogicalColumn convert(NumericColumn col, Function<Double, Boolean> fun)
    {
        int n = col.length();
        LogicalColumn res = LogicalColumn.create(col.getName(), new boolean[n]);
        for (int i = 0; i < n; i++)
        {
            res.setState(i, fun.apply(col.getValue(i)));
        }
        return res;
        
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
    public default LogicalColumn selectRows(int[] rowIndices)
    {
        boolean[] newStates = new boolean[rowIndices.length];
        for (int i = 0; i < rowIndices.length; i++)
        {
            newStates[i] = getState(rowIndices[i]);
        }
        return create(this.getName(), newStates);
    }
    
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
    public default LogicalColumn newInstance(String name, int colLength)
    {
        return create(name, new boolean[colLength]);
    }
    
    @Override
    public LogicalColumn duplicate();
    
    @Override
    public default String contentSummary()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("logical");

        return sb.toString();
    }   
}
