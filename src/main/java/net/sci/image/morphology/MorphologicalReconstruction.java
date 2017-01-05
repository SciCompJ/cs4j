/**
 * 
 */
package net.sci.image.morphology;

import net.sci.array.data.Array2D;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.image.data.Connectivity2D;
import net.sci.image.morphology.reconstruct.MorphologicalReconstruction2DHybrid;
import net.sci.image.morphology.reconstruct.ReconstructionType;

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
	/**
	 * Private constructor to avoid instantiation.
	 */
	private MorphologicalReconstruction()
	{	
	}

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
				ReconstructionType.BY_DILATION, conn);
		return algo.process(marker, mask);
	}

}
