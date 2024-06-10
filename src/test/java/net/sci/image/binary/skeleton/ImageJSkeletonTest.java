/**
 * 
 */
package net.sci.image.binary.skeleton;

import net.sci.array.binary.BinaryArray2D;

/**
 * @author dlegland
 *
 */
public class ImageJSkeletonTest
{

    /**
     * Test method for {@link net.sci.image.binary.skeleton.ImageJSkeleton#process2d(net.sci.array.binary.BinaryArray2D)}.
     */
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
        array.printContent(System.out);
        
        ImageJSkeleton skel = new ImageJSkeleton();
        BinaryArray2D res = skel.process2d(array);
        System.out.println("Output:");
        res.printContent(System.out);
    }

}
