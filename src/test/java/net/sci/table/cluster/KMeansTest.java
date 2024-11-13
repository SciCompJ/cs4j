/**
 * 
 */
package net.sci.table.cluster;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import net.sci.axis.Axis;
import net.sci.axis.CategoricalAxis;
import net.sci.table.Table;
import net.sci.table.io.DelimitedTableReader;
import net.sci.table.io.TableReader;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class KMeansTest
{

	/**
	 * Test method for {@link net.sci.table.cluster.KMeans#process(net.sci.table.Table)}.
	 * @throws IOException 
	 */
	@Test
	public final void testProcess() throws IOException
	{
		Table table = readFisherIrisTable();
		Table data = Table.selectColumns(table, new int[]{0, 1, 2, 3});

		KMeans kmeans = new KMeans(3);
		Table classes = kmeans.process(data);
		
		assertEquals(150, classes.rowCount());
		Axis rowAxis = classes.getRowAxis();
		assertNotNull(rowAxis);
		assertTrue(rowAxis instanceof CategoricalAxis);
//		for (int i = 0; i < 150; i++)
//		{
//			System.out.println(String.format("row %3d: class=%d, k=%d", i, (int) table.getValue(i, 4), (int) classes.getValue(i, 0)));
//		}
	}

    /**
     * Test method for {@link net.sci.table.cluster.KMeans#centroid()}.
     * @throws IOException 
     */
    @Test
    public final void test_centroid() throws IOException
    {
        Table table = readFisherIrisTable();
        Table data = Table.selectColumns(table, new int[]{0, 1, 2, 3});

        KMeans kmeans = new KMeans(3).fit(data);
        
        Table centroids = kmeans.centroids();
        
        assertEquals(3, centroids.rowCount());
        assertEquals(4, centroids.columnCount());
        Axis rowAxis = centroids.getRowAxis();
        assertNotNull(rowAxis);
        assertTrue(rowAxis instanceof CategoricalAxis);
        assertEquals(rowAxis.getName(), "Class");
    }
    
    private static final Table readFisherIrisTable() throws IOException
    {
        String fileName = KMeansTest.class.getResource("/tables/iris/fisherIris.txt").getFile();
        
        TableReader reader = new DelimitedTableReader();
        
        return reader.readTable(new File(fileName));
    }
}
