/**
 * 
 */
package net.sci.image.binary.distmap;

import net.sci.algo.Algo;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.BinaryArray;
import net.sci.array.scalar.ScalarArray;

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
     * Stores the results of distance transform computed on a binary image.
     */
    public class Result
    {
        public ScalarArray<?> distanceMap;
        public double maxDistance;
        
        public Result(ScalarArray<?> distMap, double distMax)
        {
            this.distanceMap = distMap;
            this.maxDistance = distMax;
        }
    }
}
