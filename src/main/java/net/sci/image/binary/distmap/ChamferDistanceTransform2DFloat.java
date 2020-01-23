/**
 * 
 */
package net.sci.image.binary.distmap;


import static java.lang.Math.min;
import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoStub;
import net.sci.array.ArrayOperator;
import net.sci.array.scalar.BinaryArray2D;
import net.sci.array.scalar.Float32Array2D;
import net.sci.image.binary.ChamferWeights2D;

/**
 * Computes 2D Chamfer distance maps using in a 3x3 or 5x5 neighborhood of each
 * pixel, and storing result in a 2D Float32 array.
 * 
 * @author David Legland
 * @see ChamferDistanceTransform2DUInt16
 */
public class ChamferDistanceTransform2DFloat extends AlgoStub implements ArrayOperator, DistanceTransform2D
{
	private float[] weights = new float[]{3, 4, 5};

	/**
	 * Flag for dividing final distance map by the value first weight. 
	 * This results in distance map values closer to Euclidean, but with 
	 * non integer values. 
	 */
	private boolean normalizeMap = true;

	int sizeX;
	int sizeY;
	
	BinaryArray2D mask;
	Float32Array2D result;
	
	public ChamferDistanceTransform2DFloat(ChamferWeights2D weights, boolean normalize)
	{
		this(weights.getFloatWeights(), normalize);
	}

	public ChamferDistanceTransform2DFloat(float[] weights, boolean normalize)
	{
		this.weights = weights;
		
		// ensure array of weights is long enough
		if (weights.length < 3) 
		{
			this.weights = new float[3];
			this.weights[0] = weights[0];
			this.weights[1] = weights[1];
			this.weights[2] = (float) (weights[0] + weights[1]);
		}
		this.normalizeMap = normalize;
	}

	public Float32Array2D process2d(BinaryArray2D array)
	{
		// size of image
		sizeX = array.size(0);
		sizeY = array.size(1);
		
		// update mask
		this.mask = array;

		// create new empty image, and fill it with black
		this.result = Float32Array2D.create(sizeX, sizeY);
		result.fill(new net.sci.array.scalar.Float32(0));
		
		this.fireStatusChanged(new AlgoEvent(this, "Initialization"));
		
		// initialize empty image with either 0 (background) or Inf (foreground)
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				boolean inside = array.getBoolean(x, y);
				result.setValue(inside ? Float.MAX_VALUE : 0, x, y);
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
					if (mask.getBoolean(x, y)) 
					{
						result.setValue(result.getValue(x, y) / weights[0], x, y);
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
		double ortho;
		double diago;
		double diag2;
		double newVal;

		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				// process only pixels within the mask
				if (!mask.getBoolean(x, y))
					continue;
				
				// init neighbor values
				ortho = Double.MAX_VALUE;
				diago = Double.MAX_VALUE;
				diag2 = Double.MAX_VALUE;
				
				// compute weighted neighbor values
				if (y > 1)
				{
					if (x > 0)
						diag2 = Math.min(diag2, result.getValue(x-1, y-2));
					if (x < sizeX - 2) 
						diag2 = Math.min(diag2, result.getValue(x+1, y-2));
				}
				if (y > 0)
				{
					if (x > 1) 
						diag2 = Math.min(diag2, result.getValue(x-2, y-1));
					if (x > 0) 
						diago = Math.min(diago, result.getValue(x-1, y-1));
					ortho = Math.min(ortho, result.getValue(x, y-1));
					if (x < sizeX - 1) 
						diago = Math.min(diago, result.getValue(x+1, y-1));
					if (x < sizeX - 2) 
						diag2 = Math.min(diag2, result.getValue(x+2, y-1));
				}
				if (x > 0)
					ortho = Math.min(ortho, result.getValue(x-1, y));
				
				// compute new putative value
				newVal = min(min(ortho + weights[0], diago + weights[1]), diag2 + weights[2]);
				
				// and update if necessary
				double value = result.getValue(x, y);
				if (newVal < value) 
				{
					result.setValue(newVal, x, y);
				}
			}
		} // end of processing for current line 
		this.fireProgressChanged(this, sizeY, sizeY);

	} // end of forward iteration

	private void backwardIteration()
	{
		// variables declaration
		double ortho;
		double diago;
		double diag2;
		double newVal;

		for (int y = sizeY - 1; y >= 0; y--)
		{
			for (int x = sizeX - 1; x >= 0; x--)
			{
				// process only pixels within the mask
				if (!mask.getBoolean(x, y))
					continue;
				
				// init neighbor values
				ortho = Double.MAX_VALUE;
				diago = Double.MAX_VALUE;
				diag2 = Double.MAX_VALUE;
				
				// compute weighted neighbor values
				if (y < sizeY - 2)
				{
					if (x < sizeX - 1)
						diag2 = Math.min(diag2, result.getValue(x+1, y+2));
					if (x > 0) 
						diag2 = Math.min(diag2, result.getValue(x-1, y+2));
				}
				if (y < sizeY - 1)
				{
					if (x < sizeX - 2) 
						diag2 = Math.min(diag2, result.getValue(x+2, y+1));
					if (x < sizeX - 1) 
						diago = Math.min(diago, result.getValue(x+1, y+1));
					ortho = Math.min(ortho, result.getValue(x, y+1));
					if (x > 0) 
						diago = Math.min(diago, result.getValue(x-1, y+1));
					if (x > 1) 
						diag2 = Math.min(diag2, result.getValue(x-2, y+1));
				}
				if (x < sizeX - 1)
					ortho = Math.min(ortho, result.getValue(x+1, y));
				
				// compute new putative value
				newVal = min(min(ortho + weights[0], diago + weights[1]), diag2 + weights[2]);
				
				// and update if necessary
				double value = result.getValue(x, y);
				if (newVal < value) 
				{
					result.setValue(newVal, x, y);
				}
			}
		} // end of processing for current line 
		
		this.fireProgressChanged(this, sizeY, sizeY);
	} // end of backward iteration
}
