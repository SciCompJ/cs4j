/**
 * 
 */
package net.sci.table.impl;

import java.util.Arrays;
import java.util.Iterator;

import net.sci.table.IntegerColumn;

/**
 * 
 */
public class DefaultIntegerColumn extends ColumnStub implements IntegerColumn
{
    int[] data;

    public DefaultIntegerColumn(String name, int[] values)
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
        return Integer.valueOf(this.data[row]);
    }
    
    @Override
    public void setValue(int row, double value)
    {
        this.data[row] = (int) value; 
    }

    @Override
    public IntegerColumn duplicate()
    {
        int[] data2 = Arrays.copyOf(this.data, this.data.length);
        return new DefaultIntegerColumn(this.name, data2);
    }
    
    @Override
    public int getInt(int row)
    {
        return this.data[row];
    }
    
    @Override
    public void setInt(int row, int b)
    {
        this.data[row] = b;
    }

    @Override
    public Iterator<Integer> iterator()
    {
        return Arrays.stream(data).iterator();
    }
}
