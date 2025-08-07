/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array2D;
import net.sci.geom.polygon2d.OrientedBox2D;
import net.sci.image.Image;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.table.Table;

/**
 * 
 */
public class OrientedBoundingBoxTest
{
    
    /**
     * Test method for {@link net.sci.image.regionfeatures.morpho2d.OrientedBoundingBox#compute(net.sci.image.regionfeatures.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        Image labelMap = createLabelMapImage();
        RegionFeatures data = RegionFeatures.initialize(labelMap);
        
        OrientedBoundingBox feature = new OrientedBoundingBox();
        OrientedBox2D[] res = (OrientedBox2D[]) feature.compute(data);
                
        assertEquals(res.length, 4);
        OrientedBox2D res0 = res[0];
        assertEquals(1.0, res0.center().x(), 0.01);
        assertEquals(1.0, res0.center().y(), 0.01);
        assertEquals(0.7, res0.size1(), 0.01);
        assertEquals(0.7, res0.size2(), 0.01);

        OrientedBox2D res1 = res[1];
        assertEquals(4.5, res1.center().x(), 0.01);
        assertEquals(1.0, res1.center().y(), 0.01);
        assertEquals(4.0, res1.size1(), 0.01);
        assertEquals(1.0, res1.size2(), 0.01);
        assertEquals(4.0, res1.size1(), 0.01);
        assertEquals(0.0, res1.orientation(), 0.01);

        OrientedBox2D res2 = res[2];
        assertEquals(1.0, res2.center().x(), 0.01);
        assertEquals(4.5, res2.center().y(), 0.01);
        assertEquals(4.0, res2.size1(), 0.01);
        assertEquals(1.0, res2.size2(), 0.01);
        assertEquals(90.0, res2.orientation(), 0.01);

        OrientedBox2D res3 = res[3];
        assertEquals(4.5, res3.center().x(), 0.01);
        assertEquals(4.5, res3.center().y(), 0.01);
        assertEquals(4.0, res3.size1(), 0.01);
        assertEquals(4.0, res3.size2(), 0.01);
    }
    
    /**
     * Test method for {@link net.sci.image.regionfeatures.morpho2d.OrientedBoundingBox#updateTable(net.sci.table.Table, net.sci.image.regionfeatures.RegionFeatures)}.
     */
    @Test
    public final void testUpdateTable()
    {
        Image labelMap = createLabelMapImage();
        RegionFeatures data = RegionFeatures.initialize(labelMap)
                .add(OrientedBoundingBox.class)
                .computeAll();

        OrientedBoundingBox feature = new OrientedBoundingBox();
        Table table = data.initializeRegionTable();
        feature.updateTable(table, data);
        
        assertEquals(4, table.rowCount());
        assertEquals(5, table.columnCount());
        assertEquals("Oriented_Box_Center_X", table.column(0).getName());
        assertEquals("Oriented_Box_Center_Y", table.column(1).getName());
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
