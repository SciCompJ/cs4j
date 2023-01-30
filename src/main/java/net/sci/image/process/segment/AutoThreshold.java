/**
 * 
 */
package net.sci.image.process.segment;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.ImageArrayOperator;

/**
 * Base implementation for auto-threshold algorithms.
 * 
 * @see OtsuThreshold
 * 
 * @author dlegland
 *
 */
public abstract class AutoThreshold extends AlgoStub implements ImageArrayOperator
{
    /**
     * Computes the threshold value according to the values within the specified
     * array. The method for choosing the value depends on the sub-classes of
     * the AutoThresold class.
     * 
     * @param array
     *            the array containing values
     * @return a threshold value for separating values in two classes.
     */
	public abstract double computeThresholdValue(ScalarArray<?> array);
	
	public void process(ScalarArray<?> source, BinaryArray target)
    {
    	// compute threshold value
	    this.fireStatusChanged(this, "Compute threshold value");
    	double threshold = computeThresholdValue(source);
    	
    	// apply threshold, by switching process according to dimension in order to monitor progress 
        this.fireStatusChanged(this, "Apply threshold");
        if (source.dimensionality() == 2 && target.dimensionality() == 2)
        {
            applyThreshold_2d(ScalarArray2D.wrapScalar2d(source), threshold, BinaryArray2D.wrap(target));
        }
        else if (source.dimensionality() == 2 && target.dimensionality() == 2)
        {
            applyThreshold_3d(ScalarArray3D.wrapScalar3d(source), threshold, BinaryArray3D.wrap(target));
        }
        else
        {
            target.fillBooleans(pos -> source.getValue(pos) >= threshold);
        }
    }
	
    private void applyThreshold_2d(ScalarArray2D<?> source, double threshold, BinaryArray2D target)
    {
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                target.setBoolean(x, y, source.getValue(x, y) >= threshold);
            }
        }
    }

    private void applyThreshold_3d(ScalarArray3D<?> source, double threshold, BinaryArray3D target)
    {
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        int sizeZ = source.size(2);
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    target.setBoolean(x, y, z, source.getValue(x, y, z) >= threshold);
                }
            }
        }
    }

	/**
	 * Computes the threshold value on a scalar array and returns the resulting
	 * binary array.
	 * 
	 * @param array
	 *            the scalar array to threshold
	 * @return the binary array resulting from thresholding
	 */
	public BinaryArray processScalar(ScalarArray<?> array)
	{
		BinaryArray result = createEmptyOutputArray(array);
		process(array, result);
		return result;
	}
	
	@Override
	public <T> BinaryArray process(Array<T> array)
	{
	    if (!(array instanceof ScalarArray))
	    {
	        throw new IllegalArgumentException("Requires a scalar array");
	    }
	    return processScalar((ScalarArray<?>) array);
	}
	
	/**
	 * Creates a new boolean array that can be used as output for processing the
	 * given input array.
	 * 
	 * @param inputArray
	 *            the reference array
	 * @return a new instance of Array that can be used for processing input
	 *         array.
	 */
	public BinaryArray createEmptyOutputArray(Array<?> inputArray)
	{
		return BinaryArray.create(inputArray.size());
	}
}
