/**
 * 
 */
package net.sci.table.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;

import net.sci.table.Column;
import net.sci.table.LogicalColumn;

/**
 * 
 */
public class DefaultLogicalColumn extends ColumnStub implements LogicalColumn
{
    String name;
    boolean[] data;

    public DefaultLogicalColumn(String name, boolean[] values)
    {
        super(name);
        this.data = values;
    }
    
    @Override
    public int length()
    {
        return this.data.length;
    }
    
    @Override
    public Object get(int row)
    {
        return Boolean.valueOf(this.data[row]);
    }
    
    @Override
    public void setValue(int row, double value)
    {
        this.data[row] = value > 0.0; 
    }

    @Override
    public Column duplicate()
    {
        boolean[] data2 = Arrays.copyOf(this.data, this.data.length);
        return new DefaultLogicalColumn(this.name, data2);
    }
    
    @Override
    public boolean getState(int row)
    {
        return this.data[row];
    }
    
    @Override
    public void setState(int row, boolean b)
    {
        this.data[row] = b;
    }

    @Override
    public Iterator<Double> iterator()
    {
        return IntStream.range(0, this.data.length)
                .mapToObj(idx -> this.data[idx] ? 1.0 : 0.0).iterator();
    }
    
}
