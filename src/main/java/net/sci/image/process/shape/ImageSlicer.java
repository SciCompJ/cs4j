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
    /**
     * Computes the planar z-slice from the specified 3D image and the slice
     * index (in z-direction).
     * 
     * @param image
     *            the input 3D image
     * @param sliceIndex
     *            the index of the slice in the z-direction (0-indexed)
     * @return the corresponding planar slice
     */
    public static final <T> Image slice2d(Image image, int sliceIndex)
    {
        return slice2d(image, 0, 1, new int[]{0, 0, sliceIndex});
    }

    /**
     * Create a 2D slice from a 3D image, specifying the axes of the slice.
     * 
     * @param image
     *            the input 3D image
     * @param dim1
     *            the direction of the 3D image corresponding to first slice
     *            axis
     * @param dim2
     *            the direction of the 3D image corresponding to second slice
     *            axis
     * @param refPos
     *            the position of a 3D point belonging to the slice
     * @return the resulting slice
     */
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
