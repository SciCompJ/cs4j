/**
 * 
 */
package net.sci.image.morphology;

import net.sci.array.Array;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.Connectivity2D;
import net.sci.image.Connectivity3D;
import net.sci.image.Image;
import net.sci.image.morphology.reconstruction.KillBorders;
import net.sci.image.morphology.reconstruction.MorphologicalReconstruction2DHybrid;
import net.sci.image.morphology.reconstruction.MorphologicalReconstruction3DHybrid;

/**
 * <p>
 * Morphological reconstruction for grayscale or binary arrays. Most algorithms works
 * for any scalar data type.
 * </p>
 * 
 * 
 * @author dlegland
 *
 * @see net.sci.image.morphology.reconstruction.MorphologicalReconstruction2DHybrid 
 * @see net.sci.image.morphology.reconstruction.MorphologicalReconstruction3DHybrid 
 */
public class MorphologicalReconstruction
{
	// ==================================================
	// Static enum

	/**
	 * The type of morphological reconstruction, that can be by dilation or by
	 * erosion.
	 */
	public enum Type 
	{
		BY_DILATION,
		BY_EROSION;
		
		/**
		 * Private constructor for avoiding direct instantiation.
		 */
		private Type()
		{
		}
		
		/**
		 * Returns the sign that can be used in algorithms generic for dilation 
		 * and erosion.
		 * @return +1 for dilation, and -1 for erosion
		 */
		public int getSign() 
		{
			switch (this)
			{
			case BY_DILATION:
				return +1;
			case BY_EROSION:
				return -1;
			default:
				throw new RuntimeException("Unknown case: " + this.toString());
			}
		}
	}
	

	// ==================================================
	// Constructors

	/**
	 * Private constructor to avoid instantiation.
	 */
	private MorphologicalReconstruction()
	{	
	}


	// ==================================================
	// Static methods for Image instance

	/**
	 * Removes the border of the input 2D or 3D image.
	 * 
	 * @param image
	 *            the image to process
	 * @return a new image with borders removed
	 * 
	 * @see #killBorders(ScalarArray2D)
	 * @see #killBorders(ScalarArray3D)
	 */
	public static final Image killBorders(Image image)
	{
		Array<?> array = image.getData();
		Array<?> res;
		if (array instanceof ScalarArray2D)
		{
			res = killBorders((ScalarArray2D<?>) array);
		}
		else if (array instanceof ScalarArray3D)
		{
			res = killBorders((ScalarArray3D<?>) array);
		}
		else
		{
			throw new RuntimeException("Requires an image containing a 2D or 3D scalar array");
		}
		
		return new Image(res, image.getType(), image);
	}

	/**
	 * Removes the border of the input 2D array. The principle is to perform a
	 * morphological reconstruction by dilation initialized with image boundary.
	 * 
	 * 
	 * @see #fillHoles(ScalarArray2D)
	 * 
	 * @param array the image to process
	 * @return a new image with borders removed
	 */
	public static final ScalarArray2D<?> killBorders(ScalarArray2D<?> array) 
	{
        return new KillBorders().processScalar2d(array);
	}

	/**
	 * Removes the border of the input 3D array. The principle is to perform a
	 * morphological reconstruction by dilation initialized with image boundary.
	 * 
	 * @see #fillHoles(ScalarArray3D)
	 * @see #killBorders(ScalarArray2D)
	 * 
	 * @param array
	 *            the image to process
	 * @return a new image with borders removed
	 */
	public static final ScalarArray3D<?> killBorders(ScalarArray3D<?> array) 
	{
		return new KillBorders().processScalar3d(array);
	}


	/**
	 * Fill the holes in the input image.
	 * 
	 * @param image
	 *            the image to process
	 * @return a new image with holes filled
	 * 
	 * @see #fillHoles(ScalarArray2D)
	 * @see #fillHoles(ScalarArray3D)
	 */
	public static final Image fillHoles(Image image)
	{
		Array<?> array = image.getData();
		Array<?> res;
		if (array instanceof ScalarArray2D)
		{
			res = fillHoles((ScalarArray2D<?>) array);
		}
		else if (array instanceof ScalarArray3D)
		{
			res = fillHoles((ScalarArray3D<?>) array);
		}
		else
		{
			throw new RuntimeException("Requires an image containing a 2D or 3D scalar array");
		}
		
        return new Image(res, image.getType(), image);
	}

	/**
	 * Fills the holes in the input array.
	 *
	 * The method consists in creating a marker image corresponding to the full
	 * array without the borders, and performing morphological reconstruction by
	 * erosion.
	 * 
	 * @see #fillHoles(ScalarArray3D)
	 * @see #killBorders(ScalarArray2D)
	 * 
	 * @param array
	 *            the image to process
	 * @return a new image with holes filled
	 */
	public static final ScalarArray2D<?> fillHoles(ScalarArray2D<?> array) 
	{
		// Image size
		int sizeX = array.size(0);
		int sizeY = array.size(1);

		// Initialize marker image with white everywhere except at borders
		ScalarArray2D<?> marker = array.duplicate();
		for (int y = 1; y < sizeY - 1; y++)
		{
			for (int x = 1; x < sizeX - 1; x++)
			{
				marker.setValue(x, y, Double.POSITIVE_INFINITY);
			}
		}
		
		// Reconstruct image from borders to find touching structures
		return reconstructByErosion(marker, array);
	}

