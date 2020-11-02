package net.sci.image.binary.geoddist;

import net.sci.algo.AlgoStub;
import net.sci.array.scalar.BinaryArray2D;
import net.sci.array.scalar.UInt16Array2D;
import net.sci.image.binary.ChamferWeights2D;

/**
 * Computation of Chamfer geodesic distances using short integer array for
 * storing result, and 5-by-5 chamfer masks.
 * 
 * The maximum propagated distance is limited to Short.MAX_VALUE.
 * 
 * All computations are performed using integers, results are stored as
 * shorts.
 * 
 * @author David Legland
 * 
 */
public class GeodesicDistanceTransform2DUInt16Scanning5x5 extends AlgoStub implements GeodesicDistanceTransform2D
{
    // ==================================================
    // Class variables 
    
	short[] weights = new short[]{5, 7, 11};

	/**
	 * Flag for dividing final distance map by the value first weight. 
	 * This results in distance map values closer to Euclidean distance. 
	 */
	boolean normalizeMap = true;
	

	// ==================================================
    // Constructors 
    
	/**
	 * Use default weights, and normalize map.
	 */
	public GeodesicDistanceTransform2DUInt16Scanning5x5()
	{
		this(ChamferWeights2D.CHESSKNIGHT.getShortWeights(), true);
	}

	public GeodesicDistanceTransform2DUInt16Scanning5x5(ChamferWeights2D weights)
	{
		this(weights.getShortWeights(), true);
	}

	public GeodesicDistanceTransform2DUInt16Scanning5x5(ChamferWeights2D weights, boolean normalizeMap) 
	{
		this(weights.getShortWeights(), normalizeMap);
	}


	public GeodesicDistanceTransform2DUInt16Scanning5x5(short[] weights)
	{
		this(weights, true);
	}

	/**
	 * Low-level constructor.
	 * 
	 * @param weights
	 *            the array of weights for orthogonal, diagonal, and eventually
	 *            2-by-1 moves
	 * @param normalizeMap
	 *            the flag for normalizing result
	 */
	public GeodesicDistanceTransform2DUInt16Scanning5x5(short[] weights, boolean normalizeMap) 
	{
		this.weights = weights;
		
		// ensure weight array has minimum size 3
		if (this.weights.length < 3)
		{
			this.weights = new short[3];
			this.weights[0] = weights[0];
			this.weights[1] = weights[1];
			this.weights[2] = (short) (weights[0] + weights[1]);
		}
		this.normalizeMap = normalizeMap;
	}

	
    // ==================================================
    // General Methods 
    
	/**
	 * Computes the geodesic distance function for each pixel in mask, using the
	 * given mask. Mask and marker should be BinaryArray2D the same size and
	 * containing integer values.
	 * 
	 * The function returns a new UInt16Array2D the same size as the input, with
	 * values greater or equal to zero.
	 */
	public UInt16Array2D process2d(BinaryArray2D marker, BinaryArray2D mask)
	{
		// TODO: check int overflow?

		// create new empty image, and fill it with black
		fireStatusChanged(this, "Initialization..."); 
		UInt16Array2D distMap = initializeResult(marker, mask);

		// Performs forward and backward scanning until stabilization
		boolean modif = false;
		int iter = 0;
		do 
		{
			modif = false;

            // forward iteration
            fireStatusChanged(this, "Forward iteration " + iter);
            modif = modif || forwardIteration(distMap, mask);

            // backward iteration
            fireStatusChanged(this, "Backward iteration " + iter); 
            modif = modif || backwardIteration(distMap, mask);

			// Iterate while pixels have been modified
			iter++;
		}
		while (modif);

		// Normalize values by the first weight
		if (this.normalizeMap) 
		{
			fireStatusChanged(this, "Normalize map");
			normalizeMap(distMap);
		}
				
		return distMap;
	}

	private UInt16Array2D initializeResult(BinaryArray2D marker, BinaryArray2D mask)
	{
        int sizeX = marker.size(0);
        int sizeY = marker.size(1);

	    // Allocate memory
	    UInt16Array2D distMap = UInt16Array2D.create(sizeX, sizeY);
	    
	    // initialize empty image with either 0 (foreground) or Inf (background)
	    for (int y = 0; y < sizeY; y++) 
	    {
	        for (int x = 0; x < sizeX; x++) 
	        {
	            distMap.setInt(x, y, marker.getBoolean(x, y) ? 0 : Short.MAX_VALUE);
	        }
	    }
	    
	    return distMap;
	}
	
