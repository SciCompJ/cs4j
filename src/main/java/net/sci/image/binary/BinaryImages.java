/**
 * 
 */
package net.sci.image.binary;

import net.sci.array.Array;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray1D;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray1D;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.IntArray3D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.Image;
import net.sci.image.ImageType;
import net.sci.image.binary.distmap.ChamferMask2D;
import net.sci.image.binary.distmap.ChamferMask3D;
import net.sci.image.binary.distmap.DistanceTransform2D;
import net.sci.image.binary.distmap.DistanceTransform3D;
import net.sci.image.binary.geoddist.GeodesicDistanceTransform2D;
import net.sci.image.binary.geoddist.GeodesicDistanceTransform3D;
import net.sci.image.binary.labeling.ComponentsLabeling;
import net.sci.image.binary.labeling.FloodFillComponentsLabeling1D;
import net.sci.image.binary.labeling.FloodFillComponentsLabeling2D;
import net.sci.image.binary.labeling.FloodFillComponentsLabeling3D;
import net.sci.image.connectivity.Connectivity2D;
import net.sci.image.connectivity.Connectivity3D;

/**
 * A collection of static methods for operating on binary images (2D/3D).
 * 
 * @author dlegland
 *
 */
public class BinaryImages
{
    // ==============================================================
    // Connected Components Labeling

    /**
     * Computes the labels in the binary 2D or 3D image contained in the given
     * Image, and computes the maximum label to set up the display range of the
     * resulting Image.
     * 
     * @param image
     *            an instance of image that contains a 3D binary array
     * @param conn
     *            the connectivity, either 4 or 8 for planar images, or 6 or 26
     *            for 3D images
     * @param bitDepth
     *            the number of bits used to create the result image (8, 16 or
     *            32)
     * @return an Image containing the label of each connected component.
     * @throws RuntimeException
     *             if the number of labels reaches the maximum number that can
     *             be represented with this bitDepth
     * 
     * @see FloodFillComponentsLabeling2D
     * @see FloodFillComponentsLabeling3D
     * @see net.sci.image.morphology.FloodFill
     */
    public final static Image componentsLabeling(Image image, int conn, int bitDepth)
    {
        Image labelImage;

        BinaryArray array = getBinaryArray(image);

        // Dispatch processing depending on input image dimensionality
        IntArray<?> labels;
        if (array instanceof BinaryArray2D)
        {
            labels = componentsLabeling((BinaryArray2D) array, conn, bitDepth);
        }
        else if (array instanceof BinaryArray3D)
        {
            labels = componentsLabeling((BinaryArray3D) array, conn, bitDepth);
        }
        else
        {
            throw new RuntimeException("Can not manage binary array of class: " + array.getClass());
        }

        labelImage = new Image(labels, ImageType.LABEL, image);

        return labelImage;
    }

    /**
     * Computes the labels of the connected components in the given 1D binary
     * image. The type of result is controlled by the bitDepth option.
     * 
     * Uses a Flood-fill type algorithm.
     * 
     * @param array
     *            contains the binary data
     * @param bitDepth
     *            the number of bits used to create the result image (8, 16 or
     *            32)
     * @return a new instance of Array1D containing the label of each connected
     *         component.
     * @throws RuntimeException
     *             if the number of labels reaches the maximum number that can
     *             be represented with this bitDepth
     * 
     * @see FloodFillConnectedComponentsLabeling1D
     */
    public final static IntArray1D<?> componentsLabeling(BinaryArray1D array, int bitDepth)
    {
        IntArray.Factory<?> factory = ComponentsLabeling.chooseIntArrayFactory(bitDepth);
        FloodFillComponentsLabeling1D algo = new FloodFillComponentsLabeling1D(factory);
        return algo.processBinary1d(array);
    }
    
    /**
     * Computes the labels of the connected components in the given planar
     * binary image. The type of result is controlled by the bitDepth option.
     * 
     * Uses a Flood-fill type algorithm.
     * 
     * @param array
     *            contains the binary data
     * @param conn
     *            the connectivity, either C4 or C8
     * @param bitDepth
     *            the number of bits used to create the result image (8, 16 or 32)
     * @return a new instance of Array2D containing the label of each connected
     *         component.
     * @throws RuntimeException
     *             if the number of labels reaches the maximum number that can
     *             be represented with this bitDepth
     * 
     * @see FloodFillConnectedComponentsLabeling2D
     */
    public final static IntArray2D<?> componentsLabeling(BinaryArray2D array,
            Connectivity2D conn, int bitDepth) 
    {
        IntArray.Factory<?> factory = ComponentsLabeling.chooseIntArrayFactory(bitDepth);
        FloodFillComponentsLabeling2D algo = new FloodFillComponentsLabeling2D(conn, factory);
        return algo.processBinary2d(array);
    }

