/**
 * 
 */
package net.sci.image.process.segment;

import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
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
public class HysteresisThreshold
        implements ImageArrayOperator, ScalarArrayOperator
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
    public BinaryArray processScalar(ScalarArray<? extends Scalar> array)
    {
        // switch processing depending on input array dimensionality
        switch(array.dimensionality())
        {
            case 2: return processScalar2d(ScalarArray2D.wrapScalar2d(array));
            case 3: return processScalar3d(ScalarArray3D.wrapScalar3d(array));
            default: 
                throw new RuntimeException("Can not process arrays with dimensionality " + array.dimensionality());
        }
    }
    
    private BinaryArray2D processScalar2d(ScalarArray2D<? extends Scalar> array)
    {
        // Compute segmentation based on upper threshold value
        BinaryArray2D upperSeg = BinaryArray2D.create(array.size(0), array.size(1));
        upperSeg.fillBooleans(pos -> array.getValue(pos) >= upperValue);
        
        // Compute segmentation based on lower threshold value
        BinaryArray2D lowerSeg = BinaryArray2D.create(array.size(0), array.size(1));
        lowerSeg.fillBooleans(pos -> array.getValue(pos) >= lowerValue);

        return BinaryArray2D.wrap((BinaryArray) MorphologicalReconstruction.reconstructByDilation(upperSeg, lowerSeg));
    }

    private BinaryArray3D processScalar3d(ScalarArray3D<? extends Scalar> array)
    {
        // Compute segmentation based on upper threshold value
        BinaryArray3D upperSeg = BinaryArray3D.create(array.size(0), array.size(1), array.size(2));
        upperSeg.fillBooleans(pos -> array.getValue(pos) >= upperValue);
        
        // Compute segmentation based on lower threshold value
        BinaryArray3D lowerSeg = BinaryArray3D.create(array.size(0), array.size(1), array.size(2));
        lowerSeg.fillBooleans(pos -> array.getValue(pos) >= lowerValue);

        return BinaryArray3D.wrap((BinaryArray) MorphologicalReconstruction.reconstructByDilation(upperSeg, lowerSeg));
    }
}
