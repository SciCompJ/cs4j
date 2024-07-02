/**
 * 
 */
package net.sci.image.process;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.image.ImageArrayOperator;

/**
 * Thresholds an image, by retaining only values strictly greater than a given
 * threshold value.
 * 
 * @author dlegland
 *
 */
public class ImageThreshold extends AlgoStub implements ImageArrayOperator, ScalarArrayOperator
{
	double value;
	
	/**
	 * Creates a new instance of ImageThreshold.
	 * 
	 * @param value
	 *     the value for threshold.
	 */
	public ImageThreshold(double value)
	{
		this.value = value;
	}

	public void processScalar(ScalarArray<?> source, BinaryArray target)
	{
	    target.fillBooleans(pos -> source.getValue(pos) > this.value);
	}
	
    @Override
    public BinaryArray processScalar(ScalarArray<?> array)
    {
        BinaryArray result = BinaryArray.create(array.size());
        processScalar(array, result);
        return result;
    }
}
