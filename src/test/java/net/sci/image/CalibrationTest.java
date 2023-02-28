/**
 * 
 */
package net.sci.image;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.color.RGB8Array2D;
import net.sci.axis.Axis;
import net.sci.axis.CategoricalAxis;

/**
 * @author dlegland
 *
 */
public class CalibrationTest
{
    /**
     * Test method for {@link net.sci.image.Calibration#getChannelAxis()}.
     */
    @Test
    public final void channelAxis_ColorImage()
    {
        RGB8Array2D array = RGB8Array2D.create(8, 6);
        Image image = new Image(array, ImageType.COLOR);
        
        Calibration calib = image.getCalibration();
        
        Axis axis = calib.getChannelAxis();
        assertNotNull(axis);
        assertTrue(axis.getType() == Axis.Type.CHANNEL);
        assertTrue(axis instanceof CategoricalAxis);
        
        CategoricalAxis caxis = (CategoricalAxis) axis;
        assertEquals(caxis.length(), 3);
//        System.out.println(caxis.getItemName(0));
//        System.out.println(caxis.getItemName(1));
//        System.out.println(caxis.getItemName(2));
    }
    
    
    /**
     * Test method for {@link net.sci.image.Calibration#getChannelAxis()}.
     */
    @Test
    public final void testGetChannelAxis()
    {
        double[] resol = new double[] {2.5, 2.5, 2.8};
        Calibration calib = new Calibration(resol, "�m");
        
        Axis axis = calib.getChannelAxis();
        assertNotNull(axis);
        assertTrue(axis.getType() == Axis.Type.CHANNEL);
    }
    
    /**
     * Test method for {@link net.sci.image.Calibration#getXAxis()}.
     */
    @Test
    public final void testGetXAxis()
    {
        double[] resol = new double[] {2.5, 2.5, 2.8};
        Calibration calib = new Calibration(resol, "�m");
        
        Axis axis = calib.getXAxis();
        assertNotNull(axis);
        assertTrue(axis.getType() == Axis.Type.SPACE);
    }
    
    /**
     * Test method for {@link net.sci.image.Calibration#getYAxis()}.
     */
    @Test
    public final void testGetYAxis()
    {
        double[] resol = new double[] {2.5, 2.5, 2.8};
        Calibration calib = new Calibration(resol, "�m");
        
        Axis axis = calib.getYAxis();
        assertNotNull(axis);
        assertTrue(axis.getType() == Axis.Type.SPACE);
    }
    
    /**
     * Test method for {@link net.sci.image.Calibration#getZAxis()}.
     */
    @Test
    public final void testGetZAxis()
    {
        double[] resol = new double[] {2.5, 2.5, 2.8};
        Calibration calib = new Calibration(resol, "�m");
        
        Axis axis = calib.getZAxis();
        assertNotNull(axis);
        assertTrue(axis.getType() == Axis.Type.SPACE);
    }
    
    /**
     * Test method for {@link net.sci.image.Calibration#getPhysicalSize()}.
     */
    @Test
    public final void testGetPhysicalSize()
    {
        double[] resol = new double[] {2.0, 2.0};
        double[] origin = new double[] {0.0, 0.0};
        Calibration calib = new Calibration(resol, origin, "�m");
        
        int[] dims = new int[] {300, 200};
        double[] sizes = calib.physicalSize(dims);
        
        assertEquals(2, sizes.length);
        assertEquals(600.0, sizes[0], .01);
        assertEquals(400.0, sizes[1], .01);
    }
    
    /**
     * Test method for {@link net.sci.image.Calibration#getPhysicalExtent()}.
     */
    @Test
    public final void testGetPhysicalExtent()
    {
        double[] resol = new double[] {2.0, 2.0};
        double[] origin = new double[] {0.0, 0.0};
        Calibration calib = new Calibration(resol, origin, "�m");
        
        int[] dims = new int[] {300, 200};
        double[][] extents = calib.physicalExtent(dims);
        
        assertEquals(2, extents.length);
        assertEquals(-1.0, extents[0][0], .01);
        assertEquals(599.0, extents[0][1], .01);
        assertEquals(-1.0, extents[1][0], .01);
        assertEquals(399.0, extents[1][1], .01);
        
    }
    
}
