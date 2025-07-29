/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array2D;
import net.sci.image.Image;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.table.Table;

/**
 * 
 */
public class CircularityTest
{
    
    /**
     * Test method for {@link net.sci.image.regionfeatures.morpho2d.Circularity#compute(net.sci.image.regionfeatures.RegionFeatures)}.
     */
    @Test
    public final void testCompute()
    {
        Image labelMap = createLabelMapImage();
        RegionFeatures data = RegionFeatures.initialize(labelMap);
        
        Circularity feature = new Circularity();
        double[] res = (double[]) feature.compute(data);
                
        assertEquals(res.length, 4);
        assertEquals(res[0], 1.74, 0.01);
        assertEquals(res[1], 0.72, 0.01);
        assertEquals(res[2], 0.72, 0.01);
        assertEquals(res[3], 1.02, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.image.regionfeatures.SingleValueFeature#updateTable(net.sci.table.Table, net.sci.image.regionfeatures.RegionFeatures)}.
     */
    @Test
    public final void testUpdateTable()
    {
        Image labelMap = createLabelMapImage();
        RegionFeatures data = RegionFeatures.initialize(labelMap)
                .add(Circularity.class)
                .computeAll();

        Circularity feature = new Circularity();
        Table table = data.initializeRegionTable();
        feature.updateTable(table, data);
        
        assertEquals(4, table.rowCount());
        assertEquals(1, table.columnCount());
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