	private boolean forwardIteration(UInt16Array2D distMap, BinaryArray2D mask) 
	{
		// create array of offsets relative to current pixel
		int[] dx = new int[]{-1, +1, -2, -1, +0, +1, +2, -1};
		int[] dy = new int[]{-2, -2, -1, -1, -1, -1, -1, +0};
		
		// also create corresponding array of weights
		int w0 = weights[0];
		int w1 = weights[1];
		int w2 = weights[2];
		int[] ws = new int[]{w2, w2, w2, w1, w0, w1, w2, w0};

        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);

		// iterate over pixels
        boolean modif = false;
        for (int y = 0; y < sizeY; y++)
		{
			fireProgressChanged(this, y, sizeY); 

			for (int x = 0; x < sizeX; x++)
			{
				if (!mask.getBoolean(x, y))
					continue;
				
                // get value of current pixel
                int value = distMap.getInt(x, y);

                // iterate over neighbor pixels
                int newVal = value;
				for(int i = 0; i < dx.length; i++)
				{
					// coordinates of neighbor pixel
					int x2 = x + dx[i];
					int y2 = y + dy[i];
					
					// check image bounds
					if (x2 < 0 || x2 > sizeX - 1)
						continue;
					if (y2 < 0 || y2 > sizeY - 1)
						continue;
					
					// process only pixels inside structure
					if (!mask.getBoolean(x2, y2))
						continue;

					// update minimum value
					newVal = Math.min(newVal, distMap.getInt(x2, y2) + ws[i]);
				}
				
                // modify current pixel if needed
                if (newVal < value) 
                {
                    modif = true;
                    distMap.setInt(x, y, newVal);
                }
			}
		}
		
		fireProgressChanged(this, 1, 1); 
        return modif;
	}

	private boolean backwardIteration(UInt16Array2D distMap, BinaryArray2D mask)
	{
		// create array of offsets relative to current pixel
		int[] dx = new int[]{+1, -1, +2, +1, +0, -1, -2, +1};
		int[] dy = new int[]{+2, +2, +1, +1, +1, +1, +1, +0};
		
		// also create corresponding array of weights
		int w0 = weights[0];
		int w1 = weights[1];
		int w2 = weights[2];
		int[] ws = new int[]{w2, w2, w2, w1, w0, w1, w2, w0};

        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);

		// iterate over pixels
        boolean modif = false;
        for (int y = sizeY - 1; y >= 0; y--)
		{
			fireProgressChanged(this, sizeY - 1 - y, sizeY); 

			for (int x = sizeX - 1; x >= 0; x--)
			{
				if (!mask.getBoolean(x, y))
					continue;
				
				// get value of current pixel
                int value = distMap.getInt(x, y);

                // iterate over neighbor pixels
                int newVal = value;
				for(int i = 0; i < dx.length; i++)
				{
					// coordinates of neighbor pixel
					int x2 = x + dx[i];
					int y2 = y + dy[i];
					
					// check image bounds
					if (x2 < 0 || x2 > sizeX - 1)
						continue;
					if (y2 < 0 || y2 > sizeY - 1)
						continue;
					
					// process only pixels inside structure
					if (!mask.getBoolean(x2, y2))
						continue;

					// update minimum value
					newVal = Math.min(newVal, distMap.getInt(x2, y2) + ws[i]);
				}
				
				// modify current pixel if needed
		        if (newVal < value) 
		        {
		            modif = true;
		            distMap.setInt(x, y, newVal);
		        }
			}
		}
		
		fireProgressChanged(this, 1, 1); 
        return modif;
	}

	private void normalizeMap(UInt16Array2D distMap)
	{
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);

        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++) 
            {
                int val = distMap.getInt(x, y);
                if (val != Short.MAX_VALUE)
                {
                    distMap.setInt(x, y, val / this.weights[0]);
                }
            }
        }
	}
}
