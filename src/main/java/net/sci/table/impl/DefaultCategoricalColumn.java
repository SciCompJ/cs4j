/**
 * 
 */
package net.sci.table.impl;

import java.util.Arrays;

import net.sci.table.CategoricalColumn;
import net.sci.table.Column;

/**
 * Default implementation for categorical columns.
 */
public class DefaultCategoricalColumn implements CategoricalColumn
{
    String name;
    int[] data;
    String[] levels;

    public DefaultCategoricalColumn(String name, int[] indices, String[] levels)
    {
        this.name = name;
        this.data = indices;
        this.levels = levels;
    }
    
    
    @Override
    public int length()
    {
        return this.data.length;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setName(String newName)
    {
        this.name = newName;
    }


    @Override
    public Object get(int row)
    {
        return this.levels[this.data[row]];
    }

    @Override
    public double getValue(int row)
    {
        return this.data[row];
    }

    @Override
    public double[] getValues()
    {
        double[] res = new double[this.data.length];
        for (int r = 0; r < this.data.length; r++)
        {
            res[r] = this.data[r];
        }
        return res;
    }

    @Override
    public String getName(int row)
    {
        return this.levels[this.data[row]];
    }

    @Override
    public String[] levelNames()
    {
        return this.levels;
    }

    @Override
    public Column duplicate()
    {
        int[] indices = Arrays.copyOf(this.data, this.data.length);
        String[] levels = Arrays.copyOf(this.levels, this.levels.length);
        return new DefaultCategoricalColumn(name, indices, levels);
    }
}
