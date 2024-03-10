/**
 * 
 */
package net.sci.image.binary.geoddist;

import net.sci.algo.Algo;
import net.sci.array.binary.BinaryArray;
import net.sci.image.binary.distmap.DistanceTransform;

/**
 * General interface for operators implementing distance transform.
 */
public interface GeodesicDistanceTransform extends Algo
{
    /**
     * Computes distance transform on the specified binary array, and returns
     * the result into an instance of the DistanceTransform.Result class.
     * 
     * @param array
     *            the array to process
     * @return an distance of Result that contains both the distance map and the
     *         value of the largest distance
     */
    public DistanceTransform.Result computeResult(BinaryArray marker, BinaryArray mask);
}
