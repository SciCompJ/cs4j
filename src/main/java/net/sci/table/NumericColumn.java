/**
 * 
 */
package net.sci.table;

import java.util.Locale;
import java.util.function.BiFunction;

import net.sci.table.impl.DefaultNumericColumn;

/**
 * A column in a Table that contains only numeric values.
 * 
 * @see FloatColumn
 * 
 * @author dlegland
 *
 */
public interface NumericColumn extends Column, Iterable<Double>
{
    /**
     * Creates a new numeric column from a name and a list of values.
     * 
     * Example:
     * {@snippet lang = "java" :
     * double[] values = new int[] { 0.1, 0.2, 2.3, 4.1, 5.0, 6.8, 3.4, 2.5 };
     * NumericColumn column = NumericColumn.create("Values", values);
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
    public static NumericColumn create(String name, double[] values)
    {
        return new DefaultNumericColumn(name, values);
    }
    
    /**
     * Creates a new numeric column by combining the values of two columns
     * through the specified function. The new column has the same length as the
     * input columns.
     * 
     * @param col1
     *            the first column to process
     * @param col2
     *            the second column to process
     * @param function
     *            the function to apply to each pair of values
     * @return a new numeric column
     */
    public static NumericColumn process(NumericColumn col1, NumericColumn col2,
            BiFunction<Double, Double, Double> function)
    {
        int n = col1.length();
        if (col2.length() != n) throw new RuntimeException("Input columns must have same length");
        
        double[] values = new double[n];
        for (int i = 0; i < n; i++)
        {
            values[i] = function.apply(col1.getValue(i), col2.getValue(i));
        }
        return NumericColumn.create(null, values);
    }
    
    /**
     * Returns the numerical value at the specified row index
     * 
     * @param row
     *            the row index
     * @return the value at specified row index
     */
    public double getValue(int row);
        
    /**
     * Returns the set of values within this column as an array of double.
     * Default behavior is to create a new array of double with the appropriate
     * size and populate it by iterating over column elements.
     * 
     * @return the set of values within this column as an array of double.
     */
    public default double[] getValues()
    {
        double[] values = new double[this.length()];
        for (int i = 0; i < length(); i++)
        {
            values[i] = this.getValue(i);
        }
        return values;
    }
    
    /**
     * Updates the values of this column (optional operation). The default
     * implementation relies on the <code>setValue(int, double)</code> method.
     * Therefore, the availability of this method depends on that of the
     * <code>setValue(int, double)</code> one.
     * 
     * @param values
     *            the new values (must have the same number of elements than column length)
     */
    public default void setValues(double[] values)
    {
        if (values.length != this.length())
        {
            throw new IllegalArgumentException("Input array must have same number of elements as column");
        }
        
        for (int i = 0; i < values.length; i++)
        {
            setValue(i, values[i]);
        }
    }
    
    /**
     * Copies the values from this column into the specified array.
     * 
     * @param values
     *            to array to put values in
     * @param index
     *            the starting index within the target array
     */
    public default void copyValues(double[] values, int index)
    {
        for (int i = 0; i < length(); i++)
        {
            values[index + i] = this.getValue(i);
        }
    }
    
    @Override
    public default String getString(int row)
    {
        return String.format(Locale.ENGLISH, "%.3f", this.getValue(row));
    }
    
    @Override
    public default String contentSummary()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("numeric");
        double minVal = Double.POSITIVE_INFINITY;
        double maxVal = Double.NEGATIVE_INFINITY;
        for (double v : getValues())
        {
            minVal = Math.min(minVal, v);
            maxVal = Math.max(maxVal, v);
        }
        sb.append(String.format(Locale.ENGLISH, " [%7.3f ; %7.3f]", minVal, maxVal));

        return sb.toString();
    }
}
