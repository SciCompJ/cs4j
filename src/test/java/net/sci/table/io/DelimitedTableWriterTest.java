package net.sci.table.io;

import java.io.File;
import java.io.IOException;

import net.sci.table.Table;
import net.sci.table.impl.DefaultNumericTable;

import org.junit.Test;

public class DelimitedTableWriterTest
{

	@Test
	public final void testWriteTable() throws IOException
	{
		Table table = new DefaultNumericTable(5, 3);
		table.setColumnNames(new String[]{"col1", "col2", "col3"});
		table.setRowNames(new String[]{"row1", "row2", "row3", "row4", "row5"});
		
		File file = new File("output.txt");
		DelimitedTableWriter tw = new DelimitedTableWriter();
		
		tw.writeTable(table, file);
	}

}
