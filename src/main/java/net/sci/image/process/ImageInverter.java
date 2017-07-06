/**
 * 
 */
package net.sci.image.process;

import net.sci.array.Array;
import net.sci.array.data.BooleanArray;
import net.sci.array.data.ScalarArray;
import net.sci.array.data.UInt16Array;
import net.sci.array.data.UInt8Array;
import net.sci.array.data.color.RGB8Array;
import net.sci.array.type.RGB8;
import net.sci.array.type.UInt16;
import net.sci.image.ArrayToArrayImageOperator;

/**
 * An image inverter. 
 * @author dlegland
 *
 */
public final class ImageInverter implements ArrayToArrayImageOperator
{
	/**
	 */
	public ImageInverter()
	{
	}

	/* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array, net.sci.array.Array)
	 */
	@Override
	public void process(Array<?> source, Array<?> target)
	{
		// Choose the best possible implementation, depending on array dimensions
		if (source instanceof ScalarArray && target instanceof ScalarArray)
		{
			processScalar((ScalarArray<?>) source, (ScalarArray<?>) target);
		}
		else if (source instanceof RGB8Array && target instanceof RGB8Array)
		{
			processRGB8((RGB8Array) source, (RGB8Array) target);
		}
		else
		{
			throw new IllegalArgumentException("Can not process array of class " + source.getClass());
		}

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
		if (array instanceof BooleanArray) return 1;
		
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
	
	/**
	 * Creates a new array the same size as original, and same type.
	 */
	public Array<?> createEmptyOutputArray(Array<?> array)
	{
		int[] dims = array.getSize();
		return array.newInstance(dims);
	}
	
	public boolean canProcess(Array<?> array)
	{
		return array instanceof ScalarArray;
	}

	public boolean canProcess(Array<?> source, Array<?> target)
	{
		return source instanceof ScalarArray && target instanceof ScalarArray;
	}
}
