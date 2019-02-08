/**
 * 
 */
package net.sci.table.process;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import net.sci.table.NumericTable;
import net.sci.table.Table;
import net.sci.table.io.DelimitedTableReader;
import net.sci.table.io.TableReader;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class CorrelationMatrixTest
{

    /**
     * Test method for {@link net.sci.table.process.CovarianceMatrix#process(net.sci.table.Table)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess() throws IOException
    {
        String fileName = getClass().getResource("/tables/iris/fisherIris.txt").getFile();
        File file = new File(fileName);
        TableReader reader = new DelimitedTableReader();
        
        Table table = reader.readTable(file);
//        table.printInfo(System.out);
        
        NumericTable data = NumericTable.keepNumericColumns(table);
        
        NumericTable corrMat = new CorrelationMatrix().process(data);
        assertEquals(4, corrMat.columnNumber());
        assertEquals(4, corrMat.rowNumber());
        
        assertEquals(1.0, corrMat.getValue(0, 0), 0.01);
        assertEquals(1.0, corrMat.getValue(1, 1), 0.01);
        assertEquals(1.0, corrMat.getValue(2, 2), 0.01);
        assertEquals(1.0, corrMat.getValue(3, 3), 0.01);
    }

}
