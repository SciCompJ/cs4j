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
 * 
 */
public class ColumnsTableTest
{
    /**
     * Test method for {@link net.sci.table.ColumnsTable#columns()}.
     * @throws IOException 
     */
    @Test
    public final void testColumns() throws IOException
    {
        ColumnsTable table = createTable();        

        int nr = table.rowCount();
        int nc = table.columnCount();

        int count = 0;
        for (Column col : table.columns())
        {
            count++;
            assertEquals(nr, col.length());
        }

        assertEquals(nc, count);
    }

    /**
     * Test method for {@link net.sci.table.ColumnsTable#columns()}.
     * @throws IOException 
     */
    public final void testPrintInfos() throws IOException
    {
        ColumnsTable table = createTable();        

        table.printInfo(System.out);
    }


    /**
     * Test method for {@link net.sci.table.ColumnsTable#size()}.
     */
    @Test
    public final void testSize() throws IOException
    {
        ColumnsTable table = createTable();
        int[] dims = table.size();
        assertEquals(dims[0], 150);
        assertEquals(dims[1], 5);
    }

    private static final ColumnsTable createTable() throws IOException
    {
        String fileName = ColumnsTableTest.class.getResource("/tables/iris/fisherIris.txt").getFile();
        
        TableReader reader = new DelimitedTableReader();
        
        Table baseTable = reader.readTable(new File(fileName));
        
        Column[] columns = new Column[baseTable.columnCount()];
        for (int c = 0; c < baseTable.columnCount(); c++)
        {
            columns[c] = baseTable.column(c);
        }
        return new ColumnsTable(columns);
    }
}
