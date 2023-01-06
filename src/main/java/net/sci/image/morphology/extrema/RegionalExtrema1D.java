/**
 * 
 */
package net.sci.image.morphology.extrema;

import static java.lang.Math.min;
import static net.sci.array.binary.Binary.TRUE;

import net.sci.algo.Algo;
import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray1D;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray1D;
import net.sci.image.ImageArrayOperator;
import net.sci.image.morphology.MinimaAndMaxima;

/**
 * Computes regional minima and maxima on 1D-arrays.
 * 
 * @author dlegland
 *
 */
public class RegionalExtrema1D extends AlgoStub implements ImageArrayOperator, ScalarArrayOperator, Algo
{
	// ==============================================================
	// Class variables
	
	MinimaAndMaxima.Type type = MinimaAndMaxima.Type.MINIMA;
	
	
	// ==============================================================
	// Constructors
	

	/**
	 * Creates a new algorithm for computing regional extrema, that computes
	 * regional minima with connectivity 4.
	 */
	public RegionalExtrema1D()
	{
	}
	
	/**
	 * Creates a new algorithm for computing regional extrema, by choosing type
	 * of extrema.
	 * 
	 * @param type
	 *            the type of extrema (minima or maxima)
	 */
	public RegionalExtrema1D(MinimaAndMaxima.Type type)
	{
		this.type = type;
	}
	
	
	// ==============================================================
	// getter and setters
	
	public MinimaAndMaxima.Type getExtremaType() 
	{
		return type;
	}

	public void setExtremaType(MinimaAndMaxima.Type type)
	{
		this.type = type;
	}

	
	// ==============================================================
	// Processing methods
	
	public void process(ScalarArray<?> source, BinaryArray target)
	{
        ScalarArray1D<?> source2d = ScalarArray1D.wrapScalar1d(source);
        BinaryArray1D target2d = BinaryArray1D.wrap(target);
		
        processScalar1d(source2d, target2d);
	}

	/**
	 * Computes regional extrema in current input 1D array.
	 * 
	 * Computations are made with floating point values.
	 */
	private void processScalar1d(ScalarArray1D<?> source, BinaryArray1D target) 
	{
	    // Note:
	    // The code was adapted from 2D FlooFill, and could be improved for 1D arrays.
	    
		// get image size
		int sizeX = source.size(0);

		// initialize result array with true everywhere
		target.fill(TRUE);
		
		// initialize local data depending on extrema type
		int sign = 1;
		if (this.type == MinimaAndMaxima.Type.MAXIMA) 
		{
			sign = -1;
		}
		
		// Iterate over array elements
		for (int x = 0; x < sizeX; x++) 
		{
		    this.fireProgressChanged(this, x, sizeX);

		    // Check if current pixel was already processed
		    if (!target.getBoolean(x))
		        continue;

		    // current value
		    double currentValue = source.getValue(x);

            // compute extrema value in neighborhood (computes max value
            // if sign is -1)
		    double value = currentValue * sign;
		    if (x > 0) 
		        value = min(value, source.getValue(x-1) * sign); 
		    if (x < sizeX - 1) 
		        value = min(value, source.getValue(x+1) * sign); 

		    // if one of the neighbors of current pixel has a lower (resp.
		    // greater) value, the the current pixel is not an extremum.
		    // Consequently, the current pixel, and all its connected 
		    // neighbors with same value are set to 0 in the output image. 
		    if (value < currentValue * sign)
		    {
		        floodFillToFalse(source, x, target);
		    }
		}
		this.fireProgressChanged(this, 1, 1);
	}
	
	/**
     * Assigns in <code>labelImage</code> all the neighbor pixels of (x) that
     * have the same value in <code>image</code>, the specified new label value
     * (<code>value</code>), using the specified connectivity.
     * 
     * @param input
     *            original image to read the pixel values from
     * @param x0
     *            x-coordinate of the seed pixel
     * @param output
     *            the binary array to fill in
     */
    private final static void floodFillToFalse(ScalarArray1D<?> input, int x0,
            BinaryArray1D output)
    {
        // retrieve array length
        int sizeX = input.size(0);
        
        // get start value
        double oldValue = input.getValue(x0);
        
        // find start of scan-line
        int x1 = x0; 
        while (x1 > 0 && input.getValue(x1-1) == oldValue)
            x1--;

        // find end of scan-line
        int x2 = x0;
        while (x2 < sizeX - 1 && input.getValue(x2+1) == oldValue)
            x2++;

        // fill current output range
        for (int x = x1; x <= x2; x++)
        {
            output.setBoolean(x, false);
        }
    }
    
	
	// ==============================================================
    // Implementation of ScalarArrayOperator interface

    @Override
    public BinaryArray1D processScalar(ScalarArray<? extends Scalar> array)
    {
        // check input validity
        if (array.dimensionality() != 1)
        {
            throw new RuntimeException("Requires a 1-dimensional array as input");
        }
        
        BinaryArray1D output = BinaryArray1D.create(array.size(0));
        process(array, output);
        return output;
    }

}
