/**
 * 
 */
package net.sci.table.process;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.table.CategoricalColumn;
import net.sci.table.LogicalColumn;
import net.sci.table.Table;

/**
 * 
 */
public class DisjunctiveTableTest
{

    /**
     * Test method for {@link net.sci.table.process.DisjunctiveTable#process(net.sci.table.Table, java.lang.String)}.
     */
    @Test
    public final void testProcessTableString()
    {
        CategoricalColumn column = createColumn();
        Table table = Table.create(column);
        
        Table res = DisjunctiveTable.process(table, column.getName());
        
        assertEquals(3, res.columnCount());
        assertEquals(column.length(), res.rowCount());
        assertTrue(res.column(0) instanceof LogicalColumn);
        assertTrue(res.column(1) instanceof LogicalColumn);
        assertTrue(res.column(2) instanceof LogicalColumn);
    }

    /**
     * Test method for {@link net.sci.table.process.DisjunctiveTable#process(net.sci.table.CategoricalColumn)}.
     */
    @Test
    public final void testProcessCategoricalColumn()
    {
        CategoricalColumn column = createColumn();
        
        LogicalColumn[] cols = DisjunctiveTable.process(column);
        
        assertEquals(3, cols.length);
        assertEquals(column.length(), cols[0].length());
    }
    
    private static final CategoricalColumn createColumn()
    {
        String[] levelNames = new String[] {"iris", "tulip", "rose"};
        int[] levels = new int[] {0, 1, 2, 1, 2, 0, 0, 1};
        return CategoricalColumn.create("species", levels, levelNames);
    }

}
