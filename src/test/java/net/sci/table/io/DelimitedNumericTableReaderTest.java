/**
 * 
 */
package net.sci.table.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sci.table.NumericTable;
import net.sci.table.Table;

/**
 * 
 */
public class DelimitedNumericTableReaderTest
{
    /**
     * Test method for {@link net.sci.table.io.DelimitedNumericTableReader#readTable(java.io.File)}.
     * @throws IOException 
     */
    @Test
    public final void test_readTableFile_numericData24x4() throws IOException
    {
        String fileName = getClass().getResource("/tables/numericData_24x4.txt").getFile();
        File file = new File(fileName);

        DelimitedNumericTableReader reader = new DelimitedNumericTableReader().setReadRowNames(false);

        Table table = reader.readTable(file);

        assertEquals(24, table.rowCount());
        assertEquals(4, table.columnCount());
        assertTrue(table instanceof NumericTable);
    }

    /**
     * Test method for {@link net.sci.table.io.DelimitedNumericTableReader#readTable(java.io.File)}.
     * @throws IOException 
     */
    @Test
    public final void test_readTableFile_numericData24x4_parseDoubleProblem() throws IOException
    {
        String fileName = getClass().getResource("/tables/numericData_24x4_problem.txt").getFile();
        File file = new File(fileName);

        DelimitedNumericTableReader reader = new DelimitedNumericTableReader().setReadRowNames(false);

        assertThrows(RuntimeException.class, () -> reader.readTable(file));
    }
}
