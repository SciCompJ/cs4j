package net.sci.table.io;

import java.io.File;
import java.io.IOException;

import net.sci.table.DataTable;

import org.junit.Test;

public class DelimitedTableWriterTest
{

	@Test
	public final void testWriteTable() throws IOException
	{
		File file = new File("output.txt");
		DelimitedTableWriter tw = new DelimitedTableWriter(file);
		tw.writeTable(new DataTable(5, 3));
	}

}
