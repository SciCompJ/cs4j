/**
 * 
 */
package net.sci.image.analyze.region2d;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.image.Calibration;
import net.sci.image.Image;
import net.sci.image.binary.distmap.ChamferMask2D;

/**
 * @author dlegland
 *
 */
public class GeodesicDiameterTest
{

    /**
     * Test method for {@link net.sci.image.analyze.region2d.RegionAnalyzer2D#analyzeRegions(net.sci.array.scalar.IntArray2D, net.sci.image.Calibration)}.
     */
    @Test
    public void testAnalyzeRegions_FiveTouchingRects_Borgefors()
    {
        UInt8Array2D labelImage = UInt8Array2D.create(17, 11);
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                labelImage.setInt(i +  1, j + 1, 1); 
                labelImage.setInt(j +  4, i + 1, 2); 
                labelImage.setInt(j +  4, i + 4, 3); 
                labelImage.setInt(j +  4, i + 7, 4); 
                labelImage.setInt(i + 13, j + 1, 5); 
            }
        }
        
        GeodesicDiameter algo = new GeodesicDiameter(ChamferMask2D.BORGEFORS);
        int[] labels = new int[]{1, 2, 3, 4, 5};
        GeodesicDiameter.Result[] geodDiams = algo.analyzeRegions(labelImage, labels, new Calibration(2));
        
        for (int i = 0; i < 5; i++)
        {
            assertEquals((26.0/3.0)+1.41, geodDiams[i].diameter, .1);
        }
    }

    /**
     * Test method for {@link net.sci.image.analyze.region2d.RegionAnalyzer2D#analyzeRegions(net.sci.array.scalar.IntArray2D, net.sci.image.Calibration)}.
     * @throws IOException 
     */
    @Test
    public void testAnalyzeRegions_Circles_ChessKnight() throws IOException
    {
        String fileName = getClass().getResource("/images/binary/circles.tif").getFile();
        Image image = Image.readImage(new File(fileName));
        image.clearCalibration();
        
        GeodesicDiameter algo = new GeodesicDiameter(ChamferMask2D.CHESSKNIGHT);
        Map<Integer, GeodesicDiameter.Result> geodDiams = algo.analyzeRegions(image);
        
        // check result size
        assertEquals(1, geodDiams.size());
        assertEquals(255, (int) geodDiams.keySet().iterator().next());
        
        // check value of first result
        GeodesicDiameter.Result res1 = geodDiams.get(255);
        assertEquals(280.0, res1.diameter, 1.0); // use a rather large tolerance
    }
    
    /**
     * Test method for {@link net.sci.image.analyze.region2d.RegionAnalyzer2D#analyzeRegions(net.sci.array.scalar.IntArray2D, net.sci.image.Calibration)}.
     */
    @Test
    public void testLongestGeodesicPaths_Rect()
    {
        UInt8Array2D image = UInt8Array2D.create(10, 3);
        for (int x = 1; x < 8; x++)
        {
            image.setInt(x, 1, 4);
        }

        GeodesicDiameter algo = new GeodesicDiameter(ChamferMask2D.CHESSKNIGHT);
        algo.setComputePaths(true);
        
        int[] labels = new int[]{4};
        Calibration calib = new Calibration(2);
        GeodesicDiameter.Result[] geodDiams = algo.analyzeRegions(image, labels, calib);

        assertEquals(1, geodDiams.length);
        List<Point2D> path1 = geodDiams[0].path;
        assertEquals(7, path1.size());
    }
}
