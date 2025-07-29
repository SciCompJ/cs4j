/**
 * 
 */
package net.sci.table.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.table.Column;
import net.sci.table.NumericColumn;

/**
 * 
 */
public class DefaultTableTest
{
    /**
     * Test method for {@link net.sci.table.impl.DefaultTable.ColumnView#setUnitName(String)}.
     */
    @Test
    public final void test_setUnitName()
    {
        DefaultTable table = createSampleTable();
        
        Column column = table.column(1);
        assertTrue(column instanceof NumericColumn);
        ((NumericColumn) column).setUnitName("mm");
        
        assertEquals("mm", ((NumericColumn) table.column(1)).getUnitName());
    }
    
    
    /**
     * Test method for {@link net.sci.table.impl.DefaultTable#addColumn(net.sci.table.Column)}.
     */
    @Test
    public final void test_addColumn_Column()
    {
        DefaultTable table = createSampleTable();
        String colName = "newName";
        double[] values = new double[] {1, 2, 3, 4, 5};
        NumericColumn column = NumericColumn.create(colName, values);
        
        table.addColumn(column);
        
        assertEquals(4, table.columnCount());
        assertEquals(colName, table.getColumnName(3));
    }
    
    /**
     * Test method for {@link net.sci.table.impl.DefaultTable#addColumn(java.lang.String, double[])}.
     */
    @Test
    public final void test_addColumn_StringDoubleArray()
    {
        DefaultTable table = createSampleTable();
        String colName = "newName";
        double[] values = new double[] {1, 2, 3, 4, 5};
        
        table.addColumn(colName, values);
        
        assertEquals(4, table.columnCount());
        assertEquals(colName, table.getColumnName(3));
    }
    
    private static final DefaultTable createSampleTable()
    {
        double[][] data = new double[3][5];
        String[] colNames = new String[] {"length", "width", "count"};
        String[] rowNames = new String[] {"row1", "row2", "row3", "row4", "row5"};
        return new DefaultTable(data, colNames, rowNames);
    }
}
