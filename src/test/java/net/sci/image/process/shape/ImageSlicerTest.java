/**
 * 
 */
package net.sci.image.process.shape;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.data.scalar3d.UInt8Array3D;
import net.sci.image.Image;

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
}
