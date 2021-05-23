/**
 * 
 */
package net.sci.table;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import net.sci.table.io.DelimitedTableReader;
import net.sci.table.io.TableReader;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class NumericTableTest
{

    /**
     * Test method for {@link net.sci.table.NumericTable#keepNumericColumns(net.sci.table.Table)}.
     * @throws IOException 
     */
    @Test
    public final void testKeepNumericColumns() throws IOException
    {
        String fileName = getClass().getResource("/tables/iris/fisherIris.txt").getFile();
        File file = new File(fileName);
        TableReader reader = new DelimitedTableReader();
        
        Table table = reader.readTable(file);
//        table.printInfo(System.out);

        NumericTable data = NumericTable.keepNumericColumns(table);
        
        assertEquals(table.rowCount(), data.rowCount());
        assertEquals(4, data.columnCount());
    }

}
