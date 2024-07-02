/**
 * 
 */
package net.sci.image.morphology.reconstruct;

import net.sci.array.numeric.ScalarArray3D;

/**
 * <p>
 * Defines the interface for morphological reconstructions algorithms applied to
 * 3D scalar arrays.
 * </p>
 * 
 * @author David Legland
 */
public interface MorphologicalReconstruction3D 
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
	public ScalarArray3D<?> process(ScalarArray3D<?> marker, ScalarArray3D<?> mask);
}
