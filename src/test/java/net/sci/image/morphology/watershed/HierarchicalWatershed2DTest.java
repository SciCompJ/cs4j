/**
 * 
 */
package net.sci.image.morphology.watershed;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import net.sci.algo.ConsoleAlgoListener;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.image.Image;
import net.sci.image.io.TiffImageReader;

/**
 * @author dlegland
 *
 */
public class HierarchicalWatershed2DTest
{
    /**
     * Test method for {@link net.sci.image.morphology.watershed.HierarchicWatershed2D#process(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testProcess_four_region_simple()
    {
        int[][] buffer = new int[][] {
            {250,  70,  60,  50,  40,   0,  40,  50,  60,  73, 250}, 
            { 35, 150,  71,  60,  50,  40,  50,  60,  74, 210,  55}, 
            { 21,  34, 250,  72,  60,  73,  60,  75, 250,  54,  41}, 
            { 20,  22,  33, 250, 260, 240, 260, 250,  53,  42,  30}, 
            { 23,  32, 250,  70,  61,  53,  62,  71, 250,  52,  43}, 
            { 31, 220,  71,  64,  52,  40,  54,  67,  72, 150,  51}, 
            {250,  72,  65,  51,  41,  10,  42,  55,  66,  73, 250}, 
        };
        UInt8Array2D array = UInt8Array2D.create(11, 7);
        for (int y = 0; y < 7; y++)
        {
            for (int x = 0; x < 11; x++)
            {
                array.setInt(x, y, buffer[y][x]);
            }
        }
        array.print(System.out);
        
        HierarchicalWatershed2D algo = new HierarchicalWatershed2D();
        ConsoleAlgoListener.monitor(algo);
        
        ScalarArray2D<?> result = algo.process(array);
        UInt8Array2D.wrap(UInt8Array.wrap(result)).print(System.out);
    }

    /**
     * Test method for {@link net.sci.image.morphology.watershed.HierarchicWatershed2D#process(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testProcess_three_regions_corner()
    {
        int[][] buffer = new int[][] {
            {  5,   6, 100,  50}, 
            { 20,  30,  60,  40}, 
            { 10,  11, 110,  55}, 
        };
        UInt8Array2D array = UInt8Array2D.create(4, 3);
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 4; x++)
            {
                array.setInt(x, y, buffer[y][x]);
            }
        }
        array.print(System.out);
        
        HierarchicalWatershed2D algo = new HierarchicalWatershed2D();
        ConsoleAlgoListener.monitor(algo);
        
        ScalarArray2D<?> result = algo.process(array);
        UInt8Array2D.wrap(UInt8Array.wrap(result)).print(System.out);
    }

    /**
     * Test method for {@link net.sci.image.morphology.watershed.HierarchicWatershed2D#process(net.sci.array.scalar.ScalarArray2D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_appleCells_sub05() throws IOException
    {
        String fileName = getClass().getResource("/images/plant_tissues/appleCells_crop_smooth_sub05.tif").getFile();
        
        TiffImageReader reader = new TiffImageReader(fileName);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());
        UInt8Array2D array = UInt8Array2D.wrap(UInt8Array.wrap((ScalarArray<?>) image.getData()));

        HierarchicalWatershed2D algo = new HierarchicalWatershed2D();
        ConsoleAlgoListener.monitor(algo);
        
        ScalarArray2D<?> res = algo.process(array);
        UInt8Array2D res8 = UInt8Array2D.wrap(UInt8Array.wrap(res));
        
        Image resultImage = new Image(res8, image);
        resultImage.show();
        
        System.out.println("finish.");
    }

    /**
     * Test method for {@link net.sci.image.morphology.watershed.HierarchicWatershed2D#process(net.sci.array.scalar.ScalarArray2D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_appleCells_sub10() throws IOException
    {
        String fileName = getClass().getResource("/images/plant_tissues/appleCells_crop_smooth_sub10.tif").getFile();
        
        TiffImageReader reader = new TiffImageReader(fileName);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());
        UInt8Array2D array = UInt8Array2D.wrap(UInt8Array.wrap((ScalarArray<?>) image.getData()));

        HierarchicalWatershed2D algo = new HierarchicalWatershed2D();
        ConsoleAlgoListener.monitor(algo);
        
        UInt8Array2D result = UInt8Array2D.wrap(UInt8Array.wrap(algo.process(array)));
        
        Image resultImage = new Image(result, image);
        resultImage.show();
        
        System.out.println("finish.");
    }

}
