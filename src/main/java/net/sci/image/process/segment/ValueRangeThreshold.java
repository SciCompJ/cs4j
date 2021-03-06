/**
 * 
 */
package net.sci.image.process.segment;

import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.scalar.BinaryArray;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;
import net.sci.image.ImageArrayOperator;

/**
 * @author dlegland
 *
 */
public class ValueRangeThreshold
        implements ImageArrayOperator, ScalarArrayOperator
{

    double minValue;

    double maxValue = Double.POSITIVE_INFINITY;
    
    /**
     * Creates a new instance of Threshold for selecting Image elements within a
     * range of values.
     * 
     * @param minValue
     *            the minimum value of the range, inclusive.
     * @param maxValue
     *            the minimum value of the range, inclusive.
     */
    public ValueRangeThreshold(double minValue, double maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    public void processScalar(ScalarArray<?> source, BinaryArray target)
    {
        for (int[] pos : source.positions())
        {
            double value = source.getValue(pos);
            target.setBoolean(pos, value >= this.minValue && value <= this.maxValue);
        }
    }
    
    @Override
    public BinaryArray processScalar(ScalarArray<? extends Scalar> array)
    {
        BinaryArray result = BinaryArray.create(array.size());
        processScalar(array, result);
        return result;
    }
}