	/**
     * Computes the labels of the connected components in the given planar
     * binary image. The type of result is controlled by the bitDepth option.
     * 
     * Uses a Flood-fill type algorithm.
     * 
     * @param array
     *            contains the binary data
     * @param connValue
     *            the connectivity value, either 4 or 8
     * @param bitDepth
     *            the number of bits used to create the result image (8, 16 or 32)
     * @return a new instance of Array2D containing the label of each connected
     *         component.
     * @throws RuntimeException
     *             if the number of labels reaches the maximum number that can
     *             be represented with this bitDepth
     * 
     * @see FloodFillConnectedComponentsLabeling2D
     */
	public final static IntArray2D<?> componentsLabeling(BinaryArray2D array,
			int connValue, int bitDepth) 
	{
	    Connectivity2D conn = Connectivity2D.fromValue(connValue);
        IntArray.Factory<?> factory = ComponentsLabeling.chooseIntArrayFactory(bitDepth);
		FloodFillComponentsLabeling2D algo = new FloodFillComponentsLabeling2D(conn, factory);
		return algo.processBinary2d(array);
	}

    /**
     * Computes the labels of the connected components in the given 3D binary
     * image. The type of result is controlled by the bitDepth option.
     * 
     * Uses a Flood-fill type algorithm.
     * 
     * @param image
     *            contains the 3D binary image
     * @param conn
     *            the connectivity, either C6 or C26
     * @param bitDepth
     *            the number of bits used to create the result stack (8, 16 or
     *            32)
     * @return a new instance of Array3D containing the label of each
     *         connected component.
     * @throws RuntimeException
     *             if the number of labels reaches the maximum number that can
     *             be represented with this bitDepth
     *             
     * @see inra.ijpb.binary.conncomp.ConnectedComponentsLabeling3D     
     */
    public final static IntArray3D<?> componentsLabeling(BinaryArray3D image,
            Connectivity3D conn, int bitDepth)
    {
        IntArray.Factory<?> factory = ComponentsLabeling.chooseIntArrayFactory(bitDepth);
        FloodFillComponentsLabeling3D algo = new FloodFillComponentsLabeling3D(conn, factory);
        return algo.processBinary3d(image);
    }

    /**
     * Computes the labels of the connected components in the given 3D binary
     * image. The type of result is controlled by the bitDepth option.
     * 
     * Uses a Flood-fill type algorithm.
     * 
     * @param image
     *            contains the 3D binary image
     * @param conn
     *            the connectivity, either 6 or 26
     * @param bitDepth
     *            the number of bits used to create the result stack (8, 16 or
     *            32)
     * @return a new instance of Array3D containing the label of each connected
     *         component.
     * @throws RuntimeException
     *             if the number of labels reaches the maximum number that can
     *             be represented with this bitDepth
     * 
     * @see FloodFillConnectedComponentsLabeling3D
     */
    public final static IntArray3D<?> componentsLabeling(BinaryArray3D image, int connValue,
            int bitDepth)
    {
        Connectivity3D conn = Connectivity3D.fromValue(connValue);
        IntArray.Factory<?> factory = ComponentsLabeling.chooseIntArrayFactory(bitDepth);
        FloodFillComponentsLabeling3D algo = new FloodFillComponentsLabeling3D(conn, factory);
        return algo.processBinary3d(image);
    }
    

    // ==============================================================
    // Distance maps

    /**
     * Computes the distance map (or distance transform) from a boolean array.
     * Distance is computed for each foreground (white) pixel or voxel, as the
     * chamfer distance to the nearest background (black) pixel or voxel.
     * 
     * @param image
     *            an Image object containing a binary array
     * @return a new Image containing the distance map
     * 
     * @see net.sci.image.binary.BinaryImages#distanceMap2d(BinaryArray2D)
     * @see net.sci.image.binary.BinaryImages#distanceMap3d(BinaryArray3D)
     */
    public static final Image distanceMap(Image image)
    {
        // computes distance map on inner binary array
        BinaryArray array = getBinaryArray(image);
        Array<?> distMap = distanceMap(array);

        // create new image by keeping calibration
        return new Image(distMap, image);
    }

    /**
     * <p>
     * Computes the distance map (or distance transform) from a binary array.
     * Distance is computed for each foreground (white) pixel, as the chamfer
     * distance to the nearest background (black) pixel.
     * </p>
     * 
     * @param array
     *            the input boolean array
     * @return a new Array containing the distance map result
     */
    public static final ScalarArray<?> distanceMap(BinaryArray array)
    {
        // Dispatch to appropriate method depending on dimension
        if (array instanceof BinaryArray2D)
        {
            // process planar image
            return distanceMap2d((BinaryArray2D) array);
        }
        else if (array instanceof BinaryArray3D)
        {
            // process 3D image
            return distanceMap3d((BinaryArray3D) array);
        }
        else
        {
            throw new RuntimeException("Can not manage binary array of class: " + array.getClass());
        }
    }
    
