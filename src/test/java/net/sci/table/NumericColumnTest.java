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
     * Test method for {@link net.sci.table.NumericColumn#getValue(int)}.
     */
    @Test
    public final void testGetValue()
    {
        NumericColumn column = createNumericColumn();
        assertEquals(5.3, column.getValue(2), 0.01);
    }
    
    /**
     * Test method for {@link net.sci.table.NumericColumn#process(NumericColumn,NumericColumn,java.util.function.BiFunction)}.
     */
    @Test
    public final void testProcess()
    {
        NumericColumn col1 = NumericColumn.create("col1", new double[] {1.0, 2.0, 3.0});
        NumericColumn col2 = NumericColumn.create("col2", new double[] {0.4, 0.5, 0.6});
        
        NumericColumn res = NumericColumn.process(col1, col2, (a,b) -> a + b);
        
        double[] exp = new double[] {1.4, 2.5, 3.6};
        for (int i = 0; i < 3; i++)
        {
            assertEquals(exp[i], res.getValue(i), 0.01);
        }
    }
    
    /**
     * Test method for {@link net.sci.table.NumericColumn#concatenate(NumericColumn,NumericColumn)}.
     */
    @Test
    public final void test_concatenate()
    {
        NumericColumn col1 = NumericColumn.create("col1", new double[] {1.0, 2.0, 3.0});
        NumericColumn col2 = NumericColumn.create("col2", new double[] {0.4, 0.5, 0.6, 0.7});
        
        NumericColumn res = NumericColumn.concatenate(col1, col2);
        
        double[] exp = new double[] {1.0, 2.0, 3.0, 0.4, 0.5, 0.6, 0.7};
        for (int i = 0; i < exp.length; i++)
        {
            assertEquals(exp[i], res.getValue(i), 0.01);
        }
    }
    
    private NumericColumn createNumericColumn()
    {
        double[] values = new double[] {3.0, 4.5, 5.3, 6.1, 7.2};
        return NumericColumn.create("values", values);
    }
}
