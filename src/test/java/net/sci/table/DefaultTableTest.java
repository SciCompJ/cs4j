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
public class DefaultTableTest
{

    /**
     * Test method for {@link net.sci.table.DefaultTable#columns()}.
     * @throws IOException 
     */
    @Test
    public final void testColumns() throws IOException
    {
        String fileName = getClass().getResource("/tables/iris/fisherIris.txt").getFile();
        
        TableReader reader = new DelimitedTableReader();
        
        Table table = reader.readTable(new File(fileName));

        int nr = table.getRowNumber();
        int nc = table.getColumnNumber();

        int count = 0;
        for (Column col : table.columns())
        {
            count++;
            assertEquals(nr, col.length());
        }

        assertEquals(nc, count);
    }

}
