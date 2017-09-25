/**
 * 
 */
package net.sci.image.process.shape;

import net.sci.array.Array;
import net.sci.image.Image;

/**
 * Encapsulates Array Slicer to also manage image meta data.
 * 
 * @author dlegland
 *
 */
public class ImageSlicer
{
    public static final <T> Image slice2d(Image image, int dim1, int dim2, int[] refPos)
    {
        @SuppressWarnings("unchecked")
        Array<T> array = (Array<T>) image.getData();
        
        // check dimensionality
        int nd = image.getDimension();
        if (dim1 >= nd || dim2 >= nd)
        {
            throw new IllegalArgumentException("slicing dimensions must be lower than input image dimension");
        }

        // check dimensionality
        if (refPos.length < nd)
        {
            throw new IllegalArgumentException("Reference position must have as many dimension as input image");
        }

        // create position pointer for source image
        int[] srcPos = new int[nd];
        System.arraycopy(refPos, 0, srcPos, 0, nd);
        
        // create position pointer for target image
        int[] pos = new int[2];

        // create output
        int sizeX = image.getSize(dim1);
        int sizeY = image.getSize(dim2);
        
        Array<T> resArray = array.newInstance(new int[]{sizeX, sizeY});
        
        // iterate over position in target image
        for (int y = 0; y < sizeY; y++)
        {
            srcPos[dim2] = y;
            pos[1] = y;
            
            for (int x = 0; x < sizeX; x++)
            {
                srcPos[dim1] = x;
                pos[0] = x;
                
                // copy value of selected position
                resArray.set(pos, array.get(srcPos));
            }
        }
     
        Image resultImage = new Image(resArray, image);
        return resultImage;
    }

}
