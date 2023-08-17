/**
 * 
 */
package net.sci.table;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sci.table.io.DelimitedTableReader;
import net.sci.table.io.TableReader;

/**
 * @author dlegland
 *
 */
public class TableTest
{

    /**
     * Test method for {@link net.sci.table.Table#create(net.sci.table.Column[])}.
     */
    @Test
    public final void test_create_fromColumnArray()
    {
        Table table0 = Table.create(10, 5);
        Column col0 = table0.column(0);
        Column col1 = table0.column(1);
        
        Table table = Table.create(col0, col1);
        
        assertEquals(2, table.columnCount());
    }

    /**
     * Test method for {@link net.sci.table.Table#create(java.lang.String[], net.sci.table.Column[])}.
     */
    @Test
    public final void testCreateStringArrayColumnArray()
    {
        Table table0 = Table.create(10, 5);
        Column col0 = table0.column(0);
        Column col1 = table0.column(1);
        
        Table table = Table.create(col0, col1);
        
        assertEquals(2, table.columnCount());
        assertTrue(table instanceof NumericTable);
    }

    /**
     * Test method for {@link net.sci.table.Table#selectColumns(net.sci.table.Table, int[])}.
     * @throws IOException 
     */
    @Test
    public final void testSelectColumns() throws IOException
    {
        String fileName = getClass().getResource("/tables/iris/fisherIris.txt").getFile();
        TableReader reader = new DelimitedTableReader();
        Table table = reader.readTable(new File(fileName));
        
        table = Table.selectColumns(table, new int[] {0, 1, 2, 3});

        int nc = table.columnCount();
        assertEquals(nc, 4);

        int nr = table.rowCount();
        for (Column col : table.columns())
        {
            assertEquals(nr, col.length());
        }
    }

    // TODO: create defaukt table (iris?), select clumns, and create table from selected columsn.
//    private Table createEmptyTab
}
