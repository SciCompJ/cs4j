/**
 * 
 */
package net.sci.image.morphology.watershed;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

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
    public final void testProcess_four_regions_very_simple()
    {
        int[][] buffer = new int[][] {
            { 10,  50,  21,  20,  22},
            {250, 250, 200, 250, 100},
            { 32,  30,  31,  60,  40},
        };
        UInt8Array2D array = UInt8Array2D.fromIntArray(buffer);
//        System.out.println(array);
        
        HierarchicalWatershed2D algo = new HierarchicalWatershed2D();
//        ConsoleAlgoListener.monitor(algo);
        
        ScalarArray2D<?> result = algo.process(array);
//        System.out.println(UInt8Array2D.wrap(UInt8Array.wrap(result)));
        
        // check size
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        // check basins have dynamic equal to zero
        assertEquals(0.0, result.getValue(0, 0), 0.01);
        assertEquals(0.0, result.getValue(4, 0), 0.01);
        assertEquals(0.0, result.getValue(0, 2), 0.01);
        assertEquals(0.0, result.getValue(4, 2), 0.01);
        // check the dynamic of the boundaries
        assertEquals(30.0, result.getValue(1, 0), 0.01); // boundary (1, 2)
        assertEquals(20.0, result.getValue(3, 2), 0.01); // boundary (3, 4)
        assertEquals(70.0, result.getValue(4, 1), 0.01); // boundary (1, 2 vs 3 ,4)
        assertEquals(70.0, result.getValue(0, 1), 0.01);
    }

    /**
     * Test method for {@link net.sci.image.morphology.watershed.HierarchicWatershed2D#process(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testProcess_four_regions_with_triple_boundaries()
    {
        int[][] buffer = new int[][] {
            { 50,  21,  20,  22, 100},
            { 10, 250, 200, 250,  40},
            {250,  31,  30,  32,  60},
        };
        UInt8Array2D array = UInt8Array2D.fromIntArray(buffer);
//        System.out.println(array);
        
        HierarchicalWatershed2D algo = new HierarchicalWatershed2D();
//        ConsoleAlgoListener.monitor(algo);
        
        ScalarArray2D<?> result = algo.process(array);
//        System.out.println(UInt8Array2D.wrap(UInt8Array.wrap(result)));
        
        // check size
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        // check basins have dynamic equal to zero
        assertEquals(0.0, result.getValue(2, 0), 0.01);
        assertEquals(0.0, result.getValue(0, 1), 0.01);
        assertEquals(0.0, result.getValue(4, 1), 0.01);
        assertEquals(0.0, result.getValue(2, 2), 0.01);
        // check the dynamic of the boundaries
        assertEquals(20.0, result.getValue(4, 2), 0.01);
        assertEquals(30.0, result.getValue(0, 0), 0.01);
        assertEquals(70.0, result.getValue(4, 0), 0.01);
        assertEquals(70.0, result.getValue(3, 1), 0.01);
        assertEquals(70.0, result.getValue(2, 1), 0.01);
        assertEquals(70.0, result.getValue(1, 1), 0.01);
        assertEquals(70.0, result.getValue(0, 2), 0.01);
    }

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
        UInt8Array2D array = UInt8Array2D.fromIntArray(buffer);
//        System.out.println(array);
//        array.print(System.out);
        
        HierarchicalWatershed2D algo = new HierarchicalWatershed2D();
//        ConsoleAlgoListener.monitor(algo);
        
        ScalarArray2D<?> result = algo.process(array);
//        UInt8Array2D.wrap(UInt8Array.wrap(result)).print(System.out);
        
        // check size
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        // check basins have dynamic equal to zero
        assertEquals(0.0, result.getValue( 5, 0), 0.01);
        assertEquals(0.0, result.getValue( 0, 3), 0.01);
        assertEquals(0.0, result.getValue(10, 3), 0.01);
        assertEquals(0.0, result.getValue( 5, 6), 0.01);
        // check the dynamic of the boundaries
        assertEquals(130.0, result.getValue( 0, 0), 0.01); // (1, 2)
        assertEquals(200.0, result.getValue(10, 0), 0.01); // (global)
        assertEquals(200.0, result.getValue( 0, 6), 0.01); // (global)
        assertEquals(120.0, result.getValue(10, 6), 0.01); // (3, 4)
        assertEquals(200.0, result.getValue( 5, 3), 0.01); // (global)
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
        UInt8Array2D array = UInt8Array2D.fromIntArray(buffer);
//        array.print(System.out);
        
        HierarchicalWatershed2D algo = new HierarchicalWatershed2D();
//        ConsoleAlgoListener.monitor(algo);
        
        ScalarArray2D<?> result = algo.process(array);
//        UInt8Array2D.wrap(UInt8Array.wrap(result)).print(System.out);
        
        // check size
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        // check basins have dynamic equal to zero
        assertEquals(0.0, result.getValue(0, 0), 0.01);
        assertEquals(0.0, result.getValue(3, 0), 0.01);
        assertEquals(0.0, result.getValue(0, 2), 0.01);
        // check the dynamic of the boundaries
        assertEquals( 10.0, result.getValue( 0, 1), 0.01); // (1, 3)
        assertEquals( 10.0, result.getValue( 1, 1), 0.01); // (1, 3)
        assertEquals( 20.0, result.getValue( 2, 0), 0.01); // (global)
        assertEquals( 20.0, result.getValue( 2, 1), 0.01); // (global)
        assertEquals( 20.0, result.getValue( 2, 2), 0.01); // (global)
    }

    /**
     * Test method for {@link net.sci.image.morphology.watershed.HierarchicWatershed2D#process(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testProcess_merge_basin_and_two_boundaries()
    {
        int[][] buffer = new int[][] {
            {250, 200,  10, 200, 250}, 
            { 20,  40, 100,  60,  30}, 
            {250,  30, 250,  40, 250}, 
        };
        UInt8Array2D array = UInt8Array2D.fromIntArray(buffer);
//        System.out.println(array);
        
        HierarchicalWatershed2D algo = new HierarchicalWatershed2D();
//        ConsoleAlgoListener.monitor(algo);
        
        ScalarArray2D<?> result = algo.process(array);
//        System.out.println(UInt8Array2D.wrap(UInt8Array.wrap(result)));
        
        // check size
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        // check basins have dynamic equal to zero
        assertEquals(0.0, result.getValue(2, 0), 0.01);
        assertEquals(0.0, result.getValue(0, 1), 0.01);
        assertEquals(0.0, result.getValue(4, 1), 0.01);
        assertEquals(0.0, result.getValue(1, 2), 0.01);
        assertEquals(0.0, result.getValue(3, 2), 0.01);
        // check the dynamic of the boundaries
        assertEquals( 10.0, result.getValue( 1, 1), 0.01); // (2, 4)
        assertEquals( 10.0, result.getValue( 0, 2), 0.01); // (2, 4)
        assertEquals( 20.0, result.getValue( 3, 1), 0.01); // (3, 5)
        assertEquals( 20.0, result.getValue( 4, 2), 0.01); // (3, 5)
        assertEquals( 70.0, result.getValue( 1, 0), 0.01); // (global)
        assertEquals( 70.0, result.getValue( 3, 0), 0.01); // (global)
        assertEquals( 70.0, result.getValue( 2, 1), 0.01); // (global)    
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
//        ConsoleAlgoListener.monitor(algo);
        
        ScalarArray2D<?> res = algo.process(array);
        UInt8Array2D res8 = UInt8Array2D.wrap(UInt8Array.wrap(res));
        
        Image resultImage = new Image(res8, image);
        resultImage.show();
    }
}