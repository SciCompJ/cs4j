/**
 * 
 */
package net.sci.table;

import java.util.Locale;
import java.util.function.BiFunction;

/**
 * A column in a Table that contains only numeric values.
 * 
 * @author dlegland
 *
 */
public interface NumericColumn extends Column, Iterable<Double>
{
    public static NumericColumn create(String name, double[] values)
    {
        return new DefaultNumericColumn(name, values);
    }
    
    public static NumericColumn combine(NumericColumn col1, NumericColumn col2, BiFunction<Double, Double, Double> function)
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
     * Changes the numerical value of the specified row.
     * 
     * @param row
     *            the row index
     * @param value
     *            the new value at specified row index
     * @return the value at specified row index
     */
    public void setValue(int row, double value);
    
    /**
     * Copies the values from this column into the specified array.
     * 
     * @param values
     *            to array to put values in
     * @param index
     *            the starting index within the target array
     */
    public void copyValues(double[] values, int index);
    
    /**
     * Returns the numerical value of the specified row.
     * 
     * @param row
     *            the row index
     * @return the value at specified row index
     */
    public double getValue(int row);
    
    /**
     * Returns the set of values within this column as an array of double.
     * 
     * @return the set of values within this column as an array of double.
     */
    public double[] getValues();
    
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
