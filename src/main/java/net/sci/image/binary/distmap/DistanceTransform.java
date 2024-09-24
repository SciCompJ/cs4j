/**
 * 
 */
package net.sci.image.binary.distmap;

import net.sci.algo.Algo;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.BinaryArray;
import net.sci.array.numeric.ScalarArray;
import net.sci.image.Image;
import net.sci.image.ImageArrayOperator;
import net.sci.image.ImageType;

/**
 * General interface for operators implementing distance transform.
 */
public interface DistanceTransform extends ArrayOperator, Algo, ImageArrayOperator
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
    
    
    // ==================================================
    // Override the ImageArrayProcessor interface
    
    /**
     * Overrides default behavior of ImageArrayOperator interface to return an
     * instance of Image initialized with the type "DISTANCE", and a display
     * range between 0 and the maximum distance within the map.
     * 
     * @param image
     *            the image to process (image data must be binary)
     * @return a new Image instance of type DISTANCE, containing the distance
     *         map, and initialized with the input image.
     */
    @Override
    public default Image process(Image image)
    {
        BinaryArray array = BinaryArray.wrap(image.getData());
        Result res = computeResult(array);
        Image resultImage = new Image(res.distanceMap, ImageType.DISTANCE, image);
        resultImage.getDisplaySettings().setDisplayRange(new double[] {0, res.maxDistance});
        return resultImage;
    }
    
}
