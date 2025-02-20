/**
 * 
 */
package net.sci.table.impl;

import java.util.Arrays;
import java.util.Iterator;

import net.sci.table.FloatColumn;

/**
 * Default implementation for columns containing numeric values.
 * 
 * Stores the values as floating-point values; hence the class also implements
 * the <code>FloatColumn</code> interface.
 */
public class DefaultNumericColumn extends ColumnStub implements FloatColumn
{
    double[] data;
    
    public DefaultNumericColumn(String name, double[] values)
    {
        super(name);
        this.data = values;
    }
    
    @Override
    public Iterable<Double> values()
    {
        return new Iterable<Double>()
        {
            @Override
            public Iterator<Double> iterator()
            {
                return Arrays.stream(data).iterator();
            }
        };
    }

    @Override
    public double[] getValues()
    {
        return Arrays.copyOf(data, data.length);
    }

    @Override
    public Iterator<Double> iterator()
    {
        return Arrays.stream(data).iterator();
    }

    @Override
    public Double get(int row)
    {
        return data[row];
    }
    
    @Override
    public double getValue(int row)
    {
        return data[row];
    }

    @Override
    public void setValue(int row, double value)
    {
        this.data[row] = value;
    }

    @Override
    public void copyValues(double[] values, int index)
    {
        System.arraycopy(this.data, 0, values, index, this.data.length);
    }

    @Override
    public int length()
    {
        return this.data.length;
    }

    @Override
    public FloatColumn duplicate()
    {
        double[] values = new double[this.data.length];
        System.arraycopy(this.data, 0, values, 0, this.data.length);
        return new DefaultNumericColumn(this.name, values);
    }
}
