/**
 * 
 */
package net.sci.image.regionfeatures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array2D;
import net.sci.image.Image;
import net.sci.image.regionfeatures.morpho2d.Area;
import net.sci.image.regionfeatures.morpho2d.Perimeter;
import net.sci.table.Column;
import net.sci.table.NumericColumn;
import net.sci.table.Table;

/**
 * 
 */
public class RegionFeaturesTest
{
    /**
     * Test method for {@link net.sci.image.regionfeatures.RegionFeatures#process(java.lang.Class)}.
     */
    @Test
    public final void test_process()
    {
        Image labelMap = createImagePlus();
        
        RegionFeatures regFeat = RegionFeatures.initialize(labelMap);
        regFeat.process(ElementCount.class);
        
        assertTrue(regFeat.isComputed(ElementCount.class));
    }
    
    /**
     * Test method for {@link net.sci.image.regionfeatures.RegionFeatures#createTable()}.
     */
    @Test
    public final void test_createTable()
    {
        Image labelMap = createImagePlus();
        
        Table table = RegionFeatures.initialize(labelMap)
                .add(ElementCount.class)
                .createTable();
                
        assertEquals(4, table.rowCount());
        assertEquals(1, table.columnCount());
    }

    /**
     * Test method for {@link net.sci.image.regionfeatures.RegionFeatures#createTable()}.
     */
    @Test
    public final void test_createTable_unitNames()
    {
        Image labelMap = createImagePlus();
        String unitName = "pix";
        labelMap.getCalibration().getXAxis().setUnitName(unitName);
        labelMap.getCalibration().getYAxis().setUnitName(unitName);

        Table table = RegionFeatures.initialize(labelMap)
                .add(Area.class)
                .add(Perimeter.class)
                .createTable();
                
        assertEquals(4, table.rowCount());
        assertEquals(2, table.columnCount());
        
        Column areaColumn = table.column(0);
        assertTrue(areaColumn instanceof NumericColumn);
        assertEquals(unitName + "^2", ((NumericColumn) areaColumn).getUnitName());
        
        Column perimeterColumn = table.column(1);
        assertTrue(perimeterColumn instanceof NumericColumn);
        assertEquals(unitName, ((NumericColumn) perimeterColumn).getUnitName());
    }

    private static final Image createImagePlus()
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
        
        Image image = new Image(array);
        image.setName("Labels");
        return image;
    }
}
