/**
 * 
 */
package net.sci.table;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class LogicalColumnTest
{
    
    /**
     * Test method for {@link net.sci.table.LogicalColumn#getValues()}.
     */
    @Test
    public final void testGetValues()
    {
        LogicalColumn column = createLogicalColumn();
        double[] vals = column.getValues();
        assertEquals(5, vals.length);
        assertEquals(0.0, vals[2], 0.01);
    }
    
    /**
     * Test method for {@link net.sci.table.Column#getValue(int)}.
     */
    @Test
    public final void testGetValue()
    {
        LogicalColumn column = createLogicalColumn();
        assertEquals(0.0, column.getValue(2), 0.01);
    }
    
    /**
     * Test method for {@link java.lang.Iterable#iterator()}.
     */
    @Test
    public final void testIterator()
    {
        LogicalColumn column = createLogicalColumn();
        int count = 0;
        double sum = 0.0;
        for (boolean b : column)
        {
            sum += b ? 1 : 0;
            count++;
        }
        
        assertEquals(5, count);
        assertEquals(2, sum, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.table.LogicalColumn#selectRows(int[])}.
     */
    @Test
    public final void testSelectRowsIntArray()
    {
        LogicalColumn column = createLogicalColumn();
        int[] inds = new int[] {1, 3, 4};
        
        LogicalColumn res = column.selectRows(inds);
        
        assertEquals(inds.length, res.length());
        assertEquals(column.getState(1), res.getState(0));
        assertEquals(column.getState(3), res.getState(1));
        assertEquals(column.getState(4), res.getState(2));
    }

    private LogicalColumn createLogicalColumn()
    {
        boolean[] values = new boolean[] {true, false, false, true, false};
        return LogicalColumn.create("values", values);
    }
}
