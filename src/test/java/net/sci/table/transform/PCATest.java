/**
 * 
 */
package net.sci.table.transform;

import static org.junit.Assert.*;

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

        pca.fit(data);
        
        assertEquals(4, pca.eigenValues.rowCount());
        assertEquals(3, pca.eigenValues.columnCount());
        assertEquals(1.0, pca.eigenValues.getValue(3, 2), .01);

        assertEquals(4, pca.loadings.columnCount());
        assertEquals(4, pca.loadings.rowCount());

        assertEquals(4, pca.scores.columnCount());
        assertEquals(150, pca.scores.rowCount());
    }
}
