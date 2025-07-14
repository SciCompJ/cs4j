/**
 * 
 */
package net.sci.table;

import java.util.stream.IntStream;

import net.sci.table.impl.DefaultNumericColumn;

/**
 * A column in a Table that contains only floating point numeric values.
 * 
 * @see IntegerColumn
 * 
 * @author dlegland
 *
 */
public interface FloatColumn extends NumericColumn, Iterable<Double>
{
    /**
     * Creates a new FloatColumn from a name and a list of values.
     * 
     * Example:
     * {@snippet lang = "java" :
     * double[] values = new int[] { 0.1, 0.2, 2.3, 4.1, 5.0, 6.8, 3.4, 2.5 };
     * FloatColumn column = FloatColumn.create("Values", values);
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
    public static FloatColumn create(String name, double[] values)
    {
        return new DefaultNumericColumn(name, values);
    }
    
    @Override
    public default FloatColumn selectRows(int[] rowIndices)
    {
        double[] newValues = IntStream.of(rowIndices)
                .mapToDouble(index -> getValue(index))
                .toArray();
        return create(this.getName(), newValues);
    }
    
    @Override
    public default FloatColumn newInstance(String name, int colLength)
    {
        return create(name, new double[colLength]);
    }

    @Override
    public FloatColumn duplicate();
}