    /**
     * <p>
     * Computes the distance map (or distance transform) from a boolean 2D
     * array. Distance is computed for each foreground (white) pixel, as the
     * chamfer distance to the nearest background (black) pixel.
     * </p>
     * 
     * <p>
     * This method uses default 5x5 chamfer weights, and normalizes the
     * resulting map. Result is given in a new instance of IntArray2D.
     * </p>
     * 
     * @param array
     *            the input boolean array
     * @return a new Array2D containing the distance map result
     */
    public static final ScalarArray2D<?> distanceMap2d(BinaryArray2D array)
    {
        return distanceMap2d(array, ChamferMask2D.CHESSKNIGHT, false, true);
    }

    /**
     * <p>
     * Computes the distance map from a boolean 2D array, by specifying the
     * weights and the normalization option.
     * </p>
     * 
     * <p>
     * Distance is computed for each foreground (white) pixel, as the chamfer
     * distance to the nearest background (black) pixel.  
     * </p>
     * 
     * @param array
     *            the input binary image
     * @param mask
     *            the chamfer mask used to propagate distances
     * @param floatingPoint
     *            indicates if the computation should be performed using
     *            floating point computation
     * @param normalize
     *            indicates whether the resulting distance map should be
     *            normalized (divide distances by the first chamfer weight)
     * @return the distance map obtained after applying the distance transform
     */
    public static final ScalarArray2D<?> distanceMap2d(BinaryArray2D array,
            ChamferMask2D mask, boolean floatingPoint, boolean normalize)
    {
        DistanceTransform2D algo = DistanceTransform2D.create(mask, floatingPoint, normalize);
        return algo.process2d(array);
    }


    /**
     * Computes the distance map from a boolean 3D array. Distance is computed
     * for each foreground (white) pixel, as the chamfer distance to the nearest
     * background (black) pixel.
     * 
     * @param array
     *            the input boolean array
     * @return the distance map obtained after applying the distance transform
     */
    public static final ScalarArray3D<?> distanceMap3d(BinaryArray3D array)
    {
        DistanceTransform3D algo = DistanceTransform3D.create(ChamferMask3D.BORGEFORS, false, true);
        return algo.process3d(array);
    }

    /**
     * <p>
     * Computes the distance map from a boolean 3D array, by specifying the
     * chamfer mask and the normalization option.
     * </p>
     * 
     * <p>
     * Distance is computed for each foreground (white) voxel, as the chamfer
     * distance to the nearest background (black) voxel.
     * </p>
     * 
     * @param array
     *            the input binary image
     * @param mask
     *            the chamfer mask used to propagate distances
     * @param floatingPoint
     *            indicates if the computation should be performed using
     *            floating point computation
     * @param normalize
     *            indicates whether the resulting distance map should be
     *            normalized (divide distances by the first chamfer weight)
     * @return the distance map obtained after applying the distance transform
     */
    public static final ScalarArray3D<?> distanceMap3d(BinaryArray3D array,
            ChamferMask3D mask, boolean floatingPoint, boolean normalize)
    {
        DistanceTransform3D algo = DistanceTransform3D.create(mask, floatingPoint, normalize);
        return algo.process3d(array);
    }


    // ==============================================================
    // Geodesic Distance maps

    /**
     * Computes the geodesic distance transform (or geodesic distance map) of a
     * binary image of marker, constrained to a binary mask.
     * 
     * Returns the result in a new Image.
     * 
     * @param marker
     *            the binary image of marker
     * @param mask
     *            the binary image of mask
     * @return the geodesic distance map in a new intensity Image
     */
    public static final Image geodesicDistanceMap(Image marker, Image mask)
    {
        // retrieve array to call more specific method
        BinaryArray markerArray = getBinaryArray(marker);
        BinaryArray maskArray = getBinaryArray(mask);
        Array<?> distMap = geodesicDistanceMap(markerArray, maskArray);
        
        // create result image by keeping spatial calibration
        return new Image(distMap, mask);
    }
    
    /**
     * Computes the geodesic distance transform (or geodesic distance map) of a
     * binary image of marker, constrained to a binary mask.
     * 
     * Returns the result in a new instance of ScalarArray.
     * 
     * @param marker
     *            the binary image of marker
     * @param mask
     *            the binary image of mask
     * @return the geodesic distance map in a new intensity Image
     */
    public static final ScalarArray<?> geodesicDistanceMap(BinaryArray marker, BinaryArray mask)
    {
        // Dispatch to appropriate function depending on dimension
        ScalarArray<?> distMap;
        if (marker instanceof BinaryArray2D && mask instanceof BinaryArray2D) 
        {
            // process 2D image
            distMap = geodesicDistanceMap2d((BinaryArray2D) marker, (BinaryArray2D) mask);
        } 
        else if (marker instanceof BinaryArray3D && mask instanceof BinaryArray3D) 
        {
            // process 3D image
            distMap = geodesicDistanceMap3d((BinaryArray3D) marker, (BinaryArray3D) mask);
        }
        else
        {
            throw new RuntimeException("Can not manage binary array of class: " + marker.getClass());
        }
        
        return distMap;
    }
    


