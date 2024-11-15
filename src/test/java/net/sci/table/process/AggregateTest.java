/**
 * 
 */
package net.sci.table.process;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sci.table.CategoricalColumn;
import net.sci.table.NumericTable;
import net.sci.table.Table;
import net.sci.table.io.DelimitedTableReader;
import net.sci.table.io.TableReader;

/**
 * 
 */
public class AggregateTest
{

    /**
     * Test method for {@link net.sci.table.process.Aggregate#aggregate(net.sci.table.Table, net.sci.table.CategoricalColumn)}.
     * @throws IOException 
     */
    @Test
    public final void testAggregateTableCategoricalColumn() throws IOException
    {
        String fileName = getClass().getResource("/tables/iris/fisherIris.txt").getFile();
        File file = new File(fileName);
        TableReader reader = new DelimitedTableReader();
        
        Table table = reader.readTable(file);
        
        NumericTable data = NumericTable.keepNumericColumns(table);
        
        CategoricalColumn groups = (CategoricalColumn) table.column(4);
        
        Table res = Aggregate.aggregate(data, groups);
        
        assertEquals(res.rowCount(), 3);
        assertEquals(res.columnCount(), data.columnCount());
        
        assertEquals(res.column(0).getName(), data.column(0).getName());
        
//        res.print();
    }

}
