/**
 * 
 */
package net.sci.image.binary.distmap;


import static java.lang.Math.min;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.data.scalar2d.BooleanArray2D;
import net.sci.array.data.scalar2d.UInt16Array2D;
import net.sci.array.type.UInt16;
import net.sci.image.binary.ChamferWeights2D;

/**
 * @author dlegland
 *
 */
public class ChamferDistanceTransform2DShort extends AlgoStub implements ArrayOperator, DistanceTransform2D
{
	private short[] weights = new short[]{3, 4, 5};

	/**
	 * Flag for dividing final distance map by the value first weight. 
	 * This results in distance map values closer to Euclidean, but with 
	 * non integer values. 
	 */
	private boolean normalizeMap = true;

	int sizeX;
	int sizeY;
	
	BooleanArray2D mask;
	UInt16Array2D result;
	
	public ChamferDistanceTransform2DShort(ChamferWeights2D weights, boolean normalize)
	{
		this(weights.getShortWeights(), normalize);
	}

	public ChamferDistanceTransform2DShort(short[] weights, boolean normalize)
	{
		this.weights = weights;
		
		// ensure array of weights is long enough
		if (weights.length < 3) 
		{
			this.weights = new short[3];
			this.weights[0] = weights[0];
			this.weights[1] = weights[1];
			this.weights[2] = (short) (weights[0] + weights[1]);
		}
		this.normalizeMap = normalize;
	}

	@Override
	public <T> Array<?> process(Array<T> array)
	{
		if (array instanceof BooleanArray2D)
		{
			return process2d((BooleanArray2D) array);
		}
		throw new RuntimeException("Unable to process array of class " + array.getClass());
	}
	
	public UInt16Array2D process2d(BooleanArray2D array)
	{
		// size of image
		sizeX = array.getSize(0);
		sizeY = array.getSize(1);
		
		// update mask
		this.mask = array;

		// create new empty image, and fill it with black
		this.result = UInt16Array2D.create(sizeX, sizeY);
		result.fill(new UInt16(0));
		
		this.fireStatusChanged(new AlgoEvent(this, "Initialization"));
		
		// initialize empty image with either 0 (background) or Inf (foreground)
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				boolean inside = array.getState(x, y);
				result.setInt(x, y, inside ? Short.MAX_VALUE : 0);
			}
		}
		
		// Two iterations are enough to compute distance map to boundary
		this.fireStatusChanged(new AlgoEvent(this, "Forward Scan"));
		forwardIteration();
		this.fireStatusChanged(new AlgoEvent(this, "Backward Scan"));
		backwardIteration();

		// Normalize values by the first weight
		if (this.normalizeMap) 
		{
			this.fireStatusChanged(new AlgoEvent(this, "Normalization"));
			for (int y = 0; y < sizeY; y++)
			{
				for (int x = 0; x < sizeX; x++) 
				{
					if (mask.getState(x, y)) 
					{
						result.setInt(x, y, result.getInt(x, y) / weights[0]);
					}
				}
			}
		}
		
		this.fireStatusChanged(new AlgoEvent(this, ""));		
		return result;
	}
	
	private void forwardIteration()
	{
		// variables declaration
		int ortho;
		int diago;
		int diag2;
		int newVal;

		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				// process only pixels within the mask
				if (!mask.getState(x, y))
					continue;
				
				// init neighbor values
				ortho = Short.MAX_VALUE;
				diago = Short.MAX_VALUE;
				diag2 = Short.MAX_VALUE;
				
				// compute weighted neighbor values
				if (y > 1)
				{
					if (x > 0)
						diag2 = Math.min(diag2, result.getInt(x-1, y-2));
					if (x < sizeX - 2) 
						diag2 = Math.min(diag2, result.getInt(x+1, y-2));
				}
				if (y > 0)
				{
					if (x > 1) 
						diag2 = Math.min(diag2, result.getInt(x-2, y-1));
					if (x > 0) 
						diago = Math.min(diago, result.getInt(x-1, y-1));
					ortho = Math.min(ortho, result.getInt(x, y-1));
					if (x < sizeX - 1) 
						diago = Math.min(diago, result.getInt(x+1, y-1));
					if (x < sizeX - 2) 
						diag2 = Math.min(diag2, result.getInt(x+2, y-1));
				}
				if (x > 0)
					ortho = Math.min(ortho, result.getInt(x-1, y));
				
				// compute new putative value
				newVal = min(min(ortho + weights[0], diago + weights[1]), diag2 + weights[2]);
				
				// and update if necessary
				int value = result.getInt(x, y);
				if (newVal < value) 
				{
					result.setInt(x, y, newVal);
				}
			}
		} // end of processing for current line 
		this.fireProgressChanged(this, sizeY, sizeY);

	} // end of forward iteration

	private void backwardIteration()
	{
		// variables declaration
		int ortho;
		int diago;
		int diag2;
		int newVal;

		for (int y = sizeY - 1; y >= 0; y--)
		{
			for (int x = sizeX - 1; x >= 0; x--)
			{
				// process only pixels within the mask
				if (!mask.getState(x, y))
					continue;
				
				// init neighbor values
				ortho = Short.MAX_VALUE;
				diago = Short.MAX_VALUE;
				diag2 = Short.MAX_VALUE;
				
				// compute weighted neighbor values
				if (y < sizeY - 2)
				{
					if (x < sizeX - 1)
						diag2 = Math.min(diag2, result.getInt(x+1, y+2));
					if (x > 0) 
						diag2 = Math.min(diag2, result.getInt(x-1, y+2));
				}
				if (y < sizeY - 1)
				{
					if (x < sizeX - 2) 
						diag2 = Math.min(diag2, result.getInt(x+2, y+1));
					if (x < sizeX - 1) 
						diago = Math.min(diago, result.getInt(x+1, y+1));
					ortho = Math.min(ortho, result.getInt(x, y+1));
					if (x > 0) 
						diago = Math.min(diago, result.getInt(x-1, y+1));
					if (x > 1) 
						diag2 = Math.min(diag2, result.getInt(x-2, y+1));
				}
				if (x < sizeX - 1)
					ortho = Math.min(ortho, result.getInt(x+1, y));
				
				// compute new putative value
				newVal = min(min(ortho + weights[0], diago + weights[1]), diag2 + weights[2]);
				
				// and update if necessary
				int value = result.getInt(x, y);
				if (newVal < value) 
				{
					result.setInt(x, y, newVal);
				}
			}
		} // end of processing for current line 
		
		this.fireProgressChanged(this, sizeY, sizeY);
	} // end of backward iteration

	
//	/**
//	 * Computes the weighted minima of orthogonal, diagonal, and (2,1)-diagonal
//	 * values.
//	 */
//	private int min3w(int ortho, int diago, int diag2)
//	{
//		return min(min(ortho + weights[0], diago + weights[1]), diag2 + weights[2]);
//	}
//	
//	/**
//	 * Update the pixel at position (i,j) with the value newVal. If newVal is
//	 * greater or equal to current value at position (i,j), do nothing.
//	 */
//	private void updateIfNeeded(int i, int j, int newVal)
//	{
//		int value = buffer.getInt(i, j);
//		if (newVal < value) 
//		{
//			buffer.setInt(i, j, newVal);
//		}
//	}
}