	/**
     * Computes the geodesic distance transform (or geodesic distance map) of a
     * binary image of marker, constrained to a binary mask.
     * 
     * Returns the result in a new instance of ScalarArray.
     * 
     * @param marker
     *            the binary image of marker
     * @param mask
     *            the binary image of mask
     * @return the geodesic distance map in a new ScalarArray
     */
    public static final ScalarArray<?> geodesicDistanceMap2d(BinaryArray2D marker, BinaryArray2D mask) 
    {
        return geodesicDistanceMap2d(marker, mask, ChamferMask2D.CHESSKNIGHT, false, true);
    }
    
    /**
     * Computes the geodesic distance transform (or geodesic distance map) of a
     * binary image of marker, constrained to a binary mask. Returns the result
     * in a new instance of Float32Array2D.
     * 
     * @param marker
     *            the binary image of marker
     * @param mask
     *            the binary image of mask
     * @param chamferMask
     *            the 2D chamfer mask to use for propagating distances
     * @param floatingPoint
     *            boolean flag indicating whether result should be provided as
     *            <code>Float32</code> (if true) or as <code>UInt16</code> (if
     *            false).
     * @param normalize
     *            indicates whether the resulting distance map should be
     *            normalized (divide distances by the first chamfer weight)
     * @return the geodesic distance map in a new ScalarArray2D
     */
    public static final ScalarArray2D<?> geodesicDistanceMap2d(BinaryArray2D marker,
            BinaryArray2D mask, ChamferMask2D chamferMask, boolean floatingPoint, boolean normalize) 
    {
        GeodesicDistanceTransform2D algo = GeodesicDistanceTransform2D.create(chamferMask, floatingPoint, normalize);
        return algo.process2d(marker, mask);
    }
    
    /**
     * Computes the geodesic distance transform (or geodesic distance map) of a
     * binary image of marker, constrained to a binary mask.
     * Returns the result in a new instance of Float32Array3D
     * 
     * @param marker
     *            the binary image of marker
     * @param mask
     *            the binary image of mask
     * @return the geodesic distance map in a new ScalarArray
     */
    public static final ScalarArray3D<?> geodesicDistanceMap3d(BinaryArray3D marker, BinaryArray3D mask) 
    {
        return geodesicDistanceMap3d(marker, mask, ChamferMask3D.BORGEFORS, true, true);
    }
    
    /**
     * Computes the geodesic distance transform (or geodesic distance map) of a
     * binary image of marker, constrained to a binary mask. Returns the result
     * in a new instance of Float32Array3D.
     * 
     * @param marker
     *            the binary image of marker
     * @param mask
     *            the binary image of mask
     * @param chamferMask
     *            the 3D chamfer mask to use for propagating distances
     * @param floatingPoint
     *            boolean flag indicating whether result should be provided as
     *            <code>Float32</code> (if true) or as <code>UInt16</code> (if
     *            false).
     * @param normalize
     *            indicates whether the resulting distance map should be
     *            normalized (divide distances by the first chamfer weight)
     * @return the geodesic distance map in a new ScalarArray3D
     */
    public static final ScalarArray3D<?> geodesicDistanceMap3d(BinaryArray3D marker,
            BinaryArray3D mask, ChamferMask3D chamferMask, boolean floatingPoint, boolean normalize) 
    {
        GeodesicDistanceTransform3D algo = GeodesicDistanceTransform3D.create(chamferMask, floatingPoint, normalize);
        return algo.process3d(marker, mask);
    }

    
    // ==============================================================
    // Utility methods

    /**
     * Checks that the image is binary, and returns the inner boolean array.
     * 
     * @param image
     *            the input image
     * @return the boolean array contained in image
     * @throws IllegalArgumentException
     *             if input image is not a binary image
     */
    private static final BinaryArray getBinaryArray(Image image)
    {
        if (!image.isBinaryImage())
        {
            throw new IllegalArgumentException("Requires a binary input image");
        }

        Array<?> array = image.getData();
        if (!(array instanceof BinaryArray))
        {
            throw new IllegalArgumentException("Requires boolean input array");
        }

        return (BinaryArray) array;
    }

    // ==============================================================
    // Constructors

    /**
     * Private constructor to prevent instantiation.
     */
    private BinaryImages()
    {
    }    
}
