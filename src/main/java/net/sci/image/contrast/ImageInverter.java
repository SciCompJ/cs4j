/**
 * 
 */
package net.sci.image.contrast;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoListener;
import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.process.Complement;
import net.sci.array.color.RGB8;
import net.sci.array.color.RGB8Array;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.UInt16;
import net.sci.array.numeric.UInt16Array;
import net.sci.array.numeric.UInt8Array;
import net.sci.image.ImageArrayOperator;

/**
 * An image inverter.
 *  
 * @author dlegland
 *
 */
public final class ImageInverter extends AlgoStub implements ImageArrayOperator, ArrayOperator, AlgoListener
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
        target.fillValues(pos -> maxVal - source.getValue(pos));
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
		if (array instanceof UInt16Array) return UInt16.MAX_INT;
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
	    target.fill(pos -> invertRGB(source.get(pos)));
	}
	
	private RGB8 invertRGB(RGB8 rgb)
	{
	    int[] vals = rgb.getSamples();
        for (int c = 0; c < 3; c++)
        {
            vals[c] = 255 - vals[c];
        }
        return new RGB8(vals[0], vals[1], vals[2]);
	}

	
	@Override
    public <T> Array<?> process(Array<T> array)
    {
	    // if array is binary, use specific algorithm
	    if (array instanceof BinaryArray)
	    {
	        Complement algo = new Complement();
	        algo.addAlgoListener(this);
	        return algo.process((BinaryArray) array);
	    }
	    
	    if (array instanceof ScalarArray)
	    {
	        ScalarArray<?> scalar = (ScalarArray<?>) array;
	        ScalarArray<?> result = scalar.newInstance(array.size());
	        processScalar(scalar, result);
	        return result;
	    }
	    else if (array instanceof RGB8Array)
	    {
	        RGB8Array rgb8 = (RGB8Array) array;
	        RGB8Array result = rgb8.newInstance(array.size());
	        processRGB8(rgb8, result);
	        return result;
	    }
	    
        throw new IllegalArgumentException("Requires either a scalar or a RGB8 array");
    }

    public boolean canProcess(Array<?> array)
	{
		return array instanceof ScalarArray || array instanceof RGB8Array;
	}

    @Override
    public void algoProgressChanged(AlgoEvent evt)
    {
        this.fireProgressChanged(evt);
    }

    @Override
    public void algoStatusChanged(AlgoEvent evt)
    {
        this.fireStatusChanged(evt);
    }
}
