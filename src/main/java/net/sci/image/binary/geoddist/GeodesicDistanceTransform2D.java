/**
 * 
 */
package net.sci.image.binary.geoddist;

import net.sci.algo.Algo;
import net.sci.array.data.scalar2d.BinaryArray2D;
import net.sci.array.data.scalar2d.ScalarArray2D;

/**
 * Computes the geodesic distance transform (or geodesic distance map) of a
 * binary image of marker, constrained to a binary mask.
 * 
 * @author dlegland
 *
 */
public interface GeodesicDistanceTransform2D extends Algo
{
	/**
	 * Computes the geodesic distance transform (or geodesic distance map) of a
	 * binary image of marker, constrained to a binary mask.
	 * 
	 * @param marker
	 *            the binary image of marker
	 * @param mask
	 *            the binary image of mask
	 * @return the geodesic distance map in a new ImageProcessor
	 */
	public ScalarArray2D<?> process(BinaryArray2D marker, BinaryArray2D mask);
}
