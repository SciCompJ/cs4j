/**
 * 
 */
package net.sci.table;

import net.sci.table.impl.DefaultNumericColumn;

/**
 * A column in a Table that contains only floating point numeric values.
 * 
 * @see FloatColumn
 * 
 * @author dlegland
 *
 */
public interface FloatColumn extends NumericColumn, Iterable<Double>
{
    public static FloatColumn create(String name, double[] values)
    {
        return new DefaultNumericColumn(name, values);
    }
}
