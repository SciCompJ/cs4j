/**
 * 
 */
package net.sci.table.transform;

import java.io.File;
import java.io.IOException;

import net.sci.table.Table;
import net.sci.table.io.DelimitedTableReader;
import net.sci.table.io.TableReader;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class PCATest
{

    /**
     * Test method for {@link net.sci.table.transform.PCA#fit(net.sci.table.Table)}.
     * @throws IOException 
     */
    @Test
    public final void testFit() throws IOException
    {
        PCA pca = new PCA();
        
        String fileName = getClass().getResource("/tables/iris/fisherIris.txt").getFile();
        
        TableReader reader = new DelimitedTableReader();
        Table table = reader.readTable(new File(fileName));
        Table data = Table.selectColumns(table, new int[]{0, 1, 2, 3});

        pca.fit(table);
        
    }

}
