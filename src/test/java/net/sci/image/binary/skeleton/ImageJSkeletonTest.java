/**
 * 
 */
package net.sci.image.binary.skeleton;

import static org.junit.Assert.*;
import net.sci.array.data.scalar2d.BinaryArray2D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class ImageJSkeletonTest
{

    /**
     * Test method for {@link net.sci.image.binary.skeleton.ImageJSkeleton#process2d(net.sci.array.data.scalar2d.BinaryArray2D)}.
     */
    @Test
    public final void testProcess2d()
    {
        BinaryArray2D array = BinaryArray2D.create(10,  6);
        for (int y = 1; y < 5; y++)
        {
            for (int x = 1; x < 9; x++)
            {
                array.setBoolean(x, y, true);
            }
        }
        System.out.println("Input:");
        array.print(System.out);
        
        ImageJSkeleton skel = new ImageJSkeleton();
        BinaryArray2D res = skel.process2d(array);
        System.out.println("Output:");
        res.print(System.out);
        
        fail("Not yet implemented"); // TODO
    }

}
