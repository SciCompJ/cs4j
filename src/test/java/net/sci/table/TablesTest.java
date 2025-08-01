/**
 * 
 */
package net.sci.table;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class TablesTest
{

    /**
     * Test method for {@link net.sci.table.Tables#concatenateRows(net.sci.table.Table, net.sci.table.Table)}.
     */
    @Test
    public final void test_concatenateRows()
    {
        NumericColumn col1 = NumericColumn.create("num", new double[] {1.2, 2.3, 3.4});
        CategoricalColumn col2 = CategoricalColumn.create("cat", new String[] {"item1", "item2", "item1"});

        Table table1 = Table.create(col1, col2);
        Table table2 = Table.create(col1, col2);
        
        Table res = Tables.concatenateRows(table1, table2);
        
        assertEquals(table1.rowCount()+table2.rowCount(), res.rowCount());
    }

}
