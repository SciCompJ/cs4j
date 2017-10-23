package net.sci.image.binary.geoddist;

import net.sci.algo.AlgoStub;
import net.sci.array.data.scalar2d.BinaryArray2D;
import net.sci.array.data.scalar2d.UInt16Array2D;
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
public class GeodesicDistanceTransform2DShortScanning5x5 extends AlgoStub implements GeodesicDistanceTransform2D
{
    // ==================================================
    // Class variables 
    
	short[] weights = new short[]{5, 7, 11};

	/**
	 * Flag for dividing final distance map by the value first weight. 
	 * This results in distance map values closer to Euclidean distance. 
	 */
	boolean normalizeMap = true;

	int sizeX;
	int sizeY;

	BinaryArray2D mask;

	/** 
	 * The value assigned to result pixels that do not belong to the mask. 
	 * Default is Short.MAX_VALUE.
	 */
	short backgroundValue = Short.MAX_VALUE;
	
	UInt16Array2D buffer;
	
	boolean modif;


	// ==================================================
    // Constructors 
    
	/**
	 * Use default weights, and normalize map.
	 */
	public GeodesicDistanceTransform2DShortScanning5x5()
	{
		this(ChamferWeights2D.CHESSKNIGHT.getShortWeights(), true);
	}

	public GeodesicDistanceTransform2DShortScanning5x5(ChamferWeights2D weights)
	{
		this(weights.getShortWeights(), true);
	}

	public GeodesicDistanceTransform2DShortScanning5x5(ChamferWeights2D weights, boolean normalizeMap) 
	{
		this(weights.getShortWeights(), normalizeMap);
	}


	public GeodesicDistanceTransform2DShortScanning5x5(short[] weights)
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
	public GeodesicDistanceTransform2DShortScanning5x5(short[] weights, boolean normalizeMap) 
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
	public UInt16Array2D process(BinaryArray2D marker, BinaryArray2D mask)
	{
		// TODO: could use hybrid algorithm
		// TODO: check int overflow?
		
		// size of image
		sizeX = mask.getSize(0);
		sizeY = mask.getSize(1);
		
		// update mask
		this.mask = mask;

		// create new empty image, and fill it with black
		fireStatusChanged(this, "Initialization..."); 
		initializeResult(marker, mask);

		// Performs forward and backward scanning until stabilization
		int iter = 0;
		do 
		{
			modif = false;

			// forward iteration
			fireStatusChanged(this, "Forward iteration " + iter);
			forwardIteration();

			// backward iteration
			fireStatusChanged(this, "Backward iteration " + iter); 
			backwardIteration();

			// Iterate while pixels have been modified
			iter++;
		}
		while (modif);

		// Normalize values by the first weight
		if (this.normalizeMap) 
		{
			fireStatusChanged(this, "Normalize map");
			normalizeMap();
		}
				
		return buffer;
	}

	private void initializeResult(BinaryArray2D marker, BinaryArray2D mask)
	{
	    // Allocate memory
	    buffer = UInt16Array2D.create(sizeX, sizeY);
	    buffer.fillValue(0);
	    
	    // initialize empty image with either 0 (foreground) or Inf (background)
	    for (int y = 0; y < sizeY; y++) 
	    {
	        for (int x = 0; x < sizeX; x++) 
	        {
	            int val = marker.getInt(x, y);
	            buffer.setInt(x, y, val == 0 ? backgroundValue : 0);
	        }
	    }
	}
	
	private void forwardIteration() 
	{
		// create array of offsets relative to current pixel
		int[] dx = new int[]{-1, +1, -2, -1, +0, +1, +2, -1};
		int[] dy = new int[]{-2, -2, -1, -1, -1, -1, -1, +0};
		
		// also create corresponding array of weights
		int w0 = weights[0];
		int w1 = weights[1];
		int w2 = weights[2];
		int[] ws = new int[]{w2, w2, w2, w1, w0, w1, w2, w0};

		// iterate over pixels
		for (int y = 0; y < sizeY; y++)
		{
			fireProgressChanged(this, y, sizeY); 

			for (int x = 0; x < sizeX; x++)
			{
				if (!mask.getBoolean(x, y))
					continue;
				
				// iterate over neighbor pixels
				int newVal = buffer.getInt(x, y);
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
					newVal = Math.min(newVal, buffer.getInt(x2, y2) + ws[i]);
				}
				
				// modify current pixel if needed
				updateIfNeeded(x, y, newVal);
			}
		}
		

		fireProgressChanged(this, 1, 1); 
	}

	private void backwardIteration()
	{
		// create array of offsets relative to current pixel
		int[] dx = new int[]{+1, -1, +2, +1, +0, -1, -2, +1};
		int[] dy = new int[]{+2, +2, +1, +1, +1, +1, +1, +0};
		
		// also create corresponding array of weights
		int w0 = weights[0];
		int w1 = weights[1];
		int w2 = weights[2];
		int[] ws = new int[]{w2, w2, w2, w1, w0, w1, w2, w0};

		// iterate over pixels
		for (int y = sizeY - 1; y >= 0; y--)
		{
			fireProgressChanged(this, sizeY - 1 - y, sizeY); 

			for (int x = sizeX - 1; x >= 0; x--)
			{
				if (!mask.getBoolean(x, y))
					continue;
				
				// iterate over neighbor pixels
				int newVal = buffer.getInt(x, y);
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
					newVal = Math.min(newVal, buffer.getInt(x2, y2) + ws[i]);
				}
				
				// modify current pixel if needed
				updateIfNeeded(x, y, newVal);
			}
		}
		
		fireProgressChanged(this, 1, 1); 
	}
		
	/**
	 * Updates the pixel at position (x,y) with the value newVal. If newVal is
	 * greater or equal to current value at position (x,y), do nothing.
	 */
	private void updateIfNeeded(int x, int y, int newVal) 
	{
		int value = buffer.getInt(x, y);
		if (newVal < value) 
		{
			modif = true;
			buffer.setInt(x, y, newVal);
		}
	}

	private void normalizeMap()
	{
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++) 
            {
                int val = buffer.getInt(x, y);
                if (val != this.backgroundValue)
                {
                    buffer.setInt(x, y, val / this.weights[0]);
                }
            }
        }
	}
}
