/**
 * 
 */
package net.sci.image.binary.geoddist;

import net.sci.algo.Algo;
import net.sci.array.data.scalar3d.BinaryArray3D;
import net.sci.array.data.scalar3d.ScalarArray3D;

/**
 * Computes the geodesic distance transform (or geodesic distance map) of a
 * 3D binary image of marker, constrained to a binary mask.
 * 
 * @author dlegland
 *
 */
public interface GeodesicDistanceTransform3D extends Algo
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
	public ScalarArray3D<?> process3d(BinaryArray3D marker, BinaryArray3D mask);
}
