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
public class CovarianceMatrixTest
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
        
        NumericTable covMat = new CovarianceMatrix().process(data);
        assertEquals(4, covMat.columnNumber());
        assertEquals(4, covMat.rowNumber());
    }

}
