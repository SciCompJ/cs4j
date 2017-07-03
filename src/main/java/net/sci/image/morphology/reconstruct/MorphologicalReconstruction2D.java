/**
 * 
 */
package net.sci.image.morphology.reconstruct;

import net.sci.array.data.scalar2d.ScalarArray2D;

/**
 * <p>
 * Defines the interface for morphological reconstructions algorithms applied to
 * planar arrays.
 * </p>
 * 
 * @author David Legland
 */
public interface MorphologicalReconstruction2D 
{
	/**
	 * Applies morphological reconstruction algorithm to the input marker and
	 * mask images.
	 * 
	 * @param marker
	 *            image used to initialize the reconstruction
	 * @param mask
	 *            image used to constrain the reconstruction
	 * @return the geodesic reconstruction of marker image constrained by mask
	 *         image
	 */
	public ScalarArray2D<?> process(ScalarArray2D<?> marker, ScalarArray2D<?> mask);
}
