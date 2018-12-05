/**
 * 
 */
package net.sci.image;

import static org.junit.Assert.*;

import org.junit.Test;

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
    public final void testGetChannelAxis()
    {
        double[] resol = new double[] {2.5, 2.5, 2.8};
        Calibration calib = new Calibration(resol, "µm");
        
        ImageAxis axis = calib.getChannelAxis();
        assertNotNull(axis);
        assertTrue(axis.getType() == ImageAxis.Type.CHANNEL);
    }
    
    /**
     * Test method for {@link net.sci.image.Calibration#getXAxis()}.
     */
    @Test
    public final void testGetXAxis()
    {
        double[] resol = new double[] {2.5, 2.5, 2.8};
        Calibration calib = new Calibration(resol, "µm");
        
        ImageAxis axis = calib.getXAxis();
        assertNotNull(axis);
        assertTrue(axis.getType() == ImageAxis.Type.SPACE);
    }
    
    /**
     * Test method for {@link net.sci.image.Calibration#getYAxis()}.
     */
    @Test
    public final void testGetYAxis()
    {
        double[] resol = new double[] {2.5, 2.5, 2.8};
        Calibration calib = new Calibration(resol, "µm");
        
        ImageAxis axis = calib.getYAxis();
        assertNotNull(axis);
        assertTrue(axis.getType() == ImageAxis.Type.SPACE);
    }
    
    /**
     * Test method for {@link net.sci.image.Calibration#getZAxis()}.
     */
    @Test
    public final void testGetZAxis()
    {
        double[] resol = new double[] {2.5, 2.5, 2.8};
        Calibration calib = new Calibration(resol, "µm");
        
        ImageAxis axis = calib.getZAxis();
        assertNotNull(axis);
        assertTrue(axis.getType() == ImageAxis.Type.SPACE);
    }
    
}
