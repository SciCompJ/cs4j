/**
 * 
 */
package net.sci.image.morphology.extrema;

import static java.lang.Math.min;
//import static net.sci.array.type.Boolean.FALSE;
import static net.sci.array.type.Binary.TRUE;

import net.sci.algo.Algo;
import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.data.BinaryArray;
import net.sci.array.data.scalar2d.BinaryArray2D;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.image.ArrayToArrayImageOperator;
import net.sci.image.data.Connectivity2D;
import net.sci.image.morphology.FloodFill;
import net.sci.image.morphology.MinimaAndMaxima;

/**
 * Computes regional minima and maxima on planar arrays.
 * 
 * @author dlegland
 *
 */
public class RegionalExtrema2D extends AlgoStub
		implements ArrayToArrayImageOperator, Algo
{
	// ==============================================================
	// Class variables
	
	MinimaAndMaxima.Type type = MinimaAndMaxima.Type.MINIMA;
	
	Connectivity2D connectivity = Connectivity2D.C4;
	
	
	// ==============================================================
	// Constructors
	

	/**
	 * Creates a new algorithm for computing regional extrema, that computes
	 * regional minima with connectivity 4.
	 */
	public RegionalExtrema2D()
	{
	}
	
	/**
	 * Creates a new algorithm for computing regional extrema, by choosing type
	 * of minima and connectivity.
	 * 
	 * @param type
	 *            the type of extrema (minima or maxima)
	 * @param connectivity
	 *            should be 4 or 8
	 */
	public RegionalExtrema2D(MinimaAndMaxima.Type type, Connectivity2D connectivity)
	{
		this.type = type;
		this.connectivity = connectivity;
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

	public Connectivity2D getConnectivity()
	{
		return this.connectivity;
	}
	
	public void setConnectivity(Connectivity2D conn)
	{
		this.connectivity = conn;
	}
	
	
	// ==============================================================
	// Implementation of Array operator interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.ArrayToArrayOperator#process(net.sci.array.Array, net.sci.array.Array)
	 */
	@Override
	public void process(Array<?> source, Array<?> target)
	{
		if (!(source instanceof ScalarArray2D))
		{
			throw new IllegalArgumentException("Source array should be 2D scalar array");
		}
		if (!(target instanceof BinaryArray2D))
		{
			throw new IllegalArgumentException("target array should be 2D boolean array");
		}
		
		if (this.connectivity == Connectivity2D.C4)
		{
			processScalar2dC4((ScalarArray2D<?>) source, (BinaryArray2D) target);
		}
		else if (this.connectivity == Connectivity2D.C8)
		{
			processScalar2dC8((ScalarArray2D<?>) source, (BinaryArray2D) target);
		}
		else
		{
			throw new RuntimeException("Unable to process connectivity: " + this.connectivity);
		}
	}

	/**
	 * Computes regional extrema in current input image, using
	 * flood-filling-like algorithm with 4 connectivity.
	 * 
	 * Computations are made with floating point values.
	 */
	private void processScalar2dC4(ScalarArray2D<?> source, BinaryArray2D target) 
	{
		// get image size
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);

		// initialize result array with true everywhere
		target.fill(TRUE);
		
		// initialize local data depending on extrema type
		int sign = 1;
		if (this.type == MinimaAndMaxima.Type.MAXIMA) 
		{
			sign = -1;
		}
		
		// Iterate over image pixels
		for (int y = 0; y < sizeY; y++) 
		{
			this.fireProgressChanged(this, y, sizeY);
			
			for (int x = 0; x < sizeX; x++) 
			{
				// Check if current pixel was already processed
				if (target.getValue(x, y) == 0)
					continue;
				
				// current value
				double currentValue = source.getValue(x, y);
				
				// compute extrema value in 4-neighborhood (computes max value
				// if sign is -1)
				double value = currentValue * sign;
				if (x > 0) 
					value = min(value, source.getValue(x-1, y) * sign); 
				if (y > 0) 
					value = min(value, source.getValue(x, y-1) * sign); 
				if (x < sizeX - 1) 
					value = min(value, source.getValue(x+1, y) * sign); 
				if (y < sizeY - 1) 
					value = min(value, source.getValue(x, y+1) * sign);
				
				// if one of the neighbors of current pixel has a lower (resp.
				// greater) value, the the current pixel is not an extremum.
				// Consequently, the current pixel, and all its connected 
				// neighbors with same value are set to 0 in the output image. 
				if (value < currentValue * sign)
				{
					FloodFill.floodFill(source, x, y, target, 0, Connectivity2D.C4);
				}
			}
		}
		this.fireProgressChanged(this, 1, 1);
	}

	/**
	 * Computes regional extrema in current input image, using
	 * flood-filling-like algorithm with 4 connectivity.
	 * 
	 * Computations are made with floating point values.
	 */
	private void processScalar2dC8(ScalarArray2D<?> source, BinaryArray2D target) 
	{
		// get image size
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);

		// initialize result array with true everywhere
		target.fill(TRUE);
		
		// initialize local data depending on extrema type
		int sign = 1;
		if (this.type == MinimaAndMaxima.Type.MAXIMA) 
		{
			sign = -1;
		}
		
		// Iterate over image pixels
		for (int y = 0; y < sizeY; y++) 
		{
			this.fireProgressChanged(this, y, sizeY);
			for (int x = 0; x < sizeX; x++) 
			{
				// Check if current pixel was already processed
				if (target.getValue(x, y) == 0)
					continue;
				
				// current value
				double currentValue = source.getValue(x, y);
				
				// compute extrema value in 4-neighborhood (computes max value
				// if sign is -1)
				double value = currentValue * sign;
				for (int y2 = Math.max(y-1, 0); y2 <= Math.min(y+1, sizeY-1); y2++) 
				{
					for (int x2 = Math.max(x-1, 0); x2 <= Math.min(x+1, sizeX-1); x2++) 
					{
						value = min(value, source.getValue(x2, y2) * sign);
					}
				}

				// if one of the neighbors of current pixel has a lower (resp.
				// greater) value, the the current pixel is not an extremum.
				// Consequently, the current pixel, and all its connected 
				// neighbors with same value are set to 0 in the output image. 
				if (value < currentValue * sign) 
				{
					FloodFill.floodFill(source, x, y, target, 0, Connectivity2D.C8);
				}
			}
		}
		this.fireProgressChanged(this, 1, 1);
	}
	
	/**
	 * Creates a new array that can be used as output for processing the given
	 * input array.
	 * 
	 * @param array
	 *            the reference array
	 * @return a new instance of Array that can be used for processing input
	 *         array.
	 */
	public BinaryArray createEmptyOutputArray(Array<?> array)
	{
		int[] dims = array.getSize();
		return BinaryArray.create(dims);
	}
	
	@Override
	public boolean canProcess(Array<?> source, Array<?> target)
	{
		return source instanceof ScalarArray2D
				&& target instanceof BinaryArray2D
				&& source.dimensionality() == target.dimensionality();
	}
}
