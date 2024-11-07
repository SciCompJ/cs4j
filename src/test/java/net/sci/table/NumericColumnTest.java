/**
 * 
 */
package net.sci.table;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class NumericColumnTest
{
    
    /**
     * Test method for {@link net.sci.table.NumericColumn#getValues()}.
     */
    @Test
    public final void testGetValues()
    {
        NumericColumn column = createNumericColumn();
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
        NumericColumn column = createNumericColumn();
        assertEquals(5.3, column.getValue(2), 0.01);
    }
    
    /**
     * Test method for {@link java.lang.Iterable#iterator()}.
     */
    @Test
    public final void testIterator()
    {
        NumericColumn column = createNumericColumn();
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
    
    private NumericColumn createNumericColumn()
    {
        double[] values = new double[] {3.0, 4.5, 5.3, 6.1, 7.2};
        return NumericColumn.create("values", values);
    }
}
