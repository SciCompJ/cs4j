/**
 * 
 */
package net.sci.image.process.shape;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array3D;
import net.sci.image.Calibration;
import net.sci.image.Image;
import net.sci.image.NumericalAxis;

/**
 * @author dlegland
 *
 */
public class ImageSlicerTest
{
    
    /**
     * Test method for {@link net.sci.image.process.shape.ImageSlicer#slice2d(net.sci.image.Image, int, int, int[])}.
     */
    @Test
    public final void testSlice2d()
    {
        Image image = new Image(create543Array());

        Image imageXY = ImageSlicer.slice2d(image, 0, 1, new int[]{1, 1, 1});
        assertEquals(5, imageXY.getSize(0));
        assertEquals(4, imageXY.getSize(1));

        Image imageZY = ImageSlicer.slice2d(image, 2, 1, new int[]{1, 1, 1});
        assertEquals(3, imageZY.getSize(0));
        assertEquals(4, imageZY.getSize(1));

        Image imageXZ = ImageSlicer.slice2d(image, 0, 2, new int[]{1, 1, 1});
        assertEquals(5, imageXZ.getSize(0));
        assertEquals(3, imageXZ.getSize(1));
    }

    @Test
    public final void testSlice2d_calibration()
    {
        Image image = new Image(create543Array());
        Calibration calib = image.getCalibration();
        calib.setSpatialCalibration(new double[] {0.3, 0.4, 0.5}, "mm");

        Image imageXY = ImageSlicer.slice2d(image, 0, 1, new int[]{1, 1, 1});
        assertEquals(5, imageXY.getSize(0));
        assertEquals(4, imageXY.getSize(1));
        Calibration calibXY = imageXY.getCalibration();
        assertEquals(0.3, ((NumericalAxis) calibXY.getAxis(0)).getSpacing(), .01);
        assertEquals(0.4, ((NumericalAxis) calibXY.getAxis(1)).getSpacing(), .01);

        Image imageZY = ImageSlicer.slice2d(image, 2, 1, new int[]{1, 1, 1});
        assertEquals(3, imageZY.getSize(0));
        assertEquals(4, imageZY.getSize(1));
        Calibration calibZY = imageZY.getCalibration();
        assertEquals(0.5, ((NumericalAxis) calibZY.getAxis(0)).getSpacing(), .01);
        assertEquals(0.4, ((NumericalAxis) calibZY.getAxis(1)).getSpacing(), .01);

        Image imageXZ = ImageSlicer.slice2d(image, 0, 2, new int[]{1, 1, 1});
        assertEquals(5, imageXZ.getSize(0));
        assertEquals(3, imageXZ.getSize(1));
        Calibration calibXZ = imageXZ.getCalibration();
        assertEquals(0.3, ((NumericalAxis) calibXZ.getAxis(0)).getSpacing(), .01);
        assertEquals(0.5, ((NumericalAxis) calibXZ.getAxis(1)).getSpacing(), .01);
    }

    private UInt8Array3D create543Array()
    {
        UInt8Array3D array = UInt8Array3D.create(5, 4, 3);
        for (int z = 0; z < 3; z++)
        {
            for (int y = 0; y < 4; y++)
            {
                for (int x = 0; x < 5; x++)
                {
                    array.setInt(x, y, z, z * 100 + y * 10 + x);
                }
            }
        }
        return array;
    }

    /**
     * Test method for {@link net.sci.image.process.shape.ImageSlicer#slice2d(net.sci.image.Image, int, int, int[])}.
     */
    @Test
    public final void testSlice2dLabelImage()
    {
        // create  a basic array with some labels on the 10-th slice
        UInt8Array3D array = UInt8Array3D.create(50, 40, 30);
        array.setInt(10, 10, 20, 10);
        array.setInt(20, 10, 20, 20);
        array.setInt(10, 20, 20, 30);
        array.setInt(20, 20, 20, 40);
        Image image = new Image(array, Image.Type.LABEL);

        Image slice = ImageSlicer.slice2d(image, 10);
        assertEquals(50, slice.getSize(0));
        assertEquals(40, slice.getSize(1));
        assertTrue(slice.isLabelImage());

    }

}