	/**
	 * Fills the holes in the input 3D array.
	 *
	 * The method consists in creating a marker image corresponding to the full
	 * array without the borders, and performing morphological reconstruction by
	 * erosion.
	 * 
	 * @see #fillHoles(ScalarArray2D)
	 * @see #killBorders(ScalarArray3D)
	 * 
	 * @param array
	 *            the image to process
	 * @return a new image with holes filled
	 */
	public static final ScalarArray3D<?> fillHoles(ScalarArray3D<?> array) 
	{
		// Image size
		int sizeX = array.size(0);
		int sizeY = array.size(1);
		int sizeZ = array.size(2);

		// Initialize marker image with white everywhere except at borders
		ScalarArray3D<?> marker = array.duplicate();
		for (int z = 1; z < sizeZ - 1; z++)
		{
			for (int y = 1; y < sizeY - 1; y++)
			{
				for (int x = 1; x < sizeX - 1; x++)
				{
					marker.setValue(x, y, z, Double.POSITIVE_INFINITY);
				}
			}
		}
		
		// Reconstruct image from borders to find touching structures
		return reconstructByErosion(marker, array);
	}


	// ==================================================
	// Morphological reconstructions shortcuts

	/**
	 * Static method to computes the morphological reconstruction by dilation of the
	 * marker image constrained by the mask image.
	 *
	 * @param markerImage
	 *            input marker image
	 * @param maskImage
	 *            input mask image
	 * @return the result of morphological reconstruction
	 */
	public final static Image reconstructByDilation(Image markerImage, Image maskImage) 
	{
		Array<?> marker = markerImage.getData();
		Array<?> mask = maskImage.getData();
		
		if (marker.dimensionality() != mask.dimensionality())
		{
			throw new IllegalArgumentException("Both images must have same dimensionality");
		}
		if (!(marker instanceof ScalarArray && mask instanceof ScalarArray))
		{
			throw new IllegalArgumentException("Both images must be instances of SclalarArray");
		}
		
		Array<?> result;
		int nd = marker.dimensionality();
		
		// dispatch processing depending on array dimensionality
		if (nd == 2)
		{
			if (!(marker instanceof ScalarArray2D && mask instanceof ScalarArray2D))
			{
				throw new IllegalArgumentException("Both images must be instances of ScalarArray2D");
			}

			Connectivity2D conn = Connectivity2D.C4;
			result = reconstructByDilation((ScalarArray2D<?>) marker, (ScalarArray2D<?>) mask, conn);			
		}
		else if (nd == 3)
		{
			if (!(marker instanceof ScalarArray3D && mask instanceof ScalarArray3D))
			{
				throw new IllegalArgumentException("Both images must be instances of ScalarArray3D");
			}

			Connectivity3D conn = Connectivity3D.C6;
			result = reconstructByDilation((ScalarArray3D<?>) marker, (ScalarArray3D<?>) mask, conn);			
		}
		else
		{
			throw new RuntimeException("Unable to process arrays with dimension " + nd);
		}

		// Create result image from result array
		Image resultImage = new Image(result, maskImage.getType(), maskImage);
		return resultImage;
	}

	/**
	 * Static method to computes the morphological reconstruction by erosion of the
	 * marker image constrained by the mask image.
	 *
	 * @param markerImage
	 *            input marker image
	 * @param maskImage
	 *            input mask image
	 * @return the result of morphological reconstruction
	 */
	public final static Image reconstructByErosion(Image markerImage, Image maskImage) 
	{
		Array<?> marker = markerImage.getData();
		Array<?> mask = maskImage.getData();
		
		if (marker.dimensionality() != mask.dimensionality())
		{
			throw new IllegalArgumentException("Both images must have same dimensionality");
		}
		if (!(marker instanceof ScalarArray && mask instanceof ScalarArray))
		{
			throw new IllegalArgumentException("Both images must be instances of SclalarArray");
		}
		
		Array<?> result;
		int nd = marker.dimensionality();
		
		// dispatch processing depending on array dimensionality
		if (nd == 2)
		{
			if (!(marker instanceof ScalarArray2D && mask instanceof ScalarArray2D))
			{
				throw new IllegalArgumentException("Both images must be instances of ScalarArray2D");
			}

			Connectivity2D conn = Connectivity2D.C4;
			result = reconstructByErosion((ScalarArray2D<?>) marker, (ScalarArray2D<?>) mask, conn);			
		}
		else if (nd == 3)
		{
			if (!(marker instanceof ScalarArray3D && mask instanceof ScalarArray3D))
			{
				throw new IllegalArgumentException("Both images must be instances of ScalarArray3D");
			}

			Connectivity3D conn = Connectivity3D.C6;
			result = reconstructByErosion((ScalarArray3D<?>) marker, (ScalarArray3D<?>) mask, conn);			
		}
		else
		{
			throw new RuntimeException("Unable to process arrays with dimension " + nd);
		}

		// Create result image from result array
		Image resultImage = new Image(result, maskImage.getType(), maskImage);
		return resultImage;
	}

