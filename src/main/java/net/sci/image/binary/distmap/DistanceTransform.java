/**
 * 
 */
package net.sci.image.binary.distmap;

import net.sci.algo.Algo;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.BinaryArray;
import net.sci.array.numeric.ScalarArray;

/**
 * General interface for operators implementing distance transform.
 */
public interface DistanceTransform extends ArrayOperator, Algo
{
    /**
     * Computes distance transform on the specified binary array, and returns
     * the result into an instance of the Result class.
     * 
     * @param array
     *            the array to process
     * @return an distance of Result that contains both the distance map and the
     *         value of the largest distance
     */
    public Result computeResult(BinaryArray array);
    
    /**
     * Stores the result of distance transform computed on a binary image, 
     * together with the maximum distance within the distance map.
     */
    public class Result
    {
        /**
         * The distance map array.
         */
        public ScalarArray<?> distanceMap;
        
        /**
         * The maximum distance within the array.
         */
        public double maxDistance;
        
        /**
         * Initializes a new Result data class.
         * 
         * @param distMap
         *            The distance map array
         * @param maxDist
         *            The maximum distance within the array
         */
        public Result(ScalarArray<?> distMap, double maxDist)
        {
            this.distanceMap = distMap;
            this.maxDistance = maxDist;
        }
    }
}
