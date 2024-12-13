/**
 * 
 */
package net.sci.image.binary.geoddist;

import net.sci.algo.Algo;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.image.binary.distmap.ChamferMask2D;

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
     * Creates a new algorithm for computing geodesic distance transforms based
     * on chamfer masks, by specifying whether floating point computation should
     * be used, and if result distance map should be normalized.
     * 
     * @param chamferMask
     *            the 2D chamfer mask to use for propagating distances
     * @param floatingPoint
     *            boolean flag indicating whether result should be provided as
     *            <code>Float32</code> (if true) or as <code>UInt16</code> (if
     *            false).
     * @param normalize
     *            indicates whether the resulting distance map should be
     *            normalized (divide distances by the first chamfer weight)
     * @return an algorithm for computing geodesic chamfer distance maps on
     *         binary images.
     */
    public static GeodesicDistanceTransform2D create(ChamferMask2D chamferMask, boolean floatingPoint,
            boolean normalize)
    {
        return floatingPoint
                ? new GeodesicDistanceTransform2DFloat32Hybrid(chamferMask, normalize)
                : new GeodesicDistanceTransform2DUInt16Hybrid(chamferMask, normalize);
    }
    
	/**
	 * Computes the geodesic distance transform (or geodesic distance map) of a
	 * binary image of marker, constrained to a binary mask.
	 * 
	 * @param marker
	 *            the binary image of markers.
	 * @param mask
	 *            the binary image of mask, the same size as the marker image.
	 * @return the geodesic distance map in a new ScalarArray.
	 */
	public ScalarArray2D<?> process2d(BinaryArray2D marker, BinaryArray2D mask);
}