	// ==================================================
	// Static methods for 2D

	/**
	 * Static method to computes the morphological reconstruction by dilation of the
	 * marker image under the mask image.
	 *
	 * @param marker
	 *            input marker array
	 * @param mask
	 *            input mask array
	 * @return the result of morphological reconstruction
	 */
	public final static ScalarArray2D<?> reconstructByDilation(ScalarArray2D<?> marker, ScalarArray2D<?> mask) 
	{
		return reconstructByDilation(marker, mask, Connectivity2D.C4);
	}

	/**
	 * Static method to computes the morphological reconstruction by dilation of
	 * the marker image under the mask image.
	 *
	 * @param marker
	 *            input marker array
	 * @param mask
	 *            input mask array
	 * @param conn
	 *            the planar connectivity (usually C4 or C8)
	 * @return the result of morphological reconstruction
	 */
	public final static ScalarArray2D<?> reconstructByDilation(ScalarArray2D<?> marker, ScalarArray2D<?> mask, 
			Connectivity2D conn) 
	{
		MorphologicalReconstruction2DHybrid algo = new MorphologicalReconstruction2DHybrid(
				Type.BY_DILATION, conn);
		return algo.process(marker, mask);
	}

	/**
	 * Static method to computes the morphological reconstruction by erosion of the
	 * marker image under the mask image.
	 *
	 * @param marker
	 *            input marker array
	 * @param mask
	 *            input mask array
	 * @return the result of morphological reconstruction
	 */
	public final static ScalarArray2D<?> reconstructByErosion(ScalarArray2D<?> marker, ScalarArray2D<?> mask) 
	{
		return reconstructByErosion(marker, mask, Connectivity2D.C4);
	}

	/**
	 * Static method to computes the morphological reconstruction by erosion of
	 * the marker image under the mask image.
	 *
	 * @param marker
	 *            input marker array
	 * @param mask
	 *            input mask array
	 * @param conn
	 *            the planar connectivity (usually C4 or C8)
	 * @return the result of morphological reconstruction
	 */
	public final static ScalarArray2D<?> reconstructByErosion(ScalarArray2D<?> marker, ScalarArray2D<?> mask, 
			Connectivity2D conn) 
	{
		MorphologicalReconstruction2DHybrid algo = new MorphologicalReconstruction2DHybrid(
				Type.BY_EROSION, conn);
		return algo.process(marker, mask);
	}

	
	// ==================================================
	// Static methods for 3D

	/**
	 * Static method to computes the morphological reconstruction by dilation of the
	 * marker image under the mask image.
	 *
	 * @param marker
	 *            input marker array
	 * @param mask
	 *            input mask array
	 * @return the result of morphological reconstruction
	 */
	public final static ScalarArray3D<?> reconstructByDilation(ScalarArray3D<?> marker, ScalarArray3D<?> mask) 
	{
		return reconstructByDilation(marker, mask, Connectivity3D.C6);
	}

	/**
	 * Static method to computes the morphological reconstruction by dilation of
	 * the marker image under the mask image.
	 *
	 * @param marker
	 *            input marker array
	 * @param mask
	 *            input mask array
	 * @param conn
	 *            the planar connectivity (usually C6 or C26)
	 * @return the result of morphological reconstruction
	 */
	public final static ScalarArray3D<?> reconstructByDilation(ScalarArray3D<?> marker, ScalarArray3D<?> mask, 
			Connectivity3D conn) 
	{
		MorphologicalReconstruction3DHybrid algo = new MorphologicalReconstruction3DHybrid(
				Type.BY_DILATION, conn);
		return algo.process(marker, mask);
	}

	/**
	 * Static method to computes the morphological reconstruction by erosion of the
	 * marker image under the mask image.
	 *
	 * @param marker
	 *            input marker array
	 * @param mask
	 *            input mask array
	 * @return the result of morphological reconstruction
	 */
	public final static ScalarArray3D<?> reconstructByErosion(ScalarArray3D<?> marker, ScalarArray3D<?> mask) 
	{
		return reconstructByErosion(marker, mask, Connectivity3D.C6);
	}

	/**
	 * Static method to computes the morphological reconstruction by erosion of
	 * the marker image under the mask image.
	 *
	 * @param marker
	 *            input marker array
	 * @param mask
	 *            input mask array
	 * @param conn
	 *            the planar connectivity (usually C6 or C26)
	 * @return the result of morphological reconstruction
	 */
	public final static ScalarArray3D<?> reconstructByErosion(ScalarArray3D<?> marker, ScalarArray3D<?> mask, 
			Connectivity3D conn) 
	{
		MorphologicalReconstruction3DHybrid algo = new MorphologicalReconstruction3DHybrid(
				Type.BY_EROSION, conn);
		return algo.process(marker, mask);
	}
}
