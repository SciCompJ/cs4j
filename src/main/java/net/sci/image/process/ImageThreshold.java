/**
 * 
 */
package net.sci.image.process;

import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.scalar.BinaryArray;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;
import net.sci.image.ImageArrayOperator;

/**
 * Thresholds an image, by retaining only values greater than or equal to a
 * given threshold value.
 * 
 * @author dlegland
 *
 */
public class ImageThreshold implements ImageArrayOperator, ScalarArrayOperator
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
		ScalarArray.Iterator<?> iter1 = source.iterator(); 
		BinaryArray.Iterator iter2 = target.iterator();
		
		while(iter1.hasNext() && iter2.hasNext())
		{
			iter2.setNextBoolean(iter1.nextValue() > this.value);
		}
	}
	
    @Override
    public BinaryArray processScalar(ScalarArray<? extends Scalar> array)
    {
        BinaryArray result = BinaryArray.create(array.getSize());
        processScalar(array, result);
        return result;
    }
}
