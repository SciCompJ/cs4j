/**
 * 
 */
package net.sci.image.shape;

import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.Array3D;
import net.sci.image.Calibration;
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
     *  
     * @param image
     *            the input 3D image
     * @param sliceIndex
     *            the index of the slice in the z-direction (0-indexed)
     * @return the corresponding planar slice
     */
    public static final <T> Image slice2d(Image image, int sliceIndex)
    {
        // Cast to 3D array
        @SuppressWarnings("unchecked")
        Array3D<T> array = Array3D.wrap((Array<T>) image.getData());
        
        // extract slice (duplicate it to enforce an array with correct type)
        Array2D<T> slice = array.slice(sliceIndex).duplicate();
//        Array2D<T> slice = array.slice(sliceIndex); // TODO: should be able to avoid duplication
        
        // convert to an image with same type
        Image resultImage = new Image(slice, image.getType(), image);
        
        // configure calibration
        Calibration calib = resultImage.getCalibration();
        calib.setAxis(0, image.getCalibration().getAxis(0));
        calib.setAxis(1, image.getCalibration().getAxis(1));
        
        return resultImage;
    }

    /**
     * Create a 2D slice from a 3D image, specifying the axes of the slice.
     * 
     * @param <T>
     *            the type of the array contained within this image
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
        
        Array2D<T> resArray;
        if (array instanceof Array3D && dim1 == 0 && dim2 == 1)
        {
            // special case of a 3D array sliced with dimensions X and Y
            resArray = ((Array3D<T>) array).slice(refPos[2]);
        }
        else
        {
            // create position pointer for source image
            int[] srcPos = new int[nd];
            System.arraycopy(refPos, 0, srcPos, 0, nd);

            // create output
            int sizeX = image.getSize(dim1);
            int sizeY = image.getSize(dim2);

            // allocate memory for result
            resArray = Array2D.wrap(array.newInstance(new int[]{sizeX, sizeY}));

            // iterate over position in target image
            for (int y = 0; y < sizeY; y++)
            {
                srcPos[dim2] = y;

                for (int x = 0; x < sizeX; x++)
                {
                    srcPos[dim1] = x;

                    // copy value of selected position
                    resArray.set(x, y, array.get(srcPos));
                }
            }
        }
     
        Image resultImage = new Image(resArray, image.getType(), image);
        
        // configure calibration
        Calibration calib = resultImage.getCalibration();
        calib.setAxis(0, image.getCalibration().getAxis(dim1));
        calib.setAxis(1, image.getCalibration().getAxis(dim2));
        
        return resultImage;
    }

}
