/**
 * 
 */
package net.sci.image.binary.distmap;


import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoStub;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.UInt16Array2D;
import net.sci.image.binary.ChamferWeights2D;

/**
 * <p>Computes 2D Chamfer distance maps using in a 3x3 or 5x5 neighborhood of each
 * pixel, and storing result in a 2D UInt16 array.</p>
 * 
 * <p>
 * Uses 5x5 chamfer map, with two passes (one forward, one backward). Three
 * different weights are provided for orthogonal, diagonal, and "chess-knight"
 * moves. Weights equal to (5,7,11) usually give nice results.
 * </p>
 * 
 * 
 * @author David Legland
 * @see ChamferDistanceTransform2DFloat32
 */
public class ChamferDistanceTransform2DUInt16 extends AlgoStub implements
		ArrayOperator, DistanceTransform2D
{
    // ==================================================
    // Class variables

    private short[] weights = new short[]{3, 4, 5};

	/**
	 * Flag for dividing final distance map by the value first weight. 
	 * This results in distance map values closer to Euclidean, but with 
	 * non integer values. 
	 */
	private boolean normalizeMap = true;
	
	
    // ==================================================
    // Constructors 
    
	public ChamferDistanceTransform2DUInt16(ChamferWeights2D weights, boolean normalize)
	{
		this(weights.getShortWeights(), normalize);
	}

	public ChamferDistanceTransform2DUInt16(short[] weights, boolean normalize)
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

	
    // ==================================================
    // Computation methods 

	public UInt16Array2D process2d(BinaryArray2D array)
	{
	    // Allocate result array
        UInt16Array2D distMap = initializeResult(array);
		
		// Two iterations are enough to compute distance map to boundary
		forwardIteration(distMap, array);
		backwardIteration(distMap, array);

        // Normalize values by the first weight
        if (this.normalizeMap)
        {
            normalizeResult(distMap, array);
        }
		
		this.fireStatusChanged(new AlgoEvent(this, ""));		
		return distMap;
	}

	
    // ==================================================
    // Inner computation methods 
    
	private UInt16Array2D initializeResult(BinaryArray2D array)
	{
        this.fireStatusChanged(new AlgoEvent(this, "Initialization"));

        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // create new empty image, and fill it with black
        UInt16Array2D result = UInt16Array2D.create(sizeX, sizeY);
        
        // initialize empty image with either 0 (background) or Inf (foreground)
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                boolean inside = array.getBoolean(x, y);
                result.setInt(x, y, inside ? Short.MAX_VALUE : 0);
            }
        }
        
        return result;
	}
	
	
	private void forwardIteration(UInt16Array2D distMap, BinaryArray2D mask)
	{
        this.fireStatusChanged(new AlgoEvent(this, "Forward Scan"));

        // Initialize pairs of offset and weights
        int[] dx = new int[]{-1, +1,  -2, -1,  0, +1, +2,  -1};
        int[] dy = new int[]{-2, -2,  -1, -1, -1, -1, -1,   0};
        short[] dw = new short[] { 
                weights[2], weights[2], 
                weights[2], weights[1], weights[0], weights[1], weights[2], 
                weights[0] };

        // size of image
        int sizeX = mask.size(0);
        int sizeY = mask.size(1);

        // Iterate over pixels
        for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				// process only pixels within the mask
				if (!mask.getBoolean(x, y))
					continue;
				
                // current distance value
                int currentDist = distMap.getInt(x, y);
                int newDist = currentDist;
                
                // iterate over neighbors
                for (int i = 0; i < dx.length; i++)
                {
                    // compute neighbor coordinates
                    int x2 = x + dx[i];
                    int y2 = y + dy[i];
                    
                    // check bounds
                    if (x2 < 0 || x2 >= sizeX)
                        continue;
                    if (y2 < 0 || y2 >= sizeY)
                        continue;
                    
                    if (mask.getBoolean(x2, y2))
                    {
                        // Foreground pixel: increment distance
                        newDist = Math.min(newDist, distMap.getInt(x2, y2) + dw[i]);
                    }
                    else
                    {
                        // Background pixel: init with first weight
                        newDist = Math.min(newDist, dw[i]);
                    }
                }
                
                if (newDist < currentDist) 
                {
                    distMap.setInt(x, y, newDist);
                }
			}
		} // end of processing for current line
		
		this.fireProgressChanged(this, sizeY, sizeY);

	} // end of forward iteration

	private void backwardIteration(UInt16Array2D distMap, BinaryArray2D mask)
	{
        this.fireStatusChanged(new AlgoEvent(this, "Backward Scan"));

        // Initialize pairs of offset and weights
        int[] dx = new int[]{+1, -1,  +2, +1,  0, -1, -2,  +1};
        int[] dy = new int[]{+2, +2,  +1, +1, +1, +1, +1,   0};
        short[] dw = new short[] { 
                weights[2], weights[2], 
                weights[2], weights[1], weights[0], weights[1], weights[2], 
                weights[0] };
        
        // size of image
        int sizeX = mask.size(0);
        int sizeY = mask.size(1);

        // Iterate over pixels
        for (int y = sizeY - 1; y >= 0; y--)
		{
			for (int x = sizeX - 1; x >= 0; x--)
			{
				// process only pixels within the mask
				if (!mask.getBoolean(x, y))
					continue;
				
                // current distance value
                int currentDist = distMap.getInt(x, y);
                int newDist = currentDist;
                
                // iterate over neighbors
                for (int i = 0; i < dx.length; i++)
                {
                    // compute neighbor coordinates
                    int x2 = x + dx[i];
                    int y2 = y + dy[i];
                    
                    // check bounds
                    if (x2 < 0 || x2 >= sizeX)
                        continue;
                    if (y2 < 0 || y2 >= sizeY)
                        continue;
                    
                    if (mask.getBoolean(x2, y2))
                    {
                        // Foreground pixel: increment distance
                        newDist = Math.min(newDist, distMap.getInt(x2, y2) + dw[i]);
                    }
                    else
                    {
                        // Background pixel: init with first weight
                        newDist = Math.min(newDist, dw[i]);
                    }
                }
                
                if (newDist < currentDist) 
                {
                    distMap.setInt(x, y, newDist);
                }
			}
		} // end of processing for current line 
		
		this.fireProgressChanged(this, sizeY, sizeY);
	} // end of backward iteration

	private void normalizeResult(UInt16Array2D distMap, BinaryArray2D array)
	{
        this.fireStatusChanged(new AlgoEvent(this, "Normalization"));

        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
	    for (int y = 0; y < sizeY; y++)
	    {
	        for (int x = 0; x < sizeX; x++) 
	        {
	            if (array.getBoolean(x, y)) 
	            {
	                distMap.setInt(x, y, distMap.getInt(x, y) / weights[0]);
	            }
	        }
	    }
	}
}
