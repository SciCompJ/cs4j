/**
 * 
 */
package net.sci.image.analyze;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.image.Calibration;

/**
 * @author dlegland
 *
 */
public class IntrinsicVolumesTest
{

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#area(net.sci.array.binary.BinaryArray2D, net.sci.image.Calibration)}.
     */
    @Test
    public final void testArea_singleRectangle()
    {
        BinaryArray2D array = BinaryArray2D.create(10, 10);
        array.fillBooleans((x,y) -> (x>=3 && x <= 6 && y >= 5 && y <= 7));
        Calibration calib = new Calibration(2);
        
        double area = IntrinsicVolumes.area(array, calib);
        
        assertEquals(12.0, area, 0.01);
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#perimeter(net.sci.array.binary.BinaryArray2D, net.sci.image.Calibration, int)}.
     */
    @Test
    public final void testPerimeter_smallSquare_D2()
    {
        BinaryArray2D array = BinaryArray2D.create(8, 8);
        array.fillBooleans((x,y) -> (x >= 2 && x <= 5 && y >= 2 && y <= 5));
        Calibration calib = new Calibration(2);
        
        double perim = IntrinsicVolumes.perimeter(array, calib, 2);
        
        // use Matlab library "MatImage" for reference value
        assertEquals(12.5664, perim, 0.001);
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#perimeter(net.sci.array.binary.BinaryArray2D, net.sci.image.Calibration, int)}.
     */
    @Test
    public final void testPerimeter_smallSquare_D4()
    {
        BinaryArray2D array = BinaryArray2D.create(8, 8);
        array.fillBooleans((x,y) -> (x >= 2 && x <= 5 && y >= 2 && y <= 5));
        Calibration calib = new Calibration(2);
        
        double perim = IntrinsicVolumes.perimeter(array, calib, 4);
        
        // use Matlab library "MatImage" for reference value
        assertEquals(14.0582, perim, 0.001); 
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#perimeter(net.sci.array.binary.BinaryArray2D, net.sci.image.Calibration, int)}.
     */
    @Test
    public final void testPerimeter_disk_D2()
    {
        double radius = 16.0;
        BinaryArray2D array = BinaryArray2D.create(40, 40);
        array.fillBooleans((x,y) -> Math.hypot(x - 20.32, y - 20.21) < radius);
        Calibration calib = new Calibration(2);
        
        double perim = IntrinsicVolumes.perimeter(array, calib, 2);
        
        // check to expected value with a tolerance of 5 percents
        double exp = 2 * Math.PI * radius;
        assertEquals(exp, perim, exp * 0.05);
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#perimeter(net.sci.array.binary.BinaryArray2D, net.sci.image.Calibration, int)}.
     */
    @Test
    public final void testPerimeter_disk_D4()
    {
        double radius = 16.0;
        BinaryArray2D array = BinaryArray2D.create(40, 40);
        array.fillBooleans((x,y) -> Math.hypot(x - 20.32, y - 20.21) < radius);
        Calibration calib = new Calibration(2);
        
        double perim = IntrinsicVolumes.perimeter(array, calib, 4);
        
        // check to expected value with a tolerance of 5 percents
        double exp = 2 * Math.PI * radius;
        assertEquals(exp, perim, exp * 0.05);
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#eulerNumber2d(net.sci.array.binary.BinaryArray2D, int)}.
     */
    @Test
    public final void testEulerNumber2d_singleSquare_C4()
    {
        BinaryArray2D array = BinaryArray2D.create(8, 8);
        array.fillBooleans((x,y) -> (x >= 2 && x <= 5 && y >= 2 && y <= 5));
        
        int euler = IntrinsicVolumes.eulerNumber2d(array, 4);
        
        assertEquals(1, euler);
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#eulerNumber2d(net.sci.array.binary.BinaryArray2D, int)}.
     */
    @Test
    public final void testEulerNumber2d_singleSquare_C8()
    {
        BinaryArray2D array = BinaryArray2D.create(8, 8);
        array.fillBooleans((x,y) -> (x >= 2 && x <= 5 && y >= 2 && y <= 5));
        
        int euler = IntrinsicVolumes.eulerNumber2d(array, 8);
        
        assertEquals(1, euler);
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#eulerNumber2d(net.sci.array.binary.BinaryArray2D, int)}.
     */
    @Test
    public final void testEulerNumber2d_fullSquare_C4()
    {
        BinaryArray2D array = BinaryArray2D.create(8, 8);
        array.fill(true);
        
        int euler = IntrinsicVolumes.eulerNumber2d(array, 4);
        
        assertEquals(1, euler);
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#eulerNumber2d(net.sci.array.binary.BinaryArray2D, int)}.
     */
    @Test
    public final void testEulerNumber2d_fullSquare_C8()
    {
        BinaryArray2D array = BinaryArray2D.create(8, 8);
        array.fill(true);
        
        int euler = IntrinsicVolumes.eulerNumber2d(array, 8);
        
        assertEquals(1, euler);
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#eulerNumber2d(net.sci.array.binary.BinaryArray2D, int)}.
     */
    @Test
    public final void testEulerNumber2d_torus_C4()
    {
        BinaryArray2D array = BinaryArray2D.create(8, 8);
        array.fillBooleans((x,y) -> x > 1 && x < 6 && y > 1 && y < 6);
        array.setBoolean(4,  4, false);
        
        int euler = IntrinsicVolumes.eulerNumber2d(array, 4);
        
        assertEquals(0, euler);
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#eulerNumber2d(net.sci.array.binary.BinaryArray2D, int)}.
     */
    @Test
    public final void testEulerNumber2d_torus_C8()
    {
        BinaryArray2D array = BinaryArray2D.create(8, 8);
        array.fillBooleans((x,y) -> x > 1 && x < 6 && y > 1 && y < 6);
        array.setBoolean(4,  4, false);
        
        int euler = IntrinsicVolumes.eulerNumber2d(array, 8);
        
        assertEquals(0, euler);
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#volume(net.sci.array.binary.BinaryArray3D, net.sci.image.Calibration)}.
     */
    @Test
    public final void testVolume()
    {
        BinaryArray3D array = createBallArray();
        
        double volume = IntrinsicVolumes.volume(array, new Calibration(3));
        
        double radius = 20.0;
        double exp = (4.0 / 3.0) * Math.PI * Math.pow(radius, 3); 
        assertEquals(exp, volume, exp * 0.01);
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#surfaceArea(net.sci.array.binary.BinaryArray3D, net.sci.image.Calibration, int)}.
     */
    @Test
    public final void testSurfaceArea_Ball_D3()
    {
        BinaryArray3D array = createBallArray();
        
        double surf = IntrinsicVolumes.surfaceArea(array, new Calibration(3), 3);
        
        double radius = 20.0;
        double exp = 4.0 * Math.PI * Math.pow(radius, 2); 
        assertEquals(exp, surf, exp * 0.01);
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#surfaceArea(net.sci.array.binary.BinaryArray3D, net.sci.image.Calibration, int)}.
     */
    @Test
    public final void testSurfaceArea_Ball_D13()
    {
        BinaryArray3D array = createBallArray();
        
        double surf = IntrinsicVolumes.surfaceArea(array, new Calibration(3), 13);
        
        double radius = 20.0;
        double exp = 4.0 * Math.PI * Math.pow(radius, 2); 
        assertEquals(exp, surf, exp * 0.01);
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#meanBreadth(net.sci.array.binary.BinaryArray3D, net.sci.image.Calibration, int, int)}.
     */
    @Test
    public final void testMeanBreadth_Ball_D3()
    {
        BinaryArray3D array = createBallArray();
        
        double breadth = IntrinsicVolumes.meanBreadth(array, new Calibration(3), 3, 4);
        
        double radius = 20.0;
        double exp = 2.0 * radius; 
        assertEquals(exp, breadth, exp * 0.01);
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#meanBreadth(net.sci.array.binary.BinaryArray3D, net.sci.image.Calibration, int, int)}.
     */
    @Test
    public final void testMeanBreadth_Ball_D13()
    {
        BinaryArray3D array = createBallArray();
        
        double breadth = IntrinsicVolumes.meanBreadth(array, new Calibration(3), 13, 8);
        
        double radius = 20.0;
        double exp = 2.0 * radius; 
        assertEquals(exp, breadth, exp * 0.01);
    }

    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#eulerNumber3d(net.sci.array.binary.BinaryArray3D, int)}.
     */
    @Test
    public final void testEulerNumber3d_ball_C6()
    {
        BinaryArray3D array = createBallArray();
        
        int euler = IntrinsicVolumes.eulerNumber3d(array, 6);
        
        assertEquals(1, euler);
    }
    
    /**
     * Test method for {@link net.sci.image.analyze.IntrinsicVolumes#eulerNumber3d(net.sci.array.binary.BinaryArray3D, int)}.
     */
    @Test
    public final void testEulerNumber3d_ball_C26()
    {
        BinaryArray3D array = createBallArray();
        
        int euler = IntrinsicVolumes.eulerNumber3d(array, 26);
        
        assertEquals(1, euler);
    }
    
    private BinaryArray3D createBallArray()
    {
        BinaryArray3D array = BinaryArray3D.create(50, 50, 50);
        array.fillBooleans((x,y,z) -> hypot3(x - 25.12, y - 25.23, z - 25.34) <= 20.0);
        return array;
    }
    
    private static final double hypot3(double x, double y, double z)
    {
        return Math.hypot(Math.hypot(x, y), z);
    }
    

}
