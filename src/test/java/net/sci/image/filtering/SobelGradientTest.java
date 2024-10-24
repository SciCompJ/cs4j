/**
 * 
 */
package net.sci.image.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Float32Array3D;
import net.sci.array.numeric.Float32VectorArray2D;
import net.sci.array.numeric.Float32VectorArray3D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.VectorArray;
import net.sci.image.Image;

/**
 * @author dlegland
 *
 */
public class SobelGradientTest
{

    /**
     * Test method for {@link net.sci.image.filtering.SobelGradient#processScalar2d(net.sci.array.scalar.ScalarArray2D, net.sci.array.vector.VectorArray2D)}.
     */
    @Test
    public final void testProcessScalar2d()
    {
        Float32Array2D array = Float32Array2D.create(10, 10);
        for (int y = 3; y < 7; y++)
        {
            for (int x = 3; x < 7; x++)
            {
                array.setValue(x, y, 100.0);
            }
        }
        Float32VectorArray2D grad = Float32VectorArray2D.create(10, 10, 2);
        
        SobelGradient algo = new SobelGradient();
        algo.processScalar2d(array, grad);
        
//        System.out.println("Gradient X:");
//        grad.channel(0).print(System.out);
//        System.out.println("Gradient Y:");
//        grad.channel(1).print(System.out);
        assertEquals( 50.0, grad.getValue(3, 5, 0), .1);
        assertEquals(-50.0, grad.getValue(7, 5, 0), .1);
        assertEquals( 50.0, grad.getValue(5, 3, 1), .1);
        assertEquals(-50.0, grad.getValue(5, 7, 1), .1);
    }

    /**
     * Test method for {@link net.sci.image.filtering.SobelGradient#processScalar3d(net.sci.array.scalar.ScalarArray3D, net.sci.array.vector.VectorArray3D)}.
     */
    @Test
    public final void testProcessScalar3d()
    {
        Float32Array3D array = Float32Array3D.create(10, 10, 10);
        for (int z = 3; z < 7; z++)
        {
            for (int y = 3; y < 7; y++)
            {
                for (int x = 3; x < 7; x++)
                {
                    array.setValue(x, y, z, 100.0);
                }
            }
        }
        Float32VectorArray3D grad = Float32VectorArray3D.create(10, 10, 10, 3);
        
        SobelGradient algo = new SobelGradient();
        algo.processScalar3d(array, grad);
        
        assertEquals( 50.0, grad.getValue(3, 5, 5, 0), .1);
        assertEquals(-50.0, grad.getValue(7, 5, 5, 0), .1);
        assertEquals( 50.0, grad.getValue(5, 3, 5, 1), .1);
        assertEquals(-50.0, grad.getValue(5, 7, 5, 1), .1);
        assertEquals( 50.0, grad.getValue(5, 5, 3, 2), .1);
        assertEquals(-50.0, grad.getValue(5, 5, 7, 2), .1);
    }

    /**
     * Test method for {@link net.sci.image.filtering.SobelGradient#process(net.sci.image.Image)}.
     * @throws IOException 
     */
    @Test
    public final void testProcessImage() throws IOException
    {
        String fileName = getClass().getResource("/images/grains.tif").getFile();
        Image image = Image.readImage(new File(fileName));
        
        assertNotNull(image);
        
        SobelGradient algo = new SobelGradient();
        Image gradImage = algo.process(image);
        
        VectorArray<?,?> vectorArray = (VectorArray<?,?>) gradImage.getData();
        ScalarArray<?> channelArray = vectorArray.channel(0);
        
        double[] range = channelArray.finiteValueRange();
        assertTrue(range[0] < 0);
    }
}
