/**
 * 
 */
package net.sci.image.label.geoddist;

import net.sci.algo.Algo;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.IntArray2D;
import net.sci.array.scalar.ScalarArray2D;

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
	 *            the binary image of markers.
	 * @param labelMap
	 *            the integer array of labels, the same size as the marker image.
	 * @return the geodesic distance map in a new ScalarArray.
	 */
	public ScalarArray2D<?> process2d(BinaryArray2D marker, IntArray2D<?> labelMap);
}
