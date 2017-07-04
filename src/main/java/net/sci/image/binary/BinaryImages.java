/**
 * 
 */
package net.sci.image.binary;

import net.sci.array.Array;
import net.sci.array.data.BooleanArray;
import net.sci.array.data.IntArray;
import net.sci.array.data.scalar2d.BooleanArray2D;
import net.sci.array.data.scalar2d.IntArray2D;
import net.sci.array.data.scalar3d.BooleanArray3D;
import net.sci.array.data.scalar3d.IntArray3D;
import net.sci.array.type.Int;
import net.sci.image.Image;

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
	 * the resulting ImagePlus.
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

		if (!image.isBinaryImage())
		{
			throw new IllegalArgumentException("Requires a binary input image");
		}
		
		Array<?> array = image.getData();
		if (!(array instanceof BooleanArray))
		{
			throw new IllegalArgumentException("Requires boolean input array");
		}
		
		// Dispatch processing depending on input image dimensionality
		IntArray<?> labels;
		if (array instanceof BooleanArray2D)
		{
			labels = componentsLabeling((BooleanArray2D) array, conn, bitDepth);
		}
		else if (array instanceof BooleanArray3D)
		{
			labels = componentsLabeling((BooleanArray3D) array, conn, bitDepth);
		} 
		else
		{
			throw new RuntimeException("Can not manage binary array of class: " + array.getClass());
		}

		labelImage = new Image(labels, Image.Type.LABEL);

		// setup display range to show largest label as white
		int nLabels = findMaxLabel(labels);
		labelImage.setDisplayRange(new double[]{0, nLabels});

		return labelImage;
	}

	/**
	 * Computes maximum value in the input array of integers.
	 * 
	 * This method is used to compute display range of result Image.
	 */
	private final static int findMaxLabel(IntArray<?> array) 
	{
		// find maximum value over array elements
		int maxVal = 0;
		for (Int i : array)
		{
			maxVal = Math.max(maxVal, i.getInt());
		}
		
		return maxVal;
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
	 * @return a new instance of ImageProcessor containing the label of each
	 *         connected component.
	 * @throws RuntimeException
	 *             if the number of labels reaches the maximum number that can
	 *             be represented with this bitDepth
	 *             
	 * @see FloodFillConnectedComponentsLabeling2D     
	 */
	public final static IntArray2D<?> componentsLabeling(BooleanArray2D array,
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
	 * @return a new instance of ImageStack containing the label of each
	 *         connected component.
	 * @throws RuntimeException
	 *             if the number of labels reaches the maximum number that can
	 *             be represented with this bitDepth
	 *             
	 * @see inra.ijpb.binary.conncomp.ConnectedComponentsLabeling3D     
	 */
	public final static IntArray3D<?> componentsLabeling(BooleanArray3D image,
			int conn, int bitDepth)
	{
		FloodFillComponentsLabeling3D algo = new FloodFillComponentsLabeling3D(conn, bitDepth);
		return algo.process(image);
	}	
}
