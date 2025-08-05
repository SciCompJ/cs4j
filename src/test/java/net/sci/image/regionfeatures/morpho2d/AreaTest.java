/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array2D;
import net.sci.image.Image;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.table.Column;
import net.sci.table.NumericColumn;
import net.sci.table.Table;

/**
 * 
 */
public class AreaTest
{
    /**
     * Test method for {@link net.sci.image.regionfeatures.morpho2d.Area#compute(net.sci.image.regionfeatures.RegionFeatures)}.
     */
    @Test
    public final void test_compute()
    {
        Image labelMap = createLabelMapImage();
        RegionFeatures data = RegionFeatures.initialize(labelMap);
        
        Area feature = new Area();
        double[] res = (double[]) feature.compute(data);
                
        assertEquals(res.length, 4);
        assertEquals(res[0],  1.0, 0.01);
        assertEquals(res[1],  4.0, 0.01);
        assertEquals(res[2],  4.0, 0.01);
        assertEquals(res[3], 16.0, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.image.regionfeatures.morpho2d.Area#updateTable(net.sci.table.Table, java.lang.Object)}.
     */
    @Test
    public final void test_updateTable()
    {
        Image labelMap = createLabelMapImage();
        RegionFeatures data = RegionFeatures.initialize(labelMap)
                .add(Area.class)
                .computeAll();

        Area feature = new Area();
        Table table = data.initializeRegionTable();
        feature.updateTable(table, data);
        
        assertEquals(4, table.rowCount());
        assertEquals(1, table.columnCount());
        assertEquals("Area", table.column(0).getName());
    }

    /**
     * Test method for {@link net.sci.image.regionfeatures.morpho2d.Area#columnUnitNames(net.sci.image.regionfeatures.RegionFeatures)}.
     */
    @Test
    public final void test_columnUnitNames()
    {
        Image labelMap = createLabelMapImage();
        String unitName = "pix";
        labelMap.getCalibration().getXAxis().setUnitName(unitName);
        labelMap.getCalibration().getYAxis().setUnitName(unitName);
        RegionFeatures data = RegionFeatures.initialize(labelMap)
                .add(Area.class)
                .computeAll();
        
        Table table = new Area().createTable(data);
        Column col = table.column(0);
        
        assertTrue(col instanceof NumericColumn);
        assertEquals(unitName + "^2", ((NumericColumn) col).getUnitName());
    }
    
    private static final Image createLabelMapImage()
    {
        UInt8Array2D array = UInt8Array2D.create(8, 8);
        array.setInt(1, 1, 3);
        for (int i = 3; i < 7; i++)
        {
            array.setInt(i, 1, 5);
            array.setInt(1, i, 8);
        }
        for (int i = 3; i < 7; i++)
        {
            for (int j = 3; j < 7; j++)
            {
                array.setInt(i, j, 9);
            }
        }
        return new Image(array);
    }
}
