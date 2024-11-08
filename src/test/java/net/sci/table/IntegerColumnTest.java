/**
 * 
 */
package net.sci.table;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class IntegerColumnTest
{
    /**
     * Test method for {@link net.sci.table.IntegerColumn#getInt(int)}.
     */
    @Test
    public final void testGetInt()
    {
        IntegerColumn column = createIntegerColumn();
        assertEquals(3, column.getInt(2));
    }
    
    /**
     * Test method for {@link net.sci.table.IntegerColumn#getValues()}.
     */
    @Test
    public final void testGetValues()
    {
        IntegerColumn column = createIntegerColumn();
        double[] vals = column.getValues();
        assertEquals(5, vals.length);
        assertEquals(3.0, vals[2], 0.01);
    }
    
    /**
     * Test method for {@link net.sci.table.Column#getValue(int)}.
     */
    @Test
    public final void testGetValue()
    {
        IntegerColumn column = createIntegerColumn();
        assertEquals(3.0, column.getValue(2), 0.01);
    }
    
    /**
     * Test method for {@link java.lang.Iterable#iterator()}.
     */
    @Test
    public final void testIterator()
    {
        IntegerColumn column = createIntegerColumn();
        int count = 0;
        double sum = 0.0;
        for (double v : column)
        {
            sum += v;
            count++;
        }
        
        assertEquals(5, count);
        assertEquals(15, sum, 0.01);
    }
    
    private IntegerColumn createIntegerColumn()
    {
        int[] values = new int[] {1, 2, 3, 4, 5};
        return IntegerColumn.create("values", values);
    }
}
