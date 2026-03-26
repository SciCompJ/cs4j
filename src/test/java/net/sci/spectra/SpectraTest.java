/**
 * 
 */
package net.sci.spectra;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.axis.CategoricalAxis;
import net.sci.table.NumericTable;

/**
 * 
 */
public class SpectraTest
{
    
    /**
     * Test method for {@link net.sci.spectra.Spectra#convert(net.sci.table.Table)}.
     */
    @Test
    public final void testConvert()
    {
        NumericTable table = NumericTable.create(5, 100);
        for (int i = 0; i < 5; i++)
        {
            table.setRowName(i, "row" + (i+1));
        }
        CategoricalAxis colAxis = (CategoricalAxis) table.getColumnAxis();
        for (int c = 0; c < 100; c++)
        {
            colAxis.setItemName(c, Double.toString(400 + 2.5*c));
        }
        colAxis.setName("WaveLength");
        
        Spectra spectra = Spectra.convert(table);
        assertEquals(5, spectra.rowCount());
        assertEquals(100, spectra.columnCount());
        assertEquals("WaveLength", spectra.getColumnAxis().getName());
    }
    
}
