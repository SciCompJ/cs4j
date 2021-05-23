/**
 * 
 */
package net.sci.table.cluster;

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
public class KMeansTest
{

	/**
	 * Test method for {@link net.sci.table.cluster.KMeans#process(net.sci.table.Table)}.
	 * @throws IOException 
	 */
	@Test
	public final void testProcess() throws IOException
	{
		String fileName = getClass().getResource("/tables/iris/fisherIris.txt").getFile();
		
		TableReader reader = new DelimitedTableReader();
		
		Table table = reader.readTable(new File(fileName));
		Table data = Table.selectColumns(table, new int[]{0, 1, 2, 3});

		KMeans kmeans = new KMeans(3);
		Table classes = kmeans.process(data);
		
		assertEquals(150, classes.rowCount());
		
//		for (int i = 0; i < 150; i++)
//		{
//			System.out.println(String.format("row %3d: class=%d, k=%d", i, (int) table.getValue(i, 4), (int) classes.getValue(i, 0)));
//		}
	}

}
