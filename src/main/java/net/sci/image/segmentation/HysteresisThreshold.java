/**
 * 
 */
package net.sci.image.segmentation;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.process.ScalarArrayOperator;
import net.sci.image.ImageArrayOperator;
import net.sci.image.morphology.MorphologicalReconstruction;

/**
 * Hysteresis threshold, that retains all array elements greater than the lower
 * value, and connected to an element greater than the upper value.
 *
 * @see ValueRangeThreshold
 *
 * @author dlegland
 */
public class HysteresisThreshold extends AlgoStub implements ImageArrayOperator, ScalarArrayOperator
{
    // ==================================================
    // Class variables
    
    /**
     * The threshold used to identify the binary markers.
     */
    double upperValue;
    
    /**
     * The threshold used to identify the binary masks used for reconstruction.
     */
    double lowerValue;

    
    // ==================================================
    // Constructor
    
    /**
     * Creates a new instance of Hysteresis threshold.
     * 
     * @param upperValue
     *            the threshold value for selecting the binary markers.
     * @param lowerValue
     *            the threshold value for selecting the extended / reconstructed
     *            binary regions.
     */
    public HysteresisThreshold(double upperValue, double lowerValue)
    {
        this.upperValue = upperValue;
        this.lowerValue = lowerValue;
    }
    

    // ==================================================
    // Processing methods
    
    @Override
    public BinaryArray processScalar(ScalarArray<?> array)
    {
        // Compute segmentation based on upper threshold value
        BinaryArray upperSeg = BinaryArray.create(array.size());
        upperSeg.fillBooleans(pos -> array.getValue(pos) >= upperValue);
        
        // Compute segmentation based on lower threshold value
        BinaryArray lowerSeg = BinaryArray.create(array.size());
        lowerSeg.fillBooleans(pos -> array.getValue(pos) >= lowerValue);

        return BinaryArray.wrap(MorphologicalReconstruction.reconstructByDilation(upperSeg, lowerSeg));
    }
}
