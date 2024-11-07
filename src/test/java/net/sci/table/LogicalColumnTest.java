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
        for (double v : column)
        {
            sum += v;
            count++;
        }
        
        assertEquals(5, count);
        assertEquals(2, sum, 0.01);
    }
    
    private LogicalColumn createLogicalColumn()
    {
        boolean[] values = new boolean[] {true, false, false, true, false};
        return LogicalColumn.create("values", values);
    }
}
