/**
 * 
 */
package net.sci.image.morphology.reconstruction;

import net.sci.array.numeric.ScalarArray2D;

/**
 * <p>
 * Defines the interface for morphological reconstructions algorithms applied to
 * planar scalar arrays.
 * </p>
 * 
 * @author David Legland
 */
public interface MorphologicalReconstruction2D 
{
	/**
	 * Applies morphological reconstruction algorithm to the input marker and
	 * mask arrays.
	 * 
	 * @param marker
	 *            the marker array used to initialize the reconstruction
	 * @param mask
	 *            the mask array used to constrain the reconstruction
	 * @return the morphological reconstruction of marker array constrained by mask
	 *         array
	 */
	public ScalarArray2D<?> process(ScalarArray2D<?> marker, ScalarArray2D<?> mask);
}
