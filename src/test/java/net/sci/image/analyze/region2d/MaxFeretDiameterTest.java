/**
 * 
 */
package net.sci.image.analyze.region2d;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import net.sci.image.Image;
import net.sci.table.Table;

/**
 * @author dlegland
 *
 */
public class MaxFeretDiameterTest
{

    /**
     * Test method for {@link net.sci.image.analyze.region2d.MaxFeretDiameter#analyzeRegions(net.sci.array.scalar.IntArray2D, int[], net.sci.image.Calibration)}.
     * @throws IOException 
     */
    @Test
    public void testAnalyzeRegions_circles() throws IOException
    {
        String fileName = getClass().getResource("/images/binary/circles.tif").getFile();
        Image image = Image.readImage(new File(fileName));
        image.clearCalibration();

        MaxFeretDiameter algo = new MaxFeretDiameter();
        
        Map<Integer, PointPair2D> maxFeretDiams = algo.analyzeRegions(image);

        assertEquals(1, maxFeretDiams.size());
        
        PointPair2D diam = maxFeretDiams.get(255);
        
        assertEquals(272.7, diam.diameter(), .2);
        
        Table table = algo.createTable(maxFeretDiams);
        assertEquals(1, table.rowNumber());
    }

}
