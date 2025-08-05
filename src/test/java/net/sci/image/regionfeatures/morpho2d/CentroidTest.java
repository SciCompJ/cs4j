/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.image.Image;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.table.Table;

/**
 * 
 */
public class CentroidTest
{
    
    /**
     * Test method for {@link net.sci.image.regionfeatures.morpho2d.Centroid#compute(net.sci.image.regionfeatures.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        Image labelMap = createLabelMapImage();
        RegionFeatures data = RegionFeatures.initialize(labelMap);
        
        Centroid feature = new Centroid();
        Point2D[] res = (Point2D[]) feature.compute(data);
                
        assertEquals(res.length, 4);
        Point2D res0 = res[0];
        assertEquals(1.0, res0.x(), 0.01);
        assertEquals(1.0, res0.y(), 0.01);

        Point2D res1 = res[1];
        assertEquals(4.5, res1.x(), 0.01);
        assertEquals(1.0, res1.y(), 0.01);

        Point2D res2 = res[2];
        assertEquals(1.0, res2.x(), 0.01);
        assertEquals(4.5, res2.y(), 0.01);

        Point2D res3 = res[3];
        assertEquals(4.5, res3.x(), 0.01);
        assertEquals(4.5, res3.y(), 0.01);
    }
    
    /**
     * Test method for {@link net.sci.image.regionfeatures.morpho2d.Centroid#updateTable(net.sci.table.Table, net.sci.image.regionfeatures.RegionFeatures)}.
     */
    @Test
    public final void testUpdateTable()
    {
        Image labelMap = createLabelMapImage();
        RegionFeatures data = RegionFeatures.initialize(labelMap)
                .add(Centroid.class)
                .computeAll();

        Centroid feature = new Centroid();
        Table table = data.initializeRegionTable();
        feature.updateTable(table, data);
        
        assertEquals(4, table.rowCount());
        assertEquals(2, table.columnCount());
        assertEquals("Centroid_X", table.column(0).getName());
        assertEquals("Centroid_Y", table.column(1).getName());
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
