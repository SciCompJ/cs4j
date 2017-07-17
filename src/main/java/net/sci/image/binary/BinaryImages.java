/**
 * 
 */
package net.sci.image.binary;

import net.sci.array.Array;
import net.sci.array.data.BinaryArray;
import net.sci.array.data.IntArray;
import net.sci.array.data.scalar2d.BinaryArray2D;
import net.sci.array.data.scalar2d.Float32Array2D;
import net.sci.array.data.scalar2d.IntArray2D;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.data.scalar3d.BinaryArray3D;
import net.sci.array.data.scalar3d.IntArray3D;
import net.sci.array.data.scalar3d.ScalarArray3D;
import net.sci.image.Image;
import net.sci.image.binary.distmap.ChamferDistanceTransform2DFloat;
import net.sci.image.binary.distmap.ChamferDistanceTransform2DUInt16;
import net.sci.image.binary.distmap.DistanceTransform3D;
import net.sci.image.binary.distmap.ChamferDistanceTransform3DFloat;
import net.sci.image.binary.distmap.ChamferDistanceTransform3DUInt16;

/**
 * A collection of static methods for operating on binary images (2D/3D).
 * 
 * @author dlegland
 *
 */
public class BinaryImages
{
	// ==============================================================
	// Constructors
	
	/**
	 * Private constructor to prevent instantiation
	 */
	private BinaryImages()
	{
	}

	
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

		BinaryArray array = getBooleanArray(image);
		
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
	 *            the connectivity, either 4 or 8
	 * @param bitDepth
	 *            the number of bits used to create the result image (8, 16 or
	 *            32)
	 * @return a new instance of Array2D containing the label of each
	 *         connected component.
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
		return algo.process(array);
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
		return algo.process(image);
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
		BinaryArray array = getBooleanArray(image);

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
		ChamferDistanceTransform2DUInt16 algo = new ChamferDistanceTransform2DUInt16(weights, normalize);
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
		ChamferDistanceTransform2DFloat algo = new ChamferDistanceTransform2DFloat(weights, normalize);
		return algo.process2d(array);
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
		float[] weights = new float[]{3.0f, 4.0f, 5.0f};
		DistanceTransform3D algo = new ChamferDistanceTransform3DFloat(weights);
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
		DistanceTransform3D algo = new ChamferDistanceTransform3DUInt16(weights, normalize);
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
		DistanceTransform3D algo = new ChamferDistanceTransform3DFloat(weights, normalize);
		return algo.process3d(array);
	}
	
	/**
	 * Checks that the image is binary, and returns the inner boolean array.
	 * 
	 * @param image
	 *            the input image
	 * @return the boolean array contained in image
	 * @throws IllegalArgumentException
	 *             if input image is not a binary image
	 */
	private static final BinaryArray getBooleanArray(Image image)
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
}
