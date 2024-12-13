/**
 * 
 */
package net.sci.image.binary.geoddist;

import net.sci.algo.Algo;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.binary.distmap.ChamferMask3D;

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
    public static GeodesicDistanceTransform3D create(ChamferMask3D chamferMask, boolean floatingPoint,
            boolean normalize)
    {
        return floatingPoint
                ? new GeodesicDistanceTransform3DFloat32Hybrid(chamferMask, normalize)
                : new GeodesicDistanceTransform3DUInt16Hybrid(chamferMask, normalize);
    }
    
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
