/**
 * 
 */
package net.sci.table.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.table.NumericColumn;

/**
 * 
 */
public class DefaultNumericTableTest
{
    /**
     * Test method for {@link net.sci.table.impl.DefaultNumericTable.ColumnView#setUnitName(String)}.
     */
    @Test
    public final void test_setUnitName()
    {
        DefaultNumericTable table = createSampleTable();
        
        table.column(1).setUnitName("mm");
        
        assertEquals("mm", table.column(1).getUnitName());
    }
    
    
    /**
     * Test method for {@link net.sci.table.impl.DefaultNumericTable#addColumn(net.sci.table.Column)}.
     */
    @Test
    public final void test_addColumn_Column()
    {
        DefaultNumericTable table = createSampleTable();
        String colName = "newName";
        double[] values = new double[] {1, 2, 3, 4, 5};
        NumericColumn column = NumericColumn.create(colName, values);
        
        table.addColumn(column);
        
        assertEquals(4, table.columnCount());
        assertEquals(colName, table.getColumnName(3));
    }
    
    /**
     * Test method for {@link net.sci.table.impl.DefaultNumericTable#addColumn(java.lang.String, double[])}.
     */
    @Test
    public final void test_addColumn_StringDoubleArray()
    {
        DefaultNumericTable table = createSampleTable();
        String colName = "newName";
        double[] values = new double[] {1, 2, 3, 4, 5};
        
        table.addColumn(colName, values);
        
        assertEquals(4, table.columnCount());
        assertEquals(colName, table.getColumnName(3));
    }
    
    private static final DefaultNumericTable createSampleTable()
    {
        double[][] data = new double[3][5];
        String[] colNames = new String[] {"length", "width", "count"};
        String[] rowNames = new String[] {"row1", "row2", "row3", "row4", "row5"};
        return new DefaultNumericTable(data, colNames, rowNames);
    }
}
