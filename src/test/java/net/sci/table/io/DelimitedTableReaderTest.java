package net.sci.table.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import net.sci.table.Table;

import org.junit.Test;

public class DelimitedTableReaderTest
{
	@Test
	public final void testReadTable_Iris() throws IOException
	{
		String fileName = getClass().getResource("/tables/iris/fisherIris.txt").getFile();
		File file = new File(fileName);
		
		TableReader reader = new DelimitedTableReader();
		
		Table table = reader.readTable(file);
		
		assertEquals(150, table.rowCount());
		assertEquals(5, table.columnCount());
	}

	@Test
	public final void testReadTable_IrisData() throws IOException
	{
		String fileName = getClass().getResource("/tables/iris/iris.data").getFile();
		File file = new File(fileName);
		
		DelimitedTableReader reader = new DelimitedTableReader(",");
		reader.setReadHeader(false);
		reader.setReadRowNames(false);
		
		Table table = reader.readTable(file);
		
		assertEquals(150, table.rowCount());
		assertEquals(5, table.columnCount());
	}

}
