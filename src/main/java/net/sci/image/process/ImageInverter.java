/**
 * 
 */
package net.sci.image.process;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.color.RGB8;
import net.sci.array.color.RGB8Array;
import net.sci.array.scalar.BinaryArray;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.UInt16;
import net.sci.array.scalar.UInt16Array;
import net.sci.array.scalar.UInt8Array;
import net.sci.image.ImageArrayOperator;

/**
 * An image inverter. 
 * @author dlegland
 *
 */
public final class ImageInverter implements ImageArrayOperator, ArrayOperator
{
	/**
	 */
	public ImageInverter()
	{
	}

    /**
     * Process scalar arrays of any dimension.
     * 
     * @param source
     *            the input array
     * @param target
     *            the target array
     */
	public void processScalar(ScalarArray<?> source, ScalarArray<?> target)
	{
		// determine max value
		double maxVal = determineUpperValue(source);
		
		// create Iterators
		ScalarArray.Iterator<?> sourceIter = source.iterator(); 
		ScalarArray.Iterator<?> targetIter = target.iterator();
		
		// iterate in parallel over both iterators
		while(sourceIter.hasNext() && targetIter.hasNext())
		{
			targetIter.forward();
			targetIter.setValue(maxVal - sourceIter.nextValue());
		}
	}
	
	/**
     * Computes the value used for inverting array.
     * 
     * @param array
     *            the array to be inverted
     * @return the value that can be used to invert this array
     */
	private double determineUpperValue(ScalarArray<?> array)
	{
		if (array instanceof UInt8Array) return 255;
		if (array instanceof UInt16Array) return UInt16.MAX_VALUE;
		if (array instanceof BinaryArray) return 1;
		
		double[] valueRange = array.valueRange();
		return valueRange[1] - valueRange[0];
	}

	/**
	 * Process RGB8 arrays of any dimension.
     * 
     * @param source
     *            the input array
     * @param target
     *            the target array
	 */
	public void processRGB8(RGB8Array source, RGB8Array target)
	{
		// create Iterators
		RGB8Array.Iterator sourceIter = source.iterator(); 
		RGB8Array.Iterator targetIter = target.iterator();

		// iterate in parallel over both iterators
		while(sourceIter.hasNext() && targetIter.hasNext())
		{
			RGB8 rgb = sourceIter.next();
			int[] vals = rgb.getSamples();
			for (int c = 0; c < 3; c++)
			{
				vals[c] = 255 - vals[c];
			}
			targetIter.set(new RGB8(vals[0], vals[1], vals[2]) );
		}
	}

	
	@Override
    public <T> Array<?> process(Array<T> array)
    {
	    if (array instanceof ScalarArray)
	    {
	        ScalarArray<?> scalar = (ScalarArray<?>) array;
	        ScalarArray<?> result = scalar.newInstance(array.getSize());
	        processScalar(scalar, result);
	        return result;
	    }
	    else if (array instanceof RGB8Array)
	    {
	        RGB8Array rgb8 = (RGB8Array) array;
	        RGB8Array result = rgb8.newInstance(array.getSize());
	        processRGB8(rgb8, result);
	        return result;
	    }
	    
        throw new IllegalArgumentException("Requires either a sclalar or a RGB8 array");
    }

    public boolean canProcess(Array<?> array)
	{
		return array instanceof ScalarArray || array instanceof RGB8Array;
	}
}
