/**
 * 
 */
package net.sci.image.binary;

import net.sci.array.Array;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.scalar.Float32Array2D;
import net.sci.array.scalar.IntArray;
import net.sci.array.scalar.IntArray2D;
import net.sci.array.scalar.IntArray3D;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.Image;
import net.sci.image.binary.distmap.ChamferDistanceTransform2DFloat32;
import net.sci.image.binary.distmap.ChamferDistanceTransform2DUInt16;
import net.sci.image.binary.distmap.ChamferDistanceTransform3DFloat32;
import net.sci.image.binary.distmap.ChamferDistanceTransform3DUInt16;
import net.sci.image.binary.distmap.ChamferMask2D;
import net.sci.image.binary.distmap.ChamferMask3D;
import net.sci.image.binary.distmap.DistanceTransform2D;
import net.sci.image.binary.distmap.DistanceTransform3D;
import net.sci.image.binary.geoddist.GeodesicDistanceTransform2D;
import net.sci.image.binary.geoddist.GeodesicDistanceTransform2DFloat32Hybrid;
import net.sci.image.binary.geoddist.GeodesicDistanceTransform2DUInt16Hybrid;
import net.sci.image.binary.geoddist.GeodesicDistanceTransform3D;
import net.sci.image.binary.geoddist.GeodesicDistanceTransform3DFloat32Hybrid;
import net.sci.image.binary.geoddist.GeodesicDistanceTransform3DUInt16Hybrid;
import net.sci.image.data.Connectivity2D;
import net.sci.image.data.Connectivity3D;

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
	 * Image, and computes the maximum label to set up the display range of
	 * the resulting Image.
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
	 * @see net.sci.image.morphology.FloodFill2D
	 * @see net.sci.image.morphology.FloodFill3D
	 */
	public final static Image componentsLabeling(Image image, 
			int conn, int bitDepth)
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

		labelImage = new Image(labels, Image.Type.LABEL, image);

		return labelImage;
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
        FloodFillComponentsLabeling2D algo = new FloodFillComponentsLabeling2D(conn, bitDepth);
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
     * @param conn
     *            the connectivity, either 4 or 8
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
			int conn, int bitDepth) 
	{
		FloodFillComponentsLabeling2D algo = new FloodFillComponentsLabeling2D(conn, bitDepth);
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
        FloodFillComponentsLabeling3D algo = new FloodFillComponentsLabeling3D(conn, bitDepth);
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
	 * @return a new instance of Array3D containing the label of each
	 *         connected component.
	 * @throws RuntimeException
	 *             if the number of labels reaches the maximum number that can
	 *             be represented with this bitDepth
	 *             
	 * @see inra.ijpb.binary.conncomp.ConnectedComponentsLabeling3D     
	 */
	public final static IntArray3D<?> componentsLabeling(BinaryArray3D image,
			int conn, int bitDepth)
	{
		FloodFillComponentsLabeling3D algo = new FloodFillComponentsLabeling3D(conn, bitDepth);
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
	 * @see net.sci.image.binary.distmap.DistanceTransform2D
	 * @see net.sci.image.binary.distmap.DistanceTransform3D
	 */
	public static final Image distanceMap(Image image)
	{
		BinaryArray array = getBinaryArray(image);

		// Dispatch to appropriate function depending on dimension
		Array<?> distMap;
		if (array instanceof BinaryArray2D) 
		{
			// process planar image
			distMap = distanceMap((BinaryArray2D) array);
		} 
		else if (array instanceof BinaryArray3D)
		{
			// process 3D image
			distMap = distanceMap((BinaryArray3D) array);
		}
		else
		{
			throw new RuntimeException("Can not manage binary array of class: " + array.getClass());
		}
		
		return new Image(distMap, image);
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
	public static final ScalarArray2D<?> distanceMap(BinaryArray2D array) 
	{
		return distanceMap(array, new short[]{5, 7, 11}, true);
	}
	
    /**
     * <p>
     * Computes the distance map from a boolean 2D array, by specifying the
     * weights and the normalization option.
     * </p>
     * 
     * <p>
     * Distance is computed for each foreground (white) pixel, as the chamfer
     * distance to the nearest background (black) pixel. Result is given as a
     * new instance of IntArray2D.
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
    public static final ScalarArray2D<?> distanceMap(BinaryArray2D array,
            ChamferMask2D mask, boolean floatingPoint, boolean normalize)
    {
        DistanceTransform2D algo = floatingPoint 
                ? new ChamferDistanceTransform2DFloat32(mask, normalize)
                : new ChamferDistanceTransform2DUInt16(mask, normalize);
        return algo.process2d(array);
    }
    
    /**
	 * <p>
	 * Computes the distance map from a boolean 2D array, by specifying
	 * weights and normalization.
	 * </p>
	 * 
	 * <p>
	 * Distance is computed for each foreground (white) pixel, as the chamfer
	 * distance to the nearest background (black) pixel. Result is given as a
	 * new instance of IntArray2D.
	 * </p>
	 * 
	 * @param array
	 *            the input binary image
	 * @param weights
	 *            an array of chamfer weights, with at least two values
	 * @param normalize
	 *            indicates whether the resulting distance map should be
	 *            normalized (divide distances by the first chamfer weight)
	 * @return the distance map obtained after applying the distance transform
	 */
	public static final IntArray2D<?> distanceMap(BinaryArray2D array,
			short[] weights, boolean normalize)
	{
	    ChamferMask2D mask = ChamferMask2D.fromWeights(weights);
	    ChamferDistanceTransform2DUInt16 algo = new ChamferDistanceTransform2DUInt16(mask, normalize);
	    return algo.process2d(array);
	}

	/**
	 * <p>
	 * Computes the distance map from a boolean array, by specifying
	 * weights and normalization.
	 * </p>
	 * 
	 * <p>
	 * Distance is computed for each foreground (white) pixel, as the chamfer
	 * distance to the nearest background (black) pixel. Result is given in a
	 * new instance of FloatArray2D.
	 * </p>
	 * 
	 * @param array
	 *            the input binary array
	 * @param weights
	 *            an array of chamfer weights, with at least two values
	 * @param normalize
	 *            indicates whether the resulting distance map should be
	 *            normalized (divide distances by the first chamfer weight)
	 * @return the distance map obtained after applying the distance transform
	 */
	public static final Float32Array2D distanceMap(BinaryArray2D array,
			float[] weights, boolean normalize) 
	{
        ChamferMask2D mask = ChamferMask2D.fromWeights(weights);
        ChamferDistanceTransform2DFloat32 algo = new ChamferDistanceTransform2DFloat32(mask, normalize);
        return algo.process2d(array);
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
    public static final ScalarArray3D<?> distanceMap(BinaryArray3D array,
            ChamferMask3D mask, boolean floatingPoint, boolean normalize)
    {
        DistanceTransform3D algo = floatingPoint 
                ? new ChamferDistanceTransform3DFloat32(mask, normalize)
                : new ChamferDistanceTransform3DUInt16(mask, normalize);
        return algo.process3d(array);
    }
    
    /**
	 * Computes the distance map from a boolean 3D array. 
	 * Distance is computed for each foreground (white) pixel, as the 
	 * chamfer distance to the nearest background (black) pixel.
	 * 
	 * @param array
	 *            the input boolean array
	 * @return the distance map obtained after applying the distance transform
	 */
	public static final ScalarArray3D<?> distanceMap(BinaryArray3D array)
	{
        ChamferMask3D mask = ChamferMask3D.BORGEFORS;
        ChamferDistanceTransform3DUInt16 algo = new ChamferDistanceTransform3DUInt16(mask);
        return algo.process3d(array);
	}
	
	/**
	 * Computes the distance map from a boolean 3D array. 
	 * Distance is computed for each foreground (white) pixel, as the 
	 * chamfer distance to the nearest background (black) pixel.
	 * 
	 * @param array
	 *            the input boolean array
	 * @param weights
	 *            an array of chamfer weights, with at least three values
	 * @param normalize
	 *            indicates whether the resulting distance map should be
	 *            normalized (divide distances by the first chamfer weight)
	 * @return the distance map obtained after applying the distance transform
	 */
	public static final ScalarArray3D<?> distanceMap(BinaryArray3D array,
			short[] weights, boolean normalize)
	{
        ChamferMask3D mask = ChamferMask3D.fromWeights(weights);
        ChamferDistanceTransform3DUInt16 algo = new ChamferDistanceTransform3DUInt16(mask, normalize);
        return algo.process3d(array);
	}
	
	/**
	 * Computes the distance map from a boolean 3D array. 
	 * Distance is computed for each foreground (white) pixel, as the 
	 * chamfer distance to the nearest background (black) pixel.
	 * 
	 * @param array
	 *            the input boolean array
	 * @param weights
	 *            an array of chamfer weights, with at least three values
	 * @param normalize
	 *            indicates whether the resulting distance map should be
	 *            normalized (divide distances by the first chamfer weight)
	 * @return the distance map obtained after applying the distance transform
	 */
	public static final ScalarArray3D<?> distanceMap(BinaryArray3D array, 
			float[] weights, boolean normalize)
	{
        ChamferMask3D mask = ChamferMask3D.fromWeights(weights);
        ChamferDistanceTransform3DFloat32 algo = new ChamferDistanceTransform3DFloat32(mask, normalize);
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
        BinaryArray markerArray = getBinaryArray(marker);
        BinaryArray maskArray = getBinaryArray(mask);

        // Dispatch to appropriate function depending on dimension
        Array<?> distMap;
        if (markerArray instanceof BinaryArray2D && maskArray instanceof BinaryArray2D) 
        {
            // process planar image
            distMap = geodesicDistanceMap2d((BinaryArray2D) markerArray, (BinaryArray2D) maskArray);
        } 
        else if (markerArray instanceof BinaryArray3D && maskArray instanceof BinaryArray3D) 
        {
            // process 3D image
            distMap = geodesicDistanceMap3d((BinaryArray3D) markerArray, (BinaryArray3D) maskArray);
        }
        else
        {
            throw new RuntimeException("Can not manage binary array of class: " + markerArray.getClass());
        }
        
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
        GeodesicDistanceTransform2D algo;
        algo = floatingPoint
                ? new GeodesicDistanceTransform2DFloat32Hybrid(chamferMask, normalize)
                : new GeodesicDistanceTransform2DUInt16Hybrid(chamferMask, normalize);
        return algo.process2d(marker, mask);
    }
    
    /**
     * Computes the geodesic distance transform (or geodesic distance map) of a
     * binary image of marker, constrained to a binary mask.
     * Returns the result in a new instance of UInt16Array2D.
     * 
     * @param marker
     *            the binary image of marker
     * @param mask
     *            the binary image of mask
     * @param weights
     *            an array of chamfer weights, with at least two values
     * @param normalize
     *            indicates whether the resulting distance map should be
     *            normalized (divide distances by the first chamfer weight)
     * @return the geodesic distance map in a new ScalarArray
     */
    public static final ScalarArray2D<?> geodesicDistanceMap2d(BinaryArray2D marker,
            BinaryArray2D mask, short[] weights, boolean normalize) 
    {
        GeodesicDistanceTransform2D algo;
        algo = new GeodesicDistanceTransform2DUInt16Hybrid(weights, normalize);
        return algo.process2d(marker, mask);
    }
    
    /**
     * Computes the geodesic distance transform (or geodesic distance map) of a
     * binary image of marker, constrained to a binary mask. 
     * Returns the result in a new instance of Float32Array2D.
     * 
     * @param marker
     *            the binary image of marker
     * @param mask
     *            the binary image of mask
     * @param weights
     *            an array of chamfer weights, with at least two values
     * @param normalize
     *            indicates whether the resulting distance map should be
     *            normalized (divide distances by the first chamfer weight)
     * @return the geodesic distance map in a new ScalarArray2D
     */
    public static final ScalarArray2D<?> geodesicDistanceMap2d(BinaryArray2D marker,
            BinaryArray2D mask, float[] weights, boolean normalize) 
    {
        GeodesicDistanceTransform2D algo;
        algo = new GeodesicDistanceTransform2DFloat32Hybrid(weights, normalize);
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
        return geodesicDistanceMap3d(marker, mask, new float[]{3, 4, 5}, true);
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
        GeodesicDistanceTransform3D algo;
        algo = floatingPoint
                ? new GeodesicDistanceTransform3DFloat32Hybrid(chamferMask, normalize)
                : new GeodesicDistanceTransform3DUInt16Hybrid(chamferMask, normalize);
        return algo.process3d(marker, mask);
    }

    
    /**
     * Computes the geodesic distance transform (or geodesic distance map) of a
     * binary image of marker, constrained to a binary mask. 
     * Returns the result in a new instance of Float32Array2D.
     * 
     * @param marker
     *            the binary image of marker
     * @param mask
     *            the binary image of mask
     * @param weights
     *            an array of chamfer weights, with at least two values
     * @param normalize
     *            indicates whether the resulting distance map should be
     *            normalized (divide distances by the first chamfer weight)
     * @return the geodesic distance map in a new ImageProcessor
     */
    public static final ScalarArray3D<?> geodesicDistanceMap3d(BinaryArray3D marker,
            BinaryArray3D mask, float[] weights, boolean normalize) 
    {
        GeodesicDistanceTransform3D algo;
        algo = new GeodesicDistanceTransform3DFloat32Hybrid(weights, normalize);
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
