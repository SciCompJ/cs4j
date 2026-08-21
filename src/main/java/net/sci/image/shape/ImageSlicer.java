/**
 * 
 */
package net.sci.image.shape;

import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.Array3D;
import net.sci.array.shape.Slice;
import net.sci.image.Calibration;
import net.sci.image.Image;

/**
 * Encapsulates Array Slice operator to also manage image meta data.
 * 
 * @see net.sci.array.shape.Slice
 * @author dlegland
 */
public class ImageSlicer
{
    /**
     * Computes a view on the planar z-slice from the specified 3D image and the
     * slice index (in z-direction). The input image must contain a 3D array.
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
        Array2D<T> slice = array.slice(sliceIndex);
        
        // convert to an image with same type
        Image resultImage = new Image(slice, image.getType(), image);
        
        // configure calibration
        Calibration calib = resultImage.getCalibration();
        calib.setAxis(0, image.getCalibration().getAxis(0));
        calib.setAxis(1, image.getCalibration().getAxis(1));
        
        return resultImage;
    }

    /**
     * Creates a 2D slice from an image, specifying the two axes of the slice.
     * 
     * @param <T>
     *            the type of the array contained within this image
     * @param image
     *            the input image
     * @param dim1
     *            the direction of the input image corresponding to first slice
     *            axis
     * @param dim2
     *            the direction of the input image corresponding to second slice
     *            axis
     * @param refPos
     *            the n-dimensional position in the original image of a point
     *            belonging to the slice
     * @return the resulting 2D slice image
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
        
        Array<T> resArray = new Slice(new int[] {dim1, dim2}, refPos).process(array);     
        Image resultImage = new Image(resArray, image.getType(), image);
        
        // configure calibration
        Calibration calib = resultImage.getCalibration();
        calib.setAxis(0, image.getCalibration().getAxis(dim1));
        calib.setAxis(1, image.getCalibration().getAxis(dim2));
        
        return resultImage;
    }
}
