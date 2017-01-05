/**
 * 
 */
package net.sci.image.morphology;

import net.sci.array.data.Array2D;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.image.data.Connectivity2D;
import net.sci.image.morphology.reconstruct.MorphologicalReconstruction2DHybrid;

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
	public final static Array2D<?> reconstructByDilation(ScalarArray2D<?> marker, ScalarArray2D<?> mask) 
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
	public final static Array2D<?> reconstructByDilation(ScalarArray2D<?> marker, ScalarArray2D<?> mask, 
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
	public final static Array2D<?> reconstructByErosion(ScalarArray2D<?> marker, ScalarArray2D<?> mask) 
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
	public final static Array2D<?> reconstructByErosion(ScalarArray2D<?> marker, ScalarArray2D<?> mask, 
			Connectivity2D conn) 
	{
		MorphologicalReconstruction2DHybrid algo = new MorphologicalReconstruction2DHybrid(
				Type.BY_EROSION, conn);
		return algo.process(marker, mask);
	}
}
