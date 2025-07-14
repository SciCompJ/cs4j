/**
 * 
 */
package net.sci.table;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class FloatColumnTest
{
    
    /**
     * Test method for {@link net.sci.table.FloatColumn#getValues()}.
     */
    @Test
    public final void testGetValues()
    {
        FloatColumn column = createFloatColumn();
        double[] vals = column.getValues();
        assertEquals(5, vals.length);
        assertEquals(5.3, vals[2], 0.01);
    }
    
    /**
     * Test method for {@link net.sci.table.Column#getValue(int)}.
     */
    @Test
    public final void testGetValue()
    {
        FloatColumn column = createFloatColumn();
        assertEquals(5.3, column.getValue(2), 0.01);
    }
    
    /**
     * Test method for {@link java.lang.Iterable#iterator()}.
     */
    @Test
    public final void testIterator()
    {
        FloatColumn column = createFloatColumn();
        int count = 0;
        double sum = 0.0;
        for (double v : column)
        {
            sum += v;
            count++;
        }
        
        assertEquals(5, count);
        assertEquals(26.1, sum, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.table.FloatColumn#selectRows(int[])}.
     */
    @Test
    public final void testSelectRowsIntArray()
    {
        FloatColumn column = createFloatColumn();
        int[] inds = new int[] {1, 3, 4};
        
        FloatColumn res = column.selectRows(inds);
        
        assertEquals(inds.length, res.length());
        assertEquals(column.getValue(1), res.getValue(0), 0.01);
        assertEquals(column.getValue(4), res.getValue(2), 0.01);
    }

    private FloatColumn createFloatColumn()
    {
        double[] values = new double[] {3.0, 4.5, 5.3, 6.1, 7.2};
        return FloatColumn.create("values", values);
    }
}
