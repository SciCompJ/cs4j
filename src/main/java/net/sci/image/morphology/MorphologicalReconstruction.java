/**
 * 
 */
package net.sci.image.morphology;

import net.sci.array.Array;
import net.sci.array.data.ScalarArray;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.data.scalar3d.ScalarArray3D;
import net.sci.image.Image;
import net.sci.image.data.Connectivity2D;
import net.sci.image.data.Connectivity3D;
import net.sci.image.morphology.reconstruct.MorphologicalReconstruction2DHybrid;
import net.sci.image.morphology.reconstruct.MorphologicalReconstruction3DHybrid;

/**
 * <p>
 * Morphological reconstruction for grayscale or binary arrays. Most algorithms works
 * for any data type.
 * </p>
 * 
 * 
 * @author dlegland
 *
 */
public class MorphologicalReconstruction
{
	// ==================================================
	// Static enum

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
	 * Static method to computes the morphological reconstruction by dilation of the
	 * marker image under the mask image.
	 *
	 * @param marker
	 *            input marker array
	 * @param mask
	 *            input mask array
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
		Image resultImage = new Image(result, maskImage);
		return resultImage;
	}

	/**
	 * Static method to computes the morphological reconstruction by erosion of the
	 * marker image over the mask image.
	 *
	 * @param marker
	 *            input marker array
	 * @param mask
	 *            input mask array
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
		Image resultImage = new Image(result, maskImage);
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
