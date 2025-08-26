/**
 * 
 */
package net.sci.image;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;

import net.sci.array.color.RGB8;
import net.sci.array.numeric.UInt8Array2D;

/**
 * 
 */
public class ImageTypeTest
{

    /**
     * Test method for {@link net.sci.image.ImageType#createAwtImage(net.sci.image.Image)}.
     */
    @Test
    public final void test_createAwtImage_label()
    {
        UInt8Array2D array = UInt8Array2D.fromIntArray(new int[][] {
            {1, 1, 1, 2, 2, 2},
            {1, 1, 4, 4, 2, 2},
            {5, 5, 4, 4, 9, 9},
            {5, 5, 5, 9, 9, 9},
        });
        Image image = new Image(array, ImageType.LABEL);
        
        BufferedImage res = ImageType.LABEL.createAwtImage(image);
        
        assertEquals(image.getSize(0), res.getWidth());
        assertEquals(image.getSize(1), res.getHeight());
        
        RGB8 rgb00 = RGB8.fromIntCode(res.getRGB(0, 0));
        RGB8 rgb02 = RGB8.fromIntCode(res.getRGB(2, 0));
        assertEquals(rgb00, rgb02);
        
        RGB8 rgb03 = RGB8.fromIntCode(res.getRGB(3, 0));
        RGB8 rgb15 = RGB8.fromIntCode(res.getRGB(5, 1));
        assertEquals(rgb03, rgb15);
        
        RGB8 rgb12 = RGB8.fromIntCode(res.getRGB(2, 1));
        RGB8 rgb23 = RGB8.fromIntCode(res.getRGB(3, 2));
        assertEquals(rgb12, rgb23);
        
        RGB8 rgb20 = RGB8.fromIntCode(res.getRGB(0, 2));
        RGB8 rgb32 = RGB8.fromIntCode(res.getRGB(2, 3));
        assertEquals(rgb20, rgb32);
        
        RGB8 rgb24 = RGB8.fromIntCode(res.getRGB(4, 2));
        RGB8 rgb33 = RGB8.fromIntCode(res.getRGB(3, 3));
        assertEquals(rgb24, rgb33);
    }
}
